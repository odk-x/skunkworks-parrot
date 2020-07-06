package Controller;


import Data.ServerDatabaseCommunicator;
import Model.Group;
import Model.Notification;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.firebase.cloud.StorageClient;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.wink.json4j.JSONException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.UUID;

public class CreateNotificationController implements Initializable {

    public TextField title_field;
    public TextField message_field;
    public TextField image_path;
    public Button send_button;
    public Button image_selector_button;
    public ProgressIndicator progressIndicator;
    public Label statusLabel;
    public ComboBox<Group> comboBox;
    private ArrayList<Group> groupArrayList;
    public ToggleGroup toggleGroup;
    public RadioButton rb_simple;
    public RadioButton rb_interactive;
    private String file_name;
    boolean imageIsSelected;


    public CreateNotificationController(ArrayList<Group> groupArrayList) {
        this.groupArrayList = groupArrayList;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progressIndicator.setVisible(false);
        statusLabel.setVisible(false);
        imageIsSelected=false;

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
        toggleGroup = new ToggleGroup();
        rb_simple.setToggleGroup(toggleGroup);
        rb_interactive.setToggleGroup(toggleGroup);
        rb_simple.setSelected(true);
        image_selector_button.setOnAction(event);

    }

    public void sendButtonClicked() {
        String titleStr = title_field.getText();
        String messageStr = message_field.getText();
        Group selected = comboBox.getSelectionModel().getSelectedItem();

        if(!(titleStr.isEmpty() && messageStr.isEmpty()) && !selected.equals(null)) {
            progressIndicator.setVisible(true);
            statusLabel.setVisible(true);
            send_button.setDisable(true);
            String type = ((RadioButton)toggleGroup.getSelectedToggle()).getText();
            String notificationId = UUID.randomUUID().toString();
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() {
                    Message message;
                    updateMessage("Please Wait...");
                    updateProgress(-1, 100);
                    String topic = selected.getId();
                    if(imageIsSelected) {
                        Bucket bucket = StorageClient.getInstance().bucket();
                        String path=image_path.getText();
                        try {
                            byte[] data = Files.readAllBytes(Paths.get(image_path.getText()));
                            Blob blob=bucket.create(file_name, data, Bucket.BlobTargetOption.predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ));
                            message = Message.builder()
                                    .putData("id", notificationId)
                                    .putData("title", titleStr)
                                    .putData("message", messageStr)
                                    .putData("group", selected.getId())
                                    .putData("type", type)
                                    .putData("img", blob.getMediaLink() )
                                    .setTopic(topic)
                                    .build();
                            String response = FirebaseMessaging.getInstance().send(message);
                            System.out.println("Response:" + response);
                            System.out.println("Successfully sent message: " + response);
                            updateProgress(100, 100);
                            updateMessage("Message sent successfully.");
                            Notification notification = new Notification(notificationId,titleStr,messageStr,new Date().getTime() ,selected.getId(), type, null);
                            notification.setAttachmentPath(path);
                            ServerDatabaseCommunicator.getInstance().uploadNotification(notification);

                        }
                        catch (IOException | FirebaseMessagingException | JSONException e) {
                            e.printStackTrace();
                            updateProgress(0, 100);
                            updateMessage("Error in sending message please try again.");
                            System.out.println("error");
                        }

                    }
                    else {


                        try {
                            message = Message.builder()
                                    .putData("id", notificationId)
                                    .putData("title", titleStr)
                                    .putData("message", messageStr)
                                    .putData("group", selected.getId())
                                    .putData("type", type)
                                    .setTopic(topic)
                                    .build();
                            String response = FirebaseMessaging.getInstance().send(message);
                            System.out.println("Response:" + response);
                            System.out.println("Successfully sent message: " + response);
                            updateProgress(100, 100);
                            updateMessage("Message sent successfully.");
                            Notification notification = new Notification(notificationId,titleStr,messageStr,new Date().getTime() ,selected.getId(), type, null);
                            ServerDatabaseCommunicator.getInstance().uploadNotification(notification);

                        } catch (Exception e) {
                            e.printStackTrace();
                            updateProgress(0, 100);
                            updateMessage("Error in sending message please try again.");
                            System.out.println("error");
                        }
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
    EventHandler<ActionEvent> event =
            new EventHandler<ActionEvent>() {

                public void handle(ActionEvent event)
                {   FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
                    FileChooser fileChooser= new FileChooser();
                    fileChooser.getExtensionFilters().add(imageFilter);
                    Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
                    // get the file selected
                    File file = fileChooser.showOpenDialog(stage);
                    file_name = file.getName();

                    if (file.exists()) {
                        imageIsSelected=true;
                        image_path.setText(file.getAbsolutePath());

                    }
                }
            };


}
