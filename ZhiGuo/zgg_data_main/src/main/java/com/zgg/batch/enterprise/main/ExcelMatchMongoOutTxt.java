package com.zgg.batch.enterprise.main;

import cn.hutool.db.DbUtil;
import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.zgg.batch.entity.Enterprise;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.BsonDocument;
import org.bson.Document;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 字段转换
 * 1、取得mongo中的数据
 * 2、mongo返回的字段转换譬如 orgName转换为entName
 * 3、转换完成的数据写入新mongo
 */
@Slf4j
public class ExcelMatchMongoOutTxt {

    static MongoClient fromMongoClient;
    static MongoDatabase fromMongoDatabase;
    static MongoCollection<Document> fromMongoCollection;

    static String formMongoHost = "192.168.10.45";
    static int mongoPort = 27017;
    static String mongoUser = "";
    static String mongoPwd = "";
    static String fromMongoDatabaseName = "QCC_DATA";
    static String fromMongoCollectionName = "qcc_basicInfo_xigua001";


    public static AtomicInteger success = new AtomicInteger(0);

    public static void main(String[] args) {

        init();
        fixEnterpriseData();
        fromMongoClient.close();
        DbUtil.close();
    }

    public static void init() {

        fromMongoClient = new MongoClient(formMongoHost, mongoPort);
        fromMongoDatabase = fromMongoClient.getDatabase(fromMongoDatabaseName);
        fromMongoCollection = fromMongoDatabase.getCollection(fromMongoCollectionName);
        log.info("mango初始化成功");
    }


    public static void fixEnterpriseData() {

        log.info("开始处理");
        long allStart = System.currentTimeMillis();

        String path = "D:\\ETLDocument\\营销中心数据--西瓜.xlsx";
        List<Enterprise> enterpriseList = getExcelData(path);
        List<String> enterpriseNameList = enterpriseList.stream().map(Enterprise::getEntName).collect(Collectors.toList());

        String lastId = "";
        long count = fromMongoCollection.count();
        try {
            List<Document> documents = Lists.newArrayList();
            long start = System.currentTimeMillis();
            MongoCursor<Document> iterator = fromMongoCollection
                    .find(BsonDocument.parse("{\"_id\":{$gt:\"" + lastId + "\"}}"))
                    .sort(BsonDocument.parse("{\"_id\":1}"))
                    .iterator();
            if(!iterator.hasNext()) {
                log.info( "处理完成" );
            }
            while (iterator.hasNext()) {
                documents.add(iterator.next());
            }
            log.info( "查询mango数据" + documents.size() + "条,耗时" + (System.currentTimeMillis()-start) + "ms;" );

            start = System.currentTimeMillis();
            List<String> enterpriseMatchList = new ArrayList<>();
            List<String> enterpriseNotMatchList = new ArrayList<>();

            //判断数据库数据是否存在于文件
//            documents.forEach(item -> {
//                if (enterpriseNameList.contains(item.getString("company"))) {
//                    enterpriseMatchList.add(item.getString("company"));
//                }else {
//                    enterpriseNotMatchList.add(item.getString("company"));
//                }
//            });

            //判断文件是否存在于数据库
            List<String> enterpriseMongoList = new ArrayList<>();
            documents.forEach(item -> enterpriseMongoList.add(item.getString("company")));
            enterpriseNameList.forEach(item -> {
                if (enterpriseMongoList.contains(item)) {
                    enterpriseMatchList.add(item);
                }else {
                    enterpriseNotMatchList.add(item);
                }
            });

            outTxt(enterpriseMatchList, enterpriseNotMatchList);
            lastId = documents.get(documents.size()-1).getString("_id");
            log.info("处理进度" + documents.size() + "/" + count + ",耗时" + (System.currentTimeMillis() - start) + "ms");
        }catch (Exception e){
            log.error( "未知报错，lastId:" + lastId , e );
        }
        log.info("总耗时{}ms",System.currentTimeMillis()-allStart);
    }

    private static List<Enterprise> getExcelData(String path) {

        List<Enterprise> customerParkList = new ArrayList<>();

        Workbook workbook = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            if (path.endsWith(".xls")) {
                workbook = new HSSFWorkbook(fileInputStream);
            } else {
                workbook = new XSSFWorkbook(fileInputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (workbook == null) {
            return customerParkList;
        }
        Sheet sheet = workbook.getSheet("Sheet1");
        if (sheet == null) {
            throw new RuntimeException("Sheet错误");
        }
        Row rowHead = sheet.getRow(0);
        //列数
        rowHead.getPhysicalNumberOfCells();
        //行数
        int count = sheet.getLastRowNum();

        for (int i = 2; i <= count; i++) {

            Enterprise enterprise = new Enterprise();
            Row row = sheet.getRow(i);

            //企业名称
            if (ObjectUtils.isNotEmpty(row) && ObjectUtils.isNotEmpty(row.getCell(3))) {
                Cell cell = row.getCell(3);
                // 拿到单元格数值
                if (StringUtils.isNotBlank(getCellValue(cell))) {
                    enterprise.setEntName(getCellValue(cell));
                    customerParkList.add(enterprise);
                }
            }
        }
        String entName = customerParkList.get(customerParkList.size() - 1).getEntName();
        log.info("最后一家企业名称{}", entName);

        System.out.println(entName);

        return customerParkList;
    }

    /**
     * 获取cell的
     *
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell) {
        String strCell = "";
        if (cell != null) {
            switch (cell.getCellType()) {
                //字符串类型
                case STRING:
                    strCell = cell.getStringCellValue();
                    break;
                //数字类型
                case NUMERIC:
                    // 处理日期格式、时间格式
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        SimpleDateFormat sdf;
                        sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = cell.getDateCellValue();
                        strCell = sdf.format(date);
                    } else {
                        strCell = String.valueOf(cell.getNumericCellValue());
                    }
                    break;
                //boolean类型
                case BOOLEAN:
                    strCell = String.valueOf(cell.getBooleanCellValue());
                    break;
                default:
                    strCell = "";
                    break;
            }
        }
        if (strCell.equals("") || strCell == null) {
            strCell = "";
        }
        return strCell.trim();
    }



    /**
     * 输出txt文件
     */
    public static void outTxt(List<String> enterpriseMatchList, List<String> enterpriseNotMatchList) {

        //加载资源
        try {

            //数据写出
            File file = new File(new ClassPathResource("D:\\ETLDocument\\enterpriseMatchList（"+enterpriseMatchList.size()+"家）.txt").getPath());
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            //数据写出文件2
            File file2 = new File(new ClassPathResource("D:\\ETLDocument\\enterpriseNotMatchList（"+enterpriseNotMatchList.size()+"家）.txt").getPath());
            if (!file2.exists()) {
                file2.createNewFile();
            }
            FileWriter fw2 = new FileWriter(file2.getAbsoluteFile());
            BufferedWriter bw2 = new BufferedWriter(fw2);
            //数据循环处理
            enterpriseMatchList.forEach(item -> {
                try {
                    bw.write(item);
                    bw.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
            //数据循环处理2
            enterpriseNotMatchList.forEach(item -> {
                try {
                    bw2.write(item);
                    bw2.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
            bw.close();
            bw2.close();
            System.out.println("文件处理成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
