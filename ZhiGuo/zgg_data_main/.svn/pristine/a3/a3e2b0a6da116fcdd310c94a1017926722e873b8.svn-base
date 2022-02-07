package com.zgg.batch.main;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.zgg.batch.entity.V2Policy;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class V2PolicyEsQueryTest {

    private static HttpHost http;
    private static RestClientBuilder builder;
    private static RestHighLevelClient restHighLevelClient;

    public static void main(String[] args) {
        init();
        query();
    }

    private static void query(){
        SearchRequest searchRequest = new SearchRequest("v2_policy");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.rangeQuery("regMoney").to(1325));//注册资本
        boolQueryBuilder.must(QueryBuilders.termQuery("province","广东省"));//注册省份
        //boolQueryBuilder.must(QueryBuilders.termQuery("city","广州市"));//注册城市
        //boolQueryBuilder.must(QueryBuilders.termQuery("area", "天河区"));//注册区
        boolQueryBuilder.must(QueryBuilders.rangeQuery("years").to(6));//注册年限

        //boolQueryBuilder.must(QueryBuilders.termQuery("entType", "民营"));//企业类型
       // boolQueryBuilder.must(QueryBuilders.termQuery("entNature", "科技型企业"));//企业性质
        List<String> list = Lists.newArrayList();
        list.add("测试1");
        list.add("高新技术企业");
        //boolQueryBuilder.must(QueryBuilders.termsQuery("entQualifications", list));//企业资质
        List<String> capabilities = Lists.newArrayList();
        capabilities.add("工业设计中心");
        capabilities.add("测试能力1");
        capabilities.add("工程实验室");
        boolQueryBuilder.must(QueryBuilders.termsQuery("capabilities", capabilities));//技术能力

        BoolQueryBuilder boolShouldQueryBuilder = QueryBuilders.boolQuery();
        boolShouldQueryBuilder.should(QueryBuilders.rangeQuery("ipCount").to(100));//知产总数
        boolShouldQueryBuilder.should(QueryBuilders.rangeQuery("brandCount").to(30));//商标数
        boolShouldQueryBuilder.should(QueryBuilders.rangeQuery("patentCount").to(50));//专利数
        boolShouldQueryBuilder.should(QueryBuilders.rangeQuery("softRightCount").to(15));//软件著作权数
        boolShouldQueryBuilder.should(QueryBuilders.rangeQuery("peoples").to(1000));//企业人数
        boolShouldQueryBuilder.should(QueryBuilders.rangeQuery("revenue").to(1010.43));//上年营收
        boolShouldQueryBuilder.should(QueryBuilders.termQuery("education", 0));//学历水平0较高 1一般
        boolQueryBuilder.must().add(boolShouldQueryBuilder);






        searchSourceBuilder.query(boolQueryBuilder);

        searchRequest.source(searchSourceBuilder);

        System.out.println("searchSourceBuilder:"+searchSourceBuilder);
        try {
            SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            SearchHits hits = search.getHits();

            for(SearchHit hit : hits){
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();

                V2Policy v2Policy = JSONObject.parseObject(JSONObject.toJSONString(sourceAsMap), V2Policy.class);
                System.out.println("v2:"+v2Policy);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void init(){
        http = new HttpHost("192.168.10.154", 9200, "http");
        builder = RestClient.builder(http);
        restHighLevelClient = new RestHighLevelClient(builder);
    }
}
