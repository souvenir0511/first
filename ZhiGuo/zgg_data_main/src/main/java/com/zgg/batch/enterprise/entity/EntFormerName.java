package com.zgg.batch.enterprise.entity;

import lombok.Data;

import java.util.Date;

/**
 * 商标
 */
@Data
public class EntFormerName {
    private Long id; /** 主键 自增id **/
    private String entId; /** 企业id **/
    private String formerName; /** 曾用名 **/
    private String entName;

}
