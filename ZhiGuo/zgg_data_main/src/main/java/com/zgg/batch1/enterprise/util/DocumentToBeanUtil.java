package com.zgg.batch1.enterprise.util;

import cn.hutool.db.Entity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zgg.batch.utils.CommonUtil;
import com.zgg.batch1.entity.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.util.CollectionUtils;


import java.util.*;


public class DocumentToBeanUtil {
    public static Enterprise spiderMongoToMongo(Document doc, List<Entity> entityList){
        Enterprise enterprise = new Enterprise();
        Object industryInfo = doc.get("industry_info");
        JSONObject industryInfoJsonObject = JSON.parseObject(JSON.toJSONString(industryInfo));

        Object head_info = doc.get("head_info");
        JSONObject headInfoJsonObject = JSON.parseObject(JSON.toJSONString(head_info));

//        enterprise.setId("_id", doc.getString("_id"));
        enterprise.setId(doc.getString("_id"));
 //       enterprise.set("entId", doc.getString("_id"));
        //enterprise.set("oldId", doc.getString("qcc_id"));
//        enterprise.set("orgLogo", "");
//        String entPriseName = doc.getString("entPriseName");
//        entPriseName = entPriseName.replace("(", "（").replace(")", "）");
//        enterprise.setEnterpriseName(entPriseName);
//        enterprise.setEnterpriseName(doc.getString("EntPriseName"));

        List<String> formerNames = null;
       // String formerNames1 = formerNames.toString();
        String legalPerson = "";
        String RegisterMoney = "";
        String registerMoneyUnit = "";
        String enterpriseStatus = "";
        String registerDate = "";
        String registerNumber = "";
        String organizationCode = "";
        String creditCode = "";
        String enterpriseType = "";
        String taxpayerCode = "";
        String staffSize = "";
        String industry = "";
        String businessTerm = "";
        String checkDate = "";
        String registAuthority = "";
        String introduce = "";
        String website = "";
        String insuredNumber = "";
        String address = "";
        Object businessScope1 = "";
        String businessScope = businessScope1.toString();
        Object importExportCode1 = "";
        String importExportCode = importExportCode1.toString();
        if (ObjectUtils.isNotEmpty(industryInfoJsonObject)) {
            if (ObjectUtils.isNotEmpty(industryInfoJsonObject.get("曾用名"))) {
                Object oldEntNamesObject = industryInfoJsonObject.get("曾用名");
                if ("-".equals(oldEntNamesObject)) {
                    formerNames = new ArrayList<>();
                    enterprise.setFormerNames(StringUtils.join(formerNames,","));
                }

            }
            if (ObjectUtils.isNotEmpty(industryInfoJsonObject.get("法定代表人"))) {
                legalPerson = industryInfoJsonObject.getString("法定代表人");
                enterprise.setLegalPerson(legalPerson);
            } else if (ObjectUtils.isNotEmpty(industryInfoJsonObject.get("负责人"))) {
                legalPerson = industryInfoJsonObject.getString("负责人");
                enterprise.setLegalPerson(legalPerson);
            } else if (ObjectUtils.isNotEmpty(industryInfoJsonObject.get("投资人"))) {
                legalPerson = industryInfoJsonObject.getString("投资人");
                enterprise.setLegalPerson(legalPerson);
            }else if (ObjectUtils.isNotEmpty(industryInfoJsonObject.get("执行事务合伙人"))) {
                legalPerson = industryInfoJsonObject.getString("执行事务合伙人");
                enterprise.setLegalPerson(legalPerson);
            }

            if (ObjectUtils.isNotEmpty(industryInfoJsonObject.get("注册资本"))) {
                String regMoneyAll = industryInfoJsonObject.get("注册资本").toString();
                RegisterMoney = regMoneyAll.replaceAll("\\s*", "").replaceAll("[^(0-9).(0-9)*]", "");
                enterprise.setRegisterMoney(RegisterMoney);
                registerMoneyUnit = regMoneyAll.replaceAll("\\s*", "").replaceAll("[^(\\u4e00-\\u9fa5)]", "");
                enterprise.setRegisterMoneyUnit(registerMoneyUnit);
            }
            enterpriseStatus = industryInfoJsonObject.getString("登记状态");
            //企业状态处理
            if (StringUtils.isNotBlank(enterpriseStatus)) {
                enterpriseStatus = enterpriseStatus.replace("(", "（").replace(")", "）");
                enterpriseStatus = enterpriseStatus.replace(",", "，");
                enterprise.setEnterpriseStatus(enterpriseStatus);
            }
            registerDate = industryInfoJsonObject.getString("成立日期");
            enterprise.setRegisterDate(registerDate);
            registerNumber = industryInfoJsonObject.getString("工商注册号");
            if (ObjectUtils.isNotEmpty(registerNumber)) {
                if (!"-".equals(registerNumber)) {
                    if (registerNumber.contains("复制")) {
                        registerNumber = registerNumber.replace("复制", "");
                        enterprise.setRegisterNumber(registerNumber);
                    }
                }else {
                    registerNumber = "";
                    enterprise.setRegisterNumber(registerNumber);
                }
            }

            organizationCode = industryInfoJsonObject.getString("组织机构代码");
            if (ObjectUtils.isNotEmpty(organizationCode)) {
                if (organizationCode.contains("复制")) {
                    organizationCode = organizationCode.replace("复制", "");
                    enterprise.setOrganizationCode(organizationCode);
                }
            }
            creditCode = industryInfoJsonObject.getString("统一社会信用代码");
            if (ObjectUtils.isNotEmpty(creditCode)) {
                if (creditCode.contains("复制")) {
                    creditCode = creditCode.replace("复制", "");
                    enterprise.setCreditCode(creditCode);
                }
            }
            enterpriseType = industryInfoJsonObject.getString("企业类型");
            enterprise.setEnterpriseType(enterpriseType);
            taxpayerCode = industryInfoJsonObject.getString("纳税人识别号");
            if (ObjectUtils.isNotEmpty(taxpayerCode)) {
                if (taxpayerCode.contains("复制")) {
                    taxpayerCode = taxpayerCode.replace("复制", "");
                    enterprise.setTaxpayerCode(taxpayerCode);
                }
            }
            industry = industryInfoJsonObject.getString("所属行业");
            enterprise.setIndustry(industry);
            businessTerm = industryInfoJsonObject.getString("营业期限");
            enterprise.setBusinessTerm(businessTerm);
            checkDate = industryInfoJsonObject.getString("核准日期");
            enterprise.setCheckDate(checkDate);
            registAuthority = industryInfoJsonObject.getString("登记机关");
            enterprise.setRegisterAuthority(registAuthority);
//            if (!"-".equals(industryInfoJsonObject.getString("英文名"))) {
//                entNameEn = industryInfoJsonObject.getString("英文名");
//            }
            staffSize = industryInfoJsonObject.getString("编制人数");
            enterprise.setStaffSize(staffSize);
            insuredNumber = industryInfoJsonObject.getString("参保人数");
            enterprise.setInsuredNumber(insuredNumber);
            businessScope = industryInfoJsonObject.getString("经营范围");
            enterprise.setBusinessScope(businessScope);
            importExportCode = industryInfoJsonObject.getString("进出口企业代码");
            enterprise.setImportExportCode(importExportCode);
        }
        //处理字符重复
        String farenSubstring = "";
        if (ObjectUtils.isNotEmpty(legalPerson)){
            String farenString = legalPerson.toString();
            String substringFirst = farenString.substring(0,1);
            String substringSecond = farenString.substring(1,2);
            if (substringFirst.equals(substringSecond)) {
                farenSubstring = farenString.substring(1);
            }else {
                farenSubstring = farenString;
            }
        }



        String tags = "";
        if (ObjectUtils.isNotEmpty(headInfoJsonObject)) {

            tags = headInfoJsonObject.getString("tags");
            enterprise.setTags(tags);
            enterprise.setAddress(headInfoJsonObject.getString("address"));
  //          enterprise.setBusinessScope(doc.getString("businessScope"));
            String phone = (String)headInfoJsonObject.get("phone");
            if (StringUtils.isNotBlank(phone) && phone.contains("*")) {
                phone = "";
                enterprise.setPhoneNumbers(phone);
            }
  //          enterprise.setPhoneNumbers(headInfoJsonObject.getString("phoneNumbers"));
            String email = (String)headInfoJsonObject.get("email");
            if (StringUtils.isNotBlank(email) && email.contains("*")) {
                email = "";
                enterprise.setEmail(email);
            }
    //        enterprise.setEmail(headInfoJsonObject.getString("email"));
            website = industryInfoJsonObject.getString("网址");
            enterprise.setWebsite(website);
            introduce = industryInfoJsonObject.getString("介绍");
            enterprise.setIntroduce(introduce);
            address = industryInfoJsonObject.getString("地址");
            enterprise.setAddress(address);



        }


   //     String finalEntName = entPriseName;
        enterprise.setHighTechFlag(false);
 //       enterprise.put("zgcGaoXin", false);
        enterprise.setGoldenSeedFlag(false);
        enterprise.setGazelleFlag(false);
        enterprise.setUnicornFlag(false);
        enterprise.setPerfectSpecialNewFlag(false);

        //根据标签判断高企
        if (StringUtils.isNotBlank(tags)) {
            if (tags.contains("高新")) {
                enterprise.setHighTechFlag(true);
            }
            if (tags.contains("金种子")) {
                enterprise.setGoldenSeedFlag(true);
            }
            if (tags.contains("瞪羚")) {
                enterprise.setGazelleFlag(true);
            }
            if (tags.contains("独角兽")) {
                enterprise.setUnicornFlag(true);
            }
            if (tags.contains("专精特新")) {
                enterprise.setPerfectSpecialNewFlag(true);
            }
        }

        //商标
        enterprise.setBrandCount(doc.getInteger("brandCount"));
        //专利
        enterprise.setPatentCount(doc.getInteger("patentCount"));
        //软件著作权
        enterprise.setSoftwareCopyrightCount(doc.getInteger("softwareCopyrightCount"));
        //著作权
        enterprise.setWorkCopyrightCount(doc.getInteger("workCopyrightCount"));
        //资质证书
        Object entCertificateObject = doc.get("ent_certificate");
//        JSONArray entCertificateJSONArray = new JSONArray();
//        if (ObjectUtils.isNotEmpty(entCertificateObject)) {
//            entCertificateJSONArray = JSON.parseArray(JSON.toJSONString(entCertificateObject));
//        }
 //       enterprise.put("certificateCount", entCertificateJSONArray.size());
        //投资
        Object invEventObject = doc.get("entPatent");
        JSONArray invEventJSONArray = new JSONArray();
//        if (ObjectUtils.isNotEmpty(invEventObject)) {
//            invEventJSONArray = JSON.parseArray(JSON.toJSONString(invEventObject));
//        }
//        enterprise.put("invEventCount", invEventJSONArray.size());
        //融资
        Object financeObject = doc.get("inv_event");
        JSONArray financeJSONArray = new JSONArray();
        //融资轮次
        List<String> financeRounds = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(financeObject)) {
            financeJSONArray = JSON.parseArray(JSON.toJSONString(financeObject));
            financeJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                financeRounds.add(itemJSONObject.getString("stage"));
            });
        }
 //       enterprise.put("financeCount", financeJSONArray.size());
        //融资轮次
        enterprise.setFinanceRounds(doc.getString("financeRounds"));

        //裁判文书
