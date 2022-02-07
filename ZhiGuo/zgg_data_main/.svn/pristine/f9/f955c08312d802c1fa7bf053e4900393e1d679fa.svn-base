package com.zgg.batch.patent.main;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.db.DbUtil;
import cn.hutool.db.sql.SqlExecutor;
import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.zgg.batch.patent.pojo.Patent;
import com.zgg.batch.patent.util.DocumentToBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonDocument;
import org.bson.Document;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 字段转换
 * 1、取得mongo中的数据
 * 2、mongo返回的字段转换譬如 orgName转换为entName
 * 3、转换完成的数据写入新mongo
 */
@Slf4j
public class MongoToMysql {
    static MongoClient fromMongoClient;
    static MongoClient toMongoClient;
    static MongoDatabase fromMongoDatabase;
    static MongoDatabase toMongoDatabase;
    static MongoCollection<Document> fromMongoCollection;
    static MongoCollection<Document> fromPatentRenewMongoCollection;
    static MongoCollection<Document> toMongoCollection;


    static String formMongoHost = "1.13.21.235";
    static String toMongoHost = "192.168.10.154";
    static int mongoPort = 27017;
    static String mongoUser = "";
    static String mongoPwd = "";
    static String fromMongoDatabaseName = "ZLJDATA";
    static String fromMongoCollectionName = "zlj_basic_info";
    static String fromPatentRenewMongoCollectionName = "zlj_renew_info";
    static String toMongoDatabaseName = "zgg_data";
    static String toMongoCollectionName = "patent";

    static String driver = "com.mysql.cj.jdbc.Driver";
    static String url="jdbc:mysql://192.168.10.154:3306/patent_mongo_to_mysql?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&autoReconnect=true&failOverReadOnly=false&rewriteBatchedStatements=true";
    static String user="zhiguo";
    static String pwd = "123456";
    static Vector<Connection> pools = new Vector<>();



    public static AtomicInteger success = new AtomicInteger(0);

    public static void main(String[] args) {
        init();
        fixEnterpriseData();
        fromMongoClient.close();
        toMongoClient.close();
        DbUtil.close();
    }

    public static void init() {

        // 需要验证
        ServerAddress serverAddress = new ServerAddress(formMongoHost, mongoPort);
        List<MongoCredential> credentials = new ArrayList<>();
        MongoCredential credential = MongoCredential.createCredential("root","admin","7ZBPq66Fjt31zJWe".toCharArray());
        credentials.add(credential);
        fromMongoClient = new MongoClient(serverAddress, Arrays.asList(credential));

//        fromMongoClient = new MongoClient(formMongoHost, mongoPort);
        fromMongoDatabase = fromMongoClient.getDatabase(fromMongoDatabaseName);
        fromMongoCollection = fromMongoDatabase.getCollection(fromMongoCollectionName);
        fromPatentRenewMongoCollection = fromMongoDatabase.getCollection(fromPatentRenewMongoCollectionName);

        toMongoClient = new MongoClient(toMongoHost, mongoPort);
        toMongoDatabase = toMongoClient.getDatabase(toMongoDatabaseName);
        toMongoCollection = toMongoDatabase.getCollection(toMongoCollectionName);
    }

