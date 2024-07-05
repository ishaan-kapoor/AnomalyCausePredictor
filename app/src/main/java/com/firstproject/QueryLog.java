package com.firstproject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
//source field of hit mapped to QueryLog
public class QueryLog {
    private String date;
    private String level;
    private String uuid;

    @JsonProperty("distributedTraceId")
    private String distributedTraceId;

    private Attributes attributes;
    private int fetchSize;
    private long timestamp;

    // Getters and setters
    public String getDate() {
        return date;
    }

    public String getLevel() {
        return level;
    }

    public String getUuid() {
        return uuid;
    }

    @JsonProperty("requestContext.distributedTraceId")
    public String getDistributedTraceId() {
        return distributedTraceId;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    @JsonProperty("fetch_size")
    public int getFetchSize() {
        return fetchSize;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "LogEntry{" +
                "date='" + date + '\'' +
                ", level='" + level + '\'' +
                ", uuid='" + uuid + '\'' +
                ", attributes=" + attributes +
                ", fetchSize=" + fetchSize +
                ", timestamp=" + timestamp +
                '}';
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Attributes {
        private int indicesCount;
        @JsonProperty("_doc_type")
        private String docType;
        private String clusterName;
        private int querySize;
        private String query;
        private int ttm;
        private int avgWaitTime;
        private int totalWaitTime;
        private List<String> indices;
        private boolean success;
        private String op;
        private int timeTakenMillis;
        private Boolean refreshInline;
        private Long totalMatchCount;

        // Getters and setters
        public int getIndicesCount() {
            return indicesCount;
        }


        public String getDocType() {
            return docType;
        }


        public String getClusterName() {
            return clusterName;
        }


        public int getQuerySize() {
            return querySize;
        }


        public String getQuery() {
            return query;
        }


        public int getTtm() {
            return ttm;
        }


        public int getAvgWaitTime() {
            return avgWaitTime;
        }

        public int getTimeTakenMillis() {
            return timeTakenMillis;
        }

        public int getTotalWaitTime() {
            return totalWaitTime;
        }

        public List<String> getIndices() {
            return indices;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getOp() {
            return op;
        }

        public Boolean isRefreshInline() {
            return refreshInline;
        }

        public Long getTotalMatchCount() {
            return totalMatchCount;
        }

        @Override
        public String toString() {
            return "Attributes{" +
                    "indicesCount=" + indicesCount +
                    ", docType='" + docType + '\'' +
                    ", clusterName='" + clusterName + '\'' +
                    ", querySize=" + querySize +
                    ", query='" + query + '\'' +
                    ", ttm=" + ttm +
                    ", indices=" + indices +
                    ", success=" + success +
                    '}';
        }
    }
}
