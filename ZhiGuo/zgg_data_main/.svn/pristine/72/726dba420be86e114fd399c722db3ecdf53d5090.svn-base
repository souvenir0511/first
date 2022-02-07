package com.zgg.batch.main;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zgg.batch.entity.EnterpriseOld;
import com.zgg.batch.utils.DocumentToBeanUtil;
import com.zgg.batch.utils.MapValueComparator;
import org.bson.Document;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JsonUrlMain {
    public static void main(String[] args) {
            Map<String, Integer> unsortMap = new HashMap<>();
            unsortMap.put("z", 10);
            unsortMap.put("b", 5);
            unsortMap.put("a", 6);
            unsortMap.put("c", 20);
            unsortMap.put("d", 1);
            unsortMap.put("e", 7);
            unsortMap.put("y", 8);
            unsortMap.put("n", 99);
            unsortMap.put("g", 50);
            unsortMap.put("m", 2);
            unsortMap.put("f", 9);
            System.out.println(unsortMap);

            Map<String, Integer> result1 = unsortMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));

            System.out.println(result1);

            Map<String, Integer> result2 = new LinkedHashMap<>();
            unsortMap.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .forEachOrdered(x -> result2.put(x.getKey(), x.getValue()));
            System.out.println(result2);
        //temp();
        /*String path = "E:\\output\\projectSources.json";
        FileReader fileReader = new FileReader(path);
        List<String> reader = fileReader.readLines();
        //getUrl(reader);
        distinctUrl(reader);*/

        System.out.println("结束");
    }

    public static void temp(){
        String path = "E:\\output\\countUrl.txt";
        FileReader fileReader = new FileReader(path);
        List<String> reader = fileReader.readLines();
        reader.stream().forEach(line -> {
            if(!line.startsWith("http")){
                System.out.println(line);
            }
        });
    }

    public static void distinctUrl(List<String> reader){

        Set<String> set = new HashSet<>();
        Map<String,Integer> map = new HashMap<>();
        reader.stream().forEach(line ->{
            JSONObject jsonObject = JSONUtil.parseObj(line);
            String id = (String)jsonObject.get("_id");
            Object o = jsonObject.get("projectSources");
            if(o != null){
                if(o instanceof List){
                    List list = (List)o;
                    if(list.size()>1){
                        for(int i=0 ;i<list.size();i++){
                            JSONObject temp = JSONUtil.parseObj(list.get(i));
                            String o1 = temp.get("url").toString();
                            try {
                                //String host = getYuminNet(o1);
                                String host = getYumin(o1);
                                Integer count = map.get(host);
                                if( count == null){
                                    count=1;
                                }else{
                                    count++;
                                }
                                map.put(host, count);

                                //String host = getYuminSub(o1);
                                set.add(host);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }else {
                        if(list != null && list.size()>0){
                            Object o1 = list.get(0);
                            JSONObject jsonObject1 = JSONUtil.parseObj(o1);
                            String o2 = jsonObject1.get("url").toString();
                            try {
                                //String host = getYuminNet(o2);
                                String host = getYumin(o2);
                                //String host = getYuminSub(o2);
                                //set.add(host);
                                Integer count = map.get(host);
                                if( count == null){
                                    count = 1;
                                }else{
                                    count++;
                                }
                                map.put(host, count);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }else{
                    System.out.println("数据类型错误");
                }
            }
        });
        System.out.println(map.size());

        Map<String, Integer> resultMap = sortMapByValue(map);
       /* Map<String, Integer> oldMap = map.entrySet()
                .stream()
                //.sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldVal, newVal) -> newVal,
                        LinkedHashMap::new));*/
        resultMap.forEach((key, value) -> {
            System.out.println(key + " -> " + value);
        });


        /*set.stream().forEach( value ->{
            FileWriter writer = new FileWriter("E:\\output\\distinctUrl.txt");
            writer.append(value+"\r\n");
        });*/

    }

    /**
     * 使用 Map按value进行排序
     * @param
     * @return
     */
    public static Map<String, Integer> sortMapByValue(Map<String, Integer> oriMap) {
        if (oriMap == null || oriMap.isEmpty()) {
            return null;
        }
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        List<Map.Entry<String, Integer>> entryList = new ArrayList<Map.Entry<String, Integer>>(
                oriMap.entrySet());
        Collections.sort(entryList, new MapValueComparator());

        Iterator<Map.Entry<String, Integer>> iter = entryList.iterator();
        Map.Entry<String, Integer> tmpEntry = null;
        while (iter.hasNext()) {
            tmpEntry = iter.next();
            sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
        }
        return sortedMap;
    }

    private static Comparator<Map.Entry> comparatorByValueDesc = (Map.Entry o1, Map.Entry o2) -> {
        if (o1.getValue() instanceof Comparable) {
            return ((Comparable) o1.getValue()).compareTo(o2.getValue());
        }
        throw new UnsupportedOperationException("值的类型尚未实现Comparable接口");
    };

    public static String getYuminNet(String url){//2599
        try {
            URL str = new URL(url);
            String host = str.getHost();

            int i = url.indexOf("//");
            String substring = url.substring(0, i + 2);
            System.out.println(substring+host);
            return substring+host;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getYuminSub(String url){//2599
        String pifxx = ".com/";
        int i = url.indexOf(pifxx);
        String substring = url.substring(0,i+pifxx.length());
        System.out.println(substring);
        return substring;
    }

    public static String getYumin(String url){
        Pattern p = Pattern.compile("[^//]*$?\\.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(url);
        matcher = p.matcher(url);
        matcher.find();
        String group = matcher.group();
        int i = url.indexOf("//");
        String substring = url.substring(0, i + 2);
        //System.out.println(substring+group);
        return substring+group;
    }

    public static void getUrl(List<String> reader){
        try{
            reader.stream().forEach(line->{
                JSONObject jsonObject = JSONUtil.parseObj(line);
                String id = (String)jsonObject.get("_id");
                //System.out.println("-------"+id);
                Object o = jsonObject.get("projectSources");
                if(o != null){
                    if(o instanceof List){
                        List list = (List)o;
                        if(list.size()>1){
                            for(int i=0 ;i<list.size();i++){
                                JSONObject temp = JSONUtil.parseObj(list.get(i));
                                FileWriter writer = new FileWriter("E:\\output\\url.txt");
                                writer.append(temp.get("url")+"\r\n");
                            }
                        }else {
                            if(list != null && list.size()>0){
                                Object o1 = list.get(0);
                                JSONObject jsonObject1 = JSONUtil.parseObj(o1);
                                FileWriter writer = new FileWriter("E:\\output\\url.txt");
                                writer.append(jsonObject1.get("url")+"\r\n");
                            }

                        }

                    }else{
                        System.out.println("数据类型错误");
                    }
                }


            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
