import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import Controller.SettingsController;

public class Launch extends Application {

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader= new FXMLLoader(getClass().getResource("fxml/LoginPage.fxml"));
        Parent root = loader.load();
        SettingsController.loginPageController=loader.getController();
        primaryStage.setTitle("ODK-X Notify Admin Panel");
        primaryStage.setScene(new Scene(root,740,420));
        primaryStage.show();

    }
}
