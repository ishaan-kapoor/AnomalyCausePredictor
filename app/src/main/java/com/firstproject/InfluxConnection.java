package com.firstproject;

import java.time.Instant;

public class InfluxConnection {
    public String token = "VAvHDVaja4941jg4NJKEncgQGXELrjwkicpNSBcl5Uc_1AX--oU-cp546RRyrvW3sXDgyBNhuyiyBXiaDW52bg==";
    public String bucket = "refined";
    public String org = "sprinklr";
    public String url = "http://localhost:8086";
    public Instant startTime;
    public Instant endTime;

    public void setRange(Instant start, Instant end){
        startTime = start;
        endTime = end;
    }
    public void setInflux(String _token, String _bucket, String _org, String _url){
        token = _token;
        bucket = _bucket;
        org = _org;
        url = _url;
    }
}
