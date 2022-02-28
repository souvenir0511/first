package com.zgg.batch.enterprise.main;

import cn.hutool.db.DbUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * 商标流程转入到mongo
 * @author EDZ
 *public class SpiderJsontoMongo {
 * }
 */
@Slf4j
public class PatentProcessJsontoMongo {

    private static String filePatentJson = "C:\\Users\\Administrator\\Documents\\WXWork\\1688857787268739\\Cache\\File\\2022-02\\商标流程示例.json";

    static MongoClient toMongoClient;
    static MongoDatabase toMongoDatabase;
    static MongoCollection<Document> toMongoCollection;

    static String toMongoHost = "192.168.10.130";
    static int mongoPort = 27017;
    static String mongoUser = "";
    static String mongoPwd = "";
    static String toMongoDatabaseName = "spider";
    static String toMongoCollectionName = "patent_process";

    //   public static AtomicInteger success = new AtomicInteger(0);

    public static void main(String[] args) throws SQLException, IOException {

        init();
        fixEnterpriseData();
        toMongoClient.close();
        DbUtil.close();
    }

    public static void init() {

        toMongoClient = new MongoClient(toMongoHost, mongoPort);
        toMongoDatabase = toMongoClient.getDatabase(toMongoDatabaseName);

        toMongoCollection = toMongoDatabase.getCollection(toMongoCollectionName);
    }



    public static void fixEnterpriseData() throws SQLException, IOException {

        //读取文件
        BufferedReader br = new BufferedReader(new FileReader(filePatentJson));
        String s = null;
        List<Document> documentList = new ArrayList<>();
        while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
            Document document = JSON.parseObject(s, Document.class);
            documentList.add(document);
        }
        List<Document> documents = new ArrayList<>();
        documentList.forEach(item -> {
            Document document = new Document();
            document.put("tm_name", item.getString("tm_name"));
            document.put("reg_no", item.getString("reg_no"));
            document.put("class_no", item.getInteger("class_no"));
            document.put("process", JSON.parseArray(item.getString("process"), Document.class));
            document.put("update_time_span2", item.getString("update_time_span2"));
            documents.add(document);
        });
        toMongoCollection.insertMany(documents);
    }

}