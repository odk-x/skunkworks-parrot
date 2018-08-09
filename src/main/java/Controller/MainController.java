package Controller;

import Data.*;
import Model.Group;
import com.google.firebase.database.*;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import org.opendatakit.sync.client.SyncClient;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
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
    public ImageView syncIcon;
    private ArrayList<Group> groupArrayList;
    DatabaseCommunicator databaseCommunicator;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        databaseCommunicator = new DatabaseCommunicator();
        getGroups();

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
        createNotificationButtonClicked();
    }


    private void NotificationGroupButtonClicked(Group group) {
        System.out.println(group.getName() + " Group button clicked");
        mainHeading.setText(group.getName());
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/NotificationGroup.fxml"));
            fxmlLoader.setController(new NotificationGroupController(group.getName()));
            setCenterScene(fxmlLoader);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getGroups() {
        groupArrayList = new ArrayList<>();
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateProgress(-1, 100);

                try {
                    groupArrayList = databaseCommunicator.getGroups();
                    updateProgress(100,100);
                } catch (Exception e) {
                    e.printStackTrace();
                    updateProgress(0, 100);
                }
                return null;
            }
        };
        task.setOnSucceeded(taskFinishEvent -> {
            listView.setItems(FXCollections.observableList(groupArrayList));
            progressIndicator.setVisible(false);
        });

        progressIndicator.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
    }


    @FXML
    private void createNotificationButtonClicked() {

        System.out.println("Create Notification Button Clicked");
        mainHeading.setText("Create Notification");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/CreateNotification.fxml"));
            fxmlLoader.setController(new CreateNotificationController(groupArrayList));
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

    public void syncFromServer() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateProgress(-1, 100);

                try {
                    //TODO: write code to fetch ODK Groups from SyncClient and save them in local Database.
                    SyncClient syncClient = new SyncClient();
                    Data data = new Data();
                    String url = data.getSYNC_CLIENT_URL();
                    URI uri = new URI(url);
                    url = url +"/odktables";
                    String appId = "default";
                    syncClient.init(uri.getHost(), LoginCredentials.credentials.getUsername(),LoginCredentials.credentials.getPassword());
                    ArrayList<Map<String, Object>> users = syncClient.getUsers(url, appId);
                    syncClient.close();

                    databaseCommunicator.clearTable("Groups");
                    ArrayList<String> groupsList = new ArrayList<>();

                    for (Map<String, Object> user : users) {
                        ArrayList<String> userGroupList = (ArrayList)user.get("roles");
                        for(String groupName : userGroupList){
                            if((groupName.startsWith("GROUP_") || groupName.startsWith("ROLE_"))&& !groupsList.contains(groupName)){
                                groupsList.add(groupName);
                                databaseCommunicator.insertGroup(new Group(groupName, groupName));
                                System.out.println(groupName);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    updateProgress(0, 100);
                }
                return null;
            }
        };
        task.setOnSucceeded(taskFinishEvent -> {
            getGroups();
            progressIndicator.setVisible(false);
        });

        progressIndicator.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
    }
}