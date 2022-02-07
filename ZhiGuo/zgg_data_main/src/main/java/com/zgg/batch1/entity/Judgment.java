package com.zgg.batch1.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Judgment {
    /**
     * id
     **/
    private Long id;
    /**
     * 企业id
     **/
    private String entId;
    /**
     * 案件名称
     **/
    private String caseName;
    /**
     * 案由
     **/
    private String caseCause;
    /**
     *  案号
     **/
    private String caseNo;
    /**
     * 身份
     **/
    private String plaintiffDefendant;
    /**
     * 金额
     **/
    private BigDecimal caseMoney;
    /**
     * 裁判结果
     **/
    private String result;
    /**
     * 正文
     **/
    private String content;
    /**
     * 裁判日期
     **/
    private Date caseDate;
    /**
     * 公布日期
     **/
    private Date noticeDate;
    /**
     * 企业名称
     **/
    private String entPriseName;

}
