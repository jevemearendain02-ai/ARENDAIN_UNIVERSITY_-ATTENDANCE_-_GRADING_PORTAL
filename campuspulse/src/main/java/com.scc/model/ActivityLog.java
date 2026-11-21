package com.scc.model;

import java.time.LocalDateTime;

public class ActivityLog {
    private int logId;
    private int userId;
    private String actionType;
    private String description;
    private LocalDateTime timestamp;
    private String ipAddress;

    public ActivityLog() {
    }

    public ActivityLog(int logId, int userId, String actionType, String description,
                       LocalDateTime timestamp, String ipAddress) {
        this.logId = logId;
        this.userId = userId;
        this.actionType = actionType;
        this.description = description;
        this.timestamp = timestamp;
        this.ipAddress = ipAddress;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}