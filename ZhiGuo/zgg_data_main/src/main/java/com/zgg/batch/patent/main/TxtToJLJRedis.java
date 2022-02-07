package com.zgg.batch.patent.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import cn.hutool.core.util.IdUtil;
import redis.clients.jedis.Jedis;

/**
 * 
 * 方法1、第一种方式，生成 id name 格式文件，执行python程序，同步到redis
 * 方法2、通过读取txt中文件内容，直接保存到redis中
 * 企业工商数据采集，直接放到Redis里
 * @author EDZ
 *
 */
public class TxtToJLJRedis {
	 
	private static  String fileQIYetxt = "C:\\Users\\Administrator\\Desktop\\jinxinguo\\无编号2.txt";

	//# 公司名队列
	private static String REDIS_ZLJ_COMPANY = "ZLJ_COMPANY";
	//# 专利号队列
	private static String REDIS_ZLJ_ZLCODE = "ZLJ_ZLCODE_PAGING";

	public static void main(String[] args) throws Exception {

		zlj_qiye();

	}
	public static void zlj_qiye() throws Exception {

		Jedis jedis = new Jedis("119.45.95.41", 7000);  //指定Redis服务Host和port
			jedis.auth("b5EgnL8VKg72B8I9") ;
			jedis.select(3) ;
		  BufferedReader br = new BufferedReader(new FileReader(fileQIYetxt));//构造一个BufferedReader类来读取文件
			String s = null;
			while((s = br.readLine())!=null){//使用readLine方法，一次读一行
				System.out.println(s);
	                jedis.lpush(REDIS_ZLJ_COMPANY, s.trim());
			}
		br.close();

		jedis.close(); //使用完关闭连接

	}
}
