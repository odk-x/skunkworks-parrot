package Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.json.JSONObject;

import java.awt.event.MouseEvent;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    public TextField serviceAccountKeyPathField;
    public TextField webAPIKeyField;
    public TextField dynamicLinkDomainField;
    public TextField packageNameField;
    public ImageView folderIcon;
   @FXML public Button saveButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        saveButton.setOnMouseClicked(e->saveButtonClicked());
        folderIcon.setOnMouseClicked(e->openFileChooser());
    }

    private void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extentionFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extentionFilter);
        String userDirectoryString = System.getProperty("user.home");
        File userDirectory = new File(userDirectoryString);
        if(!userDirectory.canRead()) {
            userDirectory = new File("c:/");
        }
        fileChooser.setInitialDirectory(userDirectory);
        File chosenFile = fileChooser.showOpenDialog(null);
        String path;
        if(chosenFile != null) {
            path = chosenFile.getPath();
            serviceAccountKeyPathField.setText(path);
        } else {
            path = null;
        }
    }

    public void saveButtonClicked()  {
        String serviceAccountKeyPath = serviceAccountKeyPathField.getText();
        String webAPIKey = webAPIKeyField.getText();
        String dynamicLinkDomain = dynamicLinkDomainField.getText();
        String packageName = packageNameField.getText();

        JSONObject obj = new JSONObject();
        obj.put("serviceAccountKeyPath", serviceAccountKeyPath);
        obj.put("webAPIKey", webAPIKey);
        obj.put("dynamicLinkDomain", dynamicLinkDomain);
        obj.put("packageName", packageName);

        try{
            FileWriter file = new FileWriter("keys.json");
            file.write(obj.toString());
            System.out.println("Successfully Copied JSON Object to File...\n" + obj.toString());
            file.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(serviceAccountKeyPath);
            os = new FileOutputStream("serviceAccountKey.json");
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }




}
