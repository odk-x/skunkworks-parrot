package Controller;

import Data.LoginData;
import Model.Group;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sun.rmi.runtime.Log;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public ListView grouplistView;
    public BorderPane content;
    public Label mainHeading;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void dashboardButtonClicked(){
        System.out.println("Dashboard Button Clicked");
        mainHeading.setText("Dashboard");
        try {
            content.getChildren().clear();
            content.getChildren().add(FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/Dashboard.fxml"))));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void createNotificationButtonClicked(){

        System.out.println("Create Notification Button Clicked");
        mainHeading.setText("Create Notification");
        try {
            content.getChildren().clear();
            content.getChildren().add(FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/CreateNotification.fxml"))));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
