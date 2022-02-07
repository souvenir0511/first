package com.zgg.batch.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "v1_policy") /**政策数据**/
public class PolicyMongo implements Serializable {
    @Id
    @Field(value = "_id")
    private String id;
    private String policyId; /** 政策id **/
    private String peName; /** 项目名称 **/
    private String policyLevel; /** 项目级别  0国家级、1省级、2市级、3区级 **/
    private String province; /** 省 **/
    private String city; /** 市 **/
    private String area; /** 区 **/
    private List<String> deptFullName; /** 受理部门 **/
    private String seTime = "" ; /** 发布时间 **/
    private String seStartTime = ""; /** 申报时间提取出来的开始时间 **/
    private String seEndTime = ""; /** 申报时间提取出来的结束时间 **/
    private List<String> category; /** 项目类别 **/
    private List<String> technical; /** 技术领域 **/
    private String poStatus; /** 申报状态 **/
    private String declareConditions; /** 申报条件 **/
    private String support; /** 支持力度 **/
    private String filingMaterials; /** 申报材料 **/
    private List<PolicyProject> projectSources; /** 项目来源 **/
    private List<Map<String,String>> projectNews; /** 项目相关新闻 **/
    private String projectAnalysis; /** 项目解读分析 **/
}
