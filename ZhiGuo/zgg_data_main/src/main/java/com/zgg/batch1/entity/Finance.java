package com.zgg.batch1.entity;

import lombok.Data;

import java.util.Date;

/**
 * 企业融资列表
 */
@Data
public class Finance {

	/**
	 * 主键id
	 **/
	private Long id;
	/**
	 * 企业id
	 **/
	private String entId;
	/**
	 * 企业名称
	 **/
	private String entPriseName;
	/**
	 *  融资时间
	 **/
	private Date financeDate;
	/**
	 * 融资轮次
	 **/
	private String financeRounds;
	/**
	 * 融资金额
	 **/
	private String financeAmount;
	/**
	 * 投资方,多个时用;分开
	 **/
	private String investor;


}
