package com.zgg.batch.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;

@Data
@Document(collection = "v1_enterprise")
public class Enterprise implements Serializable {
    @Id
    @Field(value = "_id")
    private String id;
    private String entId; /** entId **/
    private String oldId; /** 对应原始数据id **/
    private String orgLogo; /** 公司logo **/
    private String entName; /** 企业名称 **/
    private List<String> oldEntNames; /** 曾用名 **/
    private String faRen; /** 企业法人 **/
    private String faRenImage; /** 法人头像 **/
    private String regMoney; /** 注册资本 **/
    private String regMoneyUnit; /** 注册资本单位**/
    private String paidInMoney; /** 实缴金额 **/
    private String paidInMoneyUnit; /** 实缴金额单位 **/
    private String entStatus; /** 公司状态 **/
    private String regDate; /** 注册时间 **/
    private String regNumber; /** 工商注册号 **/
    private String entCode; /** 组织机构代码 **/
    private String creditCode; /** 统一信用代码 **/
    private String entType; /** 公司类型 **/
    private String taxpayerCode; /** 纳税人识别号 **/
    private String importEntCode; /** 进出口企业代码 **/
    private String industry; /** 行业 **/
    private String businessTerm; /** 营业期限 **/
    private String checkDate; /** 核准日期 **/
    private String registAuthority; /** 登记机关 **/
    private String entNameEn; /** 英文名称 **/
    private String engagedNumber; /** 从业人数 **/
    private String insuredNumber; /** 参保人数 **/
    private String address; /** 注册地址 **/
    private String experienceScope; /** 经营范围 **/
    private String tel; /** 电话 **/
    private String email; /** 邮箱 **/
    private String website; /** 网址 **/
    private String introduce; /** 简介 **/
    private String province; /** 省 **/
    private String city; /** 市 **/
    private String area; /** 区 **/
    private String areaCode; /** 邮编 **/
    private String lat; /** 纬度 x **/
    private String lon; /** 经度 y **/
    private String snapshotImag; /** 工商官网快照 **/
    private String faRenType; /** 法人类型  个人0  企业1 **/
    private String faRenEntId; /** 法人类型为企业时，对应的企业id **/
    private List<String> industrySet; /** 标签行业集 **/
    private String gaoXinExpireDate; /** 高新企业过期时间，如果没值或在今天之前，则不是高新企业 **/
    private String gaoXinStartYear; /** 高新企业开始年份 **/
    private String listedDate; /** 上市时间 如果没有值说明企业没有上市 **/
    private String listedYear; /** 上市年份 **/
    private String industryPhy; /** 企业行业编码门类 **/
    private String industryBig;/** 企业行业编码大类 **/
    private String industryCode; /** 企业行业编码 **/
}
