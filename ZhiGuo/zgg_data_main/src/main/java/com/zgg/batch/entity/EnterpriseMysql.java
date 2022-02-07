package com.zgg.batch.entity;

import lombok.Data;

@Data
public class EnterpriseMysql {
    private String entId; /** entId **/
    private String entName; /** 企业名称 **/
    private String faRen; /** 企业法人 **/
    private String regDate; /** 注册时间 **/
    private String regMoney; /** 注册资本 **/
    private String regMoneyUnit; /** 注册资本单位**/
    private String entStatus; /** 公司状态 **/
    private String province; /** 省 **/
    private String city; /** 市 **/
    private String area; /** 区 **/
    private String areaCode; /** 邮编 **/
    private String address; /** 注册地址 **/
    private String creditCode; /** 统一信用代码 **/
    private String importEntCode; /** 进出口企业代码 **/
    private String entCode; /** 组织机构代码 **/
    private String taxpayerCode; /** 纳税人识别号 **/
    private String tel; /** 电话 **/

}
