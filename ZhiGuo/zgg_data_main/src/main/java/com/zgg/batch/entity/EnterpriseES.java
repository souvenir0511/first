package com.zgg.batch.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Data
@Component
public class EnterpriseES {
    private String id; //企业id
    private String entName; //企业名称
    private String entNameText;
    private List<String> entNameOld; //企业名称
   // private String entNameSimple; //企业名称
    //private List<String> entNameSimplePinyin;
    private String faRen = "";//法人
    //private String regMoneyOld = "";//注册资本
    private BigDecimal regMoney;//注册资本
    private String regMoneyUnit = "";//注册资本单位
    private String address = "";//注册地址
    private String regDate;
    private String entType;
    private String province;
    private String creditCode;
    private String city = "";//市名
    private String area = "";//区名
    private String tel = ""; //联系方式
    private String email = "";//email
    private String webSite = "";//网址
    private String experienceScope = "";//经营范围
    private Integer brandCount;
    private Integer patentCount;
    private Integer softwareCopyrightCount;
    private Integer copyrightCount;
    private Integer certificateCount;

    private Boolean isUp;
    private String industry = "";//行业
    private String entStatus;
    private Boolean isGaoxin;
    private String regYear;
    private String gaoXinYear;
    private List<String> industrySet;
}
