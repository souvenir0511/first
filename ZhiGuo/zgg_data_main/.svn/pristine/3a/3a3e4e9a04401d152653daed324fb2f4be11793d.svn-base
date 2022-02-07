package com.zgg.batch.main;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonDocument;
import org.bson.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DistinctEntNameMain {
    private static MongoClient mongoClient;
    private static MongoDatabase db;
    private static MongoCollection<Document> fromMongoCollection;



    public static void main(String[] args) {
        test2();
        System.out.println("-------------------");
    }

    public static void test2(){
        FileReader fileReader = new FileReader("E:\\output\\DISTINCT.txt");
        List<String> list = fileReader.readLines();
        Map<String, List<String>> maps = new HashMap<>();
        list.stream().forEach(str ->{
            String[] split = str.split("--->");
            String replace = split[1].replace("[", "").replace("]", "");
            String[] split1 = replace.split(",");
            FileWriter writer = new FileWriter("E:\\output\\deleteids.txt");
            for(int i=0; i<split1.length;i++){
                if(i==0){
                    continue;
                }
                String tmp  = split1.toString().trim();
                tmp = tmp.replace(" ", "");
                writer.append(split1[i]+"\r\n");
            }

        });
    }

    public void test1(){
        FileReader fileReader = new FileReader("E:\\output\\54file_enterprise.json");
        List<String> list = fileReader.readLines();
        Map<String, List<String>> maps = new HashMap<>();
        list.stream().forEach(str ->{
            JSONObject jsonObject = JSONUtil.parseObj(str);
            String id = (String)jsonObject.get("_id");
            String entName = (String)jsonObject.get("entName");
            List<String> ids = maps.get(entName);
            if(ids !=null && ids.size()>0){
                ids.add(id);
            }else{
                ids = Lists.newArrayList();
                ids.add(id);
            }
            maps.put(entName, ids);
        });

        System.out.println("====="+maps.toString());
        FileWriter writer = new FileWriter("E:\\output\\DISTINCT.txt");
        int i=0;
        for (Map.Entry<String, List<String>> entry : maps.entrySet()) {

            if(entry.getValue().size()>1){
                i = (entry.getValue().size()-1)+i;
                // writer.append(entry.getKey()+"--->"+entry.getValue().toString()+"\r\n");
            }
            //System.out.println("===================="+i);
            //writer.append(entry.getKey()+"--->"+entry.getValue().toString()+"\\r\\n");
            //i++;
        }
        System.out.println("---"+i);
       /* String mongoIp = "192.168.10.150";
        Integer mongoPort = 27017;
        String mongoDb = "zgg_data";
        String mongoName = "54w_enterprise";
        init(mongoIp, mongoPort, mongoDb, mongoName);*/
    }

    public static void init(String mongoIp, Integer mongoPort, String mongoDb, String mongoName){
        mongoClient = new MongoClient(mongoIp, mongoPort);
        db = mongoClient.getDatabase(mongoDb);
        fromMongoCollection = db.getCollection(mongoName);
    }

    public static void fixEnterpriseData(String esName) throws Exception {
        String lastId = "";
        DBObject projection = new BasicDBObject();
        projection.put("article", 1);


    }
}
