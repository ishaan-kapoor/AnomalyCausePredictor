package com.firstproject;

import java.util.List;

public class WriteRejectedAlert extends Alerts {

    public void queryFilter() {
        report("\nWrite rejected");

        String testFile = "/Users/ishaan.kapoor/Chatra/app/src/main/resources/consoleResultWRej23_15_58.json";
        queryLog(testFile);

//        System.out.println(querySources.isEmpty());

        QueryFilter filter = new QueryFilter();
        QueryFilter.IndFilter indFilter = filter.new IndFilter();

        List<QueryLog> result =  indFilter.isWrite(querySources);

        result = indFilter.timeTakenMillis(result);

        for(QueryLog q: result){
//            System.out.println(q.getDistributedTraceId()+" "+q.getAttributes().getTimeTakenMillis());
            String rep = "Query uuid : "+q.getUuid()+"\tTime taken by Query in millis: "+q.getAttributes().getTimeTakenMillis();
            report(rep);
        }


    }

}
