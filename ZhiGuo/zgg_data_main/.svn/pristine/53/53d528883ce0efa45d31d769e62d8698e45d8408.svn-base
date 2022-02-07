package com.zgg.batch.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Entity;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.zgg.batch.entity.*;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DocumentToBeanUtil {
    private static final Logger logger = LoggerFactory.getLogger(DocumentToBeanUtil.class);

    public static Enterprise oldDocToEnt(Document doc){
        Enterprise enterprise = new Enterprise();
        String simpleId = IdUtil.simpleUUID();
        enterprise.setId(simpleId);
        enterprise.setEntId(simpleId);
        enterprise.setOldId(doc.getString("_id"));
        enterprise.setOrgLogo(doc.getString("orgLogo"));
        enterprise.setEntName(doc.getString("orgName"));
        enterprise.setOldEntNames(doc.getList("oldOrgNames", String.class));
        enterprise.setFaRen(doc.getString("corporation"));
        enterprise.setFaRenImage(doc.getString("corporationImage"));
        enterprise.setRegMoney(doc.getString("regCapital"));
        enterprise.setRegMoneyUnit(doc.getString("regCapitalUnit"));
        enterprise.setEntStatus(doc.getString("enterpriseStatus"));
        enterprise.setRegDate(doc.getString("regDate"));
        enterprise.setRegNumber(doc.getString("businessRegCode"));
        enterprise.setEntCode(doc.getString("orgCode"));
        enterprise.setCreditCode(doc.getString("creditCode"));
        enterprise.setEntType(doc.getString("enterpriseType"));
        enterprise.setTaxpayerCode(doc.getString("taxpayerIdNo"));
        enterprise.setImportEntCode(null);
        enterprise.setIndustry(doc.getString("industry"));
        enterprise.setCheckDate(doc.getString("checkDate"));
        enterprise.setRegistAuthority(doc.getString("registrationAuthority"));
        enterprise.setEntNameEn(doc.getString("orgNameEn"));
        enterprise.setEngagedNumber(doc.getString("engagedNumber"));
        enterprise.setInsuredNumber(null);
        enterprise.setAddress(doc.getString("address"));
        enterprise.setExperienceScope(doc.getString("businessScope"));
        enterprise.setTel(doc.getString("telphone"));
        enterprise.setEmail(doc.getString("email"));
        enterprise.setWebsite(doc.getString("website"));
        enterprise.setIntroduce(doc.getString("introduction"));
        enterprise.setProvince(doc.getString("province"));
        enterprise.setCity(doc.getString("city"));
        enterprise.setArea(doc.getString("area"));
        enterprise.setAreaCode(doc.getString("areaCode"));
        String lat = "";
        String lon = "";
        try{
            lat = doc.getDouble("lat")+"";
            lon = doc.getDouble("lon")+"";
        }catch(Exception e){
            e.printStackTrace();
            lat = doc.getString("lat");
            lon = doc.getString("lon");
        }
        System.out.println("lat:"+lat+"|lon:"+lon);
        enterprise.setLat(lat+"");
        enterprise.setLon(lon+"");
        enterprise.setSnapshotImag(doc.getString("snapshot"));
        enterprise.setFaRenType(doc.getString("corporationType"));
        enterprise.setFaRenEntId(doc.getString("corporationEnterpriseId"));
        enterprise.setIndustrySet(doc.getList("analysisIndustries", String.class));
        enterprise.setGaoXinExpireDate(doc.getString("gaoXinExpireDate"));
        enterprise.setGaoXinStartYear(doc.getString("gaoXinStartYear"));
        enterprise.setListedDate(null);
        enterprise.setListedYear(null);
        enterprise.setIndustryPhy(doc.getString("industryPhy"));
        enterprise.setIndustryBig(doc.getString("industryBig"));
        enterprise.setIndustryCode(doc.getString("industryCode"));
        return enterprise;
    }

    public static Document oldDocToNewDoc(Document doc){
        Document enterprise = new Document();
        String simpleId = IdUtil.simpleUUID();
        enterprise.put("_id", simpleId);
        enterprise.put("entId", simpleId);
        enterprise.put("oldId", doc.getString("_id"));
        enterprise.put("orgLogo", doc.getString("orgLogo"));
        String entName = doc.getString("orgName");
        entName = entName.replace("(", "（").replace(")", "）");
        enterprise.put("entName", entName);
        enterprise.put("oldEntNames", doc.getList("oldOrgNames", String.class));
        enterprise.put("faRen", doc.getString("corporation"));
        enterprise.put("faRenImage", doc.getString("corporationImage"));
        enterprise.put("regMoney", doc.getString("regCapital"));

        enterprise.put("regMoneyUnit", doc.getString("regCapitalUnit"));
        enterprise.put("entStatus", doc.getString("enterpriseStatus"));
        enterprise.put("regDate", doc.getString("regDate"));
        enterprise.put("regNumber", doc.getString("businessRegCode"));
        enterprise.put("entCode", doc.getString("orgCode"));
        enterprise.put("creditCode", doc.getString("creditCode"));
        enterprise.put("entType", doc.getString("enterpriseType"));
        enterprise.put("taxpayerCode", doc.getString("taxpayerIdNo"));
        enterprise.put("importEntCode", "");
        enterprise.put("industry", doc.getString("industry"));
        enterprise.put("businessTerm", doc.getString("businessTerm"));
        enterprise.put("checkDate", doc.getString("checkDate"));
        enterprise.put("registAuthority", doc.getString("registrationAuthority"));
        enterprise.put("entNameEn", doc.getString("orgNameEn"));
        enterprise.put("engagedNumber", doc.getString("engagedNumber"));
        enterprise.put("insuredNumber", "");
        enterprise.put("address", doc.getString("address"));
        enterprise.put("experienceScope", doc.getString("businessScope"));
        enterprise.put("tel", doc.getString("telphone"));
        enterprise.put("email", doc.getString("email"));
        enterprise.put("website", doc.getString("website"));
        enterprise.put("introduce", doc.getString("introduction"));
        enterprise.put("province", doc.getString("province"));
        enterprise.put("city", doc.getString("city"));
        enterprise.put("area", doc.getString("area"));
        enterprise.put("areaCode", doc.getString("areaCode"));
        String lat = "";
        String lon = "";
        try{
            lat = doc.getDouble("lat")+"";
            lon = doc.getDouble("lon")+"";
        }catch(Exception e){
            //e.printStackTrace();
            lat = doc.getString("lat");
            lon = doc.getString("lon");
        }
        enterprise.put("lat", lat);
        enterprise.put("lon", lon);
        enterprise.put("snapshotImag", doc.getString("snapshot"));
        enterprise.put("faRenType", doc.getString("corporationType"));
        enterprise.put("faRenEntId", doc.getString("corporationEnterpriseId"));
        enterprise.put("industrySet", doc.getList("analysisIndustries", String.class));
        enterprise.put("gaoXinExpireDate", doc.getString("gaoXinExpireDate"));
        enterprise.put("gaoXinStartYear", doc.getString("gaoXinStartYear"));
        enterprise.put("listedDate","");
        enterprise.put("listedYear","");
        enterprise.put("industryPhy", doc.getString("industryPhy"));
        enterprise.put("industryBig", doc.getString("industryBig"));
        enterprise.put("industryCode", doc.getString("industryCode"));
        return enterprise;
    }

    public static EnterpriseES newDocToEntEs(Document doc){
        EnterpriseES enterprise = new EnterpriseES();
        try {
            enterprise.setId(doc.getString("_id"));
            String entName = doc.getString("entName");
            if(StringUtils.isEmpty(entName)){
                System.out.println("_id:"+doc.getString("_id"+"|entName:"+entName));
            }else{
                entName = entName.replace("(", "（").replace(")", "）");
            }
            enterprise.setEntName(entName);
            enterprise.setEntNameText(entName);
            enterprise.setEntNameOld(doc.getList("oldEntNames", String.class));
            enterprise.setFaRen(doc.getString("faRen"));
            String regMoneyDoc = StrUtil.cleanBlank(doc.getString("regMoney")).replaceAll(",","");
            BigDecimal regMoney = null;
            BigDecimal divideNumber= new BigDecimal("10000");
            if (StringUtils.isNotBlank(regMoneyDoc)) {
                if (regMoneyDoc.contains("人民币元")) {
                    regMoney = new BigDecimal(regMoneyDoc.replace("人民币元", "").replaceAll("\\s*", "")).divide(divideNumber, 20, BigDecimal.ROUND_HALF_UP);
                } else if (regMoneyDoc.contains("元人民币")) {
                    regMoney = new BigDecimal(regMoneyDoc.replace("元人民币", "").replaceAll("\\s*", "")).divide(divideNumber, 20, BigDecimal.ROUND_HALF_UP);
                }else {
                    //去掉除小数点外的其他字符
                    String regMoneyString = regMoneyDoc.replaceAll("[^0-9.]", "");
                    if (StringUtils.isNotBlank(regMoneyString)) {
                        regMoney = new BigDecimal(regMoneyString);
                    }
                }
            }
            enterprise.setRegMoney(regMoney);
            enterprise.setRegMoneyUnit(doc.getString("regMoneyUnit"));
            enterprise.setEntStatus(doc.getString("entStatus"));
            String regDate = doc.getString("regDate");
            enterprise.setRegDate(regDate);
            enterprise.setCreditCode(doc.getString("creditCode"));
            enterprise.setEntType(doc.getString("entType"));
            enterprise.setIndustry(doc.getString("industry"));
            enterprise.setAddress(doc.getString("address"));
            enterprise.setExperienceScope(doc.getString("experienceScope"));
            enterprise.setTel(doc.getString("tel"));
            enterprise.setEmail(doc.getString("email"));
            enterprise.setWebSite(doc.getString("webSite"));
            enterprise.setProvince(doc.getString("province"));
            enterprise.setCity(doc.getString("city"));
            enterprise.setArea(doc.getString("area"));
            String regYear = "";
            try{
                if(StringUtils.isNotEmpty(regDate) && !"null".equalsIgnoreCase(regDate) && !"-".equalsIgnoreCase(regDate)){
                    regYear = String.valueOf(DateUtil.year(DateUtil.parse(regDate)));
                }
            }catch (Exception e){
                regYear = "";
            }

            enterprise.setRegYear(regYear);
            enterprise.setIsUp(false);
            String gaoXinStartYear = doc.getString("gaoXinStartYear");
            boolean isGaoxin = false;
            if(StringUtils.isNotEmpty(gaoXinStartYear) && !"null".equalsIgnoreCase(gaoXinStartYear)){
                isGaoxin = true;
            }
            enterprise.setIsGaoxin(doc.getBoolean("gjGaoXin"));
            enterprise.setGaoXinYear(StringUtils.isNotEmpty(gaoXinStartYear)? gaoXinStartYear : "");
            enterprise.setBrandCount(doc.getInteger("brandCount"));
            enterprise.setPatentCount(doc.getInteger("patentCount"));
            enterprise.setSoftwareCopyrightCount(doc.getInteger("softwareCopyrightCount"));
            enterprise.setCopyrightCount(doc.getInteger("copyrightCount"));
            enterprise.setCopyrightCount(doc.getInteger("copyrightCount"));
            List<String> industrySet = doc.getList("industrySet", String.class);
            if(industrySet == null){
                industrySet = Lists.newArrayList();
                if (StringUtils.isNotEmpty(doc.getString("industry"))){
                    industrySet.add(doc.getString("industry"));
                }
            }
            enterprise.setIndustrySet(industrySet);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("报错Id："+doc.getString("_id"));
        }


        return enterprise;
    }

    public static Document oldEntToNewDoc(EnterpriseOld doc){
        Document enterprise = new Document();
        String simpleId = IdUtil.simpleUUID();
        enterprise.put("_id", simpleId);
        enterprise.put("entId", simpleId);
        enterprise.put("oldId", doc.getId());
        enterprise.put("orgLogo", doc.getOrgLogo());
        String entName = doc.getOrgName();
        entName = entName.replace("(", "（").replace(")", "）");
        enterprise.put("entName", entName);
        enterprise.put("oldEntNames", doc.getOldOrgNames());
        enterprise.put("faRen", doc.getCorporation());
        enterprise.put("faRenImage", doc.getCorporationImage());
        enterprise.put("regMoney", doc.getRegCapital());

        enterprise.put("regMoneyUnit", doc.getRegCapitalUnit());
        enterprise.put("entStatus", doc.getEnterpriseStatus());
        enterprise.put("regDate", doc.getRegDate());
        enterprise.put("regNumber", doc.getBusinessRegCode());
        enterprise.put("entCode", doc.getOrgCode());
        enterprise.put("creditCode", doc.getCreditCode());
        enterprise.put("entType", doc.getEnterpriseType());
        enterprise.put("taxpayerCode", doc.getTaxpayerIdNo());
        enterprise.put("importEntCode", "");
        enterprise.put("industry", doc.getIndustry());
        enterprise.put("businessTerm", doc.getBusinessTerm());
        enterprise.put("checkDate", doc.getCheckDate());
        enterprise.put("registAuthority", doc.getRegistrationAuthority());
        enterprise.put("entNameEn", doc.getOrgNameEn());
        enterprise.put("engagedNumber", doc.getEngagedNumber());
        enterprise.put("insuredNumber", "");
        enterprise.put("address", doc.getAddress());
        enterprise.put("experienceScope", doc.getBusinessScope());
        enterprise.put("tel", doc.getTelphone());
        enterprise.put("email", doc.getEmail());
        enterprise.put("website", doc.getWebsite());
        enterprise.put("introduce", doc.getIntroduction());
        enterprise.put("province", doc.getProvince());
        enterprise.put("city", doc.getCity());
        enterprise.put("area", doc.getArea());
        enterprise.put("areaCode", doc.getAreaCode());
        String lat = doc.getLat();
        String lon = doc.getLon();
        enterprise.put("lat", lat);
        enterprise.put("lon", lon);
        enterprise.put("snapshotImag", doc.getSnapshot());
        enterprise.put("faRenType", doc.getCorporationType());
        enterprise.put("faRenEntId", doc.getCorporationEnterpriseId());
        enterprise.put("industrySet", doc.getAnalysisIndustries());
        enterprise.put("gaoXinExpireDate", doc.getGaoXinExpireDate());
        enterprise.put("gaoXinStartYear", doc.getGaoXinStartYear());
        enterprise.put("listedDate","");
        enterprise.put("listedYear","");
        enterprise.put("industryPhy", doc.getIndustryPhy());
        enterprise.put("industryBig", doc.getIndustryBig());
        enterprise.put("industryCode", doc.getIndustryCode());
        return enterprise;
    }

    public static Policy docToPolicy(Document doc) throws Exception{
        Policy policy = new Policy();
        String id = doc.getString("_id");
        policy.setPolicyId(id);
        String peName = doc.getString("peName");
        try{
            peName = peName.replace("(", "（").replace(")", "）");
        }catch (Exception e){
            logger.info("---------------------"+policy.getPolicyId());
            peName = "";
        }
        policy.setPeName(peName);
        policy.setPeNameText(peName);
        String policyLevel = doc.getString("policyLevel");
        policy.setPolicyLevel(policyLevel);
        if("4".equalsIgnoreCase(policyLevel)){
            policyLevel = "市级";
        }
        policy.setPolicyLevel(policyLevel);
        String province = doc.getString("province");
        String city = doc.getString("city");
        String area = doc.getString("area");
        if("直辖市".equalsIgnoreCase(province)){
            province = city.endsWith("市")?city:city+"市";
        }

        if(StringUtils.isNotEmpty(province) && !province.endsWith("省") && !"北京市".equalsIgnoreCase(province) && !"天津市".equalsIgnoreCase(province) && !"上海市".equalsIgnoreCase(province) && !"重庆市".equalsIgnoreCase(province)
                && !"北京".equalsIgnoreCase(province) && !"天津".equalsIgnoreCase(province) && !"上海".equalsIgnoreCase(province) && !"重庆".equalsIgnoreCase(province)){
            province = province +"省";
        }

        policy.setProvince(province);

        if(StringUtils.isNotEmpty(city) && !city.endsWith("市")){
            city = city +"市";
        }
        policy.setCity(city);

        if(StringUtils.isNotEmpty(area) && !area.endsWith("区")){
            area = area +"区";
        }
        policy.setArea(area);
        policy.setDeptFullName(doc.getList("deptFullName",String.class));
        policy.setSeTime(doc.getString("seTime"));
        policy.setSeStartTime(doc.getString("seStartTime"));
        policy.setSeEndTime(doc.getString("seEndTime"));
        policy.setCategory(doc.getList("category", String.class));
        policy.setTechnical(doc.getList("technical", String.class));
        policy.setPoStatus(doc.getString("poStatus"));
        //policy.setRegMoney(RandomUtil.randomInt(100,200000));
       // policy.setYears(RandomUtil.randomInt(1,15));
       // V2Policy v2Policy = new V2Policy();

       // String[] entType = {"有限责任公司","股份有限公司","国企"};
       // v2Policy.setEntType(entType[RandomUtil.randomInt(0,3)]);
        //String[] entNature = {"科技型企业","瞪羚羊企业","专精特新企业","雏鹰企业","独角兽企业","牛羚企业"};
       // v2Policy.setEntNature(entNature[RandomUtil.randomInt(0,6)]);
       /* List<String> entList1 = Lists.newArrayList("独角兽企业","高新技术企业");
        List<String> entList2 = Lists.newArrayList("测试1","高新技术企业","测试2");
        List<String> entList3 = Lists.newArrayList("CMMI认证企业","高新技术企业","测试2");
        List<String> [] list = new ArrayList[3];
        list[0] = entList1;
        list[1] = entList2;
        list[2] = entList3;*/
        //v2Policy.setEntQualifications(RandomUtil.randomInt(0,8));
        /*List<String> ent1 = Lists.newArrayList("工程技术研究中心","工程实验室");
        List<String> ent2 = Lists.newArrayList("测试能力1","工业设计中心","工程实验室");
        List<String> ent3 = Lists.newArrayList("企业技术中心","工业设计中心","院士工作站");
        List<String> [] capabilities = new ArrayList[3];
        capabilities[0] = ent1;
        capabilities[1] = ent2;
        capabilities[2] = ent3;*/
        //v2Policy.setCapabilities(RandomUtil.randomInt(0,8));
        //Integer faMingCount = RandomUtil.randomInt(0,20);
        //Integer shiYongCount = RandomUtil.randomInt(0,100);
       // Integer softRightCount = RandomUtil.randomInt(10,50);
       // v2Policy.setFaMingCount(faMingCount);
        //v2Policy.setWaiGuanCount(shiYongCount);
        //v2Policy.setShiYongCount(shiYongCount);
        //v2Policy.setSoftRightCount(softRightCount);
        //v2Policy.setPeoples(RandomUtil.randomInt(20,1000));
        //v2Policy.setRevenue(RandomUtil.randomInt(50,1500));
        //v2Policy.setRdInvest(RandomUtil.randomInt(10,300));
        //v2Policy.setEducation(RandomUtil.randomInt(0,3));
        //Map<String, Integer> stringIntegerMap = PolicyUtils.caclScore(v2Policy);
       // policy.setIpScore(Integer.valueOf(stringIntegerMap.get("ipScore")));
       // policy.setBaseScore(Integer.valueOf(stringIntegerMap.get("baseScore")));
        //policy.setOtherScore(Integer.valueOf(stringIntegerMap.get("otherScore")));
        policy.setTop(RandomUtil.randomInt(0,2));

        return policy;
    }
    public static PolicyMysql docToPolicyMySql(Document doc) {
        PolicyMysql policy = new PolicyMysql();
        String id = doc.getString("_id");
        policy.setId(id);
        String peName = doc.getString("peName");
        try{
            peName = peName.replace("(", "（").replace(")", "）");
        }catch (Exception e){
            logger.info("---------------------"+policy.getId());
            peName = "";
        }
        policy.setProjectName(peName);
        String policyLevel = doc.getString("policyLevel");
        policy.setPolicyLevel(policyLevel);
        if("4".equalsIgnoreCase(policyLevel)){
            policyLevel = "市级";
        }
        policy.setPolicyLevel(policyLevel);
        String province = doc.getString("province");
        String city = doc.getString("city");
        String area = doc.getString("area");
        if("直辖市".equalsIgnoreCase(province)){
            province = city.endsWith("市")?city:city+"市";
        }

        if(StringUtils.isNotEmpty(province) && !province.endsWith("省") && !"北京市".equalsIgnoreCase(province) && !"天津市".equalsIgnoreCase(province) && !"上海市".equalsIgnoreCase(province) && !"重庆市".equalsIgnoreCase(province)
                && !"北京".equalsIgnoreCase(province) && !"天津".equalsIgnoreCase(province) && !"上海".equalsIgnoreCase(province) && !"重庆".equalsIgnoreCase(province)){
            province = province +"省";
        }

        policy.setProvinceName(province);

        if(StringUtils.isNotEmpty(city) && !city.endsWith("市")){
            city = city +"市";
        }
        policy.setCityName(city);

        if(StringUtils.isNotEmpty(area) && !area.endsWith("区")){
            area = area +"区";
        }
        policy.setAreaName(area);
        policy.setSourceInfo(JSON.toJSONString(doc.getList("projectSources", Object.class)));
        policy.setGovDepartmentName(JSON.toJSONString(doc.getList("deptFullName",String.class)));
        policy.setDeclareCondition(doc.getString("declareConditions"));
        policy.setSupportDegree(doc.getString("support"));
        policy.setDeclareMaterialMethod(doc.getString("filingMaterials"));
        policy.setOfficialPublishTime(!"待定".equals(doc.getString("seTime"))? doc.getString("seTime"):null);
        policy.setDeclareStartTime(!"待定".equals(doc.getString("seStartTime"))? doc.getString("seStartTime"):null);
        policy.setDeclareEndTime(!"待定".equals(doc.getString("seEndTime"))? doc.getString("seEndTime"):null);
        policy.setProjectType(JSON.toJSONString(doc.getList("category", String.class)));
        policy.setTechField(JSON.toJSONString(doc.getList("technical", String.class)));
        policy.setPolicyStatus(doc.getString("poStatus"));
        return policy;
    }

    public static V2Policy docToV2Policy(Document doc) throws Exception {
        V2Policy policy = new V2Policy();
        String id = doc.getString("_id");
        policy.setPolicyId(id);
        String peName = doc.getString("peName");
        try{
            peName = peName.replace("(", "（").replace(")", "）");
        }catch (Exception e){
            logger.info("---------------------"+policy.getPolicyId());
            peName = "";
        }
        policy.setPeName(peName);
        policy.setPeNameText(peName);
        String policyLevel = doc.getString("policyLevel");
        policy.setPolicyLevel(policyLevel);
        if("4".equalsIgnoreCase(policyLevel)){
            policyLevel = "市级";
        }
        policy.setPolicyLevel(policyLevel);
        String province = doc.getString("province");
        String city = doc.getString("city");
        String area = doc.getString("area");
        if("直辖市".equalsIgnoreCase(province)){
            province = city.endsWith("市")?city:city+"市";
        }

        if(StringUtils.isNotEmpty(province) && !province.endsWith("省") && !"北京市".equalsIgnoreCase(province) && !"天津市".equalsIgnoreCase(province) && !"上海市".equalsIgnoreCase(province) && !"重庆市".equalsIgnoreCase(province)
                && !"北京".equalsIgnoreCase(province) && !"天津".equalsIgnoreCase(province) && !"上海".equalsIgnoreCase(province) && !"重庆".equalsIgnoreCase(province)){
            province = province +"省";
        }

        policy.setProvince(province);

        if(StringUtils.isNotEmpty(city) && !city.endsWith("市")){
            city = city +"市";
        }
        policy.setCity(city);

        if(StringUtils.isNotEmpty(area) && !area.endsWith("区")){
            area = area +"区";
        }
        policy.setArea(area);
        policy.setDeptFullName(doc.getList("deptFullName",String.class));
        policy.setSeTime(doc.getString("seTime"));
        policy.setSeStartTime(doc.getString("seStartTime"));
        policy.setSeEndTime(doc.getString("seEndTime"));
        policy.setCategory(doc.getList("category", String.class));
        policy.setTechnical(doc.getList("technical", String.class));
        policy.setPoStatus(doc.getString("poStatus"));

        policy.setRegMoney(RandomUtil.randomInt(100,200000));
        policy.setYears(RandomUtil.randomInt(1,15));
       /* String[] entType = {"有限责任公司","股份有限公司","国企"};
        policy.setEntType(entType[RandomUtil.randomInt(0,3)]);
        String[] entNature = {"科技型企业","瞪羚羊企业","专精特新企业","雏鹰企业","独角兽企业","牛羚企业"};
        policy.setEntNature(entNature[RandomUtil.randomInt(0,6)]);
        List<String> entList1 = Lists.newArrayList("独角兽企业","高新技术企业");
        List<String> entList2 = Lists.newArrayList("测试1","高新技术企业","测试2");
        List<String> entList3 = Lists.newArrayList("CMMI认证企业","高新技术企业","测试2");
        List<String> [] list = new ArrayList[3];
        list[0] = entList1;
        list[1] = entList2;
        list[2] = entList3;*/
        policy.setEntQualifications(RandomUtil.randomInt(0,8));
        /*List<String> ent1 = Lists.newArrayList("工程技术研究中心","工程实验室");
        List<String> ent2 = Lists.newArrayList("测试能力1","工业设计中心","工程实验室");
        List<String> ent3 = Lists.newArrayList("企业技术中心","工业设计中心","院士工作站");
        List<String> [] capabilities = new ArrayList[3];
        capabilities[0] = ent1;
        capabilities[1] = ent2;
        capabilities[2] = ent3;*/
        policy.setCapabilities(RandomUtil.randomInt(0,8));
        Integer faMingCount = RandomUtil.randomInt(0,20);
        Integer shiYongCount = RandomUtil.randomInt(0,100);
        Integer softRightCount = RandomUtil.randomInt(10,50);
        policy.setFaMingCount(faMingCount);
        policy.setWaiGuanCount(shiYongCount);
        policy.setShiYongCount(shiYongCount);
        policy.setSoftRightCount(softRightCount);
        policy.setPeoples(RandomUtil.randomInt(20,1000));
        policy.setRevenue(RandomUtil.randomInt(50,1500));
        policy.setRdInvest(RandomUtil.randomInt(10,300));
        policy.setEducation(RandomUtil.randomInt(0,3));
        policy.setTop(RandomUtil.randomInt(0,2));

        Map<String, Integer> stringIntegerMap = PolicyUtils.caclScore(policy);
        policy.setIpScore(Integer.valueOf(stringIntegerMap.get("ipScore")));
        policy.setBaseScore(Integer.valueOf(stringIntegerMap.get("baseScore")));
        policy.setOtherScore(Integer.valueOf(stringIntegerMap.get("otherScore")));

        /*
        policy.setRegMoney(doc.getInteger("regMoney"));
        policy.setYears(doc.getInteger("years"));
        policy.setEntType(doc.getString("entType"));
        policy.setEntNature(doc.getString("entNature"));
        policy.setEntQualifications(doc.getList("entQualifications", String.class));
        policy.setCapabilities(doc.getList("capabilities", String.class));
        policy.setIpCount(doc.getInteger("ipCount"));
        policy.setBrandCount(doc.getInteger("brandCount"));
        policy.setPatentCount(doc.getInteger("patentCount"));
        policy.setSoftRightCount(doc.getInteger("softRightCount"));
        policy.setPeoples(doc.getInteger("peoples"));
        policy.setRevenue(doc.getInteger("revenue"));
        policy.setEducation(doc.getInteger("education"));
        */
        return policy;
    }

    public static Document rsToEntMysql(Entity doc){
        Document enterprise = new Document();
        String id = IdUtil.simpleUUID();
        enterprise.put("_id", id);
        enterprise.put("entId", id);
        String entName = doc.getStr("company_name");
        entName = entName.replace("(", "（").replace(")", "）");
        enterprise.put("entName", entName);
        enterprise.put("faRen", doc.getStr("faren"));
        enterprise.put("regDate", doc.getStr("create_data"));
        String regMoneyAll = doc.getStr("registry_money");
        enterprise.put("regMoney", regMoneyAll.replaceAll("\\s*","").replaceAll("[^(0-9).(0-9)*]",""));
        enterprise.put("regMoneyUnit", regMoneyAll.replaceAll("\\s*","").replaceAll("[^(\\u4e00-\\u9fa5)]",""));
        enterprise.put("entStatus", doc.getStr("status"));
        enterprise.put("province", doc.getStr("provice"));
        enterprise.put("city", doc.getStr("city"));
        enterprise.put("area", "");
        String companyCode = doc.getStr("company_code");
        String importEntCode = "";
        String entCode = "";
        String areaCode = doc.getStr("address_code");
        if(StringUtils.isNotEmpty(companyCode) && !"null".equalsIgnoreCase(companyCode)){
            if(companyCode.length()==18){
                String importPrefix = "";
                if(areaCode.length() != 5){
                    areaCode = areaCode + "00";
                }
                importPrefix = areaCode.substring(0,4);
                entCode = companyCode.substring(8,17);
                importEntCode = importPrefix + entCode;
            }
        }
        enterprise.put("entCode", entCode);
        enterprise.put("creditCode", companyCode);
        enterprise.put("taxpayerCode", companyCode);
        enterprise.put("importEntCode", "");
        enterprise.put("areaCode", areaCode);
        enterprise.put("tel", doc.getStr("phone"));

        return enterprise;
    }


    public static Document policyMongoToMongo(Document doc){
        Document newDoc = new Document();
        String simpleId = IdUtil.simpleUUID();
        String id = doc.getString("_id");
        newDoc.put("_id", simpleId);
        newDoc.put("policyId", simpleId);
        newDoc.put("peName", doc.getString("peName"));
        newDoc.put("area", doc.getString("area"));
        String policyLevel = doc.getString("policyLevel");
        String province = doc.getString("province");
        if("0".equals(policyLevel)){
            policyLevel = "国家级";
            province = "";
        }else if("1".equals(policyLevel)){
            policyLevel = "省级";
        }else if("2".equals(policyLevel)){
            policyLevel = "市级";
        }else if("3".equals(policyLevel)){
            policyLevel = "区级";
        }
        newDoc.put("policyLevel", policyLevel);
        newDoc.put("province", province);

        newDoc.put("city", doc.getString("city"));
        newDoc.put("area", doc.getString("area"));

        newDoc.put("deptFullName", doc.getList("deptFullName", String.class));
        newDoc.put("seTime", doc.getString("seTime"));
        newDoc.put("seStartTime", doc.getString("seStartTime"));
        newDoc.put("seEndTime", doc.getString("seEndTime"));
        List<String> category = null;
        try{
            category = doc.getList("category", String.class);
        }catch (Exception e){
           System.out.println("category error--------------"+simpleId+"|"+id);
            category = Lists.newArrayList();
        }
        newDoc.put("category", category);
        List<String> technical = null;
        try{
            technical = doc.getList("technical", String.class);
        }catch (Exception e){
            System.out.println("technical error--------------"+simpleId+"|"+id);
            technical = Lists.newArrayList();
        }
        newDoc.put("technical", technical);
        newDoc.put("poStatus", doc.getString("poStatus"));
        newDoc.put("declareConditions", doc.getString("declareConditions"));
        newDoc.put("support", doc.getString("support"));
        newDoc.put("filingMaterials", doc.getString("filingMaterials"));

        List<PolicyProject> projects = Lists.newArrayList();
        try{
            List<String> list = doc.getList("projectSources", String.class);

            list.stream().forEach(str ->{
                String[] split = str.split("\n");
                if(split != null && split.length>0){
                    PolicyProject project = new PolicyProject();
                    project.setTitle(split[0]);
                    project.setUrl(split[1]);
                    projects.add(project);
                }
            });
        }catch (Exception e){
            System.out.println("projects error--------------"+simpleId+"|"+id);
        }

        newDoc.put("projectSources", JSON.toJSON(projects));
        newDoc.put("projectNews", "");
        newDoc.put("projectAnalysis", doc.getString("projectAnalysis"));

        return newDoc;
    }


}

