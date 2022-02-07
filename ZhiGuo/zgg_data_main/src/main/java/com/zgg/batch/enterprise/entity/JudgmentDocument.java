package com.zgg.batch.enterprise.entity;

import lombok.Data;

import java.util.Date;

@Data
public class JudgmentDocument {
    private Long id; /** id **/
    private String entId; /** 企业id **/
    private String caseName; /** 案件名称 **/
    private String caseCause; /** 案由 **/
    private String caseNo; /** 案号 **/
    private String plaintiff; /** 原告,多个时用;分开 **/
    private String defendant; /** 被告,多个时用;分开 **/
    private String result; /** 裁判结果 **/
    private Date caseDate; /** 裁判日期 **/
    private Date noticeDate; /** 公布日期 **/
    private String chiefJudge; /** 审判长 **/
    private String juror; /** 陪审员,多个时用;分开 **/
    private String peopleJuror; /** 人民陪审员,多个时用;分开 **/
    private String clerk; /** 书记员,多个时用;分开 **/
    private String courtName; /** 审理法院 **/
    private String casesType; /** 案件类型 **/
    private String content; /** 正文 **/

    private String	entName;
}
