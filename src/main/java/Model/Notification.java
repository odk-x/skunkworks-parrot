package Model;

import java.text.SimpleDateFormat;

public class Notification {
    private String title;
    private String message;
    private long date;
    private String group_id;
    private String status;
    private String date_str;

    public Notification(String title, String message, long date, String group_id, String status) {
        this.title = title;
        this.message = message;
        this.date = date;
        this.group_id = group_id;
        this.status = status;
        this.date_str = getDateStr(this.date);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate_str(){
        return date_str;
    }

    public long getDate() {
        return date;
    }

    private String getDateStr(long date){
        SimpleDateFormat newFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        return newFormat.format(date);
    }
}
