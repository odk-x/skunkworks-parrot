package Data;

import Model.Group;
import javafx.scene.Cursor;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseCommunicator {
    private Connection c = null;
    private Statement stmt = null;

    public DatabaseCommunicator(){
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.db");
            System.out.println("Database successfully opened");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    public ArrayList<Group> getGroups(){
        ArrayList<Group> groupList = new ArrayList<>();
       /* try{
            this.stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Groups");

            while(rs.next()){
                String groupName = rs.getString("name");
                Group group = new Group(groupName, groupName);
                groupList.add(group);
                System.out.println(groupName);
            }
        } catch (Exception e){
            e.printStackTrace();
        }*/
       //TODO : complete this method to fetch ODK Groups from database.
        groupList.add(new Group("all","all"));
        groupList.add(new Group("north","north"));
        groupList.add(new Group("south","south"));

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

    public void createGroupTable(){
        executeQuery("CREATE TABLE IF NOT EXISTS Groups (name VARCHAR PRIMARY KEY);");
    }

    public void insertGroup(Group group){
        executeQuery("INSERT INTO Groups Values ("+group.getName()+")");
    }
}
