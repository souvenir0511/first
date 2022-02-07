package com.zgg.batch.enterprise.main;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import cn.hutool.db.ds.simple.SimpleDataSource;
import cn.hutool.db.handler.EntityListHandler;
import cn.hutool.db.sql.SqlExecutor;
import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.zgg.batch.enterprise.entity.*;
import com.zgg.batch.enterprise.util.DocumentToBeanUtil;
import com.zgg.batch.entity.Enterprise2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.bson.BsonDocument;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 字段转换
 * 1、取得mongo中的数据
 * 2、mongo返回的字段转换譬如 orgName转换为entName
 * 3、转换完成的数据写入新mongo
 */
@Slf4j
public class SpiderMongoToMongoMysql2 {

    static MongoClient fromMongoClient;
    static MongoClient toMongoClient;
    static MongoDatabase fromMongoDatabase;
    static MongoDatabase toMongoDatabase;
    static MongoCollection<Document> fromMongoCollection;
    static MongoCollection<Document> toMongoCollection;


    static String formMongoHost = "192.168.10.45";
    static String toMongoHost = "192.168.10.154";
    static int mongoPort = 27017;
    static String mongoUser = "";
    static String mongoPwd = "";
    static String fromMongoDatabaseName = "QCC_DATA";
    static String fromMongoCollectionName = "qcc_basicInfo";
    static String toMongoDatabaseName = "zgg_data";
    static String toMongoCollectionName = "20211029_enterprise";

    static String driver = "com.mysql.cj.jdbc.Driver";
    static String url="jdbc:mysql://192.168.10.154:3306/spider_qcc_20211009?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&autoReconnect=true&failOverReadOnly=false&rewriteBatchedStatements=true";
    static String user="zhiguo";
    static String pwd = "123456";
    static Vector<Connection> pools = new Vector<>();

    //高企查询
    private static DataSource dataSource;
    private static Connection connection;
    static String url2="jdbc:mysql://192.168.10.154:3306/zgg_spider?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&autoReconnect=true&failOverReadOnly=false&rewriteBatchedStatements=true";


    public static AtomicInteger success = new AtomicInteger(0);

    public static void main(String[] args) throws SQLException {

        //高企查询
        dataSource = new SimpleDataSource(url2, user, pwd);
        connection = dataSource.getConnection();

        init();
        fixEnterpriseData();
        fromMongoClient.close();
        toMongoClient.close();
        DbUtil.close();
    }

    public static void init() {

        fromMongoClient = new MongoClient(formMongoHost, mongoPort);
        fromMongoDatabase = fromMongoClient.getDatabase(fromMongoDatabaseName);
        fromMongoCollection = fromMongoDatabase.getCollection(fromMongoCollectionName);

        toMongoClient = new MongoClient(toMongoHost, mongoPort);
        toMongoDatabase = toMongoClient.getDatabase(toMongoDatabaseName);
        toMongoCollection = toMongoDatabase.getCollection(toMongoCollectionName);
        log.info("mango初始化成功");
    }

