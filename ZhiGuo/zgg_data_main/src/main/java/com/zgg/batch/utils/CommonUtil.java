package com.zgg.batch.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {

    /**
     * 解析地址
     * @param address
     * @return
     */
    public static List<Map<String,String>> addressResolution(String address){
        String regex="(?<province>[^省]+自治区|.*?省|.*?行政区|.*?市)(?<city>[^市]+自治州|.*?地区|.*?行政单位|.+盟|市辖区|.*?市|.*?县)(?<area>[^县]+县|.+区|.+市|.+旗|.+海域|.+岛)?(?<town>[^区]+区|.+镇)?(?<village>.*)";
        Matcher m= Pattern.compile(regex).matcher(address);
        String province,city,area,town,village;
        List<Map<String,String>> result = new ArrayList<>();
        Map<String,String> row;
        while(m.find()){
            row= new LinkedHashMap<>();
            province = m.group("province");
            row.put("province", province == null? "" : province.trim());
            city=m.group("city");
            row.put("city", city == null?"" : city.trim());
            area=m.group("area");
            row.put("area", area==null?"":area.trim());
            town=m.group("town");
            row.put("town", town==null?"":town.trim());
            village=m.group("village");
            row.put("village", village==null?"":village.trim());
            result.add(row);
        }
        return result;
    }

}
