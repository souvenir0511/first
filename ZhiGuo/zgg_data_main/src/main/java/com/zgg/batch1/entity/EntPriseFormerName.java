package com.zgg.batch1.entity;

import lombok.Data;

/**
 * 商标
 */
@Data
public class EntPriseFormerName {

    /**
     * 主键 自增id
     **/
    private Long id;
    /**
     * 企业id
     **/
    private String entId;
    /**
     * 曾用名
     **/
    private String formerName;
    /**
     * 企业名称
     **/
    private String entPriseName;

}
