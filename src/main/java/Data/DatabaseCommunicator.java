package Data;

import Model.Group;
import Model.Notification;
import javafx.scene.Cursor;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseCommunicator {
    private Connection c = null;
    private Statement stmt = null;
    private static final String TABLE_GROUPS = "Groups";
    private static final String TABLE_NOTIFICATIONS = "Notifications";

    private static final String COLUMN_NAME = "grp_name";
    private static final String COLUMN_GRP_ID = "grp_id";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_DATE = "date_int";
    private static final String COLUMN_STATUS = "status_str";
    private static final String COLUMN_GRP_LINK="group_link";

    public DatabaseCommunicator(){
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.db");
            createGroupTable();
            createNotificationsTable();
            System.out.println("Database successfully opened");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    public ArrayList<Group> getGroups(){
        ArrayList<Group> groupList = new ArrayList<>();
        groupList.add(new Group("all","all"));

        try{
            this.stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM "+TABLE_GROUPS+";");

            while(rs.next()){
                String groupName = rs.getString(COLUMN_NAME);
                String groupId = rs.getString(COLUMN_GRP_ID);
                String groupLink = rs.getString(COLUMN_GRP_LINK);
                Group group = new Group(groupId,groupName,groupLink);
                groupList.add(group);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return groupList;
    }

    //close database
    public void closeConnection(){
        try {
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void executeQuery(String query){
        try {
            this.stmt = c.createStatement();
            stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void executeUpdate(String query){
        try {
            this.stmt = c.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createGroupTable(){
        executeUpdate("CREATE TABLE IF NOT EXISTS "+TABLE_GROUPS+" ( "+ COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_GRP_ID+" VARCHAR, "+ COLUMN_NAME+" VARCHAR, "+COLUMN_GRP_LINK+" VARCHAR);");
    }

    private void createNotificationsTable(){
        executeUpdate("CREATE TABLE IF NOT EXISTS "+TABLE_NOTIFICATIONS+" (" +
                COLUMN_ID+" Integer PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_TITLE+" VARCHAR(100), "+
                COLUMN_MESSAGE+" VARCHAR(1000), "+
                COLUMN_DATE+" Integer, "+
                COLUMN_GRP_ID+" VARCHAR(30), "+
                COLUMN_STATUS+" VARCHAR(20));");
    }

    public void insertGroup(Group group){
        executeUpdate("INSERT INTO "+TABLE_GROUPS+"("+COLUMN_GRP_ID+", "+COLUMN_NAME+") VALUES ('"+group.getId()+"', '"+group.getName()+"');");
    }

    public void clearTable(String tableName){
        executeUpdate("DELETE FROM "+ tableName +";");
    }

    public ArrayList<Notification> getNotificationsList(String groupId){
        ArrayList<Notification> notifications = new ArrayList<>();
        try{
            this.stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM "+TABLE_NOTIFICATIONS+" WHERE "+COLUMN_GRP_ID+" = '"+groupId+"';");
            System.out.println(rs);
            while(rs.next()){
                String title = rs.getString(COLUMN_TITLE);
                String message = rs.getString(COLUMN_MESSAGE);
                Integer date = rs.getInt(COLUMN_DATE);
                String group_id = rs.getString(COLUMN_GRP_ID);
                String status = rs.getString(COLUMN_STATUS);

                Notification notification = new Notification(title,message,date,group_id,status );
                notifications.add(notification);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return notifications;
    }

    public void addNotification(Notification notification){
           executeUpdate("INSERT INTO "+TABLE_NOTIFICATIONS+" ("+COLUMN_TITLE+", "+COLUMN_MESSAGE+", "+COLUMN_DATE+", "+COLUMN_GRP_ID+", "+COLUMN_STATUS+" ) VALUES ('"+notification.getTitle()+"', '"+notification.getMessage()+"', '"+notification.getDate()+"', '"+notification.getGroup_id()+"', '"+notification.getStatus()+"');");
    }
}
