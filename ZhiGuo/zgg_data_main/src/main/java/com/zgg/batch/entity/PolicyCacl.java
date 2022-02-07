package com.zgg.batch.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class PolicyCacl implements Serializable {
    private String keyP;
    private Integer startNum;
    private Integer endNum;
    private Integer score;
    private Integer defaultScore;
}
