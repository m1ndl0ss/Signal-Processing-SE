package com.alerts;

// Represents a previous  alert logic
public class BaseAlert implements Alert{
    private String patientId;
    private String condition;
    private long timestamp;

    public BaseAlert(String patientId, String condition, long timestamp) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getCondition() {
        return condition;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
