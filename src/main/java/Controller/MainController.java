package Controller;

import Model.Group;
import com.google.firebase.database.*;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public AnchorPane content;
    public Label mainHeading;
    public ProgressIndicator progressIndicator;
    public TitledPane dashboard_tp;
    public TitledPane createNotification_tp;
    public TitledPane groups_tp;
    public TitledPane settings_tp;
    public ListView listView;
    private ArrayList<Group> groupArrayList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progressIndicator.setProgress(-1.0f);
        progressIndicator.setVisible(true);
        getGroups();
        progressIndicator.setProgress(1.0f);
        progressIndicator.setVisible(false);


        listView.setCellFactory(new Callback<ListView<Group>, ListCell<Group>>() {
            @Override
            public ListCell<Group> call(ListView<Group> param) {
                ListCell<Group> cell = new ListCell<Group>(){

                    @Override
                    protected void updateItem(Group notificationGroup, boolean bln) {
                        super.updateItem(notificationGroup, bln);
                        if (notificationGroup != null) {
                            setText(notificationGroup.getName());
                        }
                    }

                };
                return cell;
            }

        });
        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Group selected =(Group)listView.getSelectionModel().getSelectedItem();
                NotificationGroupButtonClicked(selected);
            }
        });
    }


    private void NotificationGroupButtonClicked(Group group) {
        System.out.println(group.getName() + " Group button clicked");

    }

    private ArrayList<String> getGroupsNames(ArrayList<Group> groups) {
        ArrayList<String> groupNameList = new ArrayList<>();
        for (Group group : groups) {
            groupNameList.add(group.getName());
        }
        return groupNameList;
    }

    private void getGroups() {
        groupArrayList = new ArrayList<>();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("group");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                groupArrayList.clear();
                for (DataSnapshot singleGroup: snapshot.getChildren()){
                    ArrayList<String> workerIdArrayList = new ArrayList();
                    for(DataSnapshot workerSnapshot : singleGroup.child("members").getChildren()){
                        workerIdArrayList.add((String)workerSnapshot.getValue());
                    }
                    Group newGroup = new Group((String)singleGroup.child("id").getValue(),(String)singleGroup.child("name").getValue(),workerIdArrayList);
                    groupArrayList.add(newGroup);
                    //System.out.println(newGroup.getName());
                }
                listView.setItems(FXCollections.observableList(groupArrayList));
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }


    @FXML
    private void dashboardButtonClicked() {
        System.out.println("Dashboard Button Clicked");
        mainHeading.setText("Dashboard");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
            setCenterScene(fxmlLoader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void createNotificationButtonClicked() {

        System.out.println("Create Notification Button Clicked");
        mainHeading.setText("Create Notification");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/CreateNotification.fxml"));
            setCenterScene(fxmlLoader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void createGroupButtonClicked() {
        System.out.println("Create new Group Button Clicked");
        mainHeading.setText("Create Notification Group");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/CreateGroup.fxml"));
            setCenterScene(fxmlLoader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void settingsButtonClicked() {
        System.out.println("Settings Button Clicked");
        mainHeading.setText("Settings");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/settings.fxml"));
            setCenterScene(fxmlLoader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCenterScene(FXMLLoader fxmlLoader) throws Exception{
        Pane pane = fxmlLoader.load();
        AnchorPane.setTopAnchor(pane,0.0);
        AnchorPane.setBottomAnchor(pane,0.0);
        AnchorPane.setLeftAnchor(pane,0.0);
        AnchorPane.setRightAnchor(pane,0.0);
        content.getChildren().clear();
        content.getChildren().add(pane);
    }
}