package com.zgg.batch1.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Document(collection = "v1_enterprise")
public class Enterprise implements Serializable{

    @Id
    @Field(value = "_id")
    private String id;
    /**
     * 公司名称
     */
    private String enterpriseName;
    /**
     * 曾用名
     * */
    private String formerNames;
    /**
     * 标签
     */
    private String tags;
    /**
     * 法人
     */
    private String legalPerson;
    /**
     * 注册资金
     */
    private BigDecimal registerMoney1;
    String registerMoney = registerMoney1.toString();
    /**
     * 注册资金单位
     */
    private String registerMoneyUnit;
    /**
     * 企业状态
     */
    private String enterpriseStatus;
    /**
     * 注册日期
     */
    private Date registerDate1;
    String registerDate = registerDate1.toString();
    /**
     * 工商注册号
     */
    private String registerNumber;
    /**
     * 组织机构代码
     */
    private String organizationCode;
    /**
     * 统一信用代码
     */
    private String creditCode;
    /**
     * 企业类型
     */
    private String enterpriseType;
    /**
     * 纳税人识别号
     */
    private String taxpayerCode;
    /**
     * 进出口代码
     */
    private String importExportCode;
    /**
     * 行业
     */
    private String industry;
    /**
     * 营业期限
     */
    private String businessTerm;
    /**
     * 核准日期
     */
    private Date checkDate1;
    String checkDate = checkDate1.toString();
    /**
     * 登记机关
     */
    private String registerAuthority;
    /**
     * 编制人数
     */
    private String staffSize;
    /**
     * 参保人数
     */
    private Integer insuredNumber1;
    String insuredNumber = insuredNumber1.toString();
    /**
     * 地址
     */
    private String address;
    /**
     * 经营范围
     */
    private String businessScope;
    /**
     * 手机号码
     */
    private String phoneNumbers;
    /**
     * 电子邮箱
     */
    private String email;
    /**
     * 网址
     */
    private String website;
    /**
     * 简介
     */
    private String introduce;
    /**
     * 融资轮次
     */
    private String financeRounds;
    /**
     * 是否高新企业
     */
    private Boolean highTechFlag;
    /**
     * 是否专精特新
     */
    private Boolean perfectSpecialNewFlag;
    /**
     * 是否独角兽
     */
    private Boolean unicornFlag;
    /**
     * 是否瞪羚企业
     */
    private Boolean gazelleFlag;
    /**
     * 是否是金种子企业
     */
    private Boolean goldenSeedFlag;
    /**
     * 专利数量
     */
    private Integer patentCount;
    /**
     * 商标数量
     */
    private Integer brandCount;
    /**
     * 软件著作权数量
     */
    private Integer softwareCopyrightCount;
    /**
     * 作品著作权数量
     */
    private Integer workCopyrightCount;

}
