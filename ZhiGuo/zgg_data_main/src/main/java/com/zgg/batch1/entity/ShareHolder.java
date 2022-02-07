package com.zgg.batch1.entity;

import lombok.Data;

/**
 * 企业股东出资信息
 * @date 2021-01-26 15:04:39
 */
@Data
public class ShareHolder {
	/**
	 * 主键id
	 **/
	private Long id;
	/**
	 *  企业id
	 **/
	private String entId;
	/**
	 * 持股人姓名
	 **/
	private String shareHolderName;
	/**
	 * 持股比例
	 **/
	private String shareHoldingRatio;
	/**
	 * 出资额度
	 **/
	private String shareHoldingNumber;
	/**
	 * 企业名称
	 **/
	private String entPriseName;


}
