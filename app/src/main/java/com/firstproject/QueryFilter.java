package com.firstproject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.jetbrains.annotations.NotNull;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxTable;
import com.influxdb.query.FluxRecord;

import java.time.Instant;

class QueryFilter {


    // now we have all the querySources of hits,.... how to process these????

    //1. find documents being fetched per second??
    // if lots of document being fetched -> pressure on memory -> memory limit -> cluster crash
    //upload fetchsize-timestamp to influx db from here.
    //another class which performs aggregation (queries influx) and gives result

    //filter based on some field value...

    private int thresholdTtm = 100000;
    private int thresholdTimeTakenMillis = 100000;
    private int thresholdQuerySize = 1000;
    private int thresholdFetchSize = 19999;

    //individual queries which can be cause
    class IndFilter{
        public List<QueryLog> execTime(@NotNull List<QueryLog> sources){
            List<QueryLog> result = new ArrayList<QueryLog>();
            result = sources.stream()
                    .filter(query -> query.getAttributes().getTtm() > thresholdTtm)
                    .toList();

            return result;
        }
        public List<QueryLog> timeTakenMillis(@NotNull List<QueryLog> sources){
            List<QueryLog> result = new ArrayList<QueryLog>();
            result = sources.stream()
                    .filter(query -> query.getAttributes().getTimeTakenMillis() > thresholdTimeTakenMillis)
                    .toList();

            return result;
        }
        public List<QueryLog> ttm(@NotNull List<QueryLog> sources){
            List<QueryLog> result = new ArrayList<QueryLog>();
            result = sources.stream()
                    .filter(query -> query.getAttributes().getTtm() > thresholdTtm)
                    .toList();

            return result;
        }

        public List<QueryLog> isWrite(@NotNull List<QueryLog> sources){
            List<QueryLog> result = new ArrayList<QueryLog>();

            result = sources.stream()
                    .filter(query ->  query.getAttributes().getOp()!=null && query.getAttributes().getOp().equals("bulkIndex"))
                    .toList();

            return result;
        }
        public List<QueryLog> isRefreshInline(@NotNull List<QueryLog> sources){
            List<QueryLog> result = new ArrayList<QueryLog>();

            result = sources.stream()
                    .filter(query ->  query.getAttributes().isRefreshInline()!=null && query.getAttributes().isRefreshInline())
                    .toList();

            return result;
        }
        public List<QueryLog> querySize(@NotNull List<QueryLog> sources){
            List<QueryLog> result = new ArrayList<QueryLog>();
            result = sources.stream()
                    .filter(query -> query.getAttributes().getQuerySize() > thresholdQuerySize)
                    .toList();

            return result;
        }
        public List<QueryLog> fetchSize(@NotNull List<QueryLog> sources){
            List<QueryLog> result = new ArrayList<QueryLog>();
            result = sources.stream()
                    .filter(query -> query.getAttributes().getTtm() > thresholdFetchSize)
                    .toList();

            return result;
        }
        public List<QueryLog> totalMatchCount(@NotNull List<QueryLog> sources){
            List<QueryLog> result = new ArrayList<QueryLog>();
            result = sources.stream()
                    .filter(query -> query.getAttributes().getQuerySize() > thresholdQuerySize)
                    .toList();

            return result;
        }
    }

    //queries collectively putting pressure
    class AggFilter extends InfluxConnection{

        public void uploadData(@NotNull List<QueryLog> sources, String measurement){
            InfluxDBClient client = InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);

            try {
                WriteApiBlocking writeApi = client.getWriteApiBlocking();
                for (QueryLog log : sources) {
                    Point point = Point.measurement(measurement)
                            .addField("val", log.getFetchSize())
                            .time(Instant.ofEpochMilli(log.getTimestamp()), WritePrecision.MS);

                    writeApi.writePoint(point);
                }
            } finally {
                client.close();
            }
        }

        public void fetchSizeExceedingThreshold() {
            InfluxDBClient client = InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);

            String flux = "from(bucket: \"" + bucket + "\")\n" +
                    "  |> range(start: "+ startTime +", stop: "+ endTime +")\n" +
                    "  |> filter(fn: (r) => r._measurement == \"fetchSize\")\n" +
                    "  |> aggregateWindow(every: 1s, fn: sum, createEmpty: false)\n" +
                    "  |> filter(fn: (r) => r._value > " + thresholdFetchSize + ")\n" +
                    "  |> keep(columns: [\"_time\", \"_value\"])\n" +
                    "  |> yield(name: \"exceeding_values\")";

            QueryApi queryApi = client.getQueryApi();
            List<FluxTable> tables = queryApi.query(flux);

            System.out.println("Time Window(1 sec) where FetchSize exceeds threshold value");
            for (FluxTable table : tables) {
                for (FluxRecord record : table.getRecords()) {
                    System.out.println("Time: " + record.getTime() + ", FetchSize: " + record.getValueByKey("_value"));
                }
            }

            client.close();
        }


    }


}
