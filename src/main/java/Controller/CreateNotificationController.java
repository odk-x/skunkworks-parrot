package Controller;


import Data.DatabaseCommunicator;
import Model.Group;
import Model.Notification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class CreateNotificationController implements Initializable {

    public TextField title_field;
    public TextField message_field;
    public Button send_button;
    public ProgressIndicator progressIndicator;
    public Label statusLabel;
    public ComboBox<Group> comboBox;
    private ArrayList<Group> groupArrayList;

    public CreateNotificationController(ArrayList<Group> groupArrayList) {
        this.groupArrayList = groupArrayList;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progressIndicator.setVisible(false);

        comboBox.setItems(FXCollections.observableList(groupArrayList));
        comboBox.setCellFactory(new Callback<ListView<Group>, ListCell<Group>>() {
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

        comboBox.setConverter(new StringConverter<Group>() {
            @Override
            public String toString(Group object) {
                return object.getName();
            }

            @Override
            public Group fromString(String string) {
                return null;
            }
        });


    }

    public void sendButtonClicked() {
        String titleStr = title_field.getText();
        String messageStr = message_field.getText();

        if(!(titleStr.isEmpty() && messageStr.isEmpty())) {
            progressIndicator.setVisible(true);
            send_button.setDisable(true);
            Group selected = comboBox.getSelectionModel().getSelectedItem();
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() {

                    updateMessage("Please Wait...");
                    updateProgress(-1, 100);
                    String topic = selected.getId();

                    Message message = Message.builder()
                            .putData("title", titleStr)
                            .putData("message", messageStr)
                            .putData("group", selected.getId())
                            .setTopic(topic)
                            .build();

                    try {
                        String response = FirebaseMessaging.getInstance().send(message);
                        System.out.println("Response:" + response);
                        System.out.println("Successfully sent message: " + response);
                        updateProgress(100, 100);
                        updateMessage("Message sent successfully.");
                        DatabaseCommunicator dc= new DatabaseCommunicator();
                        dc.addNotification(new Notification(titleStr,messageStr,new Date().getTime() ,selected.getId(),null));
                        dc.closeConnection();

                    } catch (Exception e) {
                        e.printStackTrace();
                        updateProgress(0, 100);
                        updateMessage("Error in sending message please try again.");
                        System.out.println("error");
                    }
                    return null;
                }
            };
            task.setOnSucceeded(taskFinishEvent -> {
                send_button.setDisable(false);
                title_field.clear();
                message_field.clear();
            });

            progressIndicator.progressProperty().bind(task.progressProperty());
            statusLabel.textProperty().bind(task.messageProperty());
            new Thread(task).start();

        }
    }

}
