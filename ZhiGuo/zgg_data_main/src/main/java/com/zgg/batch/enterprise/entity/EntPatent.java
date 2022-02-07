package com.zgg.batch.enterprise.entity;

import lombok.Data;

import java.util.Date;

/**
 * 专利
 */
@Data
public class EntPatent {
    private Long id; /** 主键id **/
    private String entId; /** 企业id **/
    private String patentName; /** 专利名称 **/
    private String patentId; /** 专利号 **/
    private String patentType; /** 专利类型 **/
    private String patentStatus; /** 专利状态 **/
    private Date regDate; /** 专利申请日期 **/
    private Date openDate; /** 专利公开日期 **/
    private String openNo; /** 专利公开号 **/

    private String entName;
}
