package Controller;

import Data.DatabaseCommunicator;
import Model.Group;
import Model.Notification;
import Model.Response;
import com.google.firebase.database.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class NotificationResponseController implements Initializable {

    private Notification notification;
    @FXML private TableView tableView;
    @FXML private Label title;
    @FXML private Label message;
    @FXML private Label time;
    ObservableList observableList = FXCollections.observableArrayList();
    @FXML private Pane grp_detail_pane;
    private ArrayList<Response> responseArrayList = new ArrayList<>();


    NotificationResponseController(Notification notification) {
        this.notification = notification;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title.setText(notification.getTitle());
        message.setText(notification.getMessage());
        time.setText(notification.getDate_str());

        DatabaseReference responseRef = FirebaseDatabase.getInstance().getReference("/responses/"+notification.getId());
        responseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                responseArrayList.clear();
                for(DataSnapshot childSnapshot:snapshot.getChildren()){
                    Response response = childSnapshot.getValue(Response.class);
                    response.fetchTime();
                    responseArrayList.add(response);
                }
                Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        setTableView();
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    public void setTableView()
    {
        ObservableList<Object> responseSet = FXCollections.observableArrayList(responseArrayList);

        TableColumn<Notification, String> senderIdColumn = new TableColumn<>("Sender ID");
        senderIdColumn.setMinWidth(100);
        senderIdColumn.setCellValueFactory(new PropertyValueFactory<>("senderID"));

        TableColumn<Notification, String> responseColumn = new TableColumn<>("Response");
        responseColumn.setMinWidth(300);
        responseColumn.setCellValueFactory(new PropertyValueFactory<>("response"));

        TableColumn<Notification, String> timeColumn = new TableColumn<>("Time");
        timeColumn.setMinWidth(150);
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("date_str"));

        tableView.setItems(responseSet);
        tableView.getColumns().clear();
        tableView.getColumns().addAll(senderIdColumn,responseColumn,timeColumn);
        tableView.setRowFactory(tv->{
            TableRow<Response> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (! row.isEmpty() && event.getButton()== MouseButton.PRIMARY && event.getClickCount()==2) {
                    Response clickedRow = row.getItem();
                    System.out.println(clickedRow);
                }
            });
            return row ;
        });

    }
}
