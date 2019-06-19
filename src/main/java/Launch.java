import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launch extends Application {

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("fxml/LoginPage.fxml"));
        primaryStage.setTitle("ODK Notifications Admin Panel");
        primaryStage.setScene(new Scene(root,740,420));
        primaryStage.show();

    }
}