    public static Connection getDBConnection(){
        try {
            //1.加载驱动
            Class.forName(driver);
            //2.取得数据库连接
            return DriverManager.getConnection(url, user, pwd);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    static {
        int i = 0;
        //开启连接
        while(i<50){
            pools.add(getDBConnection());
            i++;
        }
        log.info("mysql加载连接成功");
    }

    public static synchronized Connection getPool(){
        if(pools != null && pools.size() > 0){
            int lastInd = pools.size() -1;
            return pools.remove(lastInd);
        }else{
            return getDBConnection();
        }
    }

    public static void fixEnterpriseData() throws SQLException {

        log.info("开始处理");
        int batchSize = 10000;
        String lastId = "";
        int times = 0;
        long count = fromMongoCollection.count();
        int read = 0;

        long allStart = System.currentTimeMillis();
        List<Entity> entityList = SqlExecutor.query(connection, "select enterprise_name,type from growth_enterprise", new EntityListHandler());
        log.info("查询高企{}条,耗时{}ms", entityList.size(), System.currentTimeMillis() - allStart);
        while (++times < batchSize){
            try {
                List<Document> documents = Lists.newArrayList();
                long start = System.currentTimeMillis();
                MongoCursor<Document> iterator = fromMongoCollection
                        .find(BsonDocument.parse("{\"_id\":{$gt:\"" + lastId + "\"}}"))
                        .sort(BsonDocument.parse("{\"_id\":1}"))
                        .limit(batchSize)
                        .iterator();
                if(!iterator.hasNext())
                {
                    log.info( "处理完成" );
                    break;
                }
                while (iterator.hasNext()) {
                    documents.add(iterator.next());
                }
                log.info( "查询mango数据" + batchSize + "条,耗时" + (System.currentTimeMillis()-start) + "ms;" );

                start = System.currentTimeMillis();
                List<Document> list = Lists.newArrayList();
                List<Enterprise2> enterprise2List = new ArrayList<>();
                List<EntBrand> entBrandList = new ArrayList<>();
                List<EntPatent> entPatentList = new ArrayList<>();
                List<EntSoftCopyRight> entSoftCopyRightList = new ArrayList<>();
                List<EntCopyRight> entCopyRightList = new ArrayList<>();
                List<EntCertificate> entCertificateList = new ArrayList<>();
                List<InvEvent> invEventList = new ArrayList<>();
                List<Finance> financeList = new ArrayList<>();
                List<JudgmentDocument> judgmentDocumentList = new ArrayList<>();
                List<Judgment> judgmentList = new ArrayList<>();
                List<ChangeRecord> changeRecordList = new ArrayList<>();
                List<ShareHolder> shareHolderList = new ArrayList<>();
                List<EntFormerName> entFormerNameList = new ArrayList<>();
//                AtomicInteger entPatentCount = new AtomicInteger();
                documents.forEach(item -> {

                    //企业基本信息mango
                    Document doc = DocumentToBeanUtil.spiderMongoToMongo(item, entityList);
                    list.add(doc);
                    //企业基本信息mysql
//                    Enterprise2 enterprise2 = DocumentToBeanUtil.spiderMongoToMysqlEnt(item, entityList);
//                    enterprise2List.add(enterprise2);
//                    //企业工商数据
//                    Document doc1 = DocumentToBeanUtil.spiderMongoToMysql(item);
//
//                    if (!CollectionUtils.isEmpty(doc1.getList("entBrand", EntBrand.class))) {
//                        entBrandList.addAll(doc1.getList("entBrand", EntBrand.class));
//                    }
//                    if (!CollectionUtils.isEmpty(doc1.getList("entPatent", EntPatent.class))) {
//                        entPatentList.addAll(doc1.getList("entPatent", EntPatent.class));
//                    }
//                    if (!CollectionUtils.isEmpty(doc1.getList("entSoftCopyright", EntSoftCopyRight.class))) {
//                        entSoftCopyRightList.addAll(doc1.getList("entSoftCopyright", EntSoftCopyRight.class));
//                    }
//                    if (!CollectionUtils.isEmpty(doc1.getList("enCopyright", EntCopyRight.class))) {
//                        entCopyRightList.addAll(doc1.getList("enCopyright", EntCopyRight.class));
//                    }
//                    if (!CollectionUtils.isEmpty(doc1.getList("entCertificate", EntCertificate.class))) {
//                        entCertificateList.addAll(doc1.getList("entCertificate", EntCertificate.class));
//                    }
//                    if (!CollectionUtils.isEmpty(doc1.getList("invEvent", InvEvent.class))) {
//                        invEventList.addAll(doc1.getList("invEvent", InvEvent.class));
//                    }
//                    if (!CollectionUtils.isEmpty(doc1.getList("finance", Finance.class))) {
//                        financeList.addAll(doc1.getList("finance", Finance.class));
//                    }
//                    if (!CollectionUtils.isEmpty(doc1.getList("judgmentDocument", JudgmentDocument.class))) {
//                        judgmentDocumentList.addAll(doc1.getList("judgmentDocument", JudgmentDocument.class));
//                    }
//                    if (!CollectionUtils.isEmpty(doc1.getList("judgment", Judgment.class))) {
//                        judgmentList.addAll(doc1.getList("judgment", Judgment.class));
//                    }
//                    if (!CollectionUtils.isEmpty(doc1.getList("changeRecord", ChangeRecord.class))) {
//                        changeRecordList.addAll(doc1.getList("changeRecord", ChangeRecord.class));
//                    }
//                    if (!CollectionUtils.isEmpty(doc1.getList("shareHolder", ShareHolder.class))) {
//                        shareHolderList.addAll(doc1.getList("shareHolder", ShareHolder.class));
//                    }
//                    if (!CollectionUtils.isEmpty(doc1.getList("entFormerName", EntFormerName.class))) {
//                        entFormerNameList.addAll(doc1.getList("entFormerName", EntFormerName.class));
//                    }

//                    log.info("数据已处理{}条", entPatentCount.getAndIncrement() + 1);
                });

                lastId = documents.get(documents.size()-1).getString("_id");
                read += documents.size();
                log.info("读取进度" + read + "/" + count + ",耗时" + (System.currentTimeMillis() - start) + "ms");

                try {
                    //插入mongo
                    toMongoCollection.insertMany(list);

//                    //插入MYSQL
//                    entEnterpriseBatch(enterprise2List);
//                    entBrandBatch(entBrandList);
//                    entPatentBatch(entPatentList);
//                    entSoftCopyRightBatch(entSoftCopyRightList);
//                    entCopyRightBatch(entCopyRightList);
//                    entCertificateBatch(entCertificateList);
//                    invEventBatch(invEventList);
//                    financeBatch(financeList);
//                    //judgmentDocumentBatch(judgmentDocumentList);
//                    judgmentBatch(judgmentList);
//                    changeRecordBatch(changeRecordList);
//                    shareHolderBatch(shareHolderList);
//                    entFormerNameBatch(entFormerNameList);

                    log.info("size:{};耗时:{}ms;", list.size(), System.currentTimeMillis() - start);
                }catch (Exception e){
                    log.error(e.getMessage(),e);
                }
                success.incrementAndGet();
                log.info("处理进度" + success.get()*batchSize + "/" + count + ",耗时" + (System.currentTimeMillis() - start) + "ms" );
            }catch (Exception e){
                log.error( "未知报错，lastId:" + lastId , e );
            }
        }

        log.info("总耗时{}ms",System.currentTimeMillis()-allStart);
    }
    /**
     * 计算得到总页数
     *
     * @return
     */
    public static int getTotalPage(int count, int pageSize) {
        int totalPage;
        // 假设总数是50，是能够被5整除的，那么就有10页
        if (0 == count % pageSize) {
            totalPage = count / pageSize;
        }
        // 假设总数是51，不能够被5整除的，那么就有11页
        else {
            totalPage = count / pageSize + 1;
        }

        if (0 == totalPage) {
            totalPage = 1;
        }
        return totalPage;
    }

    /**
     * 多线程处理
     * @param
     */
    public static void entEnterpriseBatch(List<Enterprise2> enterprise2List){

        //数据库插入
        int amount;
        if (CollectionUtil.isNotEmpty(enterprise2List)) {
            amount = enterprise2List.size();
            int pageSize = 5000;
            int totalPage = getTotalPage(amount, pageSize);
//            ExecutorService executorService = Executors.newFixedThreadPool(totalPage);
//            CountDownLatch latch = new CountDownLatch(totalPage);
            for (int pageNo = 0; pageNo < totalPage; pageNo++) {
                int fromIndex = pageNo * pageSize;
                int toIndex = (pageNo + 1) * pageSize;
                if (toIndex > amount) {
                    toIndex = amount;
                }
                int finalToIndex = toIndex;
                int finalPageNo = pageNo;
//                executorService.submit(() -> {
                    List<Enterprise2> entBrands = enterprise2List.subList(fromIndex, finalToIndex);
                    Enterprise2Processor(entBrands);
//                    log.info("enterprise2线程" + finalPageNo + "提交数据库" + entBrands.size());
//                    latch.countDown();
//                });
            }
        }

    }

    public static void Enterprise2Processor(List<Enterprise2> enterprise2List) {
        long start = System.currentTimeMillis();
        Connection conn = getPool();
        //字段
        String field = "ent_id,ent_name,old_ent_names,fa_ren,reg_money,ent_status,reg_date,credit_code,ent_type,industry,experience_scope,introduce,gj_gao_xin,zgc_gao_xin,jin_zhong_zi,deng_ling_yang,du_jiao_shou,jin_zhuan_te_Xin,brand_count,patent_count,software_copyright_count,copyright_count,certificate_count,inv_event_count,finance_count";
        //占位符
        String placeholder = "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?";
        //sql
        String sql = "insert into `enterprise`(" + field + ")values(" + placeholder + ")";
        List<Object[]> objectsList = new ArrayList<>();
        enterprise2List.forEach(item -> {
            //值
            Object[] objects = {
                    item.getEntId(),item.getEntName(),item.getOldEntNames(),item.getFaRen(),item.getRegMoney(),item.getEntStatus(),item.getRegDate(),item.getCreditCode(),item.getEntType(),item.getIndustry(),item.getExperienceScope(),item.getIntroduce(),item.getGjGaoXin(),item.getZgcGaoXin(),item.getJinZhongZi(),item.getDengLingYang(),item.getDuJiaoShou(),item.getJinZhuanTeXin(),item.getBrandCount(),item.getPatentCount(),item.getSoftwareCopyrightCount(),item
                    .getCopyrightCount(),item.getCertificateCount(),item.getInvEventCount(),item.getFinanceCount()
            };
            objectsList.add(objects);
        });

        try {
            SqlExecutor.executeBatch(conn, sql, objectsList);
            conn.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
            }
        }
        log.info("Enterprise2Processor，"+enterprise2List.size() + "条耗时" + (System.currentTimeMillis() - start) + "ms;");
    }

    /**
     * 多线程处理
     * @param entBrandList
     */
    public static void entBrandBatch(List<EntBrand> entBrandList){

        //数据库插入
        int amount;
        if (CollectionUtil.isNotEmpty(entBrandList)) {
            amount = entBrandList.size();
            int pageSize = 5000;
            int totalPage = getTotalPage(amount, pageSize);
//            ExecutorService executorService = Executors.newFixedThreadPool(totalPage);
//            CountDownLatch latch = new CountDownLatch(totalPage);
            for (int pageNo = 0; pageNo < totalPage; pageNo++) {
                int fromIndex = pageNo * pageSize;
                int toIndex = (pageNo + 1) * pageSize;
                if (toIndex > amount) {
                    toIndex = amount;
                }
                int finalToIndex = toIndex;
                int finalPageNo = pageNo;
//                executorService.submit(() -> {
                    List<EntBrand> entBrands = entBrandList.subList(fromIndex, finalToIndex);
                    entBrandProcessor(entBrands);
//                    log.info("entBrandBatch线程" + finalPageNo + "提交数据库" + entBrands.size());
//                    latch.countDown();
//                });
            }
        }

    }

    public static void entBrandProcessor(List<EntBrand> entBrandList) {
        long start = System.currentTimeMillis();
        Connection conn = getPool();
        //字段
        String field = "id,ent_id,brand_name,apply_date,reg_code,int_cls,brand_type,brand_status,brand_imag,ent_name";
        //占位符
        String placeholder = "?,?,?,?,?,?,?,?,?,?";
        //sql
        String sql = "insert into `ent_brand`(" + field + ")values(" + placeholder + ")";
        List<Object[]> objectsList = new ArrayList<>();
        entBrandList.forEach(item -> {
            item.setId(IdUtil.getSnowflake(1,1).nextId());
            //值
            Object[] objects = {
                    item.getId(),item.getEntId(),item.getBrandName(),item.getApplyDate(),item.getRegCode(),item.getIntCls(),item.getBrandType(),item.getBrandStatus(),item.getBrandImag(),item.getEntName()
            };
            objectsList.add(objects);
        });

        try {
            SqlExecutor.executeBatch(conn, sql, objectsList);
            conn.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
            }
        }
        log.info("entBrandProcessor任务结束，"+entBrandList.size() + "条耗时" + (System.currentTimeMillis() - start) + "ms;");
    }