    public static Connection getDBConnection(){
        try {
            //1.加载驱动
            Class.forName(driver);
            //2.取得数据库连接
            return DriverManager.getConnection(url, user, pwd);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    static {
        int i = 0;
        //开启50个连接
        while(i<10){
            pools.add(getDBConnection());
            i++;
        }
    }

    public static synchronized Connection getPool(){
        if(pools != null && pools.size() > 0){
            int lastInd = pools.size() -1;
            return pools.remove(lastInd);
        }else{
            return getDBConnection();
        }
    }

    public static void fixEnterpriseData() {
        int batchSize = 10000;
        String lastId = "";
        int times = 0;
        long count = fromMongoCollection.count();
        int read = 0;

        long allStart = System.currentTimeMillis();

        while (++times < batchSize){
            try {
                List<Document> documents = Lists.newArrayList();
                long start = System.currentTimeMillis();
                MongoCursor<Document> iterator = fromMongoCollection
                        .find(BsonDocument.parse("{\"_id\":{$gt:\"" + lastId + "\"}}"))
                        .sort(BsonDocument.parse("{\"_id\":1}"))
                        .limit(batchSize)
                        .iterator();
                if(!iterator.hasNext()){
                    log.info( "处理完成" );
                    break;
                }
                while (iterator.hasNext()) {
                    documents.add(iterator.next());
                }
                log.info("查询数据" + batchSize + "条耗时" + (System.currentTimeMillis() - start) + "ms;");

                start = System.currentTimeMillis();

                Set<String> patentCodeSet = documents.stream().map(item -> item.getString("_id")).collect(Collectors.toSet());
                List<Document> patentRenewList = new ArrayList<>();
                for (Document document : fromPatentRenewMongoCollection.find(new Document("_id", new Document("$in", patentCodeSet)))) {
                    patentRenewList.add(document);
                }

                //存MySQL
                List<Patent> patentList = DocumentToBeanUtil.getPatentList(documents, patentRenewList);
                //存mongo
//                List<Document> patentList = DocumentToBeanUtil.getPatentListToMongo(documents, patentRenewList);


                lastId = documents.get(documents.size()-1).getString("_id");
                read += documents.size();
                log.info("读取进度" + read + "/" + count + ";" );
                try {
                    //存mongo
//                    toMongoCollection.insertMany(patentList);
                    //存MySQL
                    patentBatch(patentList);
                }catch (Exception e){
                    log.error(e.getMessage(),e);
                }
                success.incrementAndGet();
                log.info( "处理进度" + success.get()*batchSize + "/" + count + ";" );
            }catch (Exception e){
                log.error( "未知报错，lastId:" + lastId , e );
                try {
                    Thread.sleep( 10*1000 );
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }

        log.info("总耗时{}ms",System.currentTimeMillis()-allStart);
    }
    /**
     * 计算得到总页数
     *
     * @return
     */
    public static int getTotalPage(int count, int pageSize) {
        int totalPage;
        // 假设总数是50，是能够被5整除的，那么就有10页
        if (0 == count % pageSize) {
            totalPage = count / pageSize;
        }
        // 假设总数是51，不能够被5整除的，那么就有11页
        else {
            totalPage = count / pageSize + 1;
        }

        if (0 == totalPage) {
            totalPage = 1;
        }
        return totalPage;
    }

    /**
     * 多线程处理
     * @param
     */
    public static void patentBatch(List<Patent> patentList){

        //数据库插入
        int amount;
        if (CollectionUtil.isNotEmpty(patentList)) {
            amount = patentList.size();
            int pageSize = 5000;
            int totalPage = getTotalPage(amount, pageSize);
//            ExecutorService executorService = Executors.newFixedThreadPool(totalPage);
//            CountDownLatch latch = new CountDownLatch(totalPage);
            for (int pageNo = 0; pageNo < totalPage; pageNo++) {
                int fromIndex = pageNo * pageSize;
                int toIndex = (pageNo + 1) * pageSize;
                if (toIndex > amount) {
                    toIndex = amount;
                }
                int finalToIndex = toIndex;
//                executorService.submit(() -> {
                    List<Patent> patents = patentList.subList(fromIndex, finalToIndex);
                    patentProcessor(patents);
//                    latch.countDown();
//                });
            }
        }

    }

    public static void patentProcessor(List<Patent> patentList) {
        long start = System.currentTimeMillis();
        Connection conn = getPool();
        //字段
        String field = "id,patent_code,patent_type,patent_name,applicant,apply_time,affiche_time,type_code,patent_status,delete_flag,create_user_id,create_time,update_user_id,update_time";
        //占位符
        String placeholder = "?,?,?,?,?,?,?,?,?,?,?,?,?,?";
        //sql
        String sql = "insert into `patent`(" + field + ")values(" + placeholder + ")";
        List<Object[]> objectsList = new ArrayList<>();
        Date date = new Date();
        patentList.forEach(item -> {
            item.setId(IdUtil.getSnowflake(1,1).nextId());
            item.setDeleteFlag(false);
            item.setCreateUserId(1L);
            item.setCreateTime(date);
            item.setUpdateUserId(1L);
            item.setUpdateTime(date);
            //值
            Object[] objects = {
                item.getId(),item.getPatentCode(),item.getPatentType(),item.getPatentName(),item.getApplicant(),item.getApplyTime(),item.getAfficheTime(),item.getTypeCode(),item.getPatentStatus(),item.getDeleteFlag(),item.getCreateUserId(),item.getCreateTime(),item.getUpdateUserId(),item.getUpdateTime()
            };
            objectsList.add(objects);
        });

        try {
            SqlExecutor.executeBatch(conn, sql, objectsList);
            conn.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }finally {
            try {
                conn.close();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
        log.info("数据库写入耗时{}ms", System.currentTimeMillis() - start);
    }

}
