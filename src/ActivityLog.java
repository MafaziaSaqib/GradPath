public class ActivityLog {
    private int logId;
    private int userId;
    private String action;
    private java.sql.Timestamp timestamp;

    public ActivityLog(int logId, int userId, String action, java.sql.Timestamp timestamp) {
        this.logId = logId;
        this.userId = userId;
        this.action = action;
        this.timestamp = timestamp;
    }

    public int getLogId() { return logId; }
    public int getUserId() { return userId; }
    public String getAction() { return action; }
    public java.sql.Timestamp getTimestamp() { return timestamp; }
}
