package com.zgg.batch.patent.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * 专利mongo对象
 * @author xucj
 * @date 2021-06-09
 */

@Data
@Document(collection = "zlj_basic_info")
public class PatentMongo implements Serializable {

    /**
     * 主键ID
     */
    @Id
    @Field(value = "_id")
    private String id;

    /**
     * 专利号
     */
    private String patentCode;

    /**
     * 专利类型
     */
    private String patentType;

    /**
     * 专利名称
     */
    private String patentName;

    /**
     * 申请人
     */
    private String applicant;

    /**
     * 申请时间
     */
    private String applyTime;

    /**
     * 公告时间
     */
    private String afficheTime;

    /**
     * 类型号
     */
    private String typeCode;

    /**
     * 数据时间
     */
    private String data_upload;

//    /**
//     * 案件状态
//     */
//    private String patentStatus;

}
