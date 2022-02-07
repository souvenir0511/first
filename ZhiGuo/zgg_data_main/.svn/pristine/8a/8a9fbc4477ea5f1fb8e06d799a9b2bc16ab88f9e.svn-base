package com.zgg.batch.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class V2Policy implements Serializable {

	private String policyId; /** 政策id **/
    private String peName; /** 项目名称 **/
    private String peNameText; /** 项目名称拆词 **/
	private String policyLevel; /** 项目级别  0国家级、1省级、2市级、3区级 **/
    private String province; /** 省 **/
    private String city; /** 市 **/
	private String area; /** 区 **/
    private List<String> deptFullName; /** 受理部门 **/
    private String seTime = ""; /** 发布时间 **/
    private String seStartTime = ""; /** 申报时间提取出来的开始时间 **/
    private String seEndTime = ""; /** 申报时间提取出来的结束时间 **/
    private List<String> category; /** 项目类别 **/
    private List<String> technical; /** 技术领域 **/
    private String poStatus; /** 申报状态 **/

    private Integer regMoney; /** 注册资本 **/
    private Integer years; /** 成立年限 **/
    //private String entType; /** 企业类型 **/
    //private String entNature; /** 企业性质 **/
    private Integer entQualifications; /** 资质要求 **/
    private Integer capabilities; /** 技术能力 **/
    private Integer faMingCount; /** 发明专利数 **/
    private Integer shiYongCount; /** 实用新型数 **/
    private Integer waiGuanCount; /** 外观设计数 **/
    //private Integer ipCount; /** 知产总数 **/
    //private Integer brandCount; /** 商标数 **/
    //private Integer patentCount; /** 专利数 **/
    private Integer softRightCount; /** 软件著作权数 **/
    private Integer peoples; /** 企业人数 **/
    private Integer revenue; /** 上年营收 **/
    private Integer education; /** 学历要求 0较高、1一般 **/
    private Integer rdInvest; /** 上年研发投入 **/
    private Integer top; /** 是否置顶 0置顶 1、不置顶 **/

    private Integer ipScore;
    private Integer baseScore;
    private Integer otherScore;
}