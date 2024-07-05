package com.firstproject;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.TreeSet;

public class EsQueryLogs {
    private static final String index = "monitoring_*";
    private static int tdelta= 120; //time in seconds


    public static void main(String[] args) {

        // Example usage
//        List<String> timestamps = new ArrayList<>();
//        timestamps.add("2024-06-17T10:00:00Z");
//        timestamps.add("2024-06-17T11:00:00Z");

//        List<String> queries = getQueriesBetweenTimestamps(timestamps);

//        // Print the retrieved queries
//        for (String query : queries) {
//            System.out.println(query);
//        }

    }
    // optimize to get cumulative queries for a time range
    //same queries shouldnt be returned again.

    public static ArrayList<String> getQueriesBetweenTimestamps(TreeSet<Instant> timestamps) {

        // Initialize the Elasticsearch client
        RestHighLevelClient client;
        RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200, "http"));
        client = new RestHighLevelClient(builder);

        ArrayList<String> queries = new ArrayList<>();

        for (Instant timestamp : timestamps) {
            // range from timestamp-tdelta to timestamp of anomaly
            Instant startTime = timestamp.minusSeconds(tdelta);
            Instant endTime = timestamp;

            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("@timestamp")
                    .gte(startTime.toString())
                    .lte(endTime.toString());

            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(rangeQuery);

            SearchRequest searchRequest = new SearchRequest(index);
            searchRequest.source(sourceBuilder);

            try {
                SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
                searchResponse.getHits().forEach(hit -> {
                    String query = hit.getSourceAsString();
                    queries.add(query);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Close the Elasticsearch client
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return queries;


    }
}
