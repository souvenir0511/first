package com.zgg.batch.enterprise.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.db.Entity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zgg.batch.enterprise.entity.*;
import com.zgg.batch.entity.Enterprise2;
import com.zgg.batch.entity.EnterpriseExpand;
import com.zgg.batch.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
public class DocumentToBeanUtil {

    public static Document spiderMongoToMongo(Document doc, List<Entity> entityList){
        Document enterprise = new Document();
        Object industryInfo = doc.get("industry_info");
        JSONObject industryInfoJsonObject = JSON.parseObject(JSON.toJSONString(industryInfo));

        Object head_info = doc.get("head_info");
        JSONObject headInfoJsonObject = JSON.parseObject(JSON.toJSONString(head_info));

        enterprise.put("_id", doc.getString("_id"));
        enterprise.put("entId", doc.getString("_id"));
        enterprise.put("oldId", doc.getString("qcc_id"));
        enterprise.put("orgLogo", "");
        String entName = doc.getString("company");
        entName = entName.replace("(", "（").replace(")", "）");
        enterprise.put("entName", entName);

        List<String> oldEntNames = null;
        String faRen = "";
        String regMoney = "";
        String regMoneyUnit = "";
        String entStatus = "";
        String regDate = "";
        String regNumber = "";
        String entCode = "";
        String creditCode = "";
        String entType = "";
        String taxpayerCode = "";
        String importEntCode = "";
        String industry = "";
        String businessTerm = "";
        String checkDate = "";
        String registAuthority = "";
        String entNameEn = "";
        String engagedNumber = "";
        String insuredNumber = "";
        //String address = "";
        Object experienceScope = "";
        Object exportImportEnterpriseCode = "";
        if (ObjectUtils.isNotEmpty(industryInfoJsonObject)) {
            if (ObjectUtils.isNotEmpty(industryInfoJsonObject.get("曾用名"))) {
                Object oldEntNamesObject = industryInfoJsonObject.get("曾用名");
                if ("-".equals(oldEntNamesObject)) {
                    oldEntNames = new ArrayList<>();
                }
            }
            if (ObjectUtils.isNotEmpty(industryInfoJsonObject.get("法定代表人"))) {
                faRen = industryInfoJsonObject.getString("法定代表人");
            } else if (ObjectUtils.isNotEmpty(industryInfoJsonObject.get("负责人"))) {
                faRen = industryInfoJsonObject.getString("负责人");
            } else if (ObjectUtils.isNotEmpty(industryInfoJsonObject.get("投资人"))) {
                faRen = industryInfoJsonObject.getString("投资人");
            }else if (ObjectUtils.isNotEmpty(industryInfoJsonObject.get("执行事务合伙人"))) {
                faRen = industryInfoJsonObject.getString("执行事务合伙人");
            }
            if (ObjectUtils.isNotEmpty(industryInfoJsonObject.get("注册资本"))) {
                String regMoneyAll = industryInfoJsonObject.get("注册资本").toString();
                regMoney = regMoneyAll.replaceAll("\\s*", "").replaceAll("[^(0-9).(0-9)*]", "");
                regMoneyUnit = regMoneyAll.replaceAll("\\s*", "").replaceAll("[^(\\u4e00-\\u9fa5)]", "");
            }
            entStatus = industryInfoJsonObject.getString("登记状态");
            //企业状态处理
            if (StringUtils.isNotBlank(entStatus)) {
                entStatus = entStatus.replace("(", "（").replace(")", "）");
                entStatus = entStatus.replace(",", "，");
            }
            regDate = industryInfoJsonObject.getString("成立日期");
            regNumber = industryInfoJsonObject.getString("工商注册号");
            if (ObjectUtils.isNotEmpty(regNumber)) {
                if (!"-".equals(regNumber)) {
                    if (regNumber.contains("复制")) {
                        regNumber = regNumber.replace("复制", "");
                    }
                }else {
                    regNumber = "";
                }
            }

            entCode = industryInfoJsonObject.getString("组织机构代码");
            if (ObjectUtils.isNotEmpty(entCode)) {
                if (entCode.contains("复制")) {
                    entCode = entCode.replace("复制", "");
                }
            }
            creditCode = industryInfoJsonObject.getString("统一社会信用代码");
            if (ObjectUtils.isNotEmpty(creditCode)) {
                if (creditCode.contains("复制")) {
                    creditCode = creditCode.replace("复制", "");
                }
            }
            entType = industryInfoJsonObject.getString("企业类型");
            taxpayerCode = industryInfoJsonObject.getString("纳税人识别号");
            if (ObjectUtils.isNotEmpty(taxpayerCode)) {
                if (taxpayerCode.contains("复制")) {
                    taxpayerCode = taxpayerCode.replace("复制", "");
                }
            }
            industry = industryInfoJsonObject.getString("所属行业");
            businessTerm = industryInfoJsonObject.getString("营业期限");
            checkDate = industryInfoJsonObject.getString("核准日期");
            registAuthority = industryInfoJsonObject.getString("登记机关");
            if (!"-".equals(industryInfoJsonObject.getString("英文名"))) {
                entNameEn = industryInfoJsonObject.getString("英文名");
            }
            insuredNumber = industryInfoJsonObject.getString("参保人数");
            experienceScope = industryInfoJsonObject.get("经营范围");
            exportImportEnterpriseCode = industryInfoJsonObject.get("进出口企业代码");
        }
        //处理字符重复
        String farenSubstring = "";
        if (ObjectUtils.isNotEmpty(faRen)){
            String farenString = faRen.toString();
            String substringFirst = farenString.substring(0,1);
            String substringSecond = farenString.substring(1,2);
            if (substringFirst.equals(substringSecond)) {
                farenSubstring = farenString.substring(1);
            }else {
                farenSubstring = farenString;
            }
        }
        enterprise.put("oldEntNames", oldEntNames);
        enterprise.put("faRen", farenSubstring);
        enterprise.put("faRenImage", "");
        enterprise.put("regMoney", regMoney);
        enterprise.put("regMoneyUnit", regMoneyUnit);
        enterprise.put("paidInMoney", "");
        enterprise.put("paidInMoneyUnit", "");
        enterprise.put("entStatus", entStatus);
        enterprise.put("regDate", regDate);
        enterprise.put("regNumber", regNumber);
        enterprise.put("entCode", entCode);
        enterprise.put("creditCode", creditCode);
        enterprise.put("entType", entType);
        enterprise.put("taxpayerCode", taxpayerCode);
        enterprise.put("importEntCode", importEntCode);
        enterprise.put("industry", industry);
        enterprise.put("businessTerm", businessTerm);
        enterprise.put("checkDate", checkDate);
        enterprise.put("registAuthority", registAuthority);
        enterprise.put("entNameEn", entNameEn);
        enterprise.put("engagedNumber", engagedNumber);
        enterprise.put("insuredNumber", insuredNumber);
        enterprise.put("exportImportEnterpriseCode", exportImportEnterpriseCode);
        String province = "";
        String city = "";
        String area = "";
        String tags = "";
        if (ObjectUtils.isNotEmpty(headInfoJsonObject)) {

            tags = headInfoJsonObject.getString("tags");
            enterprise.put("address", headInfoJsonObject.get("addr"));
            enterprise.put("experienceScope", experienceScope);
            String phone = (String)headInfoJsonObject.get("phone");
            if (StringUtils.isNotBlank(phone) && phone.contains("*")) {
                phone = "";
            }
            enterprise.put("tel", phone);
            String email = (String)headInfoJsonObject.get("email");
            if (StringUtils.isNotBlank(email) && email.contains("*")) {
                email = "";
            }
            enterprise.put("email", email);
            enterprise.put("website", headInfoJsonObject.get("website"));
            enterprise.put("introduce", headInfoJsonObject.get("desc"));
            String addr = (String)headInfoJsonObject.get("addr");
            if (StringUtils.isNotBlank(addr)) {
                int index = addr.indexOf("市");
                String substring = addr.substring(0, index + 1);
                if(StringUtils.isNotEmpty(addr) && !addr.endsWith("省") && !"北京市".equalsIgnoreCase(addr) && !"天津市".equalsIgnoreCase(addr) && !"上海市".equalsIgnoreCase(addr) && !"重庆市".equalsIgnoreCase(addr)
                        && !"北京".equalsIgnoreCase(addr) && !"天津".equalsIgnoreCase(addr) && !"上海".equalsIgnoreCase(addr) && !"重庆".equalsIgnoreCase(addr)){
                    addr = substring+addr;
                }
                List<Map<String, String>> mapList = CommonUtil.addressResolution(addr);

                if (!CollectionUtils.isEmpty(mapList)) {
                    Map<String, String> map = mapList.get(0);
                    province = map.get("province");
                    city = map.get("city");
                    area = map.get("area");
                }
            }

        }
        enterprise.put("province", province);
        enterprise.put("city", city);
        enterprise.put("area", area);
        enterprise.put("areaCode", "");
        enterprise.put("lat", "");
        enterprise.put("lon", "");
        enterprise.put("snapshotImag", "");
        enterprise.put("faRenType", null);
        enterprise.put("faRenEntId", null);
        enterprise.put("industrySet", new ArrayList<>());
        enterprise.put("gaoXinExpireDate", null);
        enterprise.put("gaoXinStartYear", null);
        enterprise.put("listedDate","");
        enterprise.put("listedYear","");
        enterprise.put("industryPhy", null);
        enterprise.put("industryBig", null);
        enterprise.put("industryCode", null);

        String finalEntName = entName;
        enterprise.put("gjGaoXin", false);
        enterprise.put("zgcGaoXin", false);
        enterprise.put("jinZhongZi", false);
        enterprise.put("dengLingYang", false);
        enterprise.put("duJiaoShou", false);
        enterprise.put("jinZhuanTeXin", false);

        //本地数据库判断高企
        if (!CollectionUtils.isEmpty(entityList)) {
            entityList.forEach(item -> {
                if (item.getStr("enterprise_name").equals(finalEntName)) {
                    switch (item.getStr("type")) {
                        case "高新技术企业":
//                            enterprise.put("gjGaoXin", true);
                            break;
                        case "中关村高新技术企业":
                            enterprise.put("zgcGaoXin", true);
                            break;
                        case "金种子企业":
//                            enterprise.put("jinZhongZi", true);
                            break;
                        case "瞪羚":
//                            enterprise.put("dengLingYang", true);
                            break;
                        case "独角兽":
//                            enterprise.put("duJiaoShou", true);
                            break;
                        case "精专特新企业":
//                            enterprise.put("jinZhuanTeXin", true);
                            break;
                    }
                }
            });
        }

        //根据标签判断高企
        if (StringUtils.isNotBlank(tags)) {
            if (tags.contains("高新")) {
                enterprise.put("gjGaoXin", true);
            }
            if (tags.contains("金种子")) {
                enterprise.put("jinZhongZi", true);
            }
            if (tags.contains("瞪羚")) {
                enterprise.put("dengLingYang", true);
            }
            if (tags.contains("独角兽")) {
                enterprise.put("duJiaoShou", true);
            }
            if (tags.contains("专精特新")) {
                enterprise.put("jinZhuanTeXin", true);
            }
        }

        //商标
        enterprise.put("brandCount", doc.getInteger("ent_brand_count"));
        //专利
        enterprise.put("patentCount", doc.getInteger("ent_patent_count"));
        //软件著作权
        enterprise.put("softwareCopyrightCount", doc.getInteger("ent_softcopyright_count"));
        //著作权
        enterprise.put("copyrightCount", doc.getInteger("ent_copyright_count"));
        //资质证书
        Object entCertificateObject = doc.get("ent_certificate");
        JSONArray entCertificateJSONArray = new JSONArray();
        if (ObjectUtils.isNotEmpty(entCertificateObject)) {
            entCertificateJSONArray = JSON.parseArray(JSON.toJSONString(entCertificateObject));
        }
        enterprise.put("certificateCount", entCertificateJSONArray.size());
        //投资
        Object invEventObject = doc.get("entPatent");
        JSONArray invEventJSONArray = new JSONArray();
        if (ObjectUtils.isNotEmpty(invEventObject)) {
            invEventJSONArray = JSON.parseArray(JSON.toJSONString(invEventObject));
        }
        enterprise.put("invEventCount", invEventJSONArray.size());
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
        enterprise.put("financeCount", financeJSONArray.size());
        //融资轮次
        enterprise.put("financeRounds", StringUtils.join(financeRounds, "，"));

        //裁判文书
        Object judgmentDocumentObject = doc.get("judgment_document");
        JSONArray judgmentDocumentJSONArray = new JSONArray();
        if (ObjectUtils.isNotEmpty(judgmentDocumentObject)) {
            judgmentDocumentJSONArray = JSON.parseArray(JSON.toJSONString(judgmentDocumentObject));
        }
        enterprise.put("judgmentDocumentCount", judgmentDocumentJSONArray.size());

        return enterprise;
    }

