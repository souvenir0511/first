package com.zgg.batch1.entity;

import lombok.Data;

import java.util.Date;

/**
 * 商标
 */
@Data
public class EntPriseBrand {

    /**
     * 主键 自增id
     **/
    private Long id;
    /**
     * 企业id
     **/
    private String entId;
    /**
     * 商标名称
     **/
    private String brandName;
    /**
     * 商标申请时间
     **/
    private Date applyDate;
    /**
     * 商标申请号
     **/
    private String regCode;
    /**
     * 商标类别1-45类
     **/
    private Integer intCls;
    /**
     * 商标类别对应的文字 1-45类文字描述
     **/
    private String brandType;
    /**
     *  商标状态
     **/
    private String brandStatus;
    /**
     * 商标图片
     **/
    private String brandImag;

    private String entPriseName;



}
