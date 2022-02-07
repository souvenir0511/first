package com.zgg.batch.main;

import cn.hutool.core.io.file.FileReader;
import com.alibaba.fastjson.JSON;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.DeleteManyModel;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.result.DeleteResult;
import com.zgg.batch.entity.EnterpriseES;
import org.apache.http.HttpHost;
import org.bson.BsonDocument;
import org.bson.Document;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class DeleteMongoEsByIdMain {

    private static final Logger logger = LoggerFactory.getLogger(DeleteMongoEsByIdMain.class);

    private static MongoClient mongoClient;
    private static MongoDatabase db;
    private static MongoCollection<Document> fromMongoCollection;

    private static HttpHost http;
    private static RestClientBuilder builder;
    private static RestHighLevelClient restHighLevelClient;

    public static void main(String[] args) throws Exception{
        String[] split = args[0].split(",");
        String mongoIp = split[0];
        Integer mongoPort = Integer.parseInt(split[1]);
        String mongoDb = split[2];
        String mongoName = split[3];
        String esIp = split[4];
        Integer esPort = Integer.parseInt(split[5]);
        String path = split[6];
        /*String mongoIp = "192.168.10.150";
        Integer mongoPort = 27017;
        String mongoDb = "zgg_data";
        String mongoName = "54w_enterprise";
        String esIp = "192.168.10.150";
        Integer esPort = 9200;
        String path = "E:\\output\\ids7.txt" */
        init(mongoIp, mongoPort, mongoDb, mongoName, esIp, esPort);

        FileReader fileReader = new FileReader(path);
        List<String> list = fileReader.readLines();
        List<String> ids = new ArrayList<>();
        list.stream().forEach( str ->{
            str = str.replace(" ", "");
            System.out.println("-----"+str);
            ids.add(str.trim());
            if(ids.size() == 10000){
                try {
                    DeleteResult deleteResult = fromMongoCollection.deleteMany(new Document("_id", new Document("$in", ids)));
                    System.out.println("mongoCount:"+deleteResult.getDeletedCount());
                    //processor(ids);
                }catch (Exception e){
                    e.printStackTrace();
                }
                ids.clear();
            }
        });
        DeleteResult deleteResult = fromMongoCollection.deleteMany(new Document("_id", new Document("$in", ids)));
        System.out.println("mongoCount:"+deleteResult.getDeletedCount());
       // processor(ids);
        System.out.println("结束");
    }

    public static void init(String mongoIp, Integer mongoPort, String mongoDb, String mongoName, String esIp, Integer esPort){
        mongoClient = new MongoClient(mongoIp, mongoPort);
        db = mongoClient.getDatabase(mongoDb);
        fromMongoCollection = db.getCollection(mongoName);

        http = new HttpHost(esIp, esPort, "http");
        builder = RestClient.builder(http);
        restHighLevelClient = new RestHighLevelClient(builder);
    }

    public static void processor(List<String> ids) throws Exception{
        BulkProcessor bulkProcessor = init(restHighLevelClient);
        ids.stream().forEach( id -> {
            DeleteRequest deleteRequest = new DeleteRequest("54w_enterprise", id);
            bulkProcessor.add(deleteRequest);
           /* DeleteResponse delete = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);

            IndexRequest request = new IndexRequest(esName);
            request.index(esName).id(String.valueOf(enterprise.getId())).source(JSON.toJSONString(enterprise), XContentType.JSON);
            bulkProcessor.add(request);*/
        });

    }

    public static BulkProcessor init(RestHighLevelClient restHighLevelClient){
        BulkProcessor.Listener listener = new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                logger.info("---尝试删除{}条数据---{}",request.numberOfActions());
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                logger.info("---尝试删除{}条数据成功---{}",request.numberOfActions());
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                logger.info("---尝试删除数据失败---{}"+failure);
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
