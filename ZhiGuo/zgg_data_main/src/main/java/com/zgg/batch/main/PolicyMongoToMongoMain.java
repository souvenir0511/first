package com.zgg.batch.main;

import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.zgg.batch.entity.Policy;
import com.zgg.batch.utils.DocumentToBeanUtil;
import org.bson.BsonDocument;
import org.bson.Document;
import org.elasticsearch.common.util.set.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class PolicyMongoToMongoMain {
    private static final Logger logger = LoggerFactory.getLogger(PolicyMongoToEsMain.class);

    private static MongoClient mongoClient;
    private static MongoDatabase db;
    private static MongoCollection<Document> fromMongoCollection;
    private static MongoCollection<Document> toMongoCollection;

    public static AtomicInteger success = new AtomicInteger(0);

    public static void main(String[] args) throws Exception{
        String mongoIp = "192.168.10.150";
        Integer mongoPort = 27017;
        String mongoDb = "zgg_data";
        String fromMongoName = "chace";
        String toMongoName = "v1_policy";

        init(mongoIp, mongoPort, mongoDb, fromMongoName, toMongoName);
        fixEnterpriseData();
        mongoClient.close();
    }

    public static void init(String mongoIp, Integer mongoPort, String mongoDb, String fromMongoName, String toMongoName)
    {
        mongoClient = new MongoClient(mongoIp, mongoPort);
        db = mongoClient.getDatabase(mongoDb);
        fromMongoCollection = db.getCollection(fromMongoName);
        toMongoCollection = db.getCollection(toMongoName);

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

                List<Document> list = Lists.newArrayList();

                for(Document document : documents ) {
                    Document doc = DocumentToBeanUtil.policyMongoToMongo(document);
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
