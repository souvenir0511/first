package com.zgg.batch.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;

@Data
@Document(collection = "enterprise")
public class EnterpriseOld implements Serializable,Cloneable{
    /**
     * 唯一标识
     */
    @Id
    @Field(value = "_id")
    private String id;

    /**
     * md5
     */
    private String md5;

    /**
     * 公司logo
     */
    private String orgLogo;

    /**
     * 企业名称
     */
    private String orgName;

    /**
     * 曾用名
     */
    private List<String> oldOrgNames;

    /**
     * 法人头像
     */
    private String corporationImage;

    /**
     * 电话
     */
    private String telphone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 网址
     */
    private String website;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 企业法人
     */
    private String corporation;

    /**
     * 注册资本
     */
    private String regCapital;

    /**
     * 注册资本单位      mongo直接存中文       solr<!-- 注册资本单位 0:万人民币  1:万美元 -->
     */
    private String regCapitalUnit;

    /**
     * 注册时间
     */
    private String regDate;

    /**
     * 公司状态
     */
    private String enterpriseStatus;

    /**
     * 工商注册号
     */
    private String businessRegCode;

    /**
     * 组织机构代码
     */
    private String orgCode;

    /**
     * 统一信用代码
     */
    private String creditCode;

    /**
     * 公司类型
     */
    private String enterpriseType;

    /**
     * 纳税人识别号
     */
    private String taxpayerIdNo;

    /**
     * 行业
     */
    private String industry;

    /**
     * 从业人数
     */
    private String engagedNumber;

    /**
     * 营业期限
     */
    private String businessTerm;

    /**
     * 核准日期
     */
    private String checkDate;

    /**
     * 登记机关
     */
    private String registrationAuthority;


    /**
     * 英文名称
     */
    private String orgNameEn;

    /**
     * 注册地址
     */
    private String address;

    /**
     * 经营范围
     */
    private String businessScope;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String area;

    /**
     * 邮编
     */
    private String areaCode;

    /**
     * 工商官网快照
     */
    private String snapshot;


    /**
     * 法人类型
     */
    private String corporationType;
    /**
     * 法人类型为企业时，对应的企业id
     */
    private String corporationEnterpriseId;

    /**
     * 企业行业编码门类
     */
    private String industryPhy;

    /**
     * 企业行业编码大类
     */
    private String industryBig;

    /**
     * 是否高新企业
     */
    private boolean isGaoXin;

    /**
     * 高新企业过期时间，如果没值或在今天之前，则不是高新企业
     */
    private String gaoXinExpireDate;

    /**
     * 高新企业开始年份
     */
    private String gaoXinStartYear;

    public Integer ipoType;

    public String ipoCode;
    public Integer hasIpo;
    public List<String> analysisIndustries;
    private String industryCode;

    /**
     * 纬度
     */
    private String lat;

    /**
     * 经度
     */
    private String lon;
}
