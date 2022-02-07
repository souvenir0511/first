package com.zgg.batch.main;

import cn.hutool.db.DbUtil;
import cn.hutool.db.ds.simple.SimpleDataSource;
import cn.hutool.db.sql.SqlExecutor;
import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.zgg.batch.entity.PolicyMysql;
import com.zgg.batch.utils.DocumentToBeanUtil;
import org.bson.BsonDocument;
import org.bson.Document;
import org.elasticsearch.common.util.set.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class PolicyMongoToMysqlMain {

    private static final Logger logger = LoggerFactory.getLogger(PolicyMongoToMysqlMain.class);

    private static DataSource dataSource;
    private static Connection connection;

    private static MongoClient mongoClient;
    private static MongoDatabase db;
//    private static MongoCollection<Document> writeMongoCollection;

    private static MongoCollection<Document> fromMongoCollection;
    public static AtomicInteger success = new AtomicInteger(0);

    public static void main(String[] args) throws Exception{
        String[] str = new String[7];
        str[0] = "192.168.10.154;3306;zgg_admin";
        str[1] = "zhiguo";
        str[2] = "123456";

        str[3] = "192.168.10.154";
        str[4] = "27017";
        str[5] = "zgg_data";
        str[6] = "v1_policy";
        args = str;

        String [] arrays = args[0].split(";");
        String uri = "jdbc:mysql://"+ arrays[0]+":"+Integer.valueOf(arrays[1])+"/"+arrays[2]+"?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
        String user = args[1];
        String password = args[2];

        String mongoHost = args[3];
        Integer mongoPort = Integer.valueOf(args[4]);
        String mongoDb = args[5];
        String mongoName = args[6];

        logger.info("---------------------mongoIp:{},mongoPort:{},mongoDb:{},mongoName:{};mysqlUri:{},mysqlUser:{},mysqlPw:{}",mongoHost,mongoPort,mongoDb,mongoName,uri,user,password);

        init(uri, user, password, mongoHost, mongoPort, mongoDb, mongoName);
        fixEnterpriseData();
        DbUtil.close();
        mongoClient.close();
    }

    private static void init(String uri, String user, String password, String mongoHost, Integer mongoPort, String mongoDb, String mongoName) throws Exception{
        dataSource = new SimpleDataSource(uri, user, password);
        connection = dataSource.getConnection();

        mongoClient = new MongoClient(mongoHost, mongoPort);
        db = mongoClient.getDatabase(mongoDb);
        fromMongoCollection = db.getCollection(mongoName);
    }

    public static void fixEnterpriseData() {
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

                List<PolicyMysql> list = Lists.newArrayList();

                for(Document document : documents ) {
                    PolicyMysql policy = DocumentToBeanUtil.docToPolicyMySql(document);
                    list.add(policy);
                }
                lastId = documents.get(documents.size()-1).getString("_id");

                read += documents.size();

                logger.info( "读取进度" + read + "/" + count + ";" );

                try{
                    // toMongoCollection.insertMany(list);
                    processor(list);
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

    public static void processor(List<PolicyMysql> policys) {
        //BulkProcessor bulkProcessor = init();
        Date date = new Date();
        policys.forEach( policy -> {
            try {

                //设置默认值
                policy.setStatus(false);
                policy.setSourceType("批量");
                policy.setCreateUserId(1L);
                policy.setCreateTime(date);
                policy.setUpdateUserId(1L);
                policy.setUpdateTime(date);
                policy.setTopFlag(false);
                policy.setDeleteFlag(false);

                //字段
                String field = "id,project_name,policy_level,source_type,source_info,province_id,province_name,city_id,city_name,area_id,area_name,declare_start_time,declare_end_time,tech_field,project_type,financial_support,gov_department_name,declare_condition,support_degree,declare_material_method,policy_status,official_publish_time,crawl_time,status,top_flag,delete_flag,create_user_id,create_time,update_user_id,update_time";
                //占位符
                String placeholder = "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?";
                //值
                Object[] objects = {
                        policy.getId(),policy.getProjectName(),policy.getPolicyLevel(),policy.getSourceType(),policy.getSourceInfo(),policy.getProvinceId(),
                        policy.getProvinceName(),policy.getCityId(),policy.getCityName(),policy.getAreaId(),policy.getAreaName(),policy.getDeclareStartTime(),
                        policy.getDeclareEndTime(),policy.getTechField(),policy.getProjectType(),policy.getFinancialSupport(),policy.getGovDepartmentName(),
                        policy.getDeclareCondition(),policy.getSupportDegree(),policy.getDeclareMaterialMethod(),policy.getPolicyStatus(),policy.getOfficialPublishTime(),
                        policy.getCrawlTime(),policy.getStatus(),policy.getTopFlag(),policy.getDeleteFlag(),policy.getCreateUserId(),policy.getCreateTime(),
                        policy.getUpdateUserId(),policy.getUpdateTime()
                };
                SqlExecutor.executeBatch(connection, "insert into `policy_mongo`("+field+")values("+placeholder+")", objects);
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        });
    }

}