    public static Enterprise2 spiderMongoToMysqlEnt(Document doc, List<Entity> entityList){
        Object industryInfo = doc.get("industry_info");
        JSONObject industryInfoJsonObject = JSON.parseObject(JSON.toJSONString(industryInfo));

        Object head_info = doc.get("head_info");
        JSONObject headInfoJsonObject = JSON.parseObject(JSON.toJSONString(head_info));

        Enterprise2 enterprise = new Enterprise2();
        enterprise.setEntId(doc.getString("_id"));
        String entName = doc.getString("company");
        entName = entName.replace("(", "（").replace(")", "）");
        enterprise.setEntName(entName);

        List<String> oldEntNames = null;
        Object faRen = "";
        Object regMoney = "";
        Object regMoneyUnit = "";
        Object entStatus = "";
        Object regDate = "";
        String regNumber = "";
        String entCode = "";
        String creditCode = "";
        Object entType = "";
        String taxpayerCode = "";
        Object importEntCode = "";
        Object industry = "";
        Object businessTerm = "";
        Object checkDate = "";
        Object registAuthority = "";
        Object entNameEn = "";
        Object engagedNumber = "";
        Object insuredNumber = "";
        //String address = "";
        Object experienceScope = "";
        if (ObjectUtils.isNotEmpty(industryInfoJsonObject)) {
            if (ObjectUtils.isNotEmpty(industryInfoJsonObject.get("曾用名"))) {
                Object oldEntNamesObject = industryInfoJsonObject.get("曾用名");
                if ("-".equals(oldEntNamesObject)) {
                    oldEntNames = new ArrayList<>();
                }
            }

            if (ObjectUtils.isNotEmpty(industryInfoJsonObject.get("法定代表人"))) {
                faRen = industryInfoJsonObject.get("法定代表人");
            } else if (ObjectUtils.isNotEmpty(industryInfoJsonObject.get("负责人"))) {
                faRen = industryInfoJsonObject.get("负责人");
            } else if (ObjectUtils.isNotEmpty(industryInfoJsonObject.get("投资人"))) {
                faRen = industryInfoJsonObject.get("投资人");
            }else if (ObjectUtils.isNotEmpty(industryInfoJsonObject.get("执行事务合伙人"))) {
                faRen = industryInfoJsonObject.get("执行事务合伙人");
            }
            if (ObjectUtils.isNotEmpty(industryInfoJsonObject.get("注册资本"))) {
                String regMoneyAll = industryInfoJsonObject.get("注册资本").toString();
                regMoney = regMoneyAll.replaceAll("\\s*", "").replaceAll("[^(0-9).(0-9)*]", "");
                regMoneyUnit = regMoneyAll.replaceAll("\\s*", "").replaceAll("[^(\\u4e00-\\u9fa5)]", "");
            }
            entStatus = industryInfoJsonObject.get("登记状态");
            regDate = industryInfoJsonObject.get("成立日期");
            regNumber = industryInfoJsonObject.getString("工商注册号");
            if (ObjectUtils.isNotEmpty(regDate)) {
                if (!"-".equals(regNumber)) {
                    if (regNumber.contains("复制")) {
                        regNumber = regNumber.replace("复制", "");
                    }
                }else {
                    regNumber = "";
                }
            }
            entCode = industryInfoJsonObject.getString("组织机构代码");
            if (ObjectUtils.isNotEmpty(entCode)) {

                if (entCode.contains("复制")) {
                    entCode = entCode.replace("复制", "");
                }
            }
            creditCode = industryInfoJsonObject.getString("统一社会信用代码");
            if (ObjectUtils.isNotEmpty(creditCode)) {
                if (creditCode.contains("复制")) {
                    creditCode = creditCode.replace("复制", "");
                }
            }
            entType = industryInfoJsonObject.get("企业类型");
            taxpayerCode = industryInfoJsonObject.getString("纳税人识别号");
            if (ObjectUtils.isNotEmpty(taxpayerCode)) {
                if (taxpayerCode.contains("复制")) {
                    taxpayerCode = taxpayerCode.replace("复制", "");
                }
            }
            industry = industryInfoJsonObject.get("所属行业");
            businessTerm = industryInfoJsonObject.get("营业期限");
            checkDate = industryInfoJsonObject.get("核准日期");
            registAuthority = industryInfoJsonObject.get("登记机关");
            if (!"-".equals(industryInfoJsonObject.get("英文名"))) {
                entNameEn = industryInfoJsonObject.get("英文名");
            }
            insuredNumber = industryInfoJsonObject.get("参保人数");
            experienceScope = industryInfoJsonObject.get("经营范围");
        }
        enterprise.setOldEntNames(CollectionUtils.isEmpty(oldEntNames)?"":String.join(",",oldEntNames));
        //处理字符重复
        String farenSubstring = "";
        if (ObjectUtils.isNotEmpty(faRen)){
            String farenString = faRen.toString();
            String substringFirst = farenString.substring(0,1);
            String substringSecond = farenString.substring(1,2);
            if (substringFirst.equals(substringSecond)) {
                farenSubstring = farenString.substring(1);
            }else {
                farenSubstring = farenString;
            }
        }
        enterprise.setFaRen(farenSubstring);
        enterprise.setRegMoney(ObjectUtils.isNotEmpty(regMoney)?regMoney.toString()+regMoneyUnit:"");
        enterprise.setEntStatus(ObjectUtils.isNotEmpty(entStatus)?entStatus.toString():"");
        enterprise.setRegDate(ObjectUtils.isNotEmpty(regDate)?regDate.toString():"");
        enterprise.setCreditCode(ObjectUtils.isNotEmpty(creditCode)?creditCode.toString():"");
        enterprise.setEntType(ObjectUtils.isNotEmpty(entType)?entType.toString():"");
        enterprise.setIndustry(ObjectUtils.isNotEmpty(industry)?industry.toString():"");
        enterprise.setExperienceScope(ObjectUtils.isNotEmpty(experienceScope)?experienceScope.toString():"");
        String tags = "";
        if (ObjectUtils.isNotEmpty(headInfoJsonObject)) {

            tags = headInfoJsonObject.getString("tags");
            String phone = headInfoJsonObject.getString("phone");
            if (StringUtils.isNotBlank(phone) && phone.contains("*")) {
                phone = "";
            }
            String email = headInfoJsonObject.getString("email");
            if (StringUtils.isNotBlank(email) && email.contains("*")) {
                email = "";
            }

            enterprise.setIntroduce(headInfoJsonObject.getString("desc"));
            String addr = headInfoJsonObject.getString("addr");
            if (StringUtils.isNotBlank(addr)) {
                int index = addr.indexOf("市");
                String substring = addr.substring(0, index + 1);
                if(StringUtils.isNotEmpty(addr) && !addr.endsWith("省") && !"北京市".equalsIgnoreCase(addr) && !"天津市".equalsIgnoreCase(addr) && !"上海市".equalsIgnoreCase(addr) && !"重庆市".equalsIgnoreCase(addr)
                        && !"北京".equalsIgnoreCase(addr) && !"天津".equalsIgnoreCase(addr) && !"上海".equalsIgnoreCase(addr) && !"重庆".equalsIgnoreCase(addr)){
                    addr = substring+addr;
                }
                List<Map<String, String>> mapList = CommonUtil.addressResolution(addr);
                String province = "";
                String city = "";
                String area = "";
                if (!CollectionUtils.isEmpty(mapList)) {
                    Map<String, String> map = mapList.get(0);
                    province = map.get("province");
                    city = map.get("city");
                    area = map.get("area");
                }
            }
        }

        enterprise.setGjGaoXin(0);
        enterprise.setZgcGaoXin(0);
        enterprise.setJinZhongZi(0);
        enterprise.setDengLingYang(0);
        enterprise.setDuJiaoShou(0);
        enterprise.setJinZhuanTeXin(0);
        if (!CollectionUtils.isEmpty(entityList)) {
            entityList.forEach(item -> {
                if (item.getStr("enterprise_name").equals(enterprise.getEntName())) {
                    switch (item.getStr("type")) {
                        case "高新技术企业":
//                            enterprise.setGjGaoXin(1);
                            break;
                        case "中关村高新技术企业":
                            enterprise.setZgcGaoXin(1);
                            break;
                        case "金种子企业":
//                            enterprise.setJinZhongZi(1);
                            break;
                        case "瞪羚":
//                            enterprise.setDengLingYang(1);
                            break;
                        case "独角兽":
//                            enterprise.setDuJiaoShou(1);
                            break;
                        case "精专特新企业":
//                            enterprise.setJinZhuanTeXin(1);
                            break;
                    }
                }
            });
        }

        //根据标签判断高企
        if (StringUtils.isNotBlank(tags)) {
            if (tags.contains("高新")) {
                enterprise.setGjGaoXin(1);
            }
            if (tags.contains("金种子")) {
                enterprise.setJinZhongZi(1);
            }
            if (tags.contains("瞪羚")) {
                enterprise.setDengLingYang(1);
            }
            if (tags.contains("独角兽")) {
                enterprise.setDuJiaoShou(1);
            }
            if (tags.contains("专精特新")) {
                enterprise.setJinZhuanTeXin(1);
            }
        }

        //商标
        enterprise.setBrandCount(doc.getInteger("ent_brand_count"));
        //专利
        enterprise.setPatentCount(doc.getInteger("ent_patent_count"));
        //软件著作权
        enterprise.setSoftwareCopyrightCount(doc.getInteger("ent_softcopyright_count"));
        //著作权
        enterprise.setCopyrightCount(doc.getInteger("ent_copyright_count"));
        //资质证书
        Object entCertificateObject = doc.get("ent_certificate");
        JSONArray entCertificateJSONArray = new JSONArray();
        if (ObjectUtils.isNotEmpty(entCertificateObject)) {
            entCertificateJSONArray = JSON.parseArray(JSON.toJSONString(entCertificateObject));
        }
        enterprise.setCertificateCount(entCertificateJSONArray.size());
        //投资
        Object invEventObject = doc.get("entPatent");
        JSONArray invEventJSONArray = new JSONArray();
        if (ObjectUtils.isNotEmpty(invEventObject)) {
            invEventJSONArray = JSON.parseArray(JSON.toJSONString(invEventObject));
        }
        enterprise.setInvEventCount(invEventJSONArray.size());
        //融资
        Object financeObject = doc.get("inv_event");
        JSONArray financeJSONArray = new JSONArray();
        if (ObjectUtils.isNotEmpty(financeObject)) {
            financeJSONArray = JSON.parseArray(JSON.toJSONString(financeObject));
        }
        enterprise.setFinanceCount(financeJSONArray.size());
        //裁判文书
        Object judgmentDocumentObject = doc.get("judgment_document");
        JSONArray judgmentDocumentJSONArray = new JSONArray();
        if (ObjectUtils.isNotEmpty(judgmentDocumentObject)) {
            judgmentDocumentJSONArray = JSON.parseArray(JSON.toJSONString(judgmentDocumentObject));
        }
        enterprise.setJudgmentDocumentCount(judgmentDocumentJSONArray.size());

        return enterprise;
    }

