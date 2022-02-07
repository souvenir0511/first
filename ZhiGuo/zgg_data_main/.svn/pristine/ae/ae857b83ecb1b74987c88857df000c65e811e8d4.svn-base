package com.zgg.batch.patent.util;

import com.zgg.batch.patent.pojo.Patent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.bson.Document;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.util.*;

public class DocumentToBeanUtil {

    /**
     * 获取专利待缴费信息
     * @param patentMongoList
     * @param patentRenewMongoList
     * @return
     */
    public static List<Patent> getPatentList(List<Document> patentMongoList, List<Document> patentRenewMongoList) {

        List<Patent> patentList = new ArrayList<>();

        patentMongoList.forEach(item -> {

            Patent patent = new Patent();
            //专利基本信息
            patent.setPatentCode(item.getString("patentCode"));
            patent.setPatentType(item.getString("patentType"));
            patent.setPatentName(item.getString("patentName"));
            patent.setApplicant(item.getString("applicant"));

            Date applyTime = null;

            if (StringUtils.isNotBlank(item.getString("applyTime"))) {
                try{
                    applyTime = item.getDate("applyTime");
                }catch(Exception e){
                    try {
                        applyTime = DateUtils.parseDate(item.getString("applyTime"),"yyyy-MM-dd");
                    } catch (ParseException parseException) {
                        parseException.printStackTrace();
                    }
                }
            }
            patent.setApplyTime(applyTime);

            Date afficheTime = null;
            if (StringUtils.isNotBlank(item.getString("afficheTime"))) {
                try{
                    afficheTime = item.getDate("afficheTime");
                }catch(Exception e){
                    try {
                        afficheTime = DateUtils.parseDate(item.getString("afficheTime"),"yyyy-MM-dd");
                    } catch (ParseException parseException) {
                        parseException.printStackTrace();
                    }
                }
            }
            patent.setAfficheTime(afficheTime);
            patent.setTypeCode(item.getString("typeCode"));
            //专利待缴费信息
            if (!CollectionUtils.isEmpty(patentRenewMongoList)) {

                Optional<Document> documentOptional = patentRenewMongoList.stream().filter(subItem -> item.getString("_id").equals(subItem.getString("_id"))).findFirst();
                if (documentOptional.isPresent()) {


                    //专利状态
                    if (StringUtils.isNotBlank(documentOptional.get().getString("patent_status"))) {
                        patent.setPatentStatus(documentOptional.get().getString("patent_status"));
                    }else {
//                        patent.setPatentStatus("专利权维持");
                    }
                }else {
//                    patent.setPatentStatus("专利权维持");
                }

            }else {

//                patent.setPatentStatus("专利权维持");
            }
            patentList.add(patent);
        });
        return patentList;
    }

    /**
     * 获取专利待缴费信息
     * @param patentMongoList
     * @param patentRenewMongoList
     * @return
     */
    public static List<Document> getPatentListToMongo(List<Document> patentMongoList, List<Document> patentRenewMongoList) {

        List<Document> documentList = new ArrayList<>();

        patentMongoList.forEach(item -> {
            String applicant = item.getString("applicant");
            if (applicant.endsWith("公司") || applicant.length() < 4) {
                return;
            }

            Document document = new Document();
            //专利基本信息
            document.put("patentCode", item.getString("patentCode"));
            document.put("patentType", item.getString("patentType"));
            document.put("patentName", item.getString("patentName"));
            document.put("applicant", item.getString("applicant"));
            document.put("applyTime", item.getString("applyTime"));
            document.put("typeCode", item.getString("typeCode"));
            //专利待缴费信息
            if (!CollectionUtils.isEmpty(patentRenewMongoList)) {

                Optional<Document> documentOptional = patentRenewMongoList.stream().filter(subItem -> item.getString("_id").equals(subItem.getString("_id"))).findFirst();
                if (documentOptional.isPresent()) {
                    //专利状态
                    if (StringUtils.isNotBlank(documentOptional.get().getString("patent_status"))) {

                        document.put("patentStatus", documentOptional.get().getString("patent_status"));
                    }else {
                        document.put("patentStatus", "专利权维持");
                    }
                }else {
                    document.put("patentStatus", "专利权维持");
                }
            }else {
                document.put("patentStatus", "专利权维持");
            }
            documentList.add(document);
        });
        return documentList;
    }

}

