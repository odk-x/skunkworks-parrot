package Model;

import java.text.SimpleDateFormat;

public class Response {
    private String response;
    private String senderID;
    private long time;
    private String date_str;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
    public void fetchTime(){
        SimpleDateFormat newFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        date_str = newFormat.format(time);
    }

    public String getDate_str() {
        return date_str;
    }

    public void setDate_str(String date_str) {
        this.date_str = date_str;
    }
}
