package Model;

import java.util.ArrayList;

public class Group {
    private String id;
    private String name;
    private String groupLink;
    private ArrayList<String> notificationsList;
    private ArrayList<String> usersList;

    public Group() {

    }

    public Group(String id, String name) {
        this.id = id;
        this.name = name;
        this.groupLink = null;
    }

    public Group(String id, String name, String groupLink) {
        this.id = id;
        this.name = name;
        this.groupLink = groupLink;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupLink() {
        return groupLink;
    }

    public void setGroupLink(String groupLink) {
        this.groupLink = groupLink;
    }

    public void setNotificationsList(ArrayList<String> notificationsList) {
        this.notificationsList = notificationsList;
    }

    public void setUsersList(ArrayList<String> usersList) {
        this.usersList = usersList;
    }

    public ArrayList<String> getNotificationsList() {
        return this.notificationsList;
    }

    public ArrayList<String> getUsersList() {
        return this.usersList;
    }
}
