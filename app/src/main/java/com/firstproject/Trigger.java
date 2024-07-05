package com.firstproject;

import java.time.Instant;
import java.util.List;

//calls kibana part (getting logs and then query filtering)
public class Trigger {
    public static List<Instant> timestamps;

    public static void writeRejectedTrigger(){
        WriteRejectedAlert filter = new WriteRejectedAlert();
        filter.setTimestampList(timestamps);
        filter.queryFilter();
        System.out.println("write rej trigger ");
    }

    public static void diskUsageTrigger(){
        DiskAlert da = new DiskAlert();
        da.setTimestampList(timestamps);
        da.queryFilter();
        System.out.println("disk alert trigger");

    }


}