//        Object judgmentDocumentObject = doc.get("judgment_document");
//        JSONArray judgmentDocumentJSONArray = new JSONArray();
//        if (ObjectUtils.isNotEmpty(judgmentDocumentObject)) {
//            judgmentDocumentJSONArray = JSON.parseArray(JSON.toJSONString(judgmentDocumentObject));
//        }
 //       enterprise.put("judgmentDocumentCount", judgmentDocumentJSONArray.size());

        return enterprise;
    }

    //商标

    public static List<EntPriseBrand> brandSpiderMongoToMongo(Document doc) {

        Object entBrandObject = doc.get("ent_brand");
        List<EntPriseBrand> entBrandList = new ArrayList<>();

        if (ObjectUtils.isNotEmpty(entBrandObject)) {
            JSONArray entBrandJSONArray = JSON.parseArray(JSON.toJSONString(entBrandObject));
            entBrandJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                EntPriseBrand entBrand = new EntPriseBrand();
                entBrand.setEntId(doc.getString("entId"));
                entBrand.setBrandName(itemJSONObject.getString("brandName"));
                try {
                    entBrand.setApplyDate(itemJSONObject.getDate("applyDate"));
                } catch (Exception e) {
                    if ("-".equals(itemJSONObject.getString("applyDate"))) {
                        entBrand.setApplyDate(null);
                    }
                }

                entBrand.setRegCode(itemJSONObject.getString("regCode"));
                entBrand.setIntCls(itemJSONObject.getInteger("intCls"));
                entBrand.setBrandType(itemJSONObject.getString("brandType"));
                entBrand.setBrandStatus(itemJSONObject.getString("brandStatus"));
                entBrand.setBrandImag(itemJSONObject.getString("brandImag"));
                entBrandList.add(entBrand);
            });
        }
        return entBrandList;
    }

    //专利
    public static List<EntPrisePatent> patentSpiderMongoToMongo(Document doc) {
        Object entPatentObject = doc.get("ent_patent");
        List<EntPrisePatent> entPatentList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(entPatentObject)) {
            JSONArray entPatentJSONArray = JSON.parseArray(JSON.toJSONString(entPatentObject));
            entPatentJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                EntPrisePatent entPatent = new EntPrisePatent();
                entPatent.setEntId(doc.getString("entId"));
                entPatent.setPatentName( itemJSONObject.getString("patentName"));
                entPatent.setPatentId( itemJSONObject.getString("patentId"));
                entPatent.setPatentType(itemJSONObject.getString("patentType"));
                entPatent.setPatentStatus( itemJSONObject.getString("patentStatus"));
                try{
                    entPatent.setRegDate(itemJSONObject.getDate("regDate"));
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("regDate"))) {
                        entPatent.setRegDate(null);
                    }
                }
                try{
                    entPatent.setOpenDate(itemJSONObject.getDate("openDate"));
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("openDate"))) {
                        entPatent.setOpenDate(null);
                    }
                }
                entPatent.setOpenNo(itemJSONObject.getString("openNo"));
                entPatentList.add(entPatent);
            });
        }
        return entPatentList;
    }

    //软件著作权
    public static List<EntPriseSoftCopyRight> softCopyrightSpiderMongoToMongo(Document doc) {
        Object entSoftCopyrightObject = doc.get("ent_softcopyright");
        List<EntPriseSoftCopyRight> entSoftCopyrightList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(entSoftCopyrightObject)) {
            JSONArray entSoftCopyrightJSONArray = JSON.parseArray(JSON.toJSONString(entSoftCopyrightObject));
            entSoftCopyrightJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                EntPriseSoftCopyRight entSoftCopyright = new EntPriseSoftCopyRight();
                entSoftCopyright.setEntId(doc.getString("entId"));
                entSoftCopyright.setSoftName(itemJSONObject.getString("softName"));
                try{
                    entSoftCopyright.setRegDate(itemJSONObject.getDate("regDate"));
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("regDate"))) {
                        entSoftCopyright.setRegDate(null);
                    }
                }
                entSoftCopyright.setRegNo(itemJSONObject.getString("regNo"));
                entSoftCopyright.setSoftEdition(itemJSONObject.getString("softEdition"));
                String softAbb = itemJSONObject.getString("softAbb");
                if (!"-".equals(softAbb)) {
                    entSoftCopyright.setSoftAbb(softAbb);
                }
                try{
                    entSoftCopyright.setNoticeDate(itemJSONObject.getDate("noticeDate"));
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("noticeDate"))) {
                        entSoftCopyright.setNoticeDate(null);
                    }
                }
                entSoftCopyrightList.add(entSoftCopyright);
            });
        }
        return entSoftCopyrightList;
    }

    //著作权
    public static List<EntPriseCopyRight> copyrightSpiderMongoToMongo(Document doc) {

        Object enCopyrightObject = doc.get("ent_copyright");
        List<EntPriseCopyRight> enCopyrightList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(enCopyrightObject)) {
            JSONArray enCopyrightJSONArray = JSON.parseArray(JSON.toJSONString(enCopyrightObject));
            enCopyrightJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                EntPriseCopyRight enCopyright = new EntPriseCopyRight();
                enCopyright.setEntId(doc.getString("entId"));
                enCopyright.setProductName(itemJSONObject.getString("productName"));
                try{
                    enCopyright.setRegDate(itemJSONObject.getDate("regDate"));
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("regDate"))) {
                        enCopyright.setRegDate(null);
                    }
                }
                enCopyright.setRegNo(itemJSONObject.getString("regNo"));
                try{
                    enCopyright.setCreateDate(itemJSONObject.getDate("createDate"));
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("createDate"))) {
                        enCopyright.setCreateDate(null);
                    }
                }
                enCopyright.setProductType(itemJSONObject.getString("productType"));
                try{
                    enCopyright.setPublishDate(itemJSONObject.getDate("publishDate"));
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("publishDate"))) {
                        enCopyright.setPublishDate(null);
                    }
                }
                enCopyrightList.add(enCopyright);
            });
        }
        return enCopyrightList;
    }

    //资质证书
    public static List<EntPriseCertificate> certificateSpiderMongoToMongo(Document doc) {

        Object entCertificateObject = doc.get("ent_certificate");
        List<EntPriseCertificate> entCertificateList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(entCertificateObject)) {
            JSONArray entCertificateJSONArray = JSON.parseArray(JSON.toJSONString(entCertificateObject));
            entCertificateJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                EntPriseCertificate entCertificate = new EntPriseCertificate();
                entCertificate.setEntId(doc.getString("entId"));
                entCertificate.setCertificateType(itemJSONObject.getString("certificateType"));
                String certificateName = itemJSONObject.getString("certificateName");
                if (!"-".equals(certificateName)) {
                    entCertificate.setCertificateName(certificateName);
                }
                entCertificate.setCertificateCode(itemJSONObject.getString("certificateCode"));
                try{
                    entCertificate.setCreateDate(itemJSONObject.getDate("createDate"));
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("createDate"))) {
                        entCertificate.setCreateDate(null);
                    }
                }
                try{
                    entCertificate.setEndDate(itemJSONObject.getDate("endData"));
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("stopData"))) {
                        entCertificate.setEntId(null);
                    }
                }
                entCertificateList.add(entCertificate);
            });
        }

        return entCertificateList;
    }

    //投资
    public static List<InvEvent> investSpiderMongoToMongo(Document doc) {

        Object invEventObject = doc.get("entPatent");
        List<InvEvent> invEventList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(invEventObject)) {
            JSONArray invEventJSONArray = JSON.parseArray(JSON.toJSONString(invEventObject));
            invEventJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                InvEvent invEvent = new InvEvent();
                invEvent.setEntId(doc.getString("entId"));
                String entName = itemJSONObject.getString("entName");
                if (!"-".equals(entName)) {
                    invEvent.setInvestedName(entName);
                }
                invEvent.setInvestedName(itemJSONObject.getString("investedName"));
                invEvent.setLegalPerson(itemJSONObject.getString("legalPerson"));
                String investmentAmount = itemJSONObject.getString("investmentAmount");
                if (!"-".equals(investmentAmount)) {
                    invEvent.setInvestmentAmount(investmentAmount);
                }
                String investmentProportion = itemJSONObject.getString("investmentProportion");
                if (!"-".equals(investmentProportion)) {
                    invEvent.setInvestmentProportion(investmentProportion);
                }
                invEventList.add(invEvent);
            });
        }

        return invEventList;
    }
    //融资
    public static List<Finance> financeSpiderMongoToMongo(Document doc) {

        Object financeObject = doc.get("inv_event");
        List<Finance> financeList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(financeObject)) {
            JSONArray financeJSONArray = JSON.parseArray(JSON.toJSONString(financeObject));
            financeJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                Finance finance = new Finance();
                finance.setEntId(doc.getString("entId"));
                finance.setEntPriseName(itemJSONObject.getString("entName"));
                try{
                    finance.setFinanceDate(itemJSONObject.getDate("financeDate"));
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("financeRounds"))) {
                        finance.setFinanceRounds(null);
                    }
                }
      //          finance.put("stage", itemJSONObject.getString("stage"));
                finance.setFinanceAmount(itemJSONObject.getString("financeAmount"));
                finance.setInvestor(itemJSONObject.getString("investor"));
                financeList.add(finance);
            });
        }

        return financeList;
    }
    //裁判文书
    public static List<Judgment> judgmentSpiderMongoToMongo(Document doc) {

        Object judgmentDocumentObject = doc.get("judgment_document");
        List<Judgment> judgmentDocumentList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(judgmentDocumentObject)) {
            JSONArray judgmentDocumentJSONArray = JSON.parseArray(JSON.toJSONString(judgmentDocumentObject));
            judgmentDocumentJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                Judgment judgmentDocument = new Judgment();
                judgmentDocument.setEntId(doc.getString("entId"));
                judgmentDocument.setCaseName(itemJSONObject.getString("caseName"));
                judgmentDocument.setCaseCause(itemJSONObject.getString("caseCause"));
                judgmentDocument.setCaseNo(itemJSONObject.getString("caseNo"));
                String plaintiffDefendant = itemJSONObject.getString("plaintiff_defendant");
                if (!"-".equals(plaintiffDefendant)) {
                    judgmentDocument.setPlaintiffDefendant(plaintiffDefendant);
                }
                String result = itemJSONObject.getString("result");
                if (!"-".equals(result)) {
                    judgmentDocument.setResult(result);
                }
                try{
                    judgmentDocument.setCaseDate(itemJSONObject.getDate("caseDate"));
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("caseDate"))) {
                        judgmentDocument.setCaseDate(null);
                    }
                }
                try{
                    judgmentDocument.setNoticeDate(itemJSONObject.getDate("noticeDate"));
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("noticeDate"))) {
                        judgmentDocument.setNoticeDate(null);
                    }
                }
                judgmentDocument.setContent(itemJSONObject.getString("content"));
                judgmentDocumentList.add(judgmentDocument);
            });
        }

        return judgmentDocumentList;
    }
}
