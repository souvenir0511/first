package com.zgg.batch.main;

import com.alibaba.fastjson.JSON;

import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.zgg.batch.entity.Policy;
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
import org.elasticsearch.common.util.set.Sets;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class PolicyMongoToEsMain {
    private static final Logger logger = LoggerFactory.getLogger(PolicyMongoToEsMain.class);

    private static MongoClient mongoClient;
    private static MongoDatabase db;
    private static MongoCollection<Document> fromMongoCollection;
    private static HttpHost http;
    private static RestClientBuilder builder;
    private static RestHighLevelClient restHighLevelClient;

    public static AtomicInteger success = new AtomicInteger(0);

    public static void main(String[] args) throws Exception{
        String mongoIp = args[0];
        Integer mongoPort = Integer.valueOf(args[1]);
        String mongoDb = args[2];
        String mongoName = args[3];
        String esIp = args[4];
        Integer esPort = Integer.valueOf(args[5]);
        String esName = args[6];
        /*String mongoIp = "192.168.10.154";
        Integer mongoPort = 27017;
        String mongoDb = "zgg_data";
        String mongoName = "v1_policy";
        String esIp = "192.168.10.154";
        Integer esPort = 9200;
        String esName = "v1_policy";*/
        logger.info("---------------------mongoIp:{},mongoPort:{},mongoDb:{},mongoName:{};esIp:{},esPort:{},esName:{}",mongoIp,mongoPort,mongoDb,mongoName,esIp,esPort,esName);
        init(mongoIp, mongoPort, mongoDb, mongoName, esIp, esPort, esName);
        fixEnterpriseData(esName);
        mongoClient.close();
    }

    public static void init(String mongoIp, Integer mongoPort, String mongoDb, String mongoName, String esIp, Integer esPort, String esName)
    {
        /*mongoClient = new MongoClient(mongoIp, mongoPort);
        db = mongoClient.getDatabase(mongoDb);
        fromMongoCollection = db.getCollection(mongoName);*/

        ServerAddress serverAddress = new ServerAddress(mongoIp, mongoPort);

        List<MongoCredential> credentials = new ArrayList<MongoCredential>();

        MongoCredential credential = MongoCredential.createCredential("root","admin","mongo321".toCharArray());  // 需要验证
        credentials.add(credential);
        //mongoClient = new MongoClient(mongoIp, mongoPort);
        mongoClient = new MongoClient(serverAddress, Arrays.asList(credential));
        db = mongoClient.getDatabase(mongoDb);
        fromMongoCollection = db.getCollection(mongoName);

        http = new HttpHost(esIp, esPort, "http");
        builder = RestClient.builder(http);
        restHighLevelClient = new RestHighLevelClient(builder);

    }

    public static void fixEnterpriseData(String esName) throws Exception {
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

                List<Policy> list = Lists.newArrayList();

                for(Document document : documents ) {
                    Policy policy = DocumentToBeanUtil.docToPolicy(document);
                    list.add(policy);
                }
                lastId = documents.get(documents.size()-1).getString("_id");

                read += documents.size();

                logger.info( "读取进度" + read + "/" + count + ";" );

                try
                {
                    // toMongoCollection.insertMany(list);
                    processor(list, esName);
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

    public static void processor(List<Policy> policys, String esName) throws Exception{
        BulkProcessor bulkProcessor = init(restHighLevelClient);
        policys.stream().forEach( policy -> {
            IndexRequest request = new IndexRequest(esName);
            request.index(esName).id(String.valueOf(policy.getPolicyId())).source(JSON.toJSONString(policy), XContentType.JSON);
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
