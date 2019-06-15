package Controller;

import Data.*;
import Model.Notification;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.ResourceBundle;

public class NotificationGroupController implements Initializable {

    private String id;
    public TableView tableView;


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
