package Controller;

import Model.Group;
import com.google.firebase.database.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

 import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    // public ListView<String> groupListView;
    public BorderPane content;
    public Label mainHeading;
    public TreeTableView tableView;
    public TreeTableColumn col1;
    private TreeItem<String> dashboard;
    private TreeItem<String> createNotification;
    private TreeItem<String> settings;
    private TreeItem<String> groupRoot;
    private TreeItem<String> root;
    @FXML
    private TreeView<String> locationTreeView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //    groupListView.getItems().addAll(getGroupsNames(getGroups()));
        loadTreeItems();
        getGroups();
    }

    public void mouseClick(MouseEvent mouseEvent) {
        TreeItem item = locationTreeView.getSelectionModel().getSelectedItem();
        if (item.equals(dashboard)) {
            dashboardButtonClicked();
        } else if (item.equals(createNotification)) {
            createNotificationButtonClicked();
        } else if (item.equals(settings)) {
            settingsButtonClicked();
        }
        else if(item.equals(groupRoot)){
            createGroupButtonClicked();
        }
        else{
            NotificationGroupButtonClicked((String)item.getValue());
        }
        System.out.println(item + " clicked");
    }

    private void NotificationGroupButtonClicked(String value) {
        System.out.println(value + " Group button clicked");
        mainHeading.setText(value);
        try {
            content.getChildren().clear();
            content.getChildren().add(FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/Group.fxml"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadTreeItems() {

        dashboard = new TreeItem<>("Dashboard");
        createNotification = new TreeItem<>("Create Notification");
        settings = new TreeItem<>("Settings");

        groupRoot = new TreeItem<String>("Groups");
        groupRoot.setExpanded(false);

        root = new TreeItem<String>("Root Node");
        root.getChildren().addAll(dashboard, createNotification, groupRoot, settings);
        locationTreeView.setRoot(root);
        locationTreeView.setShowRoot(false);
    }

    private ArrayList<String> getGroupsNames(ArrayList<Group> groups) {
        ArrayList<String> groupNameList = new ArrayList<>();
        for (Group group : groups) {
            groupNameList.add(group.getId());
        }
        return groupNameList;
    }

    private void getGroups() {
        ArrayList<Group> groupArrayList = new ArrayList<>();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("group");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                groupRoot.getChildren().clear();
                for (DataSnapshot singleGroup: snapshot.getChildren()){
                    Group newGroup = new Group((String)singleGroup.child("id").getValue(),(String)singleGroup.child("name").getValue());
                    groupArrayList.add(newGroup);
                    System.out.println(newGroup.getName());
                    groupRoot.getChildren().add(new TreeItem<>(newGroup.getName()));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void dashboardButtonClicked() {
        System.out.println("Dashboard Button Clicked");
        mainHeading.setText("Dashboard");
        try {
            content.getChildren().clear();
            content.getChildren().add(FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/Dashboard.fxml"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createNotificationButtonClicked() {

        System.out.println("Create Notification Button Clicked");
        mainHeading.setText("Create Notification");
        try {
            content.getChildren().clear();
            content.getChildren().add(FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/CreateNotification.fxml"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createGroupButtonClicked() {
        System.out.println("Create new Group Button Clicked");
        mainHeading.setText("Create Notification Group");
        try {
            content.getChildren().clear();
            content.getChildren().add(FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/CreateGroup.fxml"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void settingsButtonClicked() {
        System.out.println("Settings Button Clicked");
        mainHeading.setText("Settings");
        try {
            content.getChildren().clear();
            content.getChildren().add(FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/Settings.fxml"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}