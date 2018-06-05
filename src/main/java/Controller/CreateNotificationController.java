package Controller;

import Data.LoginData;
import Model.Group;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CreateNotificationController implements Initializable {

    public TextField title_field;
    public TextField message_field;
    public Button send_button;
    public ProgressIndicator progressIndicator;
    public Label statusLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progressIndicator.setVisible(false);
        statusLabel.setVisible(false);
    }

    public void sendButtonClicked() {
        progressIndicator.setVisible(true);
        progressIndicator.setProgress(-1.0f);
        send_button.setDisable(true);
        String titleStr = title_field.getText();
        String messageStr = message_field.getText();
        if(!(titleStr.isEmpty() && messageStr.isEmpty())) {
// See documentation on defining a message payload.
            String topic = "all";
            Message message = Message.builder()
                    .putData("title", titleStr)
                    .putData("message", messageStr)
                    .setTopic(topic)
                    .build();

// Send a message to the device corresponding to the provided
// registration token.
Task<Integer> task = new Task<Integer>() {
    @Override
    protected Integer call() throws Exception {
        
        return null;
    }
};
                Thread t = new Thread(() -> {
                    try{
                        String response = FirebaseMessaging.getInstance().send(message);
                        System.out.println("Successfully sent message: " + response.toString());
                        progressIndicator.setVisible(false);
                        send_button.setDisable(false);
                        title_field.setText("");
                        message_field.setText("");
                        statusLabel.setText("Message sent successfully.");
/*
// Send a message to the devices subscribed to the provided topic.
                        String response2 = FirebaseMessaging.getInstance().send(message);*/
// Response is a message ID string.
                     //   System.out.println("Successfully sent message: " + response2);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        progressIndicator.setVisible(false);
                        send_button.setDisable(false);
                        title_field.setText("");
                        message_field.setText("");
                        statusLabel.setText("Error in sending message please try again.");
                        System.out.println("error");
                    }
                });
                t.start();
            }
// Response is a message ID string.

    }
}
