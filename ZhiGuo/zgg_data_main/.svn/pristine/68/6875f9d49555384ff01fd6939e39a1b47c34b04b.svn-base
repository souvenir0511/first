package com.zgg.batch.patent.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * 专利续费对象
 * @author xucj
 * @date 2021-06-09
 */

@Data
@Document(collection = "zlj_renew_info")
public class PatentRenewMongo implements Serializable {


    /**
     * 主键ID
     */
    @Id
    @Field(value = "_id")
    private String id;

    /***
     * 续费记录
     */
    private PatentRenewRecord PatentRenewRecord;

    /**
     * 案件状态
     */
    private String patent_status;

    /**
     * 数据时间
     */
    private String data_upload;


}
