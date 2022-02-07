package com.zgg.batch1.entity;

import lombok.Data;

import java.util.Date;

/**
 * 著作权
 */
@Data
public class EntPriseCopyRight {

    /**
     * 主键id
     **/
    private Long id;
    /**
     * 企业id
     **/
    private String entId;
    /**
     * 作品名称
     **/
    private String productName;
    /**
     * 登记时间
     **/
    private Date regDate;
    /**
     * 登记号
     **/
    private String regNo;
    /**
     * 创作完成时间
     **/
    private Date createDate;
    /**
     * 作品类别
     **/
    private String productType;
    /**
     * 首次发表时间
     **/
    private Date publishDate;
    /**
     * 企业名称
     */
    private String entPriseName;

}
