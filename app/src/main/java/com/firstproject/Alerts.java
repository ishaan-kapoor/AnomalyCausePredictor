package com.firstproject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

abstract class Alerts {
    public List<Instant> timestamp;
    public List<QueryLog> querySources = new ArrayList<QueryLog>();

    public void setTimestampList(List<Instant> timestamp) {
        this.timestamp = timestamp;
        //continuous error can occur group those ??
        // t..ancycle , t..delKibana, t..delMetric ??
        //range set in query logs



    }

    public void queryLog(String file){
        //get the query log for timestampa...
        // then get the sources in query sources array
        List<Instant> range = new ArrayList<>();
        for(int i=0;i<timestamp.size();i++){
//            Instant tstart =
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Read the JSON file as a tree
            JsonNode rootNode = objectMapper.readTree(new File(file));

            // Navigate to the "hits" array inside the JSON structure
            JsonNode hitsNode = rootNode.path("hits").path("hits");

            if (hitsNode.isArray()) {
                for (JsonNode hitNode : hitsNode) {
                    // Get the "source" node within each "hit" node
                    JsonNode sourceNode = hitNode.path("_source");

                    // Map the "source" node to the QueryLog class
                    QueryLog source = objectMapper.treeToValue(sourceNode, QueryLog.class);
                    querySources.add(source);

//                    System.out.println("Date: " + source.getDate());
//                    System.out.println("Level: " + source.getLevel());
//                    System.out.println("UUID: " + source.getUuid());
//                    System.out.println("Fetch Size: " + source.getFetchSize());
//                    System.out.println("Timestamp: " + source.getTimestamp());
//                    System.out.println(source.getAttributes().getQuerySize());
                    // Print other fields as needed
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public synchronized void report(String text) {
        try (FileWriter writer = new FileWriter("/Users/ishaan.kapoor/Chatra/app/src/main/resources/report.txt", true)) {
            writer.write(text + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    abstract void queryFilter();


//    public List<QueryLog> getQuerySources() {
//        return querySources;
//    }


}
