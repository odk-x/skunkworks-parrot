package Controller;

import Data.*;
import Model.Group;
import Model.Notification;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Set;

public class NotificationGroupController implements Initializable {

    private String id;
    @FXML
    private
    TableView tableView;
    ObservableList observableList = FXCollections.observableArrayList();


    NotificationGroupController(String id) {
        this.id = id;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setListView();
    }

    private void setListView()
    {
        DatabaseCommunicator dc = new DatabaseCommunicator();

        ObservableList<Object> notificationSet = FXCollections.observableArrayList(dc.getNotificationsList(id));

        TableColumn<Notification, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setMinWidth(150);
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Notification, String> messageColumn = new TableColumn<>("Message");
        messageColumn.setMinWidth(300);
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));

        TableColumn<Notification, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setMinWidth(150);
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date_str"));


        tableView.setItems(notificationSet);
        tableView.getColumns().clear();
        tableView.getColumns().addAll(titleColumn,messageColumn,dateColumn);

    }

}
