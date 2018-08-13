package Controller;

import Data.*;
import Model.Group;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateGroupController implements Initializable {
    public TextField name_field;
    public ProgressIndicator progressIndicator;
    public Label statusLabel;
    public Button clipboardButton;
    public ImageView qrView;
    public Button saveImageButton;
    public Button createGroupButton;
    private String groupLink;
    private String groupName;
    private String groupId;
    private BufferedImage bufferedImage = null;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clipboardButton.setVisible(false);
        saveImageButton.setVisible(false);
    }

    public void createButtonClicked(MouseEvent mouseEvent) {
        progressIndicator.setVisible(true);
        qrView.setImage(null);
        clipboardButton.setText("Copy to clipboard");
        clipboardButton.setVisible(false);
        groupLink = null;
        groupName = null;
        bufferedImage = null;
        saveImageButton.setVisible(false);

        if (mouseEvent.getClickCount() == 1) {
            groupName = name_field.getText().toString().trim();
            if (!groupName.equals("")) {
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        updateMessage("Please Wait...");
                        updateProgress(-1, 100);
                        groupId = createNewGroup();
                        groupLink = createGroupLink(groupId);
                        if (groupLink != null) {
                            updateMessage("Group Link: " + groupLink);
                            updateProgress(100, 100);
                            addGroupToDatabase(new Group(groupId,groupName,groupLink));
                        } else {
                            updateMessage("There is some error in creating the group. Please try again.");
                            updateProgress(0, 100);
                        }
                        return null;
                    }
                };
                task.setOnSucceeded(taskFinishEvent -> {
                    if(groupLink!=null) {
                        clipboardButton.setVisible(true);
                        saveImageButton.setVisible(true);
                    }
                });

                progressIndicator.progressProperty().bind(task.progressProperty());
                statusLabel.textProperty().bind(task.messageProperty());
                new Thread(task).start();
            }
        }
    }
    private String createNewGroup() {
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child("group");
        DatabaseReference pushedPostRef = groupRef.push();
        return pushedPostRef.getKey();
    }

    private String createGroupLink( String groupId) {
        String link = null;
        try {
            link = createDynamicLink(groupId);
            System.out.println("LINK: " + link);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return link;
    }

    private String createDynamicLink(String groupId) {

        if(Data.data == null) Data.data = new Data();

        JSONObject androidInfo = new JSONObject();
        androidInfo.put("androidPackageName", Data.data.getANDROID_APP_PACKAGE_NAME());
        JSONObject dynamicLinkInfo = new JSONObject();
        dynamicLinkInfo.put("dynamicLinkDomain", Data.data.getDYNAMIC_LINK_DOMAIN());
        dynamicLinkInfo.put("link", "https://odknotificatons?id="+groupId);
        dynamicLinkInfo.put("androidInfo", androidInfo);

        JSONObject mainObject = new JSONObject();
        mainObject.put("dynamicLinkInfo", dynamicLinkInfo);

        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 1000); //Timeout Limit
        HttpResponse response;

        try {
            HttpPost post = new HttpPost(Data.data.getFIREBASE_INVITES_URL());
            StringEntity se = new StringEntity(mainObject.toString());
            post.setEntity(se);
            response = client.execute(post);

            if (response != null) {
                InputStream in = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null) {
                    result.append(line);
                }
                System.out.println(result.toString());
                JSONObject responseJSON = new JSONObject(result.toString());
                groupLink = responseJSON.getString("shortLink");
                progressIndicator.setVisible(false);
                createQRCode(groupLink);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error"+ " Cannot Establish Connection");
        }
        return groupLink;
    }

    public void copyToClipboardClicked(MouseEvent mouseEvent) {
        if(groupLink!=null){
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(groupLink);
            clipboard.setContent(content);
            clipboardButton.setText("Copied");
        }
    }

    private void createQRCode(String groupLink){
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int width = 300;
        int height = 300;
        String fileType = "png";

        try {
            BitMatrix byteMatrix = qrCodeWriter.encode(groupLink, BarcodeFormat.QR_CODE, width, height);
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            bufferedImage.createGraphics();

            Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);
            graphics.setColor(Color.BLACK);

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }

            System.out.println("Success...");

        } catch (WriterException ex) {
            Logger.getLogger(CreateGroupController.class.getName()).log(Level.SEVERE, null, ex);
        }
        qrView.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
    }

    private void saveQRCodeImage(String groupName){

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", ".png",".jpg"));
        File file = fileChooser.showSaveDialog(new Stage());
        fileChooser.setInitialFileName(groupName);
        if (file != null) {
            try {
                ImageIO.write(bufferedImage, "png", file);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void saveImageButtonClicked(MouseEvent mouseEvent) {
        if(bufferedImage!=null) saveQRCodeImage(groupName);
    }

    private void addGroupToDatabase(Group group){
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child("group").child(group.getId());
        groupRef.setValueAsync(group);

        DatabaseCommunicator dc = new DatabaseCommunicator();
        dc.insertGroup(group);
        dc.closeConnection();
    }

}

