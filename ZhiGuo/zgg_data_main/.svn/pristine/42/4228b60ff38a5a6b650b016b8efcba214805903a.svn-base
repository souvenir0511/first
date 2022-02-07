package com.zgg.batch.utils;

import cn.hutool.core.util.IdUtil;
import com.zgg.batch.entity.Enterprise;
import com.zgg.batch.entity.EnterpriseOld;

public class BeanToBeanUtil {
    public static Enterprise oldToNew(EnterpriseOld enterpriseOld){
        Enterprise enterprise = new Enterprise();
        String simpleId = IdUtil.simpleUUID();
        enterprise.setId(simpleId);
        enterprise.setEntId(simpleId);
        enterprise.setOldId(enterpriseOld.getId());
        enterprise.setOrgLogo(enterpriseOld.getOrgLogo());
        enterprise.setEntName(enterpriseOld.getOrgName());
        enterprise.setOldEntNames(enterpriseOld.getOldOrgNames());
        enterprise.setFaRen(enterpriseOld.getCorporation());
        enterprise.setFaRenImage(enterpriseOld.getCorporationImage());
        enterprise.setRegMoney(enterpriseOld.getRegCapital());
        enterprise.setRegMoneyUnit(enterpriseOld.getRegCapitalUnit());
        enterprise.setEntStatus(enterpriseOld.getEnterpriseStatus());
        enterprise.setRegDate(enterpriseOld.getRegDate());
        enterprise.setRegNumber(enterpriseOld.getBusinessRegCode());
        enterprise.setEntCode(enterpriseOld.getOrgCode());
        enterprise.setCreditCode(enterpriseOld.getCreditCode());
        enterprise.setEntType(enterpriseOld.getEnterpriseType());
        enterprise.setTaxpayerCode(enterpriseOld.getTaxpayerIdNo());
        enterprise.setImportEntCode("");
        enterprise.setIndustry(enterpriseOld.getIndustry());
        enterprise.setCheckDate(enterpriseOld.getCheckDate());
        enterprise.setRegistAuthority(enterpriseOld.getRegistrationAuthority());
        enterprise.setEntNameEn(enterpriseOld.getOrgNameEn());
        enterprise.setEngagedNumber(enterpriseOld.getEngagedNumber());
        enterprise.setInsuredNumber("");
        enterprise.setAddress(enterpriseOld.getAddress());
        enterprise.setExperienceScope(enterpriseOld.getBusinessScope());
        enterprise.setTel(enterpriseOld.getTelphone());
        enterprise.setEmail(enterpriseOld.getEmail());
        enterprise.setWebsite(enterpriseOld.getWebsite());
        enterprise.setIntroduce(enterpriseOld.getIntroduction());
        enterprise.setProvince(enterpriseOld.getProvince());
        enterprise.setCity(enterpriseOld.getCity());
        enterprise.setArea(enterpriseOld.getArea());
        enterprise.setAreaCode(enterpriseOld.getAreaCode());
        enterprise.setLat(enterpriseOld.getLat());
        enterprise.setLon(enterpriseOld.getLon());
        enterprise.setSnapshotImag(enterpriseOld.getSnapshot());
        enterprise.setFaRenType(enterpriseOld.getCorporationType());
        enterprise.setFaRenEntId(enterpriseOld.getCorporationEnterpriseId());
        enterprise.setIndustrySet(enterpriseOld.getAnalysisIndustries());
        enterprise.setGaoXinExpireDate(enterpriseOld.getGaoXinExpireDate());
        enterprise.setGaoXinStartYear(enterpriseOld.getGaoXinStartYear());
        enterprise.setListedDate("");
        enterprise.setListedYear("");
        enterprise.setIndustryPhy(enterpriseOld.getIndustryPhy());
        enterprise.setIndustryBig(enterpriseOld.getIndustryBig());
        enterprise.setIndustryCode(enterpriseOld.getIndustryCode());

        return enterprise;
    }
}
