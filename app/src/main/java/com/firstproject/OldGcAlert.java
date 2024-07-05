package com.firstproject;

import java.util.List;

//filter in ttm. heap dump???

public class OldGcAlert extends Alerts{
    @Override
    public void queryFilter() {
        report("\nOld GC Alert");

//        String testFile = "/Users/chatraraj.regmi/Desktop/FirstProject/app/src/main/resources/consoleResultWRej23_15_58.json";
//        queryLog(testFile);

//        System.out.println(querySources.isEmpty());

        QueryFilter filter = new QueryFilter();
        QueryFilter.IndFilter indFilter = filter.new IndFilter();

        List<QueryLog> result =  indFilter.ttm(querySources);



        for(QueryLog q: result){
//            System.out.println(q.getDistributedTraceId()+" "+q.getAttributes().getTimeTakenMillis());
            String rep = "Query uuid : "+q.getUuid()+"\tTime taken by Query in millis: "+q.getAttributes().getTimeTakenMillis();
            report(rep);
        }


    }
}
