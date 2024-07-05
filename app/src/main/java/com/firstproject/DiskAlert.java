package com.firstproject;

import java.time.Instant;
import java.util.List;

public class DiskAlert extends Alerts {

    @Override
    void queryFilter() {
        report("\nDisk Usage Alert");
        String file = "/Users/ishaan.kapoor/Chatra/app/src/main/resources/consoleResultdiskUsage25_23_44.json";
        queryLog(file);

        //check refresh inline
        AnomalyDetector ad = new AnomalyDetector();
        Instant end = timestamp.get(0);
        Instant start = end.minusSeconds(600);
        ad.setRange(start,end);

        List<Passing> lst = ad.checkRefreshInline();
        if(!lst.isEmpty()){
            report("\nRefresh Time Peaked at:");
            for(Passing p: lst){
                Instant time = p.time;
                String node = p.node;
                long val = p.val ;

                String rep = "Peak at: "+time+"\tAt Node: "+node+"\tValue: "+val;
                report(rep);
            }
        }

        //finding query with refreshInline true
        QueryFilter filter = new QueryFilter();
        QueryFilter.IndFilter indFilter = filter.new IndFilter();

        List<QueryLog> result =  indFilter.ttm(querySources);

        result = indFilter.isRefreshInline(result);
        report("\nQueries with refreshInline=true");
        for(QueryLog q: result){
            String rep = "Query uuid : "+q.getUuid()+"\tTime ttm: "+q.getAttributes().getTtm();
            report(rep);
        }

        //check shard relocation
        List<Passing> shardRelocating = ad.checkRelocatingShard();
        report("\nShard Relocating");
        if(!shardRelocating.isEmpty()){
            for(Passing p: shardRelocating){
                Instant time = p.time;
                long val = p.val;
                String rep = "Time: "+time+"\tRelocating Shard Value: "+val;
                report(rep);
            }
        }
    }
}
