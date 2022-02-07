package com.zgg.batch.enterprise.entity;

import lombok.Data;

import java.util.Date;

/**
 * 企业融资列表
 */
@Data
public class Finance {
	private Long id; /** 主键id **/
	private String entId; /** 企业id **/
	private String entName; /** 企业名称 **/
	private Date financeDate; /** 融资时间 **/
	private String financeRounds; /** 融资轮次 **/
	private String financeAmount; /** 融资金额 **/
	private String investor; /** 投资方,多个时用;分开 **/

}