    public static EnterpriseExpand spiderMongoToMysqlEnt(Document doc, List<Entity> entityList, List<Document> patentBaseInfoList) {

        Object industryInfo = doc.get("industry_info");
        JSONObject industryInfoJsonObject = JSON.parseObject(JSON.toJSONString(industryInfo));

        Object head_info = doc.get("head_info");
        JSONObject headInfoJsonObject = JSON.parseObject(JSON.toJSONString(head_info));

        EnterpriseExpand enterprise = new EnterpriseExpand();
        enterprise.setEntId(doc.getString("_id"));
        String entName = doc.getString("company");
        entName = entName.replace("(", "（").replace(")", "）");
        enterprise.setEntName(entName);

        List<String> oldEntNames = null;
        String faRen = "";
        Object regMoney = "";
        Object regMoneyUnit = "";
        Object entStatus = "";
        Object regDate = "";
        String regNumber = "";
        String entCode = "";
        String creditCode = "";
        Object entType = "";
        String taxpayerCode = "";
        Object importEntCode = "";
        Object industry = "";
        Object businessTerm = "";
        Object checkDate = "";
        Object registAuthority = "";
        Object entNameEn = "";
        Object engagedNumber = "";
        Integer insuredNumber = null;
        //String address = "";
        Object experienceScope = "";
        String exportImportEnterpriseCode = "";
        if (ObjectUtils.isNotEmpty(industryInfoJsonObject)) {
            if (ObjectUtils.isNotEmpty(industryInfoJsonObject.get("曾用名"))) {
                Object oldEntNamesObject = industryInfoJsonObject.get("曾用名");
                if ("-".equals(oldEntNamesObject)) {
                    oldEntNames = new ArrayList<>();
                }
            }

            if (ObjectUtils.isNotEmpty(industryInfoJsonObject.getString("法定代表人"))) {
                faRen = industryInfoJsonObject.getString("法定代表人");
            } else if (ObjectUtils.isNotEmpty(industryInfoJsonObject.getString("负责人"))) {
                faRen = industryInfoJsonObject.getString("负责人");
            } else if (ObjectUtils.isNotEmpty(industryInfoJsonObject.getString("投资人"))) {
                faRen = industryInfoJsonObject.getString("投资人");
            } else if (ObjectUtils.isNotEmpty(industryInfoJsonObject.getString("执行事务合伙人"))) {
                faRen = industryInfoJsonObject.getString("执行事务合伙人");
            }
            if (ObjectUtils.isNotEmpty(industryInfoJsonObject.get("注册资本"))) {
                String regMoneyAll = industryInfoJsonObject.get("注册资本").toString();
                regMoney = regMoneyAll.replaceAll("\\s*", "").replaceAll("[^(0-9).(0-9)*]", "");
                regMoneyUnit = regMoneyAll.replaceAll("\\s*", "").replaceAll("[^(\\u4e00-\\u9fa5)]", "");
            }
            entStatus = industryInfoJsonObject.get("登记状态");
            regDate = industryInfoJsonObject.get("成立日期");
            regNumber = industryInfoJsonObject.getString("工商注册号");
            if (ObjectUtils.isNotEmpty(regDate)) {
                if (!"-".equals(regNumber)) {
                    if (regNumber.contains("复制")) {
                        regNumber = regNumber.replace("复制", "");
                    }
                } else {
                    regNumber = "";
                }
            }
            entCode = industryInfoJsonObject.getString("组织机构代码");
            if (ObjectUtils.isNotEmpty(entCode)) {

                if (entCode.contains("复制")) {
                    entCode = entCode.replace("复制", "");
                }
            }
            creditCode = industryInfoJsonObject.getString("统一社会信用代码");
            if (ObjectUtils.isNotEmpty(creditCode)) {
                if (creditCode.contains("复制")) {
                    creditCode = creditCode.replace("复制", "");
                }
            }
            entType = industryInfoJsonObject.get("企业类型");
            taxpayerCode = industryInfoJsonObject.getString("纳税人识别号");
            if (ObjectUtils.isNotEmpty(taxpayerCode)) {
                if (taxpayerCode.contains("复制")) {
                    taxpayerCode = taxpayerCode.replace("复制", "");
                }
            }
            industry = industryInfoJsonObject.get("所属行业");
            businessTerm = industryInfoJsonObject.get("营业期限");
            checkDate = industryInfoJsonObject.get("核准日期");
            registAuthority = industryInfoJsonObject.get("登记机关");
            if (!"-".equals(industryInfoJsonObject.get("英文名"))) {
                entNameEn = industryInfoJsonObject.get("英文名");
            }
            if (StringUtils.isNotBlank(industryInfoJsonObject.getString("参保人数")) && !"-".equals(industryInfoJsonObject.getString("参保人数"))) {

                insuredNumber = Integer.parseInt(industryInfoJsonObject.getString("参保人数"));
            }
            experienceScope = industryInfoJsonObject.get("经营范围");
            if (StringUtils.isNotBlank(industryInfoJsonObject.getString("进出口企业代码")) && !"-".equals(industryInfoJsonObject.getString("进出口企业代码"))) {

                exportImportEnterpriseCode = industryInfoJsonObject.getString("进出口企业代码");
            }
        }
        enterprise.setOldEntNames(CollectionUtils.isEmpty(oldEntNames) ? "" : String.join(",", oldEntNames));
//        //处理字符重复
//        String farenSubstring = "";
//        if (ObjectUtils.isNotEmpty(faRen)) {
//            String farenString = faRen.toString();
//            String substringFirst = farenString.substring(0, 1);
//            String substringSecond = farenString.substring(1, 2);
//            if (substringFirst.equals(substringSecond)) {
//                farenSubstring = farenString.substring(1);
//            } else {
//                farenSubstring = farenString;
//            }
//        }
        enterprise.setFaRen(faRen);
        enterprise.setRegMoney(ObjectUtils.isNotEmpty(regMoney) ? regMoney.toString() + regMoneyUnit : "");
        enterprise.setEntStatus(ObjectUtils.isNotEmpty(entStatus) ? entStatus.toString() : "");
        enterprise.setRegDate(ObjectUtils.isNotEmpty(regDate) ? regDate.toString() : "");
        enterprise.setCreditCode(ObjectUtils.isNotEmpty(creditCode) ? creditCode : "");
        enterprise.setEntType(ObjectUtils.isNotEmpty(entType) ? entType.toString() : "");
        enterprise.setIndustry(ObjectUtils.isNotEmpty(industry) ? industry.toString() : "");
        enterprise.setExperienceScope(ObjectUtils.isNotEmpty(experienceScope) ? experienceScope.toString() : "");
        enterprise.setInsuredNumber(insuredNumber);
        enterprise.setExportImportEnterpriseCode(exportImportEnterpriseCode);
        String tags = "";
        if (ObjectUtils.isNotEmpty(headInfoJsonObject)) {

            tags = headInfoJsonObject.getString("tags");
            String phone = headInfoJsonObject.getString("phone");
            if (StringUtils.isNotBlank(phone) && phone.contains("*")) {
                phone = "";
            }
            String email = headInfoJsonObject.getString("email");
            if (StringUtils.isNotBlank(email) && email.contains("*")) {
                email = "";
            }

            enterprise.setIntroduce(headInfoJsonObject.getString("desc"));
            String addr = headInfoJsonObject.getString("addr");
            enterprise.setAddress(addr);
            if (StringUtils.isNotBlank(addr)) {
                int index = addr.indexOf("市");
                String substring = addr.substring(0, index + 1);
                if (StringUtils.isNotEmpty(addr) && !addr.endsWith("省") && !"北京市".equalsIgnoreCase(addr) && !"天津市".equalsIgnoreCase(addr) && !"上海市".equalsIgnoreCase(addr) && !"重庆市".equalsIgnoreCase(addr)
                        && !"北京".equalsIgnoreCase(addr) && !"天津".equalsIgnoreCase(addr) && !"上海".equalsIgnoreCase(addr) && !"重庆".equalsIgnoreCase(addr)) {
                    addr = substring + addr;
                }
                List<Map<String, String>> mapList = CommonUtil.addressResolution(addr);
                if (!CollectionUtils.isEmpty(mapList)) {
                    Map<String, String> map = mapList.get(0);
                    enterprise.setProvince(map.get("province"));
                    enterprise.setCity(map.get("city"));
                    enterprise.setArea(map.get("area"));
                }
            }
            enterprise.setTags(tags);
        }

        enterprise.setGjGaoXin(0);
        enterprise.setZgcGaoXin(0);
        enterprise.setJinZhongZi(0);
        enterprise.setDengLingYang(0);
        enterprise.setDuJiaoShou(0);
        enterprise.setJinZhuanTeXin(0);
//        if (!CollectionUtils.isEmpty(entityList)) {
//            entityList.forEach(item -> {
//                if (item.getStr("enterprise_name").equals(enterprise.getEntName())) {
//                    switch (item.getStr("type")) {
//                        case "高新技术企业":
//                            enterprise.setGjGaoXin(1);
//                            enterprise.setHighTechYears(item.getStr("years"));
//                            break;
//                        case "中关村高新技术企业":
//                            enterprise.setZgcGaoXin(1);
//                            enterprise.setHighTechYears(item.getStr("years"));
//                            break;
//                        case "金种子企业":
//                            enterprise.setJinZhongZi(1);
//                            break;
//                        case "瞪羚":
//                            enterprise.setDengLingYang(1);
//                            break;
//                        case "独角兽":
//                            enterprise.setDuJiaoShou(1);
//                            break;
//                        case "精专特新企业":
//                            enterprise.setJinZhuanTeXin(1);
//                            break;
//                    }
//                }
//            });
//        }

        //根据标签判断高企
        if (StringUtils.isNotBlank(tags)) {
            if (tags.contains("高新")) {
                enterprise.setGjGaoXin(1);
            }
            if (tags.contains("金种子")) {
                enterprise.setJinZhongZi(1);
            }
            if (tags.contains("瞪羚")) {
                enterprise.setDengLingYang(1);
            }
            if (tags.contains("独角兽")) {
                enterprise.setDuJiaoShou(1);
            }
            if (tags.contains("专精特新")) {
                enterprise.setJinZhuanTeXin(1);
            }
        }

        //企查查是高新，并且数据库有匹配年费，设高企年份
        if (!CollectionUtils.isEmpty(entityList)) {
            entityList.forEach(item -> {
                if (item.getStr("enterprise_name").equals(enterprise.getEntName()) && enterprise.getGjGaoXin() == 1) {
                    enterprise.setHighTechYears(item.getStr("years"));
                }
            });
        }

        //商标
        enterprise.setBrandCount(doc.getInteger("ent_brand_count"));
        Object entBrandObject = doc.get("ent_brand");
        List<String> brandAgencyList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(entBrandObject)) {
            JSONArray entBrandJSONArray = JSON.parseArray(JSON.toJSONString(entBrandObject));
            //取5个
            List<Object> entBrandJSONLimitArray = entBrandJSONArray.stream().limit(5).collect(Collectors.toList());
            entBrandJSONLimitArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                brandAgencyList.add(itemJSONObject.getString("agency"));
            });
        }
        enterprise.setBrandAgency(StringUtils.join(brandAgencyList, "，"));

        //专利
        enterprise.setPatentCount(doc.getInteger("ent_patent_count"));
        Object entPatentObject = doc.get("ent_patent");
        List<JSONObject> patentJSONObjectList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(entPatentObject)) {
            JSONArray entPatentJSONArray = JSON.parseArray(JSON.toJSONString(entPatentObject));
            entPatentJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                patentJSONObjectList.add(itemJSONObject);
            });
        }
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(patentJSONObjectList)){
            enterprise.setQccInventPatentCount((int) patentJSONObjectList.stream().filter(item -> item.getString("patentType").contains("发明")).count());
            enterprise.setQccPracticalPatentCount((int) patentJSONObjectList.stream().filter(item -> item.getString("patentType").contains("实用")).count());
            enterprise.setQccExteriorPatentCount((int) patentJSONObjectList.stream().filter(item -> item.getString("patentType").contains("外观")).count());
        }

        List<String> patentAgencyList = patentJSONObjectList.stream().limit(5).collect(Collectors.toList()).stream().map(item -> item.getString("agency")).collect(Collectors.toList());

        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(patentAgencyList)) {
            List<String> stringList = patentAgencyList.stream().filter(item -> ObjectUtils.isNotEmpty(item)).collect(Collectors.toList());
            enterprise.setPatentAgency(StringUtils.join(stringList, "，"));
        }

        //软件著作权
        enterprise.setSoftwareCopyrightCount(doc.getInteger("ent_softcopyright_count"));
        //著作权
        enterprise.setCopyrightCount(doc.getInteger("ent_copyright_count"));
        //资质证书
        Object entCertificateObject = doc.get("ent_certificate");
        JSONArray entCertificateJSONArray = new JSONArray();
        if (ObjectUtils.isNotEmpty(entCertificateObject)) {
            entCertificateJSONArray = JSON.parseArray(JSON.toJSONString(entCertificateObject));
        }
        enterprise.setCertificateCount(entCertificateJSONArray.size());
        //投资
        Object invEventObject = doc.get("entPatent");
        JSONArray invEventJSONArray = new JSONArray();
        if (ObjectUtils.isNotEmpty(invEventObject)) {
            invEventJSONArray = JSON.parseArray(JSON.toJSONString(invEventObject));
        }
        enterprise.setInvEventCount(invEventJSONArray.size());
        //融资
        Object financeObject = doc.get("inv_event");
        JSONArray financeJSONArray = new JSONArray();
        List<String> financeRounds = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(financeObject)) {
            financeJSONArray = JSON.parseArray(JSON.toJSONString(financeObject));
            financeJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                financeRounds.add(itemJSONObject.getString("stage"));
            });
        }
        enterprise.setFinanceCount(financeJSONArray.size());
        //融资轮次
        enterprise.setFinanceRounds(StringUtils.join(financeRounds, "，"));

        //裁判文书
        Object judgmentDocumentObject = doc.get("judgment_document");
        JSONArray judgmentDocumentJSONArray = new JSONArray();
        if (ObjectUtils.isNotEmpty(judgmentDocumentObject)) {
            judgmentDocumentJSONArray = JSON.parseArray(JSON.toJSONString(judgmentDocumentObject));
        }
        enterprise.setJudgmentDocumentCount(judgmentDocumentJSONArray.size());


        //处理专利局数据
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(patentBaseInfoList)) {

            Map<String, List<Document>> groupingByApplicantMap = patentBaseInfoList.stream().collect(Collectors.groupingBy(item -> item.getString("patentType")));
            if (ObjectUtils.isNotEmpty(groupingByApplicantMap)) {
                enterprise.setInventPatentCount(ObjectUtils.isNotEmpty(groupingByApplicantMap.get("发明专利"))? groupingByApplicantMap.get("发明专利").size():0);
                enterprise.setPracticalPatentCount(ObjectUtils.isNotEmpty(groupingByApplicantMap.get("实用新型"))?groupingByApplicantMap.get("实用新型").size():0);
                enterprise.setExteriorPatentCount(ObjectUtils.isNotEmpty(groupingByApplicantMap.get("外观设计"))?groupingByApplicantMap.get("外观设计").size():0);
            }

        }
        //国家局专利数量
        enterprise.setPatentOfficeCount(patentBaseInfoList.size());

        return enterprise;
    }

    /**
     * 高校
     * @param doc
     * @return
     */
    public static University toMysqlUniversity(Document doc){
        Object industryInfo = doc.get("industry_info");
        JSONObject industryInfoJsonObject = JSON.parseObject(JSON.toJSONString(industryInfo));

        Object head_info = doc.get("head_info");
        JSONObject headInfoJsonObject = JSON.parseObject(JSON.toJSONString(head_info));

        University university = new University();
        university.setId(doc.getString("_id"));
        String entName = doc.getString("company");
        entName = entName.replace("(", "（").replace(")", "）");
        university.setName(entName);

        List<String> oldEntNames = null;
        String faRen = "";
        Object regMoney = "";
        String regMoneyUnit = "";
        String entStatus = "";
        Date regDate = null;
        String creditCode = "";
        String experienceScope = "";
        if (ObjectUtils.isNotEmpty(industryInfoJsonObject)) {
            if (ObjectUtils.isNotEmpty(industryInfoJsonObject.get("曾用名"))) {
                Object oldEntNamesObject = industryInfoJsonObject.get("曾用名");
                if ("-".equals(oldEntNamesObject)) {
                    oldEntNames = new ArrayList<>();
                }
            }

            if (ObjectUtils.isNotEmpty(industryInfoJsonObject.get("法定代表人"))) {
                faRen = industryInfoJsonObject.getString("法定代表人");
            } else if (ObjectUtils.isNotEmpty(industryInfoJsonObject.get("负责人"))) {
                faRen = industryInfoJsonObject.getString("负责人");
            } else if (ObjectUtils.isNotEmpty(industryInfoJsonObject.get("投资人"))) {
                faRen = industryInfoJsonObject.getString("投资人");
            }else if (ObjectUtils.isNotEmpty(industryInfoJsonObject.get("执行事务合伙人"))) {
                faRen = industryInfoJsonObject.getString("执行事务合伙人");
            }
            if (ObjectUtils.isNotEmpty(industryInfoJsonObject.get("注册资本"))) {
                String regMoneyAll = industryInfoJsonObject.get("注册资本").toString();
                regMoney = regMoneyAll.replaceAll("\\s*", "").replaceAll("[^(0-9).(0-9)*]", "");
                regMoneyUnit = regMoneyAll.replaceAll("\\s*", "").replaceAll("[^(\\u4e00-\\u9fa5)]", "");
            }
            entStatus = industryInfoJsonObject.getString("登记状态");
            if (!"-".equals(entStatus)) {
                entStatus = industryInfoJsonObject.getString("登记状态");
            }else {
                entStatus = "";
            }
            regDate = industryInfoJsonObject.getDate("成立日期");
            creditCode = industryInfoJsonObject.getString("统一社会信用代码");
            if (creditCode.contains("复制")) {
                creditCode = creditCode.replace("复制", "");
            }
            if (!"-".equals(industryInfoJsonObject.get("英文名"))) {
            }
            experienceScope = industryInfoJsonObject.getString("宗旨和业务范围");
        }
        university.setOldNames(CollectionUtils.isEmpty(oldEntNames)?"":String.join(",",oldEntNames));
        //处理字符重复
        String farenSubstring = "";
        if (ObjectUtils.isNotEmpty(faRen)){
            if (!"-".equals(faRen)) {
                try {
                    String substringFirst = faRen.substring(0,1);
                    String substringSecond = faRen.substring(1,2);
                    if (substringFirst.equals(substringSecond)) {
                        farenSubstring = faRen.substring(1);
                    }else {
                        farenSubstring = faRen;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        university.setLegalPerson(farenSubstring);
        university.setRegisteredCapital(ObjectUtils.isNotEmpty(regMoney)?regMoney.toString()+regMoneyUnit:"");
        university.setRegisteredCapital(regMoneyUnit);
        university.setStatus(ObjectUtils.isNotEmpty(entStatus)? entStatus :"");
        university.setRegistrationDate(ObjectUtils.isNotEmpty(regDate)?regDate:null);
        university.setCreditCode(ObjectUtils.isNotEmpty(creditCode)? creditCode :"");
        university.setExperienceScope(experienceScope);
        if (ObjectUtils.isNotEmpty(headInfoJsonObject)) {

            String phone = headInfoJsonObject.getString("phone");
            if (StringUtils.isNotBlank(phone) && phone.contains("*")) {
                phone = "";
            }
            String email = headInfoJsonObject.getString("email");
            if (StringUtils.isNotBlank(email) && email.contains("*")) {
                email = "";
            }

            String addr = headInfoJsonObject.getString("addr");
            if (StringUtils.isNotBlank(addr)) {
                university.setAddress(addr);
            }
            String tags = headInfoJsonObject.getString("tags");
            if (StringUtils.isNotBlank(tags)) {
                int i = tags.indexOf(";司法案件");
                if (i > 0) {

                    String tagsSubstring = tags.substring(0,i).replace(";", "，");
                    university.setTags(tagsSubstring.replace("，曾用名", ""));
                }else {
                    university.setTags(tags.replace(";", "，").replace("，曾用名", ""));
                }

            }

            university.setWebsite(headInfoJsonObject.getString("website"));
            university.setEmail(email);
            university.setIntroduce(headInfoJsonObject.getString("desc"));
            university.setPhone(phone);
        }

        //商标
        university.setBrandCount(doc.getInteger("ent_brand_count"));
        //专利
        university.setPatentCount(doc.getInteger("ent_patent_count"));
        //软件著作权
        university.setSoftwareCopyrightCount(doc.getInteger("ent_softcopyright_count"));
        //著作权
        university.setCopyrightCount(doc.getInteger("ent_copyright_count"));
        //资质证书
        Object entCertificateObject = doc.get("ent_certificate");
        JSONArray entCertificateJSONArray = new JSONArray();
        if (ObjectUtils.isNotEmpty(entCertificateObject)) {
            entCertificateJSONArray = JSON.parseArray(JSON.toJSONString(entCertificateObject));
        }
        university.setCertificateCount(entCertificateJSONArray.size());
        //投资
        Object invEventObject = doc.get("entPatent");
        JSONArray invEventJSONArray = new JSONArray();
        if (ObjectUtils.isNotEmpty(invEventObject)) {
            invEventJSONArray = JSON.parseArray(JSON.toJSONString(invEventObject));
        }
        university.setInvestCount(invEventJSONArray.size());
        //融资
        Object financeObject = doc.get("inv_event");
        JSONArray financeJSONArray = new JSONArray();
        if (ObjectUtils.isNotEmpty(financeObject)) {
            financeJSONArray = JSON.parseArray(JSON.toJSONString(financeObject));
        }
        university.setFinanceCount(financeJSONArray.size());
        //裁判文书
        Object judgmentDocumentObject = doc.get("judgment_document");
        JSONArray judgmentDocumentJSONArray = new JSONArray();
        if (ObjectUtils.isNotEmpty(judgmentDocumentObject)) {
            judgmentDocumentJSONArray = JSON.parseArray(JSON.toJSONString(judgmentDocumentObject));
        }
        university.setJudgmentCount(judgmentDocumentJSONArray.size());

        //默认排序99
        university.setOrderIndex(999);
        university.setCooperationEnterpriseCount(RandomUtils.nextInt(1,100));
        university.setInventorCount(RandomUtils.nextInt(1,100));

        return university;
    }

    /**
     * 企业分析
     * @param doc
     * @return
     */
    public static EnterpriseAnalyze toEnterpriseAnalyze(Document doc){

        EnterpriseAnalyze enterpriseAnalyze = new EnterpriseAnalyze();
        enterpriseAnalyze.setEntId(doc.getString("_id"));
        String entName = doc.getString("company");
        entName = entName.replace("(", "（").replace(")", "）");
        enterpriseAnalyze.setEntName(entName);

        //商标
        Object entBrandObject = doc.get("ent_brand");
        AtomicInteger entBrandRecentCount = new AtomicInteger();
        if (ObjectUtils.isNotEmpty(entBrandObject)) {
            JSONArray entBrandJSONArray = JSON.parseArray(JSON.toJSONString(entBrandObject));
            entBrandJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                Date applyDate = null;
                if (itemJSONObject.getString("applyDate").indexOf("-") > 2) {
                    try{
                        applyDate = itemJSONObject.getDate("applyDate");
                    }catch(Exception e){
                        if ("-".equals(itemJSONObject.getString("applyDate"))) {
                            applyDate = null;
                        }
                    }
                }else {
                    try{
                        applyDate = itemJSONObject.getDate("brandType");
                    }catch(Exception e){
                        if ("-".equals(itemJSONObject.getString("brandType"))) {
                            applyDate = null;
                        }
                    }
                }

                if (ObjectUtils.isNotEmpty(applyDate) && DateUtil.betweenYear(applyDate, new Date(), true) < 3) {

                    entBrandRecentCount.getAndIncrement();
                }

            });
        }
        ///专利
        Object entPatentObject = doc.get("ent_patent");
        AtomicInteger inventPatentRecentCount = new AtomicInteger();
        AtomicInteger practicalNewRecentCount = new AtomicInteger();
        AtomicInteger entPatentCount = new AtomicInteger();
        if (enterpriseAnalyze.getEntName().contains("天津异乡好居")) {
            System.out.println(1);
        }
        if (ObjectUtils.isNotEmpty(entPatentObject)) {
            JSONArray entPatentJSONArray = JSON.parseArray(JSON.toJSONString(entPatentObject));
            entPatentJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                Date regDate1 = null;
                if (itemJSONObject.getString("regDate").indexOf("-") > 2) {

                    try {
                        regDate1 = itemJSONObject.getDate("regDate");
                    } catch (Exception e) {
                        if ("-".equals(itemJSONObject.getString("regDate"))) {
                            regDate1 = null;
                        }
                    }

                    if (ObjectUtils.isNotEmpty(regDate1) && DateUtil.betweenYear(regDate1, new Date(), true) < 3) {

                        String patentType = itemJSONObject.getString("patentType");
                        switch (patentType) {
                            case "发明专利":
                                inventPatentRecentCount.getAndIncrement();
                                break;
                            case "发明授权":
                                inventPatentRecentCount.getAndIncrement();
                                break;
                            case "发明公布":
                                inventPatentRecentCount.getAndIncrement();
                                break;
                            case "实用新型":
                                practicalNewRecentCount.getAndIncrement();
                                break;
                        }
                        entPatentCount.getAndIncrement();
                    }
                } else {
                    try {
                        regDate1 = itemJSONObject.getDate("openNo");
                    } catch (Exception e) {
                        if ("-".equals(itemJSONObject.getString("openNo"))) {
                            regDate1 = null;
                        }
                    }

                    if (ObjectUtils.isNotEmpty(regDate1) && DateUtil.betweenYear(regDate1, new Date(), true) < 3) {

                        String patentType = itemJSONObject.getString("patentStatus");
                        switch (patentType) {
                            case "发明专利":
                                inventPatentRecentCount.getAndIncrement();
                                break;
                            case "发明授权":
                                inventPatentRecentCount.getAndIncrement();
                                break;
                            case "发明公布":
                                inventPatentRecentCount.getAndIncrement();
                                break;
                            case "实用新型":
                                practicalNewRecentCount.getAndIncrement();
                                break;
                        }
                        entPatentCount.getAndIncrement();
                    }
                }

            });
        }
        //进三年发明专利
        enterpriseAnalyze.setInventPatentRecentCount(inventPatentRecentCount.get());
        //进三年实用新型专利
        enterpriseAnalyze.setPracticalNewRecentCount(practicalNewRecentCount.get());

        //软著
        Object entSoftCopyrightObject = doc.get("ent_softcopyright");
        AtomicInteger entSoftCopyrightCount = new AtomicInteger();
        if (ObjectUtils.isNotEmpty(entSoftCopyrightObject)) {
            JSONArray entSoftCopyrightJSONArray = JSON.parseArray(JSON.toJSONString(entSoftCopyrightObject));
            entSoftCopyrightJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                Date regDate1 = null;
                try{
                    regDate1 = itemJSONObject.getDate("regDate");
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("regDate"))) {
                        regDate1 = null;
                    }
                }

                if (ObjectUtils.isNotEmpty(regDate1) && DateUtil.betweenYear(regDate1, new Date(), true) < 3) {

                    entSoftCopyrightCount.getAndIncrement();
                }

            });
        }

        //版权
        Object entCopyrightObject = doc.get("ent_copyright");
        AtomicInteger entCopyrightCount = new AtomicInteger();
        if (ObjectUtils.isNotEmpty(entCopyrightObject)) {
            JSONArray entCopyrightJSONArray = JSON.parseArray(JSON.toJSONString(entCopyrightObject));
            entCopyrightJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                Date regDate1 = null;
                try{
                    regDate1 = itemJSONObject.getDate("regDate");
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("regDate"))) {
                        regDate1 = null;
                    }
                }

                if (ObjectUtils.isNotEmpty(regDate1) && DateUtil.betweenYear(regDate1, new Date(), true) < 3) {

                    entCopyrightCount.getAndIncrement();
                }

            });
        }
        //资质证书
        Object entCertificateObject = doc.get("ent_certificate");
        if (ObjectUtils.isNotEmpty(entCertificateObject)) {
            List<String> certificateTypeSet = new ArrayList<>();
            JSONArray entCertificateJSONArray = JSON.parseArray(JSON.toJSONString(entCertificateObject));
            entCertificateJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                String certificateType = itemJSONObject.getString("certificateName");
                if (StringUtils.isNotBlank(certificateType)) {
                    certificateTypeSet.add(certificateType);
                }
            });
            enterpriseAnalyze.setCertificateType(StringUtils.join(certificateTypeSet,"|"));

        }

        //近三年知识产权数量
        Integer intellectualPropertyRecentCount = entBrandRecentCount.get() + entPatentCount.get() + entSoftCopyrightCount.get() + entCopyrightCount.get();
        enterpriseAnalyze.setIntellectualPropertyRecentCount(intellectualPropertyRecentCount);

        //商标
        Integer ent_brand_count = doc.getInteger("ent_brand_count");
        //专利
        Integer ent_patent_count = doc.getInteger("ent_patent_count");
        //软著
        Integer ent_softcopyright_count = doc.getInteger("ent_softcopyright_count");
        //著作权
        Integer ent_copyright_count = doc.getInteger("ent_copyright_count");

        //知识产权总量
        Integer sum = ent_brand_count + ent_patent_count + ent_softcopyright_count + ent_copyright_count;
        enterpriseAnalyze.setIntellectualPropertyCount(sum);

        //融资
        Object financeObject = doc.get("inv_event");
        List<String> financeRounds = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(financeObject)) {
            JSONArray financeJSONArray = JSON.parseArray(JSON.toJSONString(financeObject));
            financeJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                financeRounds.add(itemJSONObject.getString("stage"));
            });
        }
        //融资轮次
        enterpriseAnalyze.setFinanceRounds(StringUtils.join(financeRounds,"，"));

        //官网
        JSONObject headInfoJsonObject = JSON.parseObject(JSON.toJSONString(doc.get("head_info")));
        if (StringUtils.isNotBlank(headInfoJsonObject.getString("website"))) {
            enterpriseAnalyze.setOfficialWebsite("是");
        }else {
            enterpriseAnalyze.setOfficialWebsite("否");
        }

        return enterpriseAnalyze;
    }

    //商标
    public static List<Document> brandSpiderMongoToMongo(Document doc) {

        Object entBrandObject = doc.get("ent_brand");
        List<Document> entBrandList = new ArrayList<>();

        if (ObjectUtils.isNotEmpty(entBrandObject)) {
            JSONArray entBrandJSONArray = JSON.parseArray(JSON.toJSONString(entBrandObject));
            entBrandJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                Document entBrand = new Document();
                entBrand.put("entId", doc.getString("_id"));
                entBrand.put("brandName", itemJSONObject.getString("brandName"));
                try {
                    entBrand.put("applyDate", itemJSONObject.getDate("applyDate"));
                } catch (Exception e) {
                    if ("-".equals(itemJSONObject.getString("applyDate"))) {
                        entBrand.put("applyDate", null);
                    }
                }

                entBrand.put("regCode", itemJSONObject.getString("regCode"));
                entBrand.put("intCls", itemJSONObject.getInteger("intCls"));
                entBrand.put("brandType", itemJSONObject.getString("brandType"));
                entBrand.put("brandStatus", itemJSONObject.getString("brandStatus"));
                entBrand.put("brandImag", itemJSONObject.getString("brandImag"));
                entBrandList.add(entBrand);
            });
        }
        return entBrandList;
    }

    //专利
    public static List<Document> patentSpiderMongoToMongo(Document doc) {
        Object entPatentObject = doc.get("ent_patent");
        List<Document> entPatentList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(entPatentObject)) {
            JSONArray entPatentJSONArray = JSON.parseArray(JSON.toJSONString(entPatentObject));
            entPatentJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                Document entPatent = new Document();
                entPatent.put("entId", doc.getString("_id"));
                entPatent.put("patentName", itemJSONObject.getString("patentName"));
                entPatent.put("patentId", itemJSONObject.getString("patentId"));
                entPatent.put("patentType", itemJSONObject.getString("patentType"));
                entPatent.put("patentStatus", itemJSONObject.getString("patentStatus"));
                try{
                    entPatent.put("regDate", itemJSONObject.getDate("regDate"));
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("regDate"))) {
                        entPatent.put("regDate", null);
                    }
                }
                try{
                    entPatent.put("openDate", itemJSONObject.getDate("openDate"));
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("openDate"))) {
                        entPatent.put("openDate", null);
                    }
                }
                entPatent.put("openNo", itemJSONObject.getString("openNo"));
                entPatentList.add(entPatent);
            });
        }
        return entPatentList;
    }

    //软件著作权
    public static List<Document> softCopyrightSpiderMongoToMongo(Document doc) {
        Object entSoftCopyrightObject = doc.get("ent_softcopyright");
        List<Document> entSoftCopyrightList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(entSoftCopyrightObject)) {
            JSONArray entSoftCopyrightJSONArray = JSON.parseArray(JSON.toJSONString(entSoftCopyrightObject));
            entSoftCopyrightJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                Document entSoftCopyright = new Document();
                entSoftCopyright.put("entId",doc.getString("_id"));
                entSoftCopyright.put("softName", itemJSONObject.getString("softName"));
                try{
                    entSoftCopyright.put("regDate",itemJSONObject.getDate("regDate"));
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("regDate"))) {
                        entSoftCopyright.put("regDate",null);
                    }
                }
                entSoftCopyright.put("regNo", itemJSONObject.getString("regNo"));
                entSoftCopyright.put("softEdition", itemJSONObject.getString("softEdition"));
                String softAbb = itemJSONObject.getString("softAbb");
                if (!"-".equals(softAbb)) {
                    entSoftCopyright.put("softAbb", softAbb);
                }
                try{
                    entSoftCopyright.put("noticeDate",itemJSONObject.getDate("noticeDate"));
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("noticeDate"))) {
                        entSoftCopyright.put("noticeDate",null);
                    }
                }
                entSoftCopyrightList.add(entSoftCopyright);
            });
        }
        return entSoftCopyrightList;
    }

    //著作权
    public static List<Document> copyrightSpiderMongoToMongo(Document doc) {

        Object enCopyrightObject = doc.get("ent_copyright");
        List<Document> enCopyrightList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(enCopyrightObject)) {
            JSONArray enCopyrightJSONArray = JSON.parseArray(JSON.toJSONString(enCopyrightObject));
            enCopyrightJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                Document enCopyright = new Document();
                enCopyright.put("entId", doc.getString("_id"));
                enCopyright.put("productName", itemJSONObject.getString("productName"));
                try{
                    enCopyright.put("regDate", itemJSONObject.getDate("regDate"));
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("regDate"))) {
                        enCopyright.put("regDate",null);
                    }
                }
                enCopyright.put("regNo",itemJSONObject.getString("regNo"));
                try{
                    enCopyright.put("createDate",itemJSONObject.getDate("createDate"));
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("createDate"))) {
                        enCopyright.put("createDate", null);
                    }
                }
                enCopyright.put("productType", itemJSONObject.getString("productType"));
                try{
                    enCopyright.put("publishDate",itemJSONObject.getDate("publishDate"));
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("publishDate"))) {
                        enCopyright.put("publishDate",null);
                    }
                }
                enCopyrightList.add(enCopyright);
            });
        }
        return enCopyrightList;
    }

    //资质证书
    public static List<Document> certificateSpiderMongoToMongo(Document doc) {

        Object entCertificateObject = doc.get("ent_certificate");
        List<Document> entCertificateList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(entCertificateObject)) {
            JSONArray entCertificateJSONArray = JSON.parseArray(JSON.toJSONString(entCertificateObject));
            entCertificateJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                Document entCertificate = new Document();
                entCertificate.put("entId",doc.getString("_id"));
                entCertificate.put("certificateType", itemJSONObject.getString("certificateType"));
                String certificateName = itemJSONObject.getString("certificateName");
                if (!"-".equals(certificateName)) {
                    entCertificate.put("certificateName", certificateName);
                }
                entCertificate.put("certificateCode",itemJSONObject.getString("certificateCode"));
                try{
                    entCertificate.put("createDate", itemJSONObject.getDate("createDate"));
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("createDate"))) {
                        entCertificate.put("createDate",null);
                    }
                }
                try{
                    entCertificate.put("stopData",itemJSONObject.getDate("stopData"));
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("stopData"))) {
                        entCertificate.put("stopData", null);
                    }
                }
                entCertificateList.add(entCertificate);
            });
        }

        return entCertificateList;
    }

    //投资
    public static List<Document> investSpiderMongoToMongo(Document doc) {

        Object invEventObject = doc.get("entPatent");
        List<Document> invEventList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(invEventObject)) {
            JSONArray invEventJSONArray = JSON.parseArray(JSON.toJSONString(invEventObject));
            invEventJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                Document invEvent = new Document();
                invEvent.put("entId",doc.getString("_id"));
                String entAbbName = itemJSONObject.getString("entAbbName");
                if (!"-".equals(entAbbName)) {
                    invEvent.put("entAbbName", entAbbName);
                }
                invEvent.put("toEntName", itemJSONObject.getString("toEntName"));
                invEvent.put("toFaRen", itemJSONObject.getString("toFaRen"));
                String invMoney = itemJSONObject.getString("invMoney");
                if (!"-".equals(invMoney)) {
                    invEvent.put("invMoney", invMoney);
                }
                String toRate = itemJSONObject.getString("toRate");
                if (!"-".equals(toRate)) {
                    invEvent.put("toRate", toRate);
                }
                invEventList.add(invEvent);
            });
        }

        return invEventList;
    }
    //融资
    public static List<Document> financeSpiderMongoToMongo(Document doc) {

        Object financeObject = doc.get("inv_event");
        List<Document> financeList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(financeObject)) {
            JSONArray financeJSONArray = JSON.parseArray(JSON.toJSONString(financeObject));
            financeJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                Document finance = new Document();
                finance.put("entId", doc.getString("_id"));
                finance.put("entAbbName", itemJSONObject.getString("entAbbName"));
                try{
                    finance.put("invTime", itemJSONObject.getDate("invTime"));
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("invTime"))) {
                        finance.put("invTime", null);
                    }
                }
                finance.put("stage", itemJSONObject.getString("stage"));
                finance.put("invMoney", itemJSONObject.getString("invMoney"));
                finance.put("investor", itemJSONObject.getString("investor"));
                financeList.add(finance);
            });
        }

        return financeList;
    }
    //裁判文书
    public static List<Document> judgmentSpiderMongoToMongo(Document doc) {

        Object judgmentDocumentObject = doc.get("judgment_document");
        List<Document> judgmentDocumentList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(judgmentDocumentObject)) {
            JSONArray judgmentDocumentJSONArray = JSON.parseArray(JSON.toJSONString(judgmentDocumentObject));
            judgmentDocumentJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                Document judgmentDocument = new Document();
                judgmentDocument.put("entId", doc.getString("_id"));
                judgmentDocument.put("caseName", itemJSONObject.getString("caseName"));
                judgmentDocument.put("caseCause", itemJSONObject.getString("caseCause"));
                judgmentDocument.put("caseNo", itemJSONObject.getString("caseNo"));
                String plaintiffDefendant = itemJSONObject.getString("plaintiff_defendant");
                if (!"-".equals(plaintiffDefendant)) {
                    judgmentDocument.put("plaintiffDefendant", plaintiffDefendant);
                }
                String result = itemJSONObject.getString("result");
                if (!"-".equals(result)) {
                    judgmentDocument.put("result", result);
                }
                try{
                    judgmentDocument.put("caseDate", itemJSONObject.getDate("caseDate"));
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("caseDate"))) {
                        judgmentDocument.put("caseDate", null);
                    }
                }
                try{
                    judgmentDocument.put("noticeDate", itemJSONObject.getDate("noticeDate"));
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("noticeDate"))) {
                        judgmentDocument.put("noticeDate", null);
                    }
                }
                judgmentDocument.put("content", itemJSONObject.getString("content"));
                judgmentDocumentList.add(judgmentDocument);
            });
        }

        return judgmentDocumentList;
    }


    public static Document spiderMongoToMysql(Document doc){
        Document document = new Document();
        //带企业名称的
        String entName = doc.getString("company");
        String entNameOK  = entName.replace("(", "（").replace(")", "）");
        //商标
        Object entBrandObject = doc.get("ent_brand");
        List<EntBrand> entBrandList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(entBrandObject)) {
            JSONArray entBrandJSONArray = JSON.parseArray(JSON.toJSONString(entBrandObject));
            entBrandJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                EntBrand entBrand = new EntBrand();
                entBrand.setEntId(doc.getString("_id"));

                entBrand.setBrandName(itemJSONObject.getString("brandName"));

                try{
                    entBrand.setApplyDate(itemJSONObject.getDate("applyDate"));

                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("applyDate"))) {
                        entBrand.setApplyDate(null);
                    }
                }

                entBrand.setRegCode(itemJSONObject.getString("regCode"));
                entBrand.setIntCls(itemJSONObject.getInteger("intCls"));
                entBrand.setBrandType(itemJSONObject.getString("brandType"));
                entBrand.setBrandStatus(itemJSONObject.getString("brandStatus"));
                entBrand.setBrandImag(itemJSONObject.getString("brandImag"));
                entBrand.setEntName(entNameOK);
                entBrandList.add(entBrand);
            });
            document.put("entBrand", entBrandList);
        }

        //专利
        Object entPatentObject = doc.get("ent_patent");
        List<EntPatent> entPatentList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(entPatentObject)) {
            JSONArray entPatentJSONArray = JSON.parseArray(JSON.toJSONString(entPatentObject));
            entPatentJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                EntPatent entPatent = new EntPatent();
                entPatent.setEntId(doc.getString("_id"));

                entPatent.setPatentName(itemJSONObject.getString("patentName"));

                entPatent.setPatentId(itemJSONObject.getString("patentId"));

                entPatent.setPatentType(itemJSONObject.getString("patentType"));

                entPatent.setPatentStatus(itemJSONObject.getString("patentStatus"));
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
                entPatent.setEntName(entNameOK);
                entPatentList.add(entPatent);
            });
            document.put("entPatent", entPatentList);
        }

        //软件著作权
        Object entSoftCopyrightObject = doc.get("ent_softcopyright");
        List<EntSoftCopyRight> entSoftCopyrightList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(entSoftCopyrightObject)) {
            JSONArray entSoftCopyrightJSONArray = JSON.parseArray(JSON.toJSONString(entSoftCopyrightObject));
            entSoftCopyrightJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                EntSoftCopyRight entSoftCopyright = new EntSoftCopyRight();
                entSoftCopyright.setEntId(doc.getString("_id"));
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
                entSoftCopyright.setEntName(entNameOK);
                entSoftCopyrightList.add(entSoftCopyright);
            });
            document.put("entSoftCopyright", entSoftCopyrightList);
        }
        //著作权
        Object enCopyrightObject = doc.get("ent_copyright");
        List<EntCopyRight> enCopyrightList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(enCopyrightObject)) {
            JSONArray enCopyrightJSONArray = JSON.parseArray(JSON.toJSONString(enCopyrightObject));
            enCopyrightJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                EntCopyRight enCopyright = new EntCopyRight();
                enCopyright.setEntId(doc.getString("_id"));
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
                enCopyright.setEntName(entNameOK);
                enCopyrightList.add(enCopyright);
            });
            document.put("enCopyright", enCopyrightList);
        }
        //资质证书
        Object entCertificateObject = doc.get("ent_certificate");
        List<EntCertificate> entCertificateList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(entCertificateObject)) {
            JSONArray entCertificateJSONArray = JSON.parseArray(JSON.toJSONString(entCertificateObject));
            entCertificateJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                EntCertificate entCertificate = new EntCertificate();
                entCertificate.setEntId(doc.getString("_id"));
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
                    entCertificate.setEndDate(itemJSONObject.getDate("stopData"));
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("stopData"))) {
                        entCertificate.setEndDate(null);
                    }
                }
                entCertificate.setEntName(entNameOK);
                entCertificateList.add(entCertificate);
            });
            document.put("entCertificate", entCertificateList);
        }

        //投资
        Object invEventObject = doc.get("entPatent");
        List<InvEvent> invEventList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(invEventObject)) {
            JSONArray invEventJSONArray = JSON.parseArray(JSON.toJSONString(invEventObject));
            invEventJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                InvEvent invEvent = new InvEvent();
                invEvent.setEntId(doc.getString("_id"));
                String entAbbName = itemJSONObject.getString("entAbbName");
                if (!"-".equals(entAbbName)) {
                    invEvent.setEntName(entAbbName);
                }
                invEvent.setInvestedName(itemJSONObject.getString("toEntName"));
                invEvent.setLegalPerson(itemJSONObject.getString("toFaRen"));
                String invMoney = itemJSONObject.getString("invMoney");
                if (!"-".equals(invMoney)) {
                    invEvent.setInvestmentAmount(invMoney);
                }
                String toRate = itemJSONObject.getString("toRate");
                if (!"-".equals(toRate)) {
                    invEvent.setInvestmentProportion(toRate);
                }
                invEvent.setEntName(entNameOK);
                invEventList.add(invEvent);
            });
            document.put("invEvent", invEventList);
        }

        //融资
        Object financeObject = doc.get("inv_event");
        List<Finance> financeList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(financeObject)) {
            JSONArray financeJSONArray = JSON.parseArray(JSON.toJSONString(financeObject));
            financeJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                Finance finance = new Finance();
                finance.setEntId(doc.getString("_id"));
                finance.setEntName(itemJSONObject.getString("entAbbName"));
                try{
                    finance.setFinanceDate(itemJSONObject.getDate("invTime"));
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("invTime"))) {
                        finance.setFinanceDate(null);
                    }
                }
                finance.setFinanceRounds(itemJSONObject.getString("stage"));
                finance.setFinanceAmount(itemJSONObject.getString("invMoney"));
                finance.setInvestor(itemJSONObject.getString("investor"));
                finance.setEntName(entNameOK);
                financeList.add(finance);
            });
            document.put("finance", financeList);
        }

        //裁判文书1
        Object judgmentDocumentObject = doc.get("judgment_document");
        List<JudgmentDocument> judgmentDocumentList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(judgmentDocumentObject)) {
            JSONArray judgmentDocumentJSONArray = JSON.parseArray(JSON.toJSONString(judgmentDocumentObject));
            judgmentDocumentJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                JudgmentDocument judgmentDocument = new JudgmentDocument();
                judgmentDocument.setEntId(doc.getString("_id"));
                judgmentDocument.setCaseName(itemJSONObject.getString("caseName"));
                judgmentDocument.setCaseCause(itemJSONObject.getString("caseCause"));
                judgmentDocument.setCaseNo(itemJSONObject.getString("caseNo"));
                String plaintiffDefendant = itemJSONObject.getString("plaintiff_defendant");
                if (!"-".equals(plaintiffDefendant)) {
                    judgmentDocument.setPlaintiff(plaintiffDefendant);
                    judgmentDocument.setDefendant(plaintiffDefendant);
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
//                judgmentDocument.setChiefJudge(itemJSONObject.getString(""));
//                judgmentDocument.setJuror(itemJSONObject.getString(""));
//                judgmentDocument.setPeopleJuror(itemJSONObject.getString(""));
//                judgmentDocument.setClerk(itemJSONObject.getString(""));
//                judgmentDocument.setCourtName(itemJSONObject.getString(""));
//                judgmentDocument.setCasesType(itemJSONObject.getString(""));
                judgmentDocument.setContent(itemJSONObject.getString("content"));
                judgmentDocument.setEntName(entNameOK);
                judgmentDocumentList.add(judgmentDocument);
            });
            document.put("judgmentDocument", judgmentDocumentList);
        }

        //裁判文书2
        Object judgmentObject = doc.get("judgment_document");
        List<Judgment> judgmentList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(judgmentObject)) {
            JSONArray judgmentJSONArray = JSON.parseArray(JSON.toJSONString(judgmentObject));
            judgmentJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                Judgment judgment = new Judgment();
                judgment.setEntId(doc.getString("_id"));
                judgment.setCaseName(itemJSONObject.getString("caseName"));
                judgment.setCaseCause(itemJSONObject.getString("caseCause"));
                judgment.setCaseNo(itemJSONObject.getString("caseNo"));
                String plaintiffDefendant = itemJSONObject.getString("plaintiff_defendant");
                if (!"-".equals(plaintiffDefendant)) {
                    judgment.setPlaintiffDefendant(plaintiffDefendant);
                }
                String caseMoney = itemJSONObject.getString("caseMoney");
                if (StringUtils.isNotBlank(caseMoney) && !"-".equals(caseMoney)) {
                    judgment.setCaseMoney(new BigDecimal(caseMoney));
                }
                String result = itemJSONObject.getString("result");
                if (!"-".equals(result)) {
                    judgment.setResult(result);
                }
                try{
                    judgment.setCaseDate(itemJSONObject.getDate("caseDate"));
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("caseDate"))) {
                        judgment.setCaseDate(null);
                    }
                }
                try{
                    judgment.setNoticeDate(itemJSONObject.getDate("noticeDate"));
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("noticeDate"))) {
                        judgment.setNoticeDate(null);
                    }
                }
                judgment.setContent(itemJSONObject.getString("content"));
                judgment.setEntName(entName);
                judgmentList.add(judgment);
            });
            document.put("judgment", judgmentList);
        }

        //变更记录
        Object changeRecordObject = doc.get("change_record");
        List<ChangeRecord> changeRecordList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(changeRecordObject)) {
            JSONArray changeRecordJSONArray = JSON.parseArray(JSON.toJSONString(changeRecordObject));
            changeRecordJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                ChangeRecord changeRecord = new ChangeRecord();
                changeRecord.setEntId(doc.getString("_id"));
                try{
                    changeRecord.setChangeDate(itemJSONObject.getDate("date"));
                }catch(Exception e){
                    if ("-".equals(itemJSONObject.getString("date"))) {
                        changeRecord.setChangeDate(null);
                    }
                }
                changeRecord.setProject(itemJSONObject.getString("project"));
                changeRecord.setOldData(itemJSONObject.getString("old_data"));
                changeRecord.setNewData(itemJSONObject.getString("new_data"));
                changeRecord.setEntName(entName);
                changeRecordList.add(changeRecord);
            });
            document.put("changeRecord", changeRecordList);
        }

        //持股比
        Object shareHolderObject = doc.get("shareholder");
        List<ShareHolder> shareHolderList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(shareHolderObject)) {
            JSONArray shareHolderJSONArray = JSON.parseArray(JSON.toJSONString(shareHolderObject));
            shareHolderJSONArray.forEach(item -> {
                JSONObject itemJSONObject = JSON.parseObject(JSON.toJSONString(item));
                ShareHolder shareHolder = new ShareHolder();
                shareHolder.setEntId(doc.getString("_id"));
                shareHolder.setShareHolderName(itemJSONObject.getString("shareholderName"));
                String shareholdingRatio = itemJSONObject.getString("shareholdingRatio");
                String substring = "";
                if (StringUtils.isNotBlank(shareholdingRatio)) {
                    int i = shareholdingRatio.indexOf("%");
                    substring = shareholdingRatio.substring(0, i + 1);
                }
                shareHolder.setShareHoldingRatio(substring);
                String shareholdingNumber = itemJSONObject.getString("shareholdingNumber");
                if (shareholdingNumber.contains("%")) {
                    shareholdingNumber = "";
                }else {
                    shareholdingNumber = shareholdingNumber.replaceAll("\\s*", "").replaceAll("[^(0-9).(0-9)*]", "");
                }
                if (shareholdingNumber.length() >= 5) {
                    shareholdingNumber = "";
                }
                shareHolder.setShareHoldingNumber(shareholdingNumber);
                shareHolder.setEntName(entName);
                shareHolderList.add(shareHolder);
            });
            document.put("shareHolder", shareHolderList);
        }

        //曾用名
        Object industryInfo = doc.get("industry_info");
        JSONObject industryInfoJsonObject = JSON.parseObject(JSON.toJSONString(industryInfo));

        List<EntFormerName> entFormerNameList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(industryInfoJsonObject)) {
            if (ObjectUtils.isNotEmpty(industryInfoJsonObject.get("曾用名"))) {
                Object oldEntNamesObject = industryInfoJsonObject.get("曾用名");
                if (ObjectUtils.isNotEmpty(oldEntNamesObject)) {
                    String oldEntNamesString = oldEntNamesObject.toString();
                    if (!"-".equals(oldEntNamesString)) {
                        List<String> stringList = Arrays.asList(oldEntNamesString.split("公司"));
                        stringList.forEach(item -> {
                            EntFormerName entFormerName = new EntFormerName();
                            entFormerName.setEntId(doc.getString("_id"));
                            entFormerName.setFormerName(item + "公司");
                            entFormerName.setEntName(entName);
                            entFormerNameList.add(entFormerName);
                        });
                        document.put("entFormerName", entFormerNameList);
                    }
                }
            }
        }

        return document;
    }

}

