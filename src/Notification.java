public class Notification {
    private int notificationId;
    private int userId;
    private String message;
    private int seenStatus;
    private java.sql.Timestamp createdAt;

    public Notification(int notificationId, int userId, String message, int seenStatus, java.sql.Timestamp createdAt) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.message = message;
        this.seenStatus = seenStatus;
        this.createdAt = createdAt;
    }

    public int getNotificationId(){ return notificationId; }
    public int getUserId(){ return userId; }
    public String getMessage(){ return message; }
    public int getSeenStatus(){ return seenStatus; }
    public java.sql.Timestamp getCreatedAt(){ return createdAt; }
}
