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
import org.bson.BsonDocument;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
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
public class EnterpriseToAnalyze {
    private static final Logger logger = LoggerFactory.getLogger(EnterpriseAnalyze.class);

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
                if(!iterator.hasNext()){
                    logger.info( "处理完成" );
                    break;
                }
                while (iterator.hasNext()) {
                    documents.add(iterator.next());
                }
                logger.info( "查询数据" + batchSize + "条耗时" + (System.currentTimeMillis()-start) + "ms;" );

                start = System.currentTimeMillis();

                List<EnterpriseAnalyze> enterpriseAnalyzeList = new ArrayList<>();
                for(Document document : documents ) {

                    EnterpriseAnalyze enterpriseAnalyze = DocumentToBeanUtil.toEnterpriseAnalyze(document);
                    enterpriseAnalyzeList.add(enterpriseAnalyze);

                }

                lastId = documents.get(documents.size()-1).getString("_id");
                read += documents.size();
                logger.info("读取进度" + read + "/" + count + ";" );
                try {

                    entEnterpriseBatch(enterpriseAnalyzeList);

                    logger.info( "size:{};;耗时:{}ms;",enterpriseAnalyzeList.size(),System.currentTimeMillis()-start);
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
     * @param
     */
    public static void entEnterpriseBatch(List<EnterpriseAnalyze> enterpriseAnalyzeList){

        //数据库插入
        int amount;
        if (CollectionUtil.isNotEmpty(enterpriseAnalyzeList)) {
            amount = enterpriseAnalyzeList.size();
            int pageSize = 10000;
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
                    List<EnterpriseAnalyze> enterpriseAnalyzes = enterpriseAnalyzeList.subList(fromIndex, finalToIndex);
                    enterpriseAnalyzeProcessor(enterpriseAnalyzes);
                    log.info("enterpriseAnalyze线程" + finalPageNo + "提交数据库" + enterpriseAnalyzes.size());
                    latch.countDown();
                });
            }
            System.out.println("enterpriseAnalyzeList剩余"+latch.getCount());
        }

    }

    public static void enterpriseAnalyzeProcessor(List<EnterpriseAnalyze> enterpriseAnalyzeList) {
        long start = System.currentTimeMillis();
        Connection conn = getPool();
        //字段
        String field = "ent_id,ent_name,invent_patent_recent_count,practical_new_recent_count,intellectual_property_recent_count,intellectual_property_count,finance_rounds,official_website,certificate_type";
        //占位符
        String placeholder = "?,?,?,?,?,?,?,?,?";
        //sql
        String sql = "insert into `enterprise_analyze`(" + field + ")values(" + placeholder + ")";
        List<Object[]> objectsList = new ArrayList<>();
        enterpriseAnalyzeList.forEach(item -> {
            //值
            Object[] objects = {
                    item.getEntId(),item.getEntName(),item.getInventPatentRecentCount(),item.getPracticalNewRecentCount(),item.getIntellectualPropertyRecentCount(),item.getIntellectualPropertyCount(),item.getFinanceRounds(),item.getOfficialWebsite(),item.getCertificateType()
            };
            objectsList.add(objects);
        });

        try {
            SqlExecutor.executeBatch(conn, sql, objectsList);
            conn.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }finally {
            try {
                conn.close();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
        log.info("enterpriseAnalyzeProcessor，"+enterpriseAnalyzeList.size() + "条耗时" + (System.currentTimeMillis() - start) / 1000 + "s;");
    }

}