    /**
     * 多线程处理
     * @param entPatentList
     */
    public static void entPatentBatch(List<EntPatent> entPatentList){

        //数据库插入
        int amount;
        if (CollectionUtil.isNotEmpty(entPatentList)) {
            amount = entPatentList.size();
            int pageSize = 5000;
            int totalPage = getTotalPage(amount, pageSize);
//            ExecutorService executorService = Executors.newFixedThreadPool(totalPage);
//            CountDownLatch latch = new CountDownLatch(totalPage);
            for (int pageNo = 0; pageNo < totalPage; pageNo++) {
                int fromIndex = pageNo * pageSize;
                int toIndex = (pageNo + 1) * pageSize;
                if (toIndex > amount) {
                    toIndex = amount;
                }
                int finalToIndex = toIndex;
                int finalPageNo = pageNo;
//                executorService.submit(() -> {
                    List<EntPatent> entPatents = entPatentList.subList(fromIndex, finalToIndex);
                    entPatentProcessor(entPatents);
//                    log.info("entPatentBatch线程" + finalPageNo + "提交数据库" + entPatents.size());
//                    latch.countDown();
//                });
            }
        }

    }

    public static void entPatentProcessor(List<EntPatent> entPatentList) {
        long start = System.currentTimeMillis();
        Connection conn = getPool();
        //字段
        String field = "id,ent_id,patent_name,patent_id,patent_type,patent_status,reg_date,open_date,open_no,ent_name";
        //占位符
        String placeholder = "?,?,?,?,?,?,?,?,?,?";
        //sql
        String sql = "insert into `ent_patent`(" + field + ")values(" + placeholder + ")";
        List<Object[]> objectsList = new ArrayList<>();
        entPatentList.forEach(item -> {
            item.setId(IdUtil.getSnowflake(1,1).nextId());
            //值
            Object[] objects = {
                    item.getId(),item.getEntId(),item.getPatentName(),item.getPatentId(),item.getPatentType(),item.getPatentStatus(),item.getRegDate(),item.getOpenDate(),item.getOpenNo(),item.getEntName()
            };
            objectsList.add(objects);
        });

        try {
            SqlExecutor.executeBatch(conn, sql, objectsList);
            conn.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
            }
        }
        log.info("entPatentProcessor任务结束,"+entPatentList.size() + "条耗时" + (System.currentTimeMillis() - start) + "ms;");
    }

    /**
     * 多线程处理
     * @param entSoftCopyRightList
     */
    public static void entSoftCopyRightBatch(List<EntSoftCopyRight> entSoftCopyRightList){

        //数据库插入
        int amount;
        if (CollectionUtil.isNotEmpty(entSoftCopyRightList)) {
            amount = entSoftCopyRightList.size();
            int pageSize = 5000;
            int totalPage = getTotalPage(amount, pageSize);
//            ExecutorService executorService = Executors.newFixedThreadPool(totalPage);
//            CountDownLatch latch = new CountDownLatch(totalPage);
            for (int pageNo = 0; pageNo < totalPage; pageNo++) {
                int fromIndex = pageNo * pageSize;
                int toIndex = (pageNo + 1) * pageSize;
                if (toIndex > amount) {
                    toIndex = amount;
                }
                int finalToIndex = toIndex;
                int finalPageNo = pageNo;
//                executorService.submit(() -> {
                    List<EntSoftCopyRight> entSoftCopyRights = entSoftCopyRightList.subList(fromIndex, finalToIndex);
                    entSoftCopyRightProcessor(entSoftCopyRights);
//                    log.info("entSoftCopyRightBatch线程" + finalPageNo + "提交数据库" + entSoftCopyRights.size());
//                    latch.countDown();
//                });
            }
        }

    }

    public static void entSoftCopyRightProcessor(List<EntSoftCopyRight> entSoftCopyRightList) {
        long start = System.currentTimeMillis();
        Connection conn = getPool();
        //字段
        String field = "id,ent_id,soft_name,reg_date,reg_no,soft_edition,soft_abb,notice_date,ent_name";
        //占位符
        String placeholder = "?,?,?,?,?,?,?,?,?";
        //sql
        String sql = "insert into `ent_softcopyright`(" + field + ")values(" + placeholder + ")";
        List<Object[]> objectsList = new ArrayList<>();
        entSoftCopyRightList.forEach(item -> {
            item.setId(IdUtil.getSnowflake(1,1).nextId());
            //值
            Object[] objects = {
                    item.getId(),item.getEntId(),item.getSoftName(),item.getRegDate(),item.getRegNo(),item.getSoftEdition(),item.getSoftAbb(),item.getNoticeDate(),item.getEntName()
            };
            objectsList.add(objects);
        });

        try {
            SqlExecutor.executeBatch(conn, sql, objectsList);
            conn.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
            }
        }
        log.info("entSoftCopyRightProcessor任务结束，"+entSoftCopyRightList.size() + "条耗时" + (System.currentTimeMillis() - start) + "ms;");
    }

    /**
     * 多线程处理
     * @param entCopyRightList
     */
    public static void entCopyRightBatch(List<EntCopyRight> entCopyRightList){

        //数据库插入
        int amount;
        if (CollectionUtil.isNotEmpty(entCopyRightList)) {
            amount = entCopyRightList.size();
            int pageSize = 5000;
            int totalPage = getTotalPage(amount, pageSize);
//            ExecutorService executorService = Executors.newFixedThreadPool(totalPage);
//            CountDownLatch latch = new CountDownLatch(totalPage);
            for (int pageNo = 0; pageNo < totalPage; pageNo++) {
                int fromIndex = pageNo * pageSize;
                int toIndex = (pageNo + 1) * pageSize;
                if (toIndex > amount) {
                    toIndex = amount;
                }
                int finalToIndex = toIndex;
                int finalPageNo = pageNo;
//                executorService.submit(() -> {
                    List<EntCopyRight> entCopyRights = entCopyRightList.subList(fromIndex, finalToIndex);
                    entCopyRightProcessor(entCopyRights);
//                    log.info("entCopyRightBatch线程" + finalPageNo + "提交数据库" + entCopyRights.size());
//                    latch.countDown();
//                });
            }
        }

    }

    public static void entCopyRightProcessor(List<EntCopyRight> entCopyRightList) {
        long start = System.currentTimeMillis();
        Connection conn = getPool();
        //字段
        String field = "id,ent_id,product_name,reg_date,reg_no,create_date,product_type,publish_date,ent_name";
        //占位符
        String placeholder = "?,?,?,?,?,?,?,?,?";
        //sql
        String sql = "insert into `ent_copyright`(" + field + ")values(" + placeholder + ")";
        List<Object[]> objectsList = new ArrayList<>();
        entCopyRightList.forEach(item -> {
            item.setId(IdUtil.getSnowflake(1,1).nextId());
            //值
            Object[] objects = {
                    item.getId(),item.getEntId(),item.getProductName(),item.getRegDate(),item.getRegNo(),item.getCreateDate(),item.getProductType(),item.getPublishDate(),item.getEntName()
            };
            objectsList.add(objects);
        });

        try {
            SqlExecutor.executeBatch(conn, sql, objectsList);
            conn.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
            }
        }
        log.info("entCopyRightProcessor任务结束，"+entCopyRightList.size() + "条耗时" + (System.currentTimeMillis() - start) + "ms;");
    }

    /**
     * 多线程处理
     * @param entCertificateList
     */
    public static void entCertificateBatch(List<EntCertificate> entCertificateList){

        //数据库插入
        int amount;
        if (CollectionUtil.isNotEmpty(entCertificateList)) {
            amount = entCertificateList.size();
            int pageSize = 5000;
            int totalPage = getTotalPage(amount, pageSize);
//            ExecutorService executorService = Executors.newFixedThreadPool(totalPage);
//            CountDownLatch latch = new CountDownLatch(totalPage);
            for (int pageNo = 0; pageNo < totalPage; pageNo++) {
                int fromIndex = pageNo * pageSize;
                int toIndex = (pageNo + 1) * pageSize;
                if (toIndex > amount) {
                    toIndex = amount;
                }
                int finalToIndex = toIndex;
                int finalPageNo = pageNo;
//                executorService.submit(() -> {
                    List<EntCertificate> entCertificates = entCertificateList.subList(fromIndex, finalToIndex);
                    entCertificateProcessor(entCertificates);
//                    log.info("entCertificateBatch线程" + finalPageNo + "提交数据库" + entCertificates.size());
//                    latch.countDown();
//                });
            }
        }

    }

    public static void entCertificateProcessor(List<EntCertificate> entCertificateList) {
        long start = System.currentTimeMillis();
        Connection conn = getPool();
        //字段
        String field = "id,ent_id,certificate_type,certificate_name,certificate_code,create_date,stop_date,ent_name";
        //占位符
        String placeholder = "?,?,?,?,?,?,?,?";
        //sql
        String sql = "insert into `ent_certificate`(" + field + ")values(" + placeholder + ")";
        List<Object[]> objectsList = new ArrayList<>();
        entCertificateList.forEach(item -> {
            item.setId(IdUtil.getSnowflake(1,1).nextId());
            //值
            Object[] objects = {
                    item.getId(),item.getEntId(),item.getCertificateType(),item.getCertificateName(),item.getCertificateCode(),item.getCreateDate(),item.getEndDate(),item.getEntName()
            };
            objectsList.add(objects);
        });

        try {
            SqlExecutor.executeBatch(conn, sql, objectsList);
            conn.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
            }
        }
        log.info("entCertificateProcessor任务结束，"+entCertificateList.size() + "条耗时" + (System.currentTimeMillis() - start) + "ms;");
    }

    /**
     * 多线程处理
     * @param invEventList
     */
    public static void invEventBatch(List<InvEvent> invEventList){

        //数据库插入
        int amount;
        if (CollectionUtil.isNotEmpty(invEventList)) {
            amount = invEventList.size();
            int pageSize = 5000;
            int totalPage = getTotalPage(amount, pageSize);
//            ExecutorService executorService = Executors.newFixedThreadPool(totalPage);
//            CountDownLatch latch = new CountDownLatch(totalPage);
            for (int pageNo = 0; pageNo < totalPage; pageNo++) {
                int fromIndex = pageNo * pageSize;
                int toIndex = (pageNo + 1) * pageSize;
                if (toIndex > amount) {
                    toIndex = amount;
                }
                int finalToIndex = toIndex;
                int finalPageNo = pageNo;
//                executorService.submit(() -> {
                    List<InvEvent> invEvents = invEventList.subList(fromIndex, finalToIndex);
                    invEventProcessor(invEvents);
//                    log.info("invEventBatch线程" + finalPageNo + "提交数据库" + invEvents.size());
//                    latch.countDown();
//                });
            }
        }

    }

    public static void invEventProcessor(List<InvEvent> invEventList) {
        long start = System.currentTimeMillis();
        Connection conn = getPool();
        //字段
        String field = "id,ent_id,ent_name,invested_name,legal_person,investment_amount,investment_proportion";
        //占位符
        String placeholder = "?,?,?,?,?,?,?";
        //sql
        String sql = "insert into `investment`(" + field + ")values(" + placeholder + ")";
        List<Object[]> objectsList = new ArrayList<>();
        invEventList.forEach(item -> {
            item.setId(IdUtil.getSnowflake(1,1).nextId());
            //值
            Object[] objects = {
                    item.getId(),item.getEntId(),item.getEntName(),item.getInvestedName(),item.getLegalPerson(),item.getInvestmentAmount(),item.getInvestmentProportion()
            };
            objectsList.add(objects);
        });

        try {
            SqlExecutor.executeBatch(conn, sql, objectsList);
            conn.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
            }
        }
        log.info("invEventProcessor任务结束，" + invEventList.size() + "条耗时" + (System.currentTimeMillis() - start) + "ms;");
    }

    /**
     * 多线程处理
     * @param financeList
     */
    public static void financeBatch(List<Finance> financeList){

        //数据库插入
        int amount;
        if (CollectionUtil.isNotEmpty(financeList)) {
            amount = financeList.size();
            int pageSize = 5000;
            int totalPage = getTotalPage(amount, pageSize);
//            ExecutorService executorService = Executors.newFixedThreadPool(totalPage);
//            CountDownLatch latch = new CountDownLatch(totalPage);
            for (int pageNo = 0; pageNo < totalPage; pageNo++) {
                int fromIndex = pageNo * pageSize;
                int toIndex = (pageNo + 1) * pageSize;
                if (toIndex > amount) {
                    toIndex = amount;
                }
                int finalToIndex = toIndex;
                int finalPageNo = pageNo;
//                executorService.submit(() -> {
                    List<Finance> finances = financeList.subList(fromIndex, finalToIndex);
                    financeProcessor(finances);
//                    log.info("financeBatch线程" + finalPageNo + "提交数据库" + finances.size());
//                    latch.countDown();
//                });
            }
        }

    }

    public static void financeProcessor(List<Finance> financeList) {
        long start = System.currentTimeMillis();
        Connection conn = getPool();
        //字段
        String field = "id,ent_id,ent_name,financ_date,financ_rounds,financ_amount,investor";
        //占位符
        String placeholder = "?,?,?,?,?,?,?";
        //sql
        String sql = "insert into `financ`(" + field + ")values(" + placeholder + ")";
        List<Object[]> objectsList = new ArrayList<>();
        financeList.forEach(item -> {
            item.setId(IdUtil.getSnowflake(1,1).nextId());
            //值
            Object[] objects = {
                    item.getId(),item.getEntId(),item.getEntName(),item.getFinanceDate(),item.getFinanceRounds(),item.getFinanceAmount(),item.getInvestor()
            };
            objectsList.add(objects);
        });

        try {
            SqlExecutor.executeBatch(conn, sql, objectsList);
            conn.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
            }
        }
        log.info("financeProcessor任务结束，"+financeList.size() + "条耗时" + (System.currentTimeMillis() - start) + "ms;");
    }

    /**
     * 多线程处理
     * @param judgmentDocumentList
     */
    public static void judgmentDocumentBatch(List<JudgmentDocument> judgmentDocumentList){

        //数据库插入
        int amount;
        if (CollectionUtil.isNotEmpty(judgmentDocumentList)) {
            amount = judgmentDocumentList.size();
            int pageSize = 5000;
            int totalPage = getTotalPage(amount, pageSize);
//            ExecutorService executorService = Executors.newFixedThreadPool(totalPage);
//            CountDownLatch latch = new CountDownLatch(totalPage);
            for (int pageNo = 0; pageNo < totalPage; pageNo++) {
                int fromIndex = pageNo * pageSize;
                int toIndex = (pageNo + 1) * pageSize;
                if (toIndex > amount) {
                    toIndex = amount;
                }
                int finalToIndex = toIndex;
                int finalPageNo = pageNo;
//                executorService.submit(() -> {
                    List<JudgmentDocument> judgmentDocuments = judgmentDocumentList.subList(fromIndex, finalToIndex);
                    judgmentDocumentProcessor(judgmentDocuments);
//                    log.info("judgmentDocumentBatch线程" + finalPageNo + "提交数据库" + judgmentDocuments.size());
//                    latch.countDown();
//                });
            }
        }

    }

    public static void judgmentDocumentProcessor(List<JudgmentDocument> judgmentDocumentList) {
        long start = System.currentTimeMillis();
        Connection conn = getPool();
        //字段
        String field = "id,ent_id,case_name,case_cause,case_no,plaintiff,defendant,result,case_date,notice_date,chief_judge,juror,people_juror,clerk,court_name,cases_type,content";
        //占位符
        String placeholder = "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?";
        //sql
        String sql = "insert into `judgment_document`(" + field + ")values(" + placeholder + ")";
        List<Object[]> objectsList = new ArrayList<>();
        judgmentDocumentList.forEach(item -> {
            item.setId(IdUtil.getSnowflake(1,1).nextId());
            //值
            Object[] objects = {
                    item.getId(),item.getEntId(),item.getCaseName(),item.getCaseCause(),item.getCaseNo(),item.getPlaintiff(),item.getDefendant(),item.getResult(),item.getCaseDate(),item.getNoticeDate(),item.getChiefJudge(),item.getJuror(),item.getPeopleJuror(),item.getClerk(),item.getCourtName(),item.getCasesType(),item.getContent()
            };
            objectsList.add(objects);
        });

        try {
            SqlExecutor.executeBatch(conn, sql, objectsList);
            conn.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
            }
        }
        log.info("judgmentDocumentProcessor任务结束，"+judgmentDocumentList.size() + "条耗时" + (System.currentTimeMillis() - start) + "ms;");
    }

    /**
     * 多线程处理
     * @param judgmentList
     */
    public static void judgmentBatch(List<Judgment> judgmentList){

        //数据库插入
        int amount;
        if (CollectionUtil.isNotEmpty(judgmentList)) {
            amount = judgmentList.size();
            int pageSize = 1000;
            int totalPage = getTotalPage(amount, pageSize);
//            ExecutorService executorService = Executors.newFixedThreadPool(totalPage);
//            CountDownLatch latch = new CountDownLatch(totalPage);
            for (int pageNo = 0; pageNo < totalPage; pageNo++) {
                int fromIndex = pageNo * pageSize;
                int toIndex = (pageNo + 1) * pageSize;
                if (toIndex > amount) {
                    toIndex = amount;
                }
                int finalToIndex = toIndex;
                int finalPageNo = pageNo;
//                executorService.submit(() -> {
                    List<Judgment> judgments = judgmentList.subList(fromIndex, finalToIndex);
                    judgmentProcessor(judgments);
//                    log.info("judgmentBatch线程" + finalPageNo + "提交数据库" + judgments.size());
//                    latch.countDown();
//                });
            }
        }

    }

    public static void judgmentProcessor(List<Judgment> judgmentList) {
        long start = System.currentTimeMillis();
        Connection conn = getPool();
        //字段
        String field = "id,ent_id,case_name,case_cause,case_no,plaintiff_defendant,case_money,result,content,case_date,notice_date,ent_name";
        //占位符
        String placeholder = "?,?,?,?,?,?,?,?,?,?,?,?";
        //sql
        String sql = "insert into `judgment`(" + field + ")values(" + placeholder + ")";
        List<Object[]> objectsList = new ArrayList<>();
        judgmentList.forEach(item -> {
            item.setId(IdUtil.getSnowflake(1,1).nextId());
            //值
            Object[] objects = {
                    item.getId(),item.getEntId(),item.getCaseName(),item.getCaseCause(),item.getCaseNo(),item.getPlaintiffDefendant(),item.getCaseMoney(),item.getResult(),item.getContent(),item.getCaseDate(),item.getNoticeDate(),item.getEntName()
            };
            objectsList.add(objects);
        });

        try {
            SqlExecutor.executeBatch(conn, sql, objectsList);
            conn.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
            }
        }
        log.info("judgmentProcessor任务结束，"+judgmentList.size() + "条耗时" + (System.currentTimeMillis() - start) + "ms;");
    }

    /**
     * 多线程处理
     * @param changeRecordList
     */
    public static void changeRecordBatch(List<ChangeRecord> changeRecordList){

        //数据库插入
        int amount;
        if (CollectionUtil.isNotEmpty(changeRecordList)) {
            amount = changeRecordList.size();
            int pageSize = 5000;
            int totalPage = getTotalPage(amount, pageSize);
//            ExecutorService executorService = Executors.newFixedThreadPool(totalPage);
//            CountDownLatch latch = new CountDownLatch(totalPage);
            for (int pageNo = 0; pageNo < totalPage; pageNo++) {
                int fromIndex = pageNo * pageSize;
                int toIndex = (pageNo + 1) * pageSize;
                if (toIndex > amount) {
                    toIndex = amount;
                }
                int finalToIndex = toIndex;
                int finalPageNo = pageNo;
//                executorService.submit(() -> {
                    List<ChangeRecord> changeRecords = changeRecordList.subList(fromIndex, finalToIndex);
                    changeRecordProcessor(changeRecords);
//                    log.info("changeRecordBatch线程" + finalPageNo + "提交数据库" + changeRecords.size());
//                    latch.countDown();
//                });
            }
        }

    }

    public static void changeRecordProcessor(List<ChangeRecord> changeRecordList) {
        long start = System.currentTimeMillis();
        Connection conn = getPool();
        //字段
        String field = "id,ent_id,project,change_date,old_data,new_data,ent_name";
        //占位符
        String placeholder = "?,?,?,?,?,?,?";
        //sql
        String sql = "insert into `change_record`(" + field + ")values(" + placeholder + ")";
        List<Object[]> objectsList = new ArrayList<>();
        changeRecordList.forEach(item -> {
            item.setId(IdUtil.getSnowflake(1,1).nextId());
            //值
            Object[] objects = {
                    item.getId(),item.getEntId(),item.getProject(),item.getChangeDate(),item.getOldData(),item.getNewData(),item.getEntName()
            };
            objectsList.add(objects);
        });

        try {
            SqlExecutor.executeBatch(conn, sql, objectsList);
            conn.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
            }
        }
        log.info("changeRecordProcessor任务结束，"+changeRecordList.size() + "条耗时" + (System.currentTimeMillis() - start) + "ms;");
    }


    /**
     * 多线程处理
     * @param shareHolderList
     */
    public static void shareHolderBatch(List<ShareHolder> shareHolderList){

        //数据库插入
        int amount;
        if (CollectionUtil.isNotEmpty(shareHolderList)) {
            amount = shareHolderList.size();
            int pageSize = 5000;
            int totalPage = getTotalPage(amount, pageSize);
//            ExecutorService executorService = Executors.newFixedThreadPool(totalPage);
//            CountDownLatch latch = new CountDownLatch(totalPage);
            for (int pageNo = 0; pageNo < totalPage; pageNo++) {
                int fromIndex = pageNo * pageSize;
                int toIndex = (pageNo + 1) * pageSize;
                if (toIndex > amount) {
                    toIndex = amount;
                }
                int finalToIndex = toIndex;
                int finalPageNo = pageNo;
//                executorService.submit(() -> {
                    List<ShareHolder> shareHolders = shareHolderList.subList(fromIndex, finalToIndex);
                    shareHolderProcessor(shareHolders);
//                    log.info("shareHolderBatch线程" + finalPageNo + "提交数据库" + shareHolders.size());
//                    latch.countDown();
//                });
            }
        }

    }

    public static void shareHolderProcessor(List<ShareHolder> shareHolderList) {
        long start = System.currentTimeMillis();
        Connection conn = getPool();
        //字段
        String field = "id,ent_id,share_holder_name,share_holding_ratio,share_holding_number,ent_name";
        //占位符
        String placeholder = "?,?,?,?,?,?";
        //sql
        String sql = "insert into `share_holder`(" + field + ")values(" + placeholder + ")";
        List<Object[]> objectsList = new ArrayList<>();
        shareHolderList.forEach(item -> {
            item.setId(IdUtil.getSnowflake(1,1).nextId());
            //值
            Object[] objects = {
                    item.getId(),item.getEntId(),item.getShareHolderName(),item.getShareHoldingRatio(),item.getShareHoldingNumber(),item.getEntName()
            };
            objectsList.add(objects);
        });

        try {
            SqlExecutor.executeBatch(conn, sql, objectsList);
            conn.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
            }
        }
        log.info("shareHolderProcessor任务结束，"+shareHolderList.size() + "条耗时" + (System.currentTimeMillis() - start) + "ms;");
    }


    /**
     * 多线程处理
     * @param entFormerNameList
     */
    public static void entFormerNameBatch(List<EntFormerName> entFormerNameList){

        //数据库插入
        int amount;
        if (CollectionUtil.isNotEmpty(entFormerNameList)) {
            amount = entFormerNameList.size();
            int pageSize = 5000;
            int totalPage = getTotalPage(amount, pageSize);
//            ExecutorService executorService = Executors.newFixedThreadPool(totalPage);
//            CountDownLatch latch = new CountDownLatch(totalPage);
            for (int pageNo = 0; pageNo < totalPage; pageNo++) {
                int fromIndex = pageNo * pageSize;
                int toIndex = (pageNo + 1) * pageSize;
                if (toIndex > amount) {
                    toIndex = amount;
                }
                int finalToIndex = toIndex;
                int finalPageNo = pageNo;
//                executorService.submit(() -> {
                    List<EntFormerName> entFormerNames = entFormerNameList.subList(fromIndex, finalToIndex);
                    entFormerNameProcessor(entFormerNames);
//                    log.info("entFormerNameBatch线程" + finalPageNo + "提交数据库" + entFormerNames.size());
//                    latch.countDown();
//                });
            }
        }

    }

    public static void entFormerNameProcessor(List<EntFormerName> entFormerNameList) {
        long start = System.currentTimeMillis();
        Connection conn = getPool();
        //字段
        String field = "id,ent_id,former_name,ent_name";
        //占位符
        String placeholder = "?,?,?,?";
        //sql
        String sql = "insert into `ent_former_name`(" + field + ")values(" + placeholder + ")";
        List<Object[]> objectsList = new ArrayList<>();
        entFormerNameList.forEach(item -> {
            item.setId(IdUtil.getSnowflake(1,1).nextId());
            //值
            Object[] objects = {
                    item.getId(),item.getEntId(),item.getFormerName(),item.getEntName()
            };
            objectsList.add(objects);
        });

        try {
            SqlExecutor.executeBatch(conn, sql, objectsList);
            conn.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
            }
        }
        log.info("entFormerNameProcessor任务结束，"+entFormerNameList.size() + "条耗时" + (System.currentTimeMillis() - start) + "ms;");
    }

}
