package com.zgg.batch.enterprise.main;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.db.DbUtil;
import cn.hutool.db.sql.SqlExecutor;
import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.zgg.batch.enterprise.entity.*;
import com.zgg.batch.enterprise.util.DocumentToBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonDocument;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 字段转换
 * 1、取得mongo中的数据
 * 2、mongo返回的字段转换譬如 orgName转换为entName
 * 3、转换完成的数据写入新mongo
 */
@Slf4j
public class SpiderMongoToMongoMysql {
    private static final Logger logger = LoggerFactory.getLogger(SpiderMongoToMongoMysql.class);

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
    static String toMongoCollectionName = "40_enterprise";

    static String driver = "com.mysql.cj.jdbc.Driver";
    static String url="jdbc:mysql://192.168.10.154:3306/zgg_data?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&autoReconnect=true&failOverReadOnly=false&rewriteBatchedStatements=true";
    static String user="zhiguo";
    static String pwd = "123456";
    static Vector<Connection> pools = new Vector<>();

    public static AtomicInteger success = new AtomicInteger(0);

    public static void main(String[] args) {

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
        //开启50个连接
        while(i<50){
            pools.add(getDBConnection());
            i++;
        }
    }

    public static synchronized Connection getPool(){
        if(pools != null && pools.size() > 0){
            int lastInd = pools.size() -1;
            return pools.remove(lastInd);
        }else{
            return getDBConnection();
        }
    }

    public static void fixEnterpriseData() {
        int batchSize = 10000;
        String lastId = "";
        int times = 0;
        long count = fromMongoCollection.count();
        int read = 0;

        long allStart = System.currentTimeMillis();
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
                    logger.info( "处理完成" );
                    break;
                }
                while (iterator.hasNext()) {
                    documents.add(iterator.next());
                }
                logger.info( "查询数据" + batchSize + "条耗时" + (System.currentTimeMillis()-start) + "ms;" );

                start = System.currentTimeMillis();

                List<Document> list = Lists.newArrayList();
                List<Document> entBrandDocumentList = Lists.newArrayList();
                List<EntBrand> entBrandList = new ArrayList<>();
                List<EntPatent> entPatentList = new ArrayList<>();
                List<EntSoftCopyRight> entSoftCopyRightList = new ArrayList<>();
                List<EntCopyRight> entCopyRightList = new ArrayList<>();
                List<EntCertificate> entCertificateList = new ArrayList<>();
                List<InvEvent> invEventList = new ArrayList<>();
                List<Finance> financeList = new ArrayList<>();
                List<JudgmentDocument> judgmentDocumentList = new ArrayList<>();
                for(Document document : documents ) {

                    //企业基本信息
                    Document doc = DocumentToBeanUtil.spiderMongoToMongo(document,new ArrayList<>());
                    list.add(doc);
                    //企业工商数据
                    Document doc1 = DocumentToBeanUtil.spiderMongoToMysql(document);

                    if (!CollectionUtils.isEmpty(doc1.getList("entBrand", EntBrand.class))) {
                        entBrandList.addAll(doc1.getList("entBrand", EntBrand.class));
                    }
                    if (!CollectionUtils.isEmpty(doc1.getList("entPatent", EntPatent.class))) {
                        entPatentList.addAll(doc1.getList("entPatent", EntPatent.class));
                    }
                    if (!CollectionUtils.isEmpty(doc1.getList("entSoftCopyright", EntSoftCopyRight.class))) {
                        entSoftCopyRightList.addAll(doc1.getList("entSoftCopyright", EntSoftCopyRight.class));
                    }
                    if (!CollectionUtils.isEmpty(doc1.getList("enCopyright", EntCopyRight.class))) {
                        entCopyRightList.addAll(doc1.getList("enCopyright", EntCopyRight.class));
                    }
                    if (!CollectionUtils.isEmpty(doc1.getList("entCertificate", EntCertificate.class))) {
                        entCertificateList.addAll(doc1.getList("entCertificate", EntCertificate.class));
                    }
                    if (!CollectionUtils.isEmpty(doc1.getList("invEvent", InvEvent.class))) {
                        invEventList.addAll(doc1.getList("invEvent", InvEvent.class));
                    }
                    if (!CollectionUtils.isEmpty(doc1.getList("finance", Finance.class))) {
                        financeList.addAll(doc1.getList("finance", Finance.class));
                    }
                    if (!CollectionUtils.isEmpty(doc1.getList("judgmentDocument", JudgmentDocument.class))) {
                        judgmentDocumentList.addAll(doc1.getList("judgmentDocument", JudgmentDocument.class));
                    }
                }

