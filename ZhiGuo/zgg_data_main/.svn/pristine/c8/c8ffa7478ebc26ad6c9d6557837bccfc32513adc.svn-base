package com.zgg.batch.main;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.zgg.batch.utils.DocumentToBeanUtil;
import org.bson.BsonDocument;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 字段转换
 * 1、取得mongo中的数据
 * 2、mongo返回的字段转换譬如 orgName转换为entName
 * 3、转换完成的数据写入新mongo
 */
public class OldToNewMain {
    private static final Logger logger = LoggerFactory.getLogger(OldToNewMain.class);

    private static MongoClient mongoClient;
    private static MongoDatabase db;
    private static MongoCollection<Document> fromMongoCollection;
    private static MongoCollection<Document> toMongoCollection;


    private static String mongoHost = "192.168.10.150";
    private static int mongoPort = 27017;
    private static String mongoUser = "";
    private static String mongoPwd = "";
    private static String mongoDatabase = "zgg_data";
    private static String fromMongoCollectionName = "gaoqi_enterprise";
    private static String toMongoCollectionName = "v3_enterprise";

    public static AtomicInteger success = new AtomicInteger(0);

    public static void init()
    {
        mongoClient = new MongoClient(mongoHost, mongoPort);
        db = mongoClient.getDatabase(mongoDatabase);
        fromMongoCollection = db.getCollection(fromMongoCollectionName);
        toMongoCollection = db.getCollection(toMongoCollectionName);

    }

    public static void main(String[] args) throws Exception {
        init();
        fixEnterpriseData();
        mongoClient.close();

    }

    public static void fixEnterpriseData() throws Exception {
        int batchSize = 10000;
        String lastId = "";
        int times = 0;
        long count = fromMongoCollection.count();
        int read = 0;

        Set<String> repeatOrgName = Sets.newHashSet();

        long allStart = System.currentTimeMillis();

        while (++times < batchSize)
        {
            try
            {
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

               // List<Enterprise> toDocuments = Lists.newArrayList();
                List<Document> list = Lists.newArrayList();

                for(Document document : documents ) {
                    String id = document.getString("_id");

                    Document doc = DocumentToBeanUtil.oldDocToNewDoc(document);
                    list.add(doc);
                }
                lastId = documents.get(documents.size()-1).getString("_id");

                read += documents.size();

                logger.info( "读取进度" + read + "/" + count + ";" );

                try
                {
                    toMongoCollection.insertMany(list);
                    logger.info( "size:{};;耗时:{}ms;",list.size(),System.currentTimeMillis()-start);
                }
                catch (Exception e){
                    logger.error(e.getMessage(),e);
                }
                success.incrementAndGet();
                logger.info( "处理进度" + success.get()*batchSize + "/" + count + ";" );
            }
            catch (Exception e)
            {
                logger.error( "未知报错，lastId:" + lastId , e );
                try {
                    Thread.sleep( 10*1000 );
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }

        logger.info("总耗时{}ms",System.currentTimeMillis()-allStart);
        for(String key : repeatOrgName)
        {
            logger.info(key);
        }

    }
}
