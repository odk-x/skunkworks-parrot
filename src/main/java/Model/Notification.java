package Model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Notification {
    private String id;
    private String title;
    private String message;
    private long date;
    private String group_id;
    private String type;
    private String status;
    private String date_str;
    private String attachmentPath;
    private ArrayList<String> responseList;

    public Notification(){

    }
    public Notification(String id, String title, String message, long date, String group_id, String type, String status) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.date = date;
        this.group_id = group_id;
        this.type = type;
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

    public void setDate(long date) {
        this.date = date;
        this.date_str = getDateStr(date);
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAttachmentPath(String attachmentPath){
        this.attachmentPath = attachmentPath;
    }

    public String getAttachmentPath(){
        return this.attachmentPath;
    }

    public void setResponseList(ArrayList<String>responseList){
        this.responseList = responseList;
    }

    public ArrayList<String> getResponseList(){
        return this.responseList;
    }
}
