package com.zgg.batch1.entity;

import lombok.Data;

import java.util.Date;

/**
 * 变更记录
 */
@Data
public class ChangeRecord {
    /**
     * 主键 自增id
     **/
    private Long id;
    /**
     * 企业id
     **/
    private String entId;
    /**
     * 改变项目
     **/
    private String project;
    /**
     * 改变日期
     **/
    private Date changeDate;
    /**
     * 改变前数据
     **/
    private String oldData;
    /**
     * 改变后数据
     **/
    private String newData;
    /**
     * 企业名称
     */
    private String entPriseName;

}
