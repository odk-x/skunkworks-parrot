package Controller;

import Data.Data;
import Data.LoginCredentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.wink.json4j.JSONException;
import org.opendatakit.sync.client.SyncClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.net.ConnectException;


import static Data.Data.FIREBASE_KEYS_FILE_NAME;

public class LoginFormController implements Initializable {
    public TextField usernameField;
    public TextField passwordField;
    public Button loginButton;
    public ProgressIndicator progressIndicator;
    public Label statusLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        stopProgress();
        setStatusText(null);

    }

    private void setStatusText(String status){
        statusLabel.setText(status);
    }

    public void loginButtonClicked(){
        loginButton.setDisable(true);
        setStatusText(null);
        startProgress();
        usernameField.setDisable(true);
        passwordField.setDisable(true);

        if(usernameField.getText().trim().isEmpty()){
            setStatusText("Username field can't be empty");
        }
        else if (passwordField.getText().trim().isEmpty()){
            setStatusText("Password field can't be empty");
        }
        else{
            attemptLogin(usernameField.getText().trim(), passwordField.getText().trim());
        }
    }
    boolean flag = true;

    private void attemptLogin(String username, String password) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                flag = true;
                updateMessage("Please Wait...");
                updateProgress(-1, 100);

                try {
                    if(verifyCredentials(username,password)) {
                        LoginCredentials.credentials = new LoginCredentials(username,password);
                        try {
                            initializeFirebaseSDK();
                        }catch (IOException e){
                            flag = false;
                            updateProgress(0,100);
                            updateMessage("Error: Firebase key file not found.");
                        }
                    }else{
                        flag = false;
                        updateProgress(0,100);
                        updateMessage("Invalid username/password, please try again.");

                    }
                } catch (ConnectException e){
                    flag = false;
                    updateProgress(0,100);
                    updateMessage("Error: Network Connection Error");
                } catch (Exception e) {
                    flag = false;
                    e.printStackTrace();
                    updateProgress(0, 100);
                }
                return null;
            }
        };
        task.setOnSucceeded(taskFinishEvent -> {
            //TODO: complete onSucceeded method.
            if(flag)  moveToDashboard();
            else{
                usernameField.setDisable(false);
                passwordField.setDisable(false);
                loginButton.setDisable(false);
            }
        });

        progressIndicator.progressProperty().bind(task.progressProperty());
        statusLabel.textProperty().bind(task.messageProperty());
        new Thread(task).start();
    }

    private boolean verifyCredentials(String username, String password) throws IOException, JSONException, URISyntaxException {
        SyncClient syncClient = new SyncClient();
        Data data = new Data();
        String url = data.getSYNC_CLIENT_URL();
        URI uri = new URI(url);
        url = url +"/odktables";
        String appId = "default";
        syncClient.init(uri.getHost(),username,password);
        ArrayList<Map<String, Object>> users = syncClient.getUsers(url, appId);
        syncClient.close();
        for (Map<String, Object> user : users) {
            if(user.get("user_id").equals("username:"+ username)){
                if(((ArrayList<String>)(user.get("roles"))).contains("ROLE_SITE_ACCESS_ADMIN")){
                    return true;
                }
            }
        }
        return false;
    }

    private void initializeFirebaseSDK() throws IOException{
        Data data = new Data();
        FileInputStream serviceAccount = new FileInputStream(FIREBASE_KEYS_FILE_NAME);
        FirebaseOptions options = new FirebaseOptions.Builder()
                 .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                 .setDatabaseUrl(data.getFIREBASE_DATABASE_URL())
                 .build();
        FirebaseApp.initializeApp(options);

    }


    private void moveToDashboard() {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MainUI.fxml"));
        Stage stage = new Stage();
        stage.setTitle("ODK-X Notify Admin Panel");
        try {
            stage.setScene(new Scene(fxmlLoader.load(),    1024, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.show();
        ((Stage) loginButton.getScene().getWindow()).close();
    }

    private void startProgress(){
        progressIndicator.setVisible(true);
        progressIndicator.setProgress(-1.0);
    }

    private void stopProgress(){
        progressIndicator.setVisible(false);
        progressIndicator.setProgress(0.0);
    }

}
