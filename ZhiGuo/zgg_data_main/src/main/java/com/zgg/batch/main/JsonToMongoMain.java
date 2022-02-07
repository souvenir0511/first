package com.zgg.batch.main;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.zgg.batch.entity.EnterpriseOld;
import com.zgg.batch.utils.DocumentToBeanUtil;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class JsonToMongoMain {
    private static final Logger logger = LoggerFactory.getLogger(JsonToMongoMain.class);

    private static MongoClient mongoClient;
    private static MongoDatabase db;
    private static MongoCollection<Document> fromMongoCollection;

    private static String mongoHost = "192.168.10.150";
    private static int mongoPort = 27017;
    private static String mongoDatabase = "zgg_data";
    private static String fromMongoCollectionName = "23_enterprise";

    public static AtomicInteger success = new AtomicInteger(0);

    public static void main(String[] args) throws Exception{
        init();

        String path = "E:\\input\\";
        List<String> list1 = FileUtil.listFileNames(path);
        list1.stream().forEach(str->{
            System.out.println("----"+str);
            long start = System.currentTimeMillis();
            FileReader fileReader = new FileReader(path + str);
            List<String> reader = fileReader.readLines();
            List<Document> ents = Lists.newArrayList();
            reader.stream().forEach(line->{
                line = line.replace("_id","id");
                EnterpriseOld enterpriseOld = JSONUtil.toBean(line, EnterpriseOld.class);
                Document document = DocumentToBeanUtil.oldEntToNewDoc(enterpriseOld);
                ents.add(document);
                success.incrementAndGet();
            });
            logger.info( "查询数据size:{}条;查询处理耗时:{}ms;",reader.size(),System.currentTimeMillis()-start);

            start = System.currentTimeMillis();
            fromMongoCollection.insertMany(ents);
            logger.info( "入库size:{}条;入库耗时:{}ms;",ents.size(),System.currentTimeMillis()-start);
            try {
                Thread.sleep( 10*1000 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        logger.info("处理结束,共处理{}条",success);
        /**/
    }

    public static void init()
    {
        mongoClient = new MongoClient(mongoHost, mongoPort);
        db = mongoClient.getDatabase(mongoDatabase);
        fromMongoCollection = db.getCollection(fromMongoCollectionName);
    }
}
