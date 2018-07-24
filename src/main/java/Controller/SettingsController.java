package Controller;

import Data.LoginData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    public TextField serviceAccountKeyPathField;
    public TextField webAPIKeyField;
    public TextField dynamicLinkDomainField;
    public TextField packageNameField;
    public ImageView folderIcon;
    public TextField syncClientURLField;
    private HashMap<String, String> configure;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        readKeyData();
    }

    private void readKeyData() {
       if(LoginData.loginData==null) LoginData.loginData = new LoginData();

       serviceAccountKeyPathField.setText(LoginData.loginData.getSERVICE_ACCOUNT_KEY_PATH());
       webAPIKeyField.setText(LoginData.loginData.getWEB_API_KEY());
       dynamicLinkDomainField.setText(LoginData.loginData.getDYNAMIC_LINK_DOMAIN());
       packageNameField.setText(LoginData.loginData.getANDROID_APP_PACKAGE_NAME());
       syncClientURLField.setText(LoginData.loginData.getSYNC_CLIENT_URL());
       System.out.print(LoginData.loginData);
    }

    @FXML
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
        LoginData.loginData.setANDROID_APP_PACKAGE_NAME(packageNameField.getText());
        LoginData.loginData.setDYNAMIC_LINK_DOMAIN(dynamicLinkDomainField.getText());
        LoginData.loginData.setSERVICE_ACCOUNT_KEY_PATH(serviceAccountKeyPathField.getText());
        LoginData.loginData.setWEB_API_KEY(webAPIKeyField.getText());
        LoginData.loginData.setSYNC_CLIENT_URL(syncClientURLField.getText());
        LoginData.loginData.saveKeys();
    }
}
