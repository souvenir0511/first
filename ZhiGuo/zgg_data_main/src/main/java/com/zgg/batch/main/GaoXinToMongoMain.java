package com.zgg.batch.main;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.BasicDBObject;
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
 * 高新企业数据处理
 * 1、从高新企业库中读取高新企业数据取得id
 * 2、通过高新企业id去全库中读取企业数据
 * 3、转换企业数据字段
 * 4、转换完成的数据写入新mongo
 */
public class GaoXinToMongoMain {
    private static final Logger logger = LoggerFactory.getLogger(GaoXinToMongoMain.class);

    private static MongoClient mongoClient;
    private static MongoDatabase db;
    private static MongoCollection<Document> fromMongoCollection;
    private static MongoCollection<Document> toMongoCollection;
    private static MongoCollection<Document> readMongoCollection;


    private static String mongoHost = "192.168.10.150";
    private static int mongoPort = 27017;
    private static String mongoUser = "";
    private static String mongoPwd = "";
    private static String mongoDatabase = "zgg_data";
    private static String fromMongoCollectionName = "gaoxin";
    private static String readMongoCollectionName = "enterprise";
    private static String toMongoCollectionName = "test3";

    public static AtomicInteger success = new AtomicInteger(0);

    public static void init()
    {
        ServerAddress sa = new ServerAddress( mongoHost ,mongoPort );
        //MongoCredential credential = MongoCredential.createCredential(mongoUser, mongoDatabase, mongoPwd.toCharArray());
        //mongoClient = new MongoClient(Arrays.asList(sa), Arrays.asList(credential));
        mongoClient = new MongoClient("192.168.10.150", 27017);
        db = mongoClient.getDatabase(mongoDatabase);
        fromMongoCollection = db.getCollection(fromMongoCollectionName);
        readMongoCollection = db.getCollection(readMongoCollectionName);
        toMongoCollection = db.getCollection(toMongoCollectionName);

    }

    public static void main(String[] args) throws Exception {
        init();
        /*List<String> ids = Lists.newArrayList();
        ids.add("5cb4fa0f858f4873ad3aaaa170296066");
        ids.add("43762066b50b40eba59d8d1f10a7c40a");
        BasicDBObject  query = new BasicDBObject ();
        query.put("_id", new BasicDBObject("$in",ids));
        MongoCursor<Document> iterator = readMongoCollection.find(query).iterator();
        while(iterator.hasNext()){
            System.out.println(iterator.next());
        }
*/
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


                List<String> ids = Lists.newArrayList();
                for(Document document : documents ) {
                    String enterpriseId = null;
                    try {
                        enterpriseId = document.getString("enterpriseId");
                        ids.add(enterpriseId);

                    } catch (Exception e) {
                        logger.error("转换数据出错了，id:" + enterpriseId + ";数据：" + document.toJson(), e);
                    }
                }
                lastId = documents.get(documents.size()-1).getString("_id");

                read += documents.size();

                logger.info( "读取进度" + read + "/" + count + ";" );

                try
                {
                    BasicDBObject  query = new BasicDBObject ();
                    query.put("_id", new BasicDBObject("$in",ids));
                    MongoCursor<Document> oldDocument = readMongoCollection.find(query).iterator();
                    List<Document> toDocuments = Lists.newArrayList();
                    while (oldDocument.hasNext()) {
                        Document doc = oldDocument.next();
                        toDocuments.add(DocumentToBeanUtil.oldDocToNewDoc(doc));
                    }

                    toMongoCollection.insertMany(toDocuments);
                    logger.info( "size:{};;耗时:{}ms;",toDocuments.size(),System.currentTimeMillis()-start);
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
       /* for(String key : repeatOrgName)
        {
            logger.info(key);
        }*/

    }
}
