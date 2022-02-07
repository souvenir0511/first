package com.zgg.batch.utils;



import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zgg.batch.entity.PolicyCacl;
import com.zgg.batch.entity.V2Policy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PolicyUtils {

    public static Map<String, Integer> caclScore(V2Policy queryVo) throws Exception {
        Map<String, Integer> params = new HashMap<>();
        Integer ipScore = PolicyUtils.checkModule("ip", queryVo);
        Integer baseScore = PolicyUtils.checkModule("base", queryVo);
        Integer otherScore = PolicyUtils.checkModule("other", queryVo);
        Map<String, List<PolicyCacl>> maps = getMapPolicyCacl();
        if(ipScore == 0){
            params.put("ipScore", ipScore);
        }else {
            Integer faMingScore = PolicyUtils.getScoreByInterval(maps, "faMingCount", queryVo.getFaMingCount());
            Integer shiYongScore = PolicyUtils.getScoreByInterval(maps, "shiYongCount", queryVo.getShiYongCount());
            Integer waiGuanScore = PolicyUtils.getScoreByInterval(maps, "waiGuanCount", queryVo.getWaiGuanCount());
            Integer softRightCaclScore = PolicyUtils.getScoreByInterval(maps, "softRightCount", queryVo.getSoftRightCount());
            ipScore = faMingScore + shiYongScore + waiGuanScore+ softRightCaclScore;
            params.put("ipScore", ipScore);
        }

        if(baseScore == 0){
            params.put("baseScore", baseScore);
        }else {
            Integer entQualificationsScore = PolicyUtils.getScoreByInterval(maps, "entQualifications", queryVo.getEntQualifications());
            Integer capabilitiesScore = PolicyUtils.getScoreByInterval(maps, "capabilities", queryVo.getCapabilities());
            baseScore = entQualificationsScore + capabilitiesScore;
            params.put("baseScore", baseScore);
        }

        if(otherScore == 0){
            params.put("otherScore", otherScore);
        }else {
            Integer peoplesScore = PolicyUtils.getScoreByInterval(maps, "peoples", queryVo.getPeoples());
            Integer revenueScore = PolicyUtils.getScoreByInterval(maps, "revenue", queryVo.getRevenue());
            Integer rdInvestScore = PolicyUtils.getScoreByInterval(maps, "rdInvest", queryVo.getRdInvest());
            Integer educationScore = PolicyUtils.getScoreByInterval(maps, "education", queryVo.getEducation());
            /*if(queryVo.getEducation() == 0){
                educationScore = maps.get("educationScore").
            }*/
            otherScore = peoplesScore + revenueScore + rdInvestScore + educationScore;
            params.put("otherScore", otherScore);
        }
        return params;
    }

    public static Integer checkModule(String module, V2Policy queryVo){
        Integer score = 1;
        switch (module){
            case "ip":
                if((queryVo.getFaMingCount() == null || queryVo.getFaMingCount() == 0)
                        && (queryVo.getShiYongCount() == null || queryVo.getShiYongCount() == 0)
                        && (queryVo.getWaiGuanCount() == null || queryVo.getWaiGuanCount() == 0)
                        && (queryVo.getSoftRightCount() == null || queryVo.getSoftRightCount() == 0)){
                    score = 0;
                }
                break;
            case "base":
                if((queryVo.getCapabilities() == null || queryVo.getCapabilities() == 0)
                    && (queryVo.getEntQualifications() == null || queryVo.getEntQualifications() == 0)){
                    score = 0;
                }
                break;
            case "other":
                if((queryVo.getPeoples() == null || queryVo.getPeoples() == 0)
                    && (queryVo.getRevenue() == null || queryVo.getRevenue() == 0)
                    && (queryVo.getRdInvest() == null || queryVo.getRdInvest() == 0)){
                    score = 0;
                }
                break;
        }
        return score;
    }

    public static Integer caclColumnsScore(String column, Integer count){
        Integer columnScore = 0;
        switch (column){
            case "ipCount": //知产数量
                break;
            case "brandCount": //商标数量
                break;
            case "patentCount": //专利数量
                break;
            case  "softRightCount": //软件著作数量
                break;
            case "peoples": //企业人数
                break;
            case  "revenue": //上年营收
                break;
            case "rdInvest": //上年研发投入
                break;
            case  "entQualifications": //企业资质
                break;
            case "capabilities": //技术能力
                break;
            default:
                break;
        }
        return columnScore;
    }

    public static Integer getScoreByInterval(Map caclMap, String column, Integer count){

        Object o = caclMap.get(column);
        List<PolicyCacl> policyCacls = JSON.parseObject(JSON.toJSONString(o), new TypeReference<List<PolicyCacl>>() {});
        Integer score = 0;
        if(policyCacls != null && policyCacls.size() > 0){
            for(PolicyCacl policyCalcScore : policyCacls){
                if("education".equalsIgnoreCase(column)){
                    if(count == policyCalcScore.getStartNum() && count == policyCalcScore.getEndNum()){
                        return policyCalcScore.getScore();
                    }else {
                        score = policyCalcScore.getDefaultScore();
                    }
                }else{
                    if(count == 0){
                        return policyCalcScore.getDefaultScore();
                    }
                    Integer start = policyCalcScore.getStartNum();
                    Integer end = policyCalcScore.getEndNum();
                    if(count >= start && count < end){
                        return policyCalcScore.getScore();
                    }else{
                        score = policyCalcScore.getDefaultScore();
                    }
                }

            }
        }
        return score;
    }

    public static Map getMapPolicyCacl() throws Exception{
        Map<String, List<PolicyCacl>> caclMaps = new HashMap<>();
        List<PolicyCacl> policyCacls = new ArrayList();
        String jsonStr = "{\"peoples\":[{\"defaultScore\":5,\"endNum\":100,\"keyP\":\"peoples\",\"score\":5,\"startNum\":1},{\"defaultScore\":5,\"endNum\":150,\"keyP\":\"peoples\",\"score\":8,\"startNum\":100},{\"defaultScore\":5,\"endNum\":500,\"keyP\":\"peoples\",\"score\":10,\"startNum\":150},{\"defaultScore\":5,\"endNum\":999999999,\"keyP\":\"peoples\",\"score\":12,\"startNum\":500}],\"waiGuanCount\":[{\"defaultScore\":1,\"endNum\":15,\"keyP\":\"waiGuanCount\",\"score\":1,\"startNum\":1},{\"defaultScore\":1,\"endNum\":25,\"keyP\":\"waiGuanCount\",\"score\":3,\"startNum\":15},{\"defaultScore\":1,\"endNum\":50,\"keyP\":\"waiGuanCount\",\"score\":5,\"startNum\":25},{\"defaultScore\":1,\"endNum\":999999999,\"keyP\":\"waiGuanCount\",\"score\":6,\"startNum\":50}],\"revenue\":[{\"defaultScore\":2,\"endNum\":100,\"keyP\":\"revenue\",\"score\":2,\"startNum\":1},{\"defaultScore\":2,\"endNum\":500,\"keyP\":\"revenue\",\"score\":4,\"startNum\":100},{\"defaultScore\":2,\"endNum\":1000,\"keyP\":\"revenue\",\"score\":6,\"startNum\":500},{\"defaultScore\":2,\"endNum\":999999999,\"keyP\":\"revenue\",\"score\":8,\"startNum\":1000}],\"education\":[{\"defaultScore\":2,\"endNum\":0,\"keyP\":\"education\",\"score\":8,\"startNum\":0},{\"defaultScore\":2,\"endNum\":1,\"keyP\":\"education\",\"score\":4,\"startNum\":1},{\"defaultScore\":2,\"endNum\":2,\"keyP\":\"education\",\"score\":2,\"startNum\":2}],\"capabilities\":[{\"defaultScore\":3,\"endNum\":3,\"keyP\":\"capabilities\",\"score\":3,\"startNum\":1},{\"defaultScore\":3,\"endNum\":5,\"keyP\":\"capabilities\",\"score\":5,\"startNum\":3},{\"defaultScore\":3,\"endNum\":999999999,\"keyP\":\"capabilities\",\"score\":10,\"startNum\":5}],\"softRightCount\":[{\"defaultScore\":1,\"endNum\":5,\"keyP\":\"softRightCount\",\"score\":1,\"startNum\":1},{\"defaultScore\":1,\"endNum\":20,\"keyP\":\"softRightCount\",\"score\":3,\"startNum\":5},{\"defaultScore\":1,\"endNum\":100,\"keyP\":\"softRightCount\",\"score\":5,\"startNum\":20},{\"defaultScore\":1,\"endNum\":999999999,\"keyP\":\"softRightCount\",\"score\":6,\"startNum\":100}],\"entQualifications\":[{\"defaultScore\":3,\"endNum\":3,\"keyP\":\"entQualifications\",\"score\":3,\"startNum\":1},{\"defaultScore\":3,\"endNum\":5,\"keyP\":\"entQualifications\",\"score\":5,\"startNum\":3},{\"defaultScore\":3,\"endNum\":999999999,\"keyP\":\"entQualifications\",\"score\":10,\"startNum\":5}],\"faMingCount\":[{\"defaultScore\":5,\"endNum\":3,\"keyP\":\"faMingCount\",\"score\":5,\"startNum\":1},{\"defaultScore\":5,\"endNum\":5,\"keyP\":\"faMingCount\",\"score\":15,\"startNum\":3},{\"defaultScore\":5,\"endNum\":10,\"keyP\":\"faMingCount\",\"score\":20,\"startNum\":5},{\"defaultScore\":5,\"endNum\":999999999,\"keyP\":\"faMingCount\",\"score\":30,\"startNum\":10}],\"rdInvest\":[{\"defaultScore\":2,\"endNum\":50,\"keyP\":\"rdInvest\",\"score\":2,\"startNum\":1},{\"defaultScore\":2,\"endNum\":100,\"keyP\":\"rdInvest\",\"score\":4,\"startNum\":50},{\"defaultScore\":2,\"endNum\":200,\"keyP\":\"rdInvest\",\"score\":6,\"startNum\":100},{\"defaultScore\":2,\"endNum\":999999999,\"keyP\":\"rdInvest\",\"score\":8,\"startNum\":200}],\"shiYongCount\":[{\"defaultScore\":3,\"endNum\":15,\"keyP\":\"shiYongCount\",\"score\":3,\"startNum\":1},{\"defaultScore\":3,\"endNum\":25,\"keyP\":\"shiYongCount\",\"score\":6,\"startNum\":15},{\"defaultScore\":3,\"endNum\":50,\"keyP\":\"shiYongCount\",\"score\":9,\"startNum\":25},{\"defaultScore\":3,\"endNum\":999999999,\"keyP\":\"shiYongCount\",\"score\":12,\"startNum\":50}]}";
        Map maps = (Map) JSON.parse(jsonStr);
        //Map<String,List<PolicyCacl>> map = JSONUtil.toBean(jsonStr, Map.class);
        return maps;
    }

    public static void main(String[] args) throws Exception{
        Map mapPolicyCacl = getMapPolicyCacl();
        Integer education = PolicyUtils.getScoreByInterval(mapPolicyCacl, "education", 3);
        System.out.println(education);
        /*Map mapPolicyCacl = getMapPolicyCacl();
        Object o = mapPolicyCacl.get("education");
        System.out.println(o);
        List<PolicyCacl> policyCacls = JSON.parseObject(JSON.toJSONString(mapPolicyCacl.get("education")), new TypeReference<List<PolicyCacl>>() {});
        for(PolicyCacl policyCacl : policyCacls){
            System.out.println(policyCacl);
        }
        System.out.println();*/

       // Integer ipCount = caclColumnsScore("ipCount", 0);
        //System.out.println("ipScore:"+ipCount);
    }
}
