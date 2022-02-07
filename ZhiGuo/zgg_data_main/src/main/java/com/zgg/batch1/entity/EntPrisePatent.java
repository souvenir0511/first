package com.zgg.batch1.entity;

import lombok.Data;

import java.util.Date;

/**
 * 专利
 */
@Data
public class EntPrisePatent {
    /**
     * 主键id
     **/
    private Long id;
    /**
     * 企业id
     **/
    private String entId;
    /**
     * 专利名称
     **/
    private String patentName;
    /**
     * 专利号
     **/
    private String patentId;
    /**
     * 专利类型
     **/
    private String patentType;
    /**
     * 专利状态
     **/
    private String patentStatus;
    /**
     * 专利申请日期
     **/
    private Date regDate;
    /**
     * 专利公开日期
     **/
    private Date openDate;
    /**
     * 专利公开号
     **/
    private String openNo;
    /**
     * 企业名称
     **/
    private String entPriseName;

}
