package com.zgg.batch.enterprise.entity;

import lombok.Data;

import java.util.Date;

/**
 * 资质证书
 */
@Data
public class EntCertificate {
	private Long id; /** 主键id **/
	private String entId; /** 企业id **/
	private String certificateType; /** 证书类型 **/
	private String certificateName; /** 证书名称 **/
	private String certificateCode; /** 证书编号 **/
	private Date createDate; /** 开始日期 **/
	private Date endDate; /** 截止日期 **/

	private String entName;

}
