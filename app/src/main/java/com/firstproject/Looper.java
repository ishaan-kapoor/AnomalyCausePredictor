package com.firstproject;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Looper {
    private static List<Instant> timestamps = new ArrayList<>();
    private static Instant startTime = Instant.parse("2024-06-25T18:13:00.000Z");
    private static Instant endTime = Instant.parse("2024-06-25T18:15:00.000Z");
    public static void loop() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable task = () -> {
            try {
                AnomalyDetector ad = new AnomalyDetector();
                ad.setRange(startTime,endTime);

                timestamps =  ad.checkWriteRejected();
                if(!timestamps.isEmpty()) {
                    Trigger.timestamps = timestamps;
                    Trigger.writeRejectedTrigger();
                }

                timestamps = ad.checkDiskUsage();
                if(!timestamps.isEmpty()) {
                    Trigger.timestamps = timestamps;
                    Trigger.diskUsageTrigger();
                }

                //move the window to next range
                startTime = startTime.plus(Duration.ofMinutes(2));
                endTime = endTime.plus(Duration.ofMinutes(2));


            } catch (Exception e) {
                System.err.println("Exception in anomaly detection: " + e.getMessage());
            }
        };
        scheduler.scheduleAtFixedRate(task, 0, 5, TimeUnit.SECONDS);

    }
}
