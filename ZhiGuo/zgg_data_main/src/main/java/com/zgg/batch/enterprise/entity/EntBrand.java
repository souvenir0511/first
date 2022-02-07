package com.zgg.batch.enterprise.entity;

import lombok.Data;

import java.util.Date;

/**
 * 商标
 */
@Data
public class EntBrand {
    private Long id; /** 主键 自增id **/
    private String entId; /** 企业id **/
    private String brandName; /** 商标名称 **/
    private Date applyDate; /** 商标申请时间 **/
    private String regCode; /** 商标申请号 **/
    private Integer intCls; /** 商标类别1-45类 **/
    private String brandType; /** 商标类别对应的文字 1-45类文字描述 **/
    private String brandStatus; /** 商标状态 **/
    private String brandImag; /** 商标图片 **/

    private String entName;

}