                lastId = documents.get(documents.size()-1).getString("_id");
                read += documents.size();
                logger.info("读取进度" + read + "/" + count + ";" );
                try {
                    //插入mongo
                    toMongoCollection.insertMany(list);

//                    ExecutorService executorService = Executors.newFixedThreadPool(8);
//                    CountDownLatch latch = new CountDownLatch(8);
//                   executorService.submit(() -> {
                        //插入mysql
                        entBrandBatch(entBrandList);
//                        latch.countDown();
//                    });
//                    executorService.submit(() -> {
                        //插入mysql
                        entPatentBatch(entPatentList);
//                        latch.countDown();
//                    });
//                    executorService.submit(() -> {
                        //插入mysql
                        entSoftCopyRightBatch(entSoftCopyRightList);
//                        latch.countDown();
//                    });
//                    executorService.submit(() -> {
                        //插入mysql
                        entCopyRightBatch(entCopyRightList);
//                        latch.countDown();
//                    });
//                    executorService.submit(() -> {
                        //插入mysql
                        entCertificateBatch(entCertificateList);
//                        latch.countDown();
//                    });
//                    executorService.submit(() -> {
                        //插入mysql
                        invEventBatch(invEventList);
//                        latch.countDown();
//                    });
//                    executorService.submit(() -> {
                        //插入mysql
                        financeBatch(financeList);
//                        latch.countDown();
//                    });
//                    executorService.submit(() -> {
                        //插入mysql
                        judgmentDocumentBatch(judgmentDocumentList);
//                        latch.countDown();
//                    });
//                    long count1 = latch.getCount();
//                    System.out.println("剩余线程数量:"+count1);
//                    latch.await();
//                    System.out.println("剩余线程数量1:"+count1);

                    logger.info( "size:{};;耗时:{}ms;",list.size(),System.currentTimeMillis()-start);
                }catch (Exception e){
                    logger.error(e.getMessage(),e);
                }
                success.incrementAndGet();
                logger.info( "处理进度" + success.get()*batchSize + "/" + count + ";" );
            }catch (Exception e){
                logger.error( "未知报错，lastId:" + lastId , e );
                try {
                    Thread.sleep( 10*1000 );
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }

        logger.info("总耗时{}ms",System.currentTimeMillis()-allStart);
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
     * @param entBrandList
     */
    public static void entBrandBatch(List<EntBrand> entBrandList){

        //数据库插入
        int amount;
        if (CollectionUtil.isNotEmpty(entBrandList)) {
            amount = entBrandList.size();
            int pageSize = 1000;
            int totalPage = getTotalPage(amount, pageSize);
            ExecutorService executorService = Executors.newFixedThreadPool(totalPage);
            CountDownLatch latch = new CountDownLatch(totalPage);
            for (int pageNo = 0; pageNo < totalPage; pageNo++) {
                int fromIndex = pageNo * pageSize;
                int toIndex = (pageNo + 1) * pageSize;
                if (toIndex > amount) {
                    toIndex = amount;
                }
                int finalToIndex = toIndex;
                int finalPageNo = pageNo;
                executorService.submit(() -> {
                    List<EntBrand> entBrands = entBrandList.subList(fromIndex, finalToIndex);
                    entBrandProcessor(entBrands);
                    log.info("entBrandBatch线程" + finalPageNo + "提交数据库" + entBrands.size());
                    latch.countDown();
                });
            }
            System.out.println("entBrandList剩余"+latch.getCount());
        }

    }

    public static void entBrandProcessor(List<EntBrand> entBrandList) {
        long start = System.currentTimeMillis();
        Connection conn = getPool();
        //字段
        String field = "id,ent_id,brand_name,apply_date,reg_code,int_cls,brand_type,brand_status,brand_imag";
        //占位符
        String placeholder = "?,?,?,?,?,?,?,?,?";
        //sql
        String sql = "insert into `ent_brand`(" + field + ")values(" + placeholder + ")";
        List<Object[]> objectsList = new ArrayList<>();
        entBrandList.forEach(item -> {
            item.setId(IdUtil.getSnowflake(1,1).nextId());
            //值
            Object[] objects = {
                    item.getId(),item.getEntId(),item.getBrandName(),item.getApplyDate(),item.getRegCode(),item.getIntCls(),item.getBrandType(),item.getBrandStatus(),item.getBrandImag()
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
        log.info("entBrandProcessor任务结束，"+entBrandList.size() + "条耗时" + (System.currentTimeMillis() - start) / 1000 + "s;");
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
            int pageSize = 1000;
            int totalPage = getTotalPage(amount, pageSize);
            ExecutorService executorService = Executors.newFixedThreadPool(totalPage);
            CountDownLatch latch = new CountDownLatch(totalPage);
            for (int pageNo = 0; pageNo < totalPage; pageNo++) {
                int fromIndex = pageNo * pageSize;
                int toIndex = (pageNo + 1) * pageSize;
                if (toIndex > amount) {
                    toIndex = amount;
                }
                int finalToIndex = toIndex;
                int finalPageNo = pageNo;
                executorService.submit(() -> {
                    List<EntPatent> entPatents = entPatentList.subList(fromIndex, finalToIndex);
                    entPatentProcessor(entPatents);
                    log.info("entPatentBatch线程" + finalPageNo + "提交数据库" + entPatents.size());
                    latch.countDown();
                });
            }
        }

    }

    public static void entPatentProcessor(List<EntPatent> entPatentList) {
        long start = System.currentTimeMillis();
        Connection conn = getPool();
        //字段
        String field = "id,ent_id,patent_name,patent_id,patent_type,patent_status,reg_date,open_date,open_no";
        //占位符
        String placeholder = "?,?,?,?,?,?,?,?,?";
        //sql
        String sql = "insert into `ent_patent`(" + field + ")values(" + placeholder + ")";
        List<Object[]> objectsList = new ArrayList<>();
        entPatentList.forEach(item -> {
            item.setId(IdUtil.getSnowflake(1,1).nextId());
            //值
            Object[] objects = {
                    item.getId(),item.getEntId(),item.getPatentName(),item.getPatentId(),item.getPatentType(),item.getPatentStatus(),item.getRegDate(),item.getOpenDate(),item.getOpenNo()
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
        log.info("entPatentProcessor任务结束,"+entPatentList.size() + "条耗时" + (System.currentTimeMillis() - start) / 1000 + "s;");
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
            int pageSize = 1000;
            int totalPage = getTotalPage(amount, pageSize);
            ExecutorService executorService = Executors.newFixedThreadPool(totalPage);
            CountDownLatch latch = new CountDownLatch(totalPage);
            for (int pageNo = 0; pageNo < totalPage; pageNo++) {
                int fromIndex = pageNo * pageSize;
                int toIndex = (pageNo + 1) * pageSize;
                if (toIndex > amount) {
                    toIndex = amount;
                }
                int finalToIndex = toIndex;
                int finalPageNo = pageNo;
                executorService.submit(() -> {
                    List<EntSoftCopyRight> entSoftCopyRights = entSoftCopyRightList.subList(fromIndex, finalToIndex);
                    entSoftCopyRightProcessor(entSoftCopyRights);
                    log.info("entSoftCopyRightBatch线程" + finalPageNo + "提交数据库" + entSoftCopyRights.size());
                    latch.countDown();
                });
            }
        }

    }

    public static void entSoftCopyRightProcessor(List<EntSoftCopyRight> entSoftCopyRightList) {
        long start = System.currentTimeMillis();
        Connection conn = getPool();
        //字段
        String field = "id,ent_id,soft_name,reg_date,reg_no,soft_edition,soft_abb,notice_date";
        //占位符
        String placeholder = "?,?,?,?,?,?,?,?";
        //sql
        String sql = "insert into `ent_softcopyright`(" + field + ")values(" + placeholder + ")";
        List<Object[]> objectsList = new ArrayList<>();
        entSoftCopyRightList.forEach(item -> {
            item.setId(IdUtil.getSnowflake(1,1).nextId());
            //值
            Object[] objects = {
                    item.getId(),item.getEntId(),item.getSoftName(),item.getRegDate(),item.getRegNo(),item.getSoftEdition(),item.getSoftAbb(),item.getNoticeDate()
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
        log.info("entSoftCopyRightProcessor任务结束，"+entSoftCopyRightList.size() + "条耗时" + (System.currentTimeMillis() - start) / 1000 + "s;");
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
            int pageSize = 1000;
            int totalPage = getTotalPage(amount, pageSize);
            ExecutorService executorService = Executors.newFixedThreadPool(totalPage);
            CountDownLatch latch = new CountDownLatch(totalPage);
            for (int pageNo = 0; pageNo < totalPage; pageNo++) {
                int fromIndex = pageNo * pageSize;
                int toIndex = (pageNo + 1) * pageSize;
                if (toIndex > amount) {
                    toIndex = amount;
                }
                int finalToIndex = toIndex;
                int finalPageNo = pageNo;
                executorService.submit(() -> {
                    List<EntCopyRight> entCopyRights = entCopyRightList.subList(fromIndex, finalToIndex);
                    entCopyRightProcessor(entCopyRights);
                    log.info("entCopyRightBatch线程" + finalPageNo + "提交数据库" + entCopyRights.size());
                    latch.countDown();
                });
            }
        }

    }

    public static void entCopyRightProcessor(List<EntCopyRight> entCopyRightList) {
        long start = System.currentTimeMillis();
        Connection conn = getPool();
        //字段
        String field = "id,ent_id,product_name,reg_date,reg_no,create_date,product_type,publish_date";
        //占位符
        String placeholder = "?,?,?,?,?,?,?,?";
        //sql
        String sql = "insert into `ent_copyright`(" + field + ")values(" + placeholder + ")";
        List<Object[]> objectsList = new ArrayList<>();
        entCopyRightList.forEach(item -> {
            item.setId(IdUtil.getSnowflake(1,1).nextId());
            //值
            Object[] objects = {
                    item.getId(),item.getEntId(),item.getProductName(),item.getRegDate(),item.getRegNo(),item.getCreateDate(),item.getProductType(),item.getPublishDate()
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
        log.info("entCopyRightProcessor任务结束，"+entCopyRightList.size() + "条耗时" + (System.currentTimeMillis() - start) / 1000 + "s;");
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
            int pageSize = 1000;
            int totalPage = getTotalPage(amount, pageSize);
            ExecutorService executorService = Executors.newFixedThreadPool(totalPage);
            CountDownLatch latch = new CountDownLatch(totalPage);
            for (int pageNo = 0; pageNo < totalPage; pageNo++) {
                int fromIndex = pageNo * pageSize;
                int toIndex = (pageNo + 1) * pageSize;
                if (toIndex > amount) {
                    toIndex = amount;
                }
                int finalToIndex = toIndex;
                int finalPageNo = pageNo;
                executorService.submit(() -> {
                    List<EntCertificate> entCertificates = entCertificateList.subList(fromIndex, finalToIndex);
                    entCertificateProcessor(entCertificates);
                    log.info("entCertificateBatch线程" + finalPageNo + "提交数据库" + entCertificates.size());
                    latch.countDown();
                });
            }
        }

    }

    public static void entCertificateProcessor(List<EntCertificate> entCertificateList) {
        long start = System.currentTimeMillis();
        Connection conn = getPool();
        //字段
        String field = "id,ent_id,certificate_type,certificate_name,certificate_no,end_date";
        //占位符
        String placeholder = "?,?,?,?,?,?,?,?";
        //sql
        String sql = "insert into `qualifications`(" + field + ")values(" + placeholder + ")";
        List<Object[]> objectsList = new ArrayList<>();
        entCertificateList.forEach(item -> {
            item.setId(IdUtil.getSnowflake(1,1).nextId());
            //值
            Object[] objects = {
                    item.getId(),item.getEntId(),item.getCertificateType(),item.getCertificateName(),item.getCertificateCode(),item.getEndDate()
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
        log.info("entCertificateProcessor任务结束，"+entCertificateList.size() + "条耗时" + (System.currentTimeMillis() - start) / 1000 + "s;");
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
            int pageSize = 1000;
            int totalPage = getTotalPage(amount, pageSize);
            ExecutorService executorService = Executors.newFixedThreadPool(totalPage);
            CountDownLatch latch = new CountDownLatch(totalPage);
            for (int pageNo = 0; pageNo < totalPage; pageNo++) {
                int fromIndex = pageNo * pageSize;
                int toIndex = (pageNo + 1) * pageSize;
                if (toIndex > amount) {
                    toIndex = amount;
                }
                int finalToIndex = toIndex;
                int finalPageNo = pageNo;
                executorService.submit(() -> {
                    List<InvEvent> invEvents = invEventList.subList(fromIndex, finalToIndex);
                    invEventProcessor(invEvents);
                    log.info("invEventBatch线程" + finalPageNo + "提交数据库" + invEvents.size());
                    latch.countDown();
                });
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
        log.info("invEventProcessor任务结束，" + invEventList.size() + "条耗时" + (System.currentTimeMillis() - start) / 1000 + "s;");
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
            int pageSize = 1000;
            int totalPage = getTotalPage(amount, pageSize);
            ExecutorService executorService = Executors.newFixedThreadPool(totalPage);
            CountDownLatch latch = new CountDownLatch(totalPage);
            for (int pageNo = 0; pageNo < totalPage; pageNo++) {
                int fromIndex = pageNo * pageSize;
                int toIndex = (pageNo + 1) * pageSize;
                if (toIndex > amount) {
                    toIndex = amount;
                }
                int finalToIndex = toIndex;
                int finalPageNo = pageNo;
                executorService.submit(() -> {
                    List<Finance> finances = financeList.subList(fromIndex, finalToIndex);
                    financeProcessor(finances);
                    log.info("financeBatch线程" + finalPageNo + "提交数据库" + finances.size());
                    latch.countDown();
                });
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
        log.info("financeProcessor任务结束，"+financeList.size() + "条耗时" + (System.currentTimeMillis() - start) / 1000 + "s;");
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
            int pageSize = 1000;
            int totalPage = getTotalPage(amount, pageSize);
            ExecutorService executorService = Executors.newFixedThreadPool(totalPage);
            CountDownLatch latch = new CountDownLatch(totalPage);
            for (int pageNo = 0; pageNo < totalPage; pageNo++) {
                int fromIndex = pageNo * pageSize;
                int toIndex = (pageNo + 1) * pageSize;
                if (toIndex > amount) {
                    toIndex = amount;
                }
                int finalToIndex = toIndex;
                int finalPageNo = pageNo;
                executorService.submit(() -> {
                    List<JudgmentDocument> judgmentDocuments = judgmentDocumentList.subList(fromIndex, finalToIndex);
                    judgmentDocumentProcessor(judgmentDocuments);
                    log.info("judgmentDocumentBatch线程" + finalPageNo + "提交数据库" + judgmentDocuments.size());
                    latch.countDown();
                });
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
        log.info("judgmentDocumentProcessor任务结束，"+judgmentDocumentList.size() + "条耗时" + (System.currentTimeMillis() - start) / 1000 + "s;");
    }
}
