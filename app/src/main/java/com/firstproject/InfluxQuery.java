package com.firstproject;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxTable;
import com.influxdb.query.FluxRecord;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class InfluxQuery {

    private static final String TOKEN = "VAvHDVaja4941jg4NJKEncgQGXELrjwkicpNSBcl5Uc_1AX--oU-cp546RRyrvW3sXDgyBNhuyiyBXiaDW52bg==";
    private static final String BUCKET = "refined";
    private static final String ORG = "sprinklr";
    private static final String URL = "http://localhost:8086";
    private static final String start_time = "2024-05-29T00:00:00Z";
    private static final String end_time = "2024-06-01T00:00:00Z";

//    public static void qcall(String measurement) {
//        // Initialize the InfluxDB client
//        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(URL, TOKEN.toCharArray());
//
//        // Create the Flux query to get min, max, and mean
//        String flux = "min_value = from(bucket: \"" + BUCKET + "\")\n" +
//                "  |> range(start: 2024-06-11T15:11:51Z)\n" +
//                "  |> filter(fn: (r) => r._measurement == \""+measurement+"\" and r._field == \"val\")\n" +
//                "  |> min()\n" +
//                "  |> map(fn: (r) => ({ r with _value: float(v: r._value) }))\n" +
//                "\n" +
//                "max_value = from(bucket: \"" + BUCKET + "\")\n" +
//                "  |> range(start: 2024-06-11T15:11:51Z)\n" +
//                "  |> filter(fn: (r) => r._measurement == \""+measurement+"\" and r._field == \"val\")\n" +
//                "  |> max()\n" +
//                "  |> map(fn: (r) => ({ r with _value: float(v: r._value) }))\n" +
//                "\n" +
//                "mean_value = from(bucket: \"" + BUCKET + "\")\n" +
//                "  |> range(start: 2024-06-11T15:11:51Z)\n" +
//                "  |> filter(fn: (r) => r._measurement == \""+measurement+"\" and r._field == \"val\")\n" +
//                "  |> mean()\n" +
//                "  |> map(fn: (r) => ({ r with _value: float(v: r._value) }))\n" +
//                "\n" +
//                "union(tables: [min_value, max_value, mean_value])";
//
//
//
//        // Execute the query
//        QueryApi queryApi = influxDBClient.getQueryApi();
//        List<FluxTable> tables = queryApi.query(flux, ORG);
//
//        // Process the results
//
//        for (FluxTable table : tables) {
//            for (FluxRecord record : table.getRecords()) {
//                System.out.println(record.getField() + ": " + record.getValue());
//            }
//        }
//        // Close the client
//        influxDBClient.close();
//    }

    public static void spike_1(String measurement){
        InfluxDBClient client = InfluxDBClientFactory.create(URL, TOKEN.toCharArray(), ORG, BUCKET);

        String sdev = "from(bucket: \"" + BUCKET + "\")\n" +
                "  |> range(start: 2024-06-11T15:11:51Z)\n" +
                "  |> filter(fn: (r) => r._measurement == \""+measurement+"\" and r._field == \"val\")\n" +
                "  |> stddev()\n" +
                "  |> yield(name: \"stddev\")";

        String aboveThreshold = "data = from(bucket: \"" + BUCKET + "\")\n" +
                "  |> range(start: 2024-06-11T15:11:51Z)\n" +
                "  |> filter(fn: (r) => r._measurement == \""+measurement+"\" and r._field == \"val\")\n" +
                "\n" +
                "// Calculate mean and standard deviation\n" +
                "mean_value = data\n" +
                "  |> mean(column: \"_value\")\n" +
                "  |> map(fn: (r) => ({ _start: r._start, _stop: r._stop, mean: r._value }))\n" +
                "\n" +
                "stddev_value = data\n" +
                "  |> stddev(column: \"_value\")\n" +
                "  |> map(fn: (r) => ({ _start: r._start, _stop: r._stop, stddev: r._value }))\n" +
                "\n" +
                "// Calculate thresholds\n" +
                "thresholds = join(tables: {mean: mean_value, stddev: stddev_value}, on: [\"_start\", \"_stop\"])\n" +
                "  |> map(fn: (r) => ({ _start: r._start, _stop: r._stop, threshold_upper: r.mean + 3.0 * r.stddev, threshold_lower: r.mean - 3.0 * r.stddev }))\n" +
                "\n" +
                "// Filter spikes\n" +
                "spikes = join(tables: {data: data, thresholds: thresholds}, on: [\"_start\", \"_stop\"])\n" +
                "  |> filter(fn: (r) => r._value > r.threshold_upper or r._value < r.threshold_lower)\n" +
                "  |> yield(name: \"spikes\")";




        QueryApi queryApi = client.getQueryApi();
        List<FluxTable> tables = queryApi.query(aboveThreshold);

        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                System.out.println(record.getField()+ ": " + record.getTime() + ": " + record.getValueByKey("_value"));
            }
        }

        client.close();
    }

    public static void spike_2(String measurement){
        InfluxDBClient client = InfluxDBClientFactory.create(URL, TOKEN.toCharArray(), ORG, BUCKET);
        QueryApi queryApi = client.getQueryApi();

        String field = "val";

        // Query for mean value
        String meanFlux = "from(bucket: \"" + BUCKET + "\")\n" +
                "  |> range(start: "+start_time+"\n)\n" +
                "  |> filter(fn: (r) => r._measurement == \""+measurement+"\" and r._field == \"val\")\n" +
                "  |> mean(column: \"_value\")";
        List<FluxTable> meanTables = queryApi.query(meanFlux);
        double mean = 0;
        for (FluxTable table : meanTables) {
            for (FluxRecord record : table.getRecords()) {
                mean = ((Number) record.getValue()).doubleValue();
            }
        }

        // Query for standard deviation
        String stddevFlux = "from(bucket: \"" + BUCKET + "\")\n" +
                "  |> range(start: "+start_time+"\n)\n" +
                "  |> filter(fn: (r) => r._measurement == \""+measurement+"\" and r._field == \"val\")\n" +
                "  |> stddev(column: \"_value\")";
        List<FluxTable> stddevTables = queryApi.query(stddevFlux);
        double stddev = 0;
        for (FluxTable table : stddevTables) {
            for (FluxRecord record : table.getRecords()) {
                stddev = ((Number) record.getValue()).doubleValue();
            }
        }

//         Print mean and standard deviation
        System.out.println("Mean: " + mean);
        System.out.println("Standard Deviation: " + stddev);


        String flux = "data = from(bucket: \"" + BUCKET + "\")\n" +
                //select range here.......
                "  |> range(start: "+start_time+" ,stop: "+end_time+")\n" +  // Adjust the time range as needed
                "  |> filter(fn: (r) => r._measurement == \"" + measurement + "\" and r._field == \"" + field + "\")\n" +
                "\n" +
                "// Calculate mean and standard deviation\n" +
                "mean_value = data\n" +
                "  |> mean(column: \"_value\")\n" +
                "  |> map(fn: (r) => ({ _start: r._start, _stop: r._stop, mean: r._value }))\n" +
                "\n" +
                "stddev_value = data\n" +
                "  |> stddev(column: \"_value\")\n" +
                "  |> map(fn: (r) => ({ _start: r._start, _stop: r._stop, stddev: r._value }))\n" +
                "\n" +
                "// Calculate thresholds\n" +
                "thresholds = join(tables: {mean: mean_value, stddev: stddev_value}, on: [\"_start\", \"_stop\"])\n" +
                "  |> map(fn: (r) => ({ _start: r._start, _stop: r._stop, threshold_upper: r.mean + 3.0 * r.stddev, threshold_lower: r.mean - 3.0 * r.stddev }))\n" +
                "\n" +
                "// Filter anomalies\n" +
                "anomalies = join(tables: {data: data, thresholds: thresholds}, on: [\"_start\", \"_stop\"])\n" +
                "  |> filter(fn: (r) => r._value > r.threshold_upper or r._value < r.threshold_lower)\n" +
                "  |> keep(columns: [\"_time\", \"_value\", \"threshold_upper\", \"threshold_lower\"])\n" +
                "  |> yield(name: \"anomalies\")";

        List<FluxTable> tables = queryApi.query(flux);

//        for (FluxTable table : tables) {
//            for (FluxRecord record : table.getRecords()) {
//                Long cnt = (Long) record.getValueByKey("_value");
//                if(cnt== 0.0) continue; //specific to threadpool use case
//                System.out.println("Time: " + record.getTime() + ", Value: " + record.getValueByKey("_value") +
//                        ", Upper Threshold: " + record.getValueByKey("threshold_upper") +
//                        ", Lower Threshold: " + record.getValueByKey("threshold_lower"));
//            }
//
//        }

        // adding the anomalous timestamp to string( limit to 10 for now)
        TreeSet<Instant> timestamps = new TreeSet<>();
        for (FluxTable table: tables){
            for(FluxRecord record : table.getRecords()){
                Long cnt = (Long) record.getValueByKey("_value");
                if(cnt== 0.0) continue; //specific to threadpool use case
                if(timestamps.size() >10) break;
                timestamps.add(record.getTime());
            }
        }

        for(Instant i:timestamps){
            System.out.println(i);
        }

        client.close();

    }

}
