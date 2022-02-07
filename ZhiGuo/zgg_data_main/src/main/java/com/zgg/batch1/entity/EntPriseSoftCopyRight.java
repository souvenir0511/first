package com.zgg.batch1.entity;

import lombok.Data;

import java.util.Date;

/**
 * 软件著作权
 */
@Data
public class EntPriseSoftCopyRight {

    /**
     * 主键id
     **/
    private Long id;
    /**
     * 企业id
     **/
    private String entId;
    /**
     * 软著名称
     **/
    private String softName;
    /**
     * 登记日期
     **/
    private Date regDate;
    /**
     * 登记号
     **/
    private String regNo;
    /**
     * 版本号
     **/
    private String softEdition;
    /**
     * 简称
     **/
    private String softAbb;
    /**
     * 发布日期
     **/
    private Date noticeDate;
    /**
     * 企业名称
     **/
    private String entPriseName;


}
