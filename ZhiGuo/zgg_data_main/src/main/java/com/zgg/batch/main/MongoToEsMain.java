package com.zgg.batch.main;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.zgg.batch.entity.EnterpriseES;
import com.zgg.batch.utils.DocumentToBeanUtil;
import org.apache.http.HttpHost;
import org.bson.BsonDocument;
import org.bson.Document;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 从mongo中读取数据建索引
 * 1、从最新企业mongo中读取mongo数据
 * 2、mongo数据转换Document转换成对象
 * 3、批量写入es中
 */
public class MongoToEsMain {
    private static final Logger logger = LoggerFactory.getLogger(MongoToEsMain.class);

    private static MongoClient mongoClient;
    private static MongoDatabase db;
    private static MongoCollection<Document> fromMongoCollection;


    /*private static String mongoHost = "192.168.10.150";
    private static int mongoPort = 27017;
    private static String mongoDatabase = "zgg_data";
    private static String fromMongoCollectionName = "test3";
*/
    private static HttpHost http;
    private static RestClientBuilder builder;
    private static RestHighLevelClient restHighLevelClient;
   /*
    private static String esHost = "192.168.10.150";
    private static int esPort = 9200;
    private static String indexName = "v4_enterprise";
*/
    public static AtomicInteger success = new AtomicInteger(0);

    public static void init(String mongoIp, Integer mongoPort, String mongoDb, String mongoName, String esIp, Integer esPort, String esName)
    {
        // 需要验证
//        ServerAddress serverAddress = new ServerAddress(mongoIp, mongoPort);
//        List<MongoCredential> credentials = new ArrayList<>();
//        MongoCredential credential = MongoCredential.createCredential("root","admin","mongo321".toCharArray());
//        credentials.add(credential);
//        mongoClient = new MongoClient(serverAddress, Arrays.asList(credential));

        // 不需要验证
        mongoClient = new MongoClient(mongoIp, mongoPort);
        db = mongoClient.getDatabase(mongoDb);
        fromMongoCollection = db.getCollection(mongoName);

        http = new HttpHost(esIp, esPort, "http");
        builder = RestClient.builder(http);
        restHighLevelClient = new RestHighLevelClient(builder);

    }

    public static void main(String[] args) {

//        String mongoIp = args[0];
//        Integer mongoPort = Integer.valueOf(args[1]);
//        String mongoDb = args[2];
//        String mongoName = args[3];
//        String esIp = args[4];
//        Integer esPort = Integer.valueOf(args[5]);
//        String esName = args[6];

        String mongoIp = "192.168.10.154";
        Integer mongoPort = 27017;
        String mongoDb = "zgg_data";
        String mongoName = "40_enterprise";
        String esIp = "192.168.10.154";
        Integer esPort = 9200;
        String esName = "40_enterprise";
        logger.info("mongoIp:{},mongoPort:{},mongoDb:{},mongoName:{};esIp:{},esPort:{},esName:{}",mongoIp,mongoPort,mongoDb,mongoName,esIp,esPort,esName);
        init(mongoIp, mongoPort, mongoDb, mongoName, esIp, esPort, esName);
        fixEnterpriseData(esName);
        mongoClient.close();
        //System.exit(0);
    }

    public static void fixEnterpriseData(String esName) {
        int batchSize = 10000;
        String lastId = "";
        long count = fromMongoCollection.count();
        int read = 0;

        int totalPage = new Long(count).intValue()/batchSize+1;

        long allStart = System.currentTimeMillis();

        for(int i = 0; i< totalPage; i++){
            try{
                List<Document> documents = Lists.newArrayList();
                long start = System.currentTimeMillis();
                MongoCursor<Document> iterator = fromMongoCollection
                        .find(BsonDocument.parse("{\"_id\":{$gt:\"" + lastId + "\"}}"))
                        .sort(BsonDocument.parse("{\"_id\":1}"))
                        .limit(batchSize)
                        .iterator();
                while (iterator.hasNext()) {
                    documents.add(iterator.next());
                }
                logger.info( "查询数据" + batchSize + "条耗时" + (System.currentTimeMillis()-start) + "ms;" );

                start = System.currentTimeMillis();
                List<EnterpriseES> list = Lists.newArrayList();
                for(Document document : documents ) {
                    EnterpriseES enterpriseES = DocumentToBeanUtil.newDocToEntEs(document);
                    list.add(enterpriseES);
                }
                lastId = documents.get(documents.size()-1).getString("_id");
                read += documents.size();
                logger.info( "读取进度" + read + "/" + count + ";" );

                try
                {
                    processor(list, esName);
                    logger.info( "size:{};;耗时:{}ms;",list.size(),System.currentTimeMillis()-start);
                }
                catch (Exception e){
                    logger.error(e.getMessage(),e);
                }
                success.incrementAndGet();
                logger.info( "处理进度" + success.get()*batchSize + "/" + count + ";" );
                Thread.sleep(5*1000);
            }catch (Exception e)
            {
                logger.error( "未知报错，lastId:" + lastId , e );
                try {
                    Thread.sleep( 10*1000 );
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        logger.info("数据处理完成,总耗时{}ms",System.currentTimeMillis()-allStart);
    }

    public static void processor(List<EnterpriseES> enterprises, String esName) {
        BulkProcessor bulkProcessor = init(restHighLevelClient);
        enterprises.forEach(enterprise -> {
            IndexRequest request = new IndexRequest(esName);
            request.index(esName).id(String.valueOf(enterprise.getId())).source(JSON.toJSONString(enterprise), XContentType.JSON);
            bulkProcessor.add(request);
        });
    }

    public static BulkProcessor init(RestHighLevelClient restHighLevelClient){
        BulkProcessor.Listener listener = new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                logger.info("---尝试插入{}条数据---{}",request.numberOfActions());
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                logger.info("---尝试插入{}条数据成功---{}",request.numberOfActions());
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                logger.info("---尝试插入数据失败---{}"+failure);
            }
        };

        return BulkProcessor.builder((request, bulkListener) ->
                restHighLevelClient.bulkAsync(request, RequestOptions.DEFAULT, bulkListener), listener)
                // 每添加1000个request，执行一次bulk操作
                .setBulkActions(5000)
                // 每达到5M的请求size时，执行一次bulk操作
                .setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB))
                // 每5s执行一次bulk操作
                .setFlushInterval(TimeValue.timeValueSeconds(5))
                // 设置并发请求数。默认是1，表示允许执行1个并发请求，积累bulk requests和发送bulk是异步的，其数值表示发送bulk的并发线程数（可以为2、3、...）；若设置为0表示二者同步。
                .setConcurrentRequests(2)
                .build();

    }
}
