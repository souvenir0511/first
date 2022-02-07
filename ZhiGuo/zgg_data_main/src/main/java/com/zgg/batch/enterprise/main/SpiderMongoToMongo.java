package com.zgg.batch.enterprise.main;

import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import cn.hutool.db.ds.simple.SimpleDataSource;
import cn.hutool.db.handler.EntityListHandler;
import cn.hutool.db.sql.SqlExecutor;
import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.zgg.batch.enterprise.util.DocumentToBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonDocument;
import org.bson.Document;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
public class SpiderMongoToMongo{

    static MongoClient fromMongoClient;
    static MongoClient toMongoClient;
    static MongoDatabase fromMongoDatabase;
    static MongoDatabase toMongoDatabase;
    static MongoCollection<Document> fromMongoCollection;
    static MongoCollection<Document> toMongoCollection;
    static MongoCollection<Document> toBrandMongoCollection;
    static MongoCollection<Document> toPatentMongoCollection;
    static MongoCollection<Document> toSoftCopyrightMongoCollection;
    static MongoCollection<Document> toCopyrightMongoCollection;
    static MongoCollection<Document> toCertificateMongoCollection;
    static MongoCollection<Document> toInvestMongoCollection;
    static MongoCollection<Document> toFinanceMongoCollection;
    static MongoCollection<Document> toJudgmentMongoCollection;


    static String formMongoHost = "192.168.10.45";
    static String toMongoHost = "192.168.10.154";
    static int mongoPort = 27017;
    static String mongoUser = "";
    static String mongoPwd = "";
    static String fromMongoDatabaseName = "QCC_DATA";
    static String fromMongoCollectionName = "qcc_basicInfo";
    static String toMongoDatabaseName = "zgg_data";
    static String toMongoCollectionName = "enterprise1";
    static String toBrandMongoCollectionName = "brand1";
    static String toPatentMongoCollectionName = "patent1";
    static String toSoftCopyrightMongoCollectionName = "soft_copyright1";
    static String toCopyrightMongoCollectionName = "copyright1";
    static String toCertificateMongoCollectionName = "certificate1";
    static String toInvestMongoCollectionName = "invest1";
    static String toFinanceMongoCollectionName = "finance1";
    static String toJudgmentMongoCollectionName = "judgment1";

    public static AtomicInteger success = new AtomicInteger(0);

    //高企查询
    private static DataSource dataSource;
    private static Connection connection;
    static String url2="jdbc:mysql://192.168.10.154:3306/zgg_spider?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&autoReconnect=true&failOverReadOnly=false&rewriteBatchedStatements=true";
    static String user="zhiguo";
    static String pwd = "123456";

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

        MongoClientOptions.Builder options = MongoClientOptions.builder();
        fromMongoClient = new MongoClient(formMongoHost, options.build());

        toMongoClient = new MongoClient(toMongoHost, mongoPort);
        toMongoDatabase = toMongoClient.getDatabase(toMongoDatabaseName);

