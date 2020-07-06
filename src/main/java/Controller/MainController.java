package Controller;

import Data.DatabaseCommunicator;
import Data.ServerDatabaseCommunicator;
import Model.Group;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
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
    public TitledPane createNotification_tp;
    public TitledPane groups_tp;
    public TitledPane settings_tp;
    public ListView<Group> listView;
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
                return new ListCell<Group>(){

                    @Override
                    protected void updateItem(Group notificationGroup, boolean bln) {
                        super.updateItem(notificationGroup, bln);
                        if (notificationGroup != null) {
                            setText(notificationGroup.getName());
                        }
                    }
                };
            }

        });
        listView.setOnMouseClicked(event -> {
            Group selected = listView.getSelectionModel().getSelectedItem();
            NotificationGroupButtonClicked(selected);
        });
        createNotificationButtonClicked();
    }


    private void NotificationGroupButtonClicked(Group group) {
        System.out.println(group.getName() + " Group button clicked");
        mainHeading.setText(group.getName());
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/NotificationGroup.fxml"));
            fxmlLoader.setController(new NotificationGroupController(group));
            setCenterScene(fxmlLoader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getGroups() {
        groupArrayList = new ArrayList<>();
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                updateProgress(-1, 100);

                try {
                    groupArrayList = ServerDatabaseCommunicator.getInstance().getGroups();
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
            groupArrayList = ServerDatabaseCommunicator.getInstance().getGroups();
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
            fxmlLoader.setController(new CreateGroupController(this));
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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Settings.fxml"));
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
        progressIndicator.setVisible(true);
        syncIcon.setVisible(false);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                updateProgress(-1, 100);

                try {
                    getGroups();
                } catch (Exception e) {
                    e.printStackTrace();
                    updateProgress(0, 100);
                }
                return null;
            }
        };
        task.setOnSucceeded(taskFinishEvent -> {
            progressIndicator.setVisible(false);
            syncIcon.setVisible(true);
        });

        progressIndicator.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
    }
}