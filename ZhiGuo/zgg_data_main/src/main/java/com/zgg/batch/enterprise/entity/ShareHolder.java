package com.zgg.batch.enterprise.entity;

import lombok.Data;

/**
 * 企业股东出资信息
 * @date 2021-01-26 15:04:39
 */
@Data
public class ShareHolder {
	 /** 主键id **/
	private Long id; /** 主键id **/
	private String entId; /** 企业id **/
	private String shareHolderName; /** 持股人姓名 **/
	private String shareHoldingRatio; /** 持股比例 **/
	private String shareHoldingNumber; /** 出资额度 **/
	private String EntName;/**企业名称*/
}