        toMongoCollection = toMongoDatabase.getCollection(toMongoCollectionName);
        toBrandMongoCollection = toMongoDatabase.getCollection(toBrandMongoCollectionName);
        toPatentMongoCollection = toMongoDatabase.getCollection(toPatentMongoCollectionName);
        toSoftCopyrightMongoCollection = toMongoDatabase.getCollection(toSoftCopyrightMongoCollectionName);
        toCopyrightMongoCollection = toMongoDatabase.getCollection(toCopyrightMongoCollectionName);
        toCertificateMongoCollection = toMongoDatabase.getCollection(toCertificateMongoCollectionName);
        toInvestMongoCollection = toMongoDatabase.getCollection(toInvestMongoCollectionName);
        toFinanceMongoCollection = toMongoDatabase.getCollection(toFinanceMongoCollectionName);
        toJudgmentMongoCollection = toMongoDatabase.getCollection(toJudgmentMongoCollectionName);
    }


    public static void fixEnterpriseData() throws SQLException {
        int batchSize = 10000;
        String lastId = "";
        int times = 0;
        long count = fromMongoCollection.count();
        int read = 0;

        long allStart = System.currentTimeMillis();

        List<Entity> entityList = SqlExecutor.query(connection, "select enterprise_name,type from growth_enterprise", new EntityListHandler());
        System.out.println("查询到"+entityList.size()+"条高企");
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
                log.info( "查询数据" + documents.size() + "条耗时" + (System.currentTimeMillis()-start) + "ms;" );

                start = System.currentTimeMillis();

                List<Document> enterpriseList = Lists.newArrayList();
                List<Document> entBrandList = new ArrayList<>();
                List<Document> entPatentList = new ArrayList<>();
                List<Document> entSoftCopyRightList = new ArrayList<>();
                List<Document> entCopyRightList = new ArrayList<>();
                List<Document> entCertificateList = new ArrayList<>();
                List<Document> invEventList = new ArrayList<>();
                List<Document> financeList = new ArrayList<>();
                List<Document> judgmentDocumentList = new ArrayList<>();
                for(Document document : documents ) {
                    //企业基本信息
                    enterpriseList.add(DocumentToBeanUtil.spiderMongoToMongo(document, entityList));
                    //企业工商数据
                    entBrandList.addAll(DocumentToBeanUtil.brandSpiderMongoToMongo(document));
                    //企业工商数据
                    entPatentList.addAll(DocumentToBeanUtil.patentSpiderMongoToMongo(document));
                    //企业工商数据
                    entSoftCopyRightList.addAll(DocumentToBeanUtil.softCopyrightSpiderMongoToMongo(document));
                    //企业工商数据
                    entCopyRightList.addAll(DocumentToBeanUtil.copyrightSpiderMongoToMongo(document));
                    //企业工商数据
                    entCertificateList.addAll(DocumentToBeanUtil.certificateSpiderMongoToMongo(document));
                    //企业工商数据
                    invEventList.addAll(DocumentToBeanUtil.investSpiderMongoToMongo(document));
                    //企业工商数据
                    financeList.addAll(DocumentToBeanUtil.financeSpiderMongoToMongo(document));
                    //企业工商数据
                    judgmentDocumentList.addAll(DocumentToBeanUtil.judgmentSpiderMongoToMongo(document));

                }

                lastId = documents.get(documents.size()-1).getString("_id");
                read += documents.size();
                log.info("读取进度" + read + "/" + count + ";" );
                try {
                    int threadPoolCount = getThreadPoolCount(enterpriseList, entBrandList, entPatentList, entSoftCopyRightList, entCopyRightList, entCertificateList, invEventList, financeList, judgmentDocumentList);

                    ExecutorService executorService = Executors.newFixedThreadPool(threadPoolCount);
                    CountDownLatch latch = new CountDownLatch(threadPoolCount);
                    executorService.submit(() -> {
                       toMongoCollection.insertMany(enterpriseList);
                        latch.countDown();
                    });
                    executorService.submit(() -> {
                        toBrandMongoCollection.insertMany(entBrandList);
                        latch.countDown();
                    });
                    executorService.submit(() -> {
                        toPatentMongoCollection.insertMany(entPatentList);
                        latch.countDown();
                    });
                    executorService.submit(() -> {
                        toSoftCopyrightMongoCollection.insertMany(entSoftCopyRightList);
                        latch.countDown();
                    });
                    executorService.submit(() -> {
                        toCopyrightMongoCollection.insertMany(entCopyRightList);
                        latch.countDown();
                    });
                    executorService.submit(() -> {
                        toCertificateMongoCollection.insertMany(entCertificateList);
                        latch.countDown();
                    });
                    executorService.submit(() -> {
                        toInvestMongoCollection.insertMany(invEventList);
                        latch.countDown();
                    });
                    executorService.submit(() -> {
                        toFinanceMongoCollection.insertMany(financeList);
                        latch.countDown();
                    });
                    executorService.submit(() -> {
                        toJudgmentMongoCollection.insertMany(judgmentDocumentList);
                        latch.countDown();
                    });

                    try {
                        long threadStart = System.currentTimeMillis();
                        log.info("开启{}条线程，线程正在执行中",latch.getCount());
                        //阻塞当前线程，直到子线程之后执行
                        latch.await();
                        if (latch.getCount() == 0) {
                            log.info("线程执行结束,耗时:{}ms;",System.currentTimeMillis()-threadStart);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally{
                        //最后关闭线程池，但执行以前提交的任务，不接受新任务
                        executorService.shutdown();
                        //关闭线程池，停止所有正在执行的活动任务，暂停处理正在等待的任务，并返回等待执行的任务列表。
                        //threadPool.shutdownNow();
                    }
                    log.info( "size:{};;耗时:{}ms;",entBrandList.size(),System.currentTimeMillis()-start);
                }catch (Exception e){
                    log.error(e.getMessage(),e);
                }
                success.incrementAndGet();
                if (success.get() == times) {
                    log.info( "处理进度" + read + "/" + count + ";" );
                }
            }catch (Exception e){
                log.error( "未知报错，lastId:" + lastId , e );
                try {
                    Thread.sleep( 10*1000 );
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }

        log.info("总耗时{}ms",System.currentTimeMillis()-allStart);
    }

    static int getThreadPoolCount(List<Document> enterpriseList,List<Document> entBrandList,List<Document> entPatentList,List<Document> entSoftCopyRightList,List<Document> entCopyRightList,List<Document> entCertificateList,List<Document> invEventList,List<Document> financeList,List<Document> judgmentDocumentList){

        int threadPoolCount = 0;

        if (!CollectionUtils.isEmpty(enterpriseList)) {
            threadPoolCount++;
        }
        if (!CollectionUtils.isEmpty(entBrandList)) {
            threadPoolCount++;
        }
        if (!CollectionUtils.isEmpty(entPatentList)) {
            threadPoolCount++;
        }
        if (!CollectionUtils.isEmpty(entSoftCopyRightList)) {
            threadPoolCount++;
        }
        if (!CollectionUtils.isEmpty(entCopyRightList)) {
            threadPoolCount++;
        }
        if (!CollectionUtils.isEmpty(entCertificateList)) {
            threadPoolCount++;
        }
        if (!CollectionUtils.isEmpty(invEventList)) {
            threadPoolCount++;
        }
        if (!CollectionUtils.isEmpty(financeList)) {
            threadPoolCount++;
        }
        if (!CollectionUtils.isEmpty(judgmentDocumentList)) {
            threadPoolCount++;
        }
        return threadPoolCount;
    }

}
