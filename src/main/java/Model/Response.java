package Model;

import java.text.SimpleDateFormat;

public class Response {
    private String response;
    private String senderName;
    private String notificationId;
    private long time;
    private String date_str;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setTime(long time) {
        this.time = time;
        SimpleDateFormat newFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        this.date_str = newFormat.format(time);
    }

    public long getTime() {
        return time;
    }

    public void fetchTime() {
        SimpleDateFormat newFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        date_str = newFormat.format(time);
    }

    public String getDate_str() {
        return date_str;
    }

    public void setDate_str(String date_str) {
        this.date_str = date_str;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationId() {
        return this.notificationId;
    }
}
