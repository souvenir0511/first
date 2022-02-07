package com.zgg.batch.main;

import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import cn.hutool.db.ds.simple.SimpleDataSource;
import cn.hutool.db.handler.EntityListHandler;
import cn.hutool.db.sql.SqlExecutor;
import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.zgg.batch.entity.Enterprise;
import com.zgg.batch.utils.DocumentToBeanUtil;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MysqlToMongoMain {

    private static final Logger logger = LoggerFactory.getLogger(MysqlToMongoMain.class);

    private static DataSource dataSource;
    private static Connection connection;

    private static MongoClient mongoClient;
    private static MongoDatabase db;
    private static MongoCollection<Document> writeMongoCollection;

    public static void main(String[] args) throws Exception{
        String [] arrays = args[0].split(";");
        String uri = "jdbc:mysql://"+ arrays[0]+":"+Integer.valueOf(arrays[1])+"/"+arrays[2]+"?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
        String user = args[1];
        String password = args[2];

        String mongoHost = args[3];
        Integer mongoPort = Integer.valueOf(args[4]);
        String mongoDb = args[5];
        String mongoName = args[6];

        logger.info("---------------------mongoIp:{},mongoPort:{},mongoDb:{},mongoName:{};mysqlUri:{},mysqlUser:{},mysqlPw:{}",mongoHost,mongoPort,mongoDb,mongoName,uri,user,password);

        init(uri, user, password, mongoHost, mongoPort, mongoDb, mongoName);
        fixEnterpriseData();
        DbUtil.close();
        mongoClient.close();
    }

    private static void init(String uri, String user, String password, String mongoHost, Integer mongoPort, String mongoDb, String mongoName) throws Exception{
        dataSource = new SimpleDataSource(uri, user, password);
        connection = dataSource.getConnection();

        mongoClient = new MongoClient(mongoHost, mongoPort);
        db = mongoClient.getDatabase(mongoDb);
        writeMongoCollection = db.getCollection(mongoName);
    }

    public static void fixEnterpriseData() throws Exception {
        int batchSize = 5000;

        String sql = "select count(*) from compay_info";
        int count = 0;
        Statement sta = connection.createStatement();
        ResultSet res = sta.executeQuery(sql);
        while(res.next()){
            count = res.getInt(1);
        }
        int totalPage = count/batchSize+1;
        int read = 0;

        long allStart = System.currentTimeMillis();
        for(int i =0 ;i<totalPage;i++){
            /** 读取数据 **/
            long start = System.currentTimeMillis();
            int page = i*batchSize+1;
            List<Entity> entityList = SqlExecutor.query(connection, "select * from compay_info limit "+page+","+batchSize, new EntityListHandler());
            logger.info( "查询数据" + batchSize + "条耗时" + (System.currentTimeMillis()-start) + "ms;" );

            /** 对象转换 **/
            start = System.currentTimeMillis();
            List<Document> enterprises = Lists.newArrayList();
            entityList.stream().forEach(entity -> {
                Document enterprise = DocumentToBeanUtil.rsToEntMysql(entity);
                enterprises.add(enterprise);
            });
            logger.info( "对象转换" + batchSize + "条耗时" + (System.currentTimeMillis()-start) + "ms;" );

            /** 数据写入mongo **/
            start = System.currentTimeMillis();
            writeMongoCollection.insertMany(enterprises);
            logger.info( "size:{};;耗时:{}ms;",enterprises.size(),System.currentTimeMillis()-start);
            read += enterprises.size();
            logger.info( "---------处理进度" + read + "/" + count + ";" );

            Thread.sleep(10*1000);
        }
        logger.info("总耗时{}ms",System.currentTimeMillis()-allStart);



    }
}
