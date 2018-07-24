import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;

public class Launch extends Application {
    Stage window;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
       /* try {
           // initilizeSDK();
        }catch(Exception e){
            e.printStackTrace();
        }*/

        Parent root = FXMLLoader.load(getClass().getResource("fxml/LoginPage.fxml"));
        window = primaryStage;
        window.setTitle("ODK Notifications Admin Panel");
        window.setScene(new Scene(root,740,420));
        window.show();

    }

    private static void initilizeSDK() throws IOException {
        FileInputStream serviceAccount = new FileInputStream("key.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://odk-notifications.firebaseio.com/")
                .build();
        FirebaseApp.initializeApp(options);
    }
}
