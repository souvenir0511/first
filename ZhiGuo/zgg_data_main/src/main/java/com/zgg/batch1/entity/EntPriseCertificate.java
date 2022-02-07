package com.zgg.batch1.entity;

import lombok.Data;

import java.util.Date;

/**
 * 资质证书
 */
@Data
public class EntPriseCertificate {

	/**
	 * 主键id
	 **/
	private Long id;
	/**
	 * 企业id
	 **/
	private String entId;
	/**
	 * 证书类型
	 **/
	private String certificateType;
	/**
	 * 证书名称
	 **/
	private String certificateName;
	/**
	 * 证书编号
	 **/
	private String certificateCode;
	/**
	 * 开始日期
	 **/
	private Date createDate;
	/**
	 * 截止日期
	 **/
	private Date endDate;
	/**
	 * 企业名称
	 */
	private String entPriseName;


}
