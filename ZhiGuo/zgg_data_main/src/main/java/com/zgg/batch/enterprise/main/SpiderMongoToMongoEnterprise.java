package com.zgg.batch.enterprise.main;

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
import com.zgg.batch.enterprise.util.DocumentToBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonDocument;
import org.bson.Document;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * QCC写入mango企业基本信息
 */
@Slf4j
public class SpiderMongoToMongoEnterprise {

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
    static String toMongoCollectionName = "20211009_enterprise";

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
                documents.forEach(item -> {

                    //企业基本信息mango
                    Document doc = DocumentToBeanUtil.spiderMongoToMongo(item, entityList);
                    list.add(doc);
                });

                lastId = documents.get(documents.size()-1).getString("_id");
                read += documents.size();
                log.info("读取进度" + read + "/" + count + ",耗时" + (System.currentTimeMillis() - start) + "ms");

                try {
                    //插入mongo
                    toMongoCollection.insertMany(list);
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

}
