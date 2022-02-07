package com.zgg.batch.patent.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 专利续费对象
 * @author xucj
 * @date 2021-06-09
 */

@Data
public class PatentRenewRecord implements Serializable {

    /**
     * 专利号
     */
    private String patentNo;

    /**
     * 待缴费集合
     */
    private List<PatentPendingFee> feePaids;

    /**
     * 已缴费集合
     */
    private List<PatentPaidFee> feeOriginals;

}
