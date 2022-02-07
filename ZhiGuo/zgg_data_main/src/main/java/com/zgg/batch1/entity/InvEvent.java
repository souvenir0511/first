package com.zgg.batch1.entity;

import lombok.Data;

/**
 * 企业投资
 */
@Data
public class InvEvent {
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
	 * 被投资企业名称
	 **/
	private String investedName;
	/**
	 * 法人
	 **/
	private String legalPerson;
	/**
	 * 投资金额
	 **/
	private String investmentAmount;
	/**
	 * 投资比例
	 **/
	private String investmentProportion;

}
