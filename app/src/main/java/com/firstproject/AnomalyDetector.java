package com.firstproject;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxTable;
import com.influxdb.query.FluxRecord;


import java.time.Instant;
import java.util.List;
import java.util.ArrayList;

public class AnomalyDetector extends InfluxConnection {

//    public List<Instant> checkClusterHealth() {
//        InfluxDBClient client = InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
//
//        String fluxQuery = String.format(
//                "from(bucket: \"%s\") |> range(start: %s, stop: %s) |> filter(fn: (r) => r._measurement == \"es-cluster_health_status\" and r._field == \"val\") |> sort(columns: [\"_time\"], desc: false)",
//                bucket, startTime.toString(), endTime.toString());
//
//        QueryApi queryApi = client.getQueryApi();
//        List<FluxTable> tables = queryApi.query(fluxQuery);
//        List<Instant> timestamps = new ArrayList<>();
//
//        Long previousVal = null;
//        for (FluxTable table : tables) {
//            for (FluxRecord record : table.getRecords()) {
//                Instant timestamp = record.getTime();
//                Long currentVal = ((Number) record.getValueByKey("_value")).longValue();
//
//                if (currentVal == 2 && (previousVal == null || previousVal != 2)) {
//                    timestamps.add(timestamp);
//                }
//
//                previousVal = currentVal;
//            }
//        }
//
//        client.close();
//        return timestamps;
//    }

    public List<Instant> checkDiskUsage(){

        List<Instant> lst = new ArrayList<Instant>();

        String measurement = "diskUsage";
        long thresholdDiskUsage = 95;
        InfluxDBClient client = InfluxDBClientFactory.create(url, token.toCharArray(),org);

        String fluxQuery = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: %s, stop: %s) " +
                        "|> filter(fn: (r) => r._measurement == \"%s\" and r._field == \"val\" and r._value >= %d)",
                bucket, startTime.toString(), endTime.toString(), measurement, thresholdDiskUsage);


        QueryApi queryApi = client.getQueryApi();
        List<FluxTable> tables = queryApi.query(fluxQuery);


        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                Instant timestamp = record.getTime();
                lst.add(timestamp);
//                System.out.println("disk usage peak at "+timestamp);
            }
        }

        client.close();
        return lst;
    }

    //condition for diskUsage anomaly
    public List<Passing> checkRefreshInline(){
        List<Passing> lst = new ArrayList<Passing>();

        String measurement = "es-index_indices_index_refresh_time";
        long thresholdRefreshInline = 90000;
        InfluxDBClient client = InfluxDBClientFactory.create(url, token.toCharArray(),org);

        String fluxQuery = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: %s, stop: %s) " +
                        "|> filter(fn: (r) => r._measurement == \"%s\" and r._field == \"val\" and r._value >= %d)",
                bucket, startTime.toString(), endTime.toString(), measurement, thresholdRefreshInline);


        QueryApi queryApi = client.getQueryApi();
        List<FluxTable> tables = queryApi.query(fluxQuery);

        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                Instant timestamp = record.getTime();
                String hostNode = (String) record.getValueByKey("hostNode");
                long value = (long) record.getValueByKey("_value");
                Passing obj = new Passing();
                obj.time = timestamp;
                obj.node = hostNode;
                obj.val = value;
                lst.add(obj);
//                System.out.println("refresh inline peak at \n"+timestamp+" "+hostNode);
            }
        }

        client.close();
        return lst;
    }
    public List<Passing> checkRelocatingShard(){
        List<Passing> lst = new ArrayList<Passing>();

        String measurement = "es-cluster_health_relocating_shards";
        long thresholdRelocatingShard = 1;
        InfluxDBClient client = InfluxDBClientFactory.create(url, token.toCharArray(),org);

        String fluxQuery = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: %s, stop: %s) " +
                        "|> filter(fn: (r) => r._measurement == \"%s\" and r._field == \"val\" and r._value >= %d)",
                bucket, startTime.toString(), endTime.toString(), measurement, thresholdRelocatingShard);


        QueryApi queryApi = client.getQueryApi();
        List<FluxTable> tables = queryApi.query(fluxQuery);

        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                Instant timestamp = record.getTime();
                long value = (long) record.getValueByKey("_value");
                Passing obj = new Passing();
                obj.time = timestamp;
                obj.val = value;
                lst.add(obj);
//                System.out.println("refresh inline peak at \n"+timestamp+" "+hostNode);
            }
        }

        client.close();
        return lst;
    }

    public List<Instant> checkWriteRejected(){
        List<Instant> lst = new ArrayList<Instant>();

        String measurement = "es-threadpool_thread_pool_write_rejected.difference";
        long thresholdWriteRejected = 0;
        InfluxDBClient client = InfluxDBClientFactory.create(url, token.toCharArray(),org);

        String fluxQuery = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: %s, stop: %s) " +
                        "|> filter(fn: (r) => r._measurement == \"%s\" and r._field == \"val\" and r._value > %d)",
                bucket, startTime.toString(), endTime.toString(), measurement, thresholdWriteRejected);


        QueryApi queryApi = client.getQueryApi();
        List<FluxTable> tables = queryApi.query(fluxQuery);


        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                Instant timestamp = record.getTime();
                lst.add(timestamp);
//                System.out.println("write rejected at "+timestamp);
            }
        }

        client.close();
        return lst;

    }

    public List<Instant> checkOldGc(){
        List<Instant> lst = new ArrayList<Instant>();

        String measurement = "es-jvm_jvm_gc_collectors_old_collection_count";
        long thresholdWriteRejected = 10;
        InfluxDBClient client = InfluxDBClientFactory.create(url, token.toCharArray(),org);

        String fluxQuery = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: %s, stop: %s) " +
                        "|> filter(fn: (r) => r._measurement == \"%s\" and r._field == \"val\" and r._value > %d)",
                bucket, startTime.toString(), endTime.toString(), measurement, thresholdWriteRejected);


        QueryApi queryApi = client.getQueryApi();
        List<FluxTable> tables = queryApi.query(fluxQuery);


        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                Instant timestamp = record.getTime();
                lst.add(timestamp);
//                System.out.println("write rejected at "+timestamp);
            }
        }

        client.close();
        return lst;

    }

}
