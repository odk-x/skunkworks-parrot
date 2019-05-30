package Controller;

import Data.Data;
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
    public TextField databaseURLField;
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
       if(Data.data ==null) Data.data = new Data();

       serviceAccountKeyPathField.setText(Data.data.getSERVICE_ACCOUNT_KEY_PATH());
       webAPIKeyField.setText(Data.data.getWEB_API_KEY());
       databaseURLField.setText(Data.data.getDATABASE_URL());
       dynamicLinkDomainField.setText(Data.data.getDYNAMIC_LINK_DOMAIN());
       packageNameField.setText(Data.data.getANDROID_APP_PACKAGE_NAME());
       syncClientURLField.setText(Data.data.getSYNC_CLIENT_URL());
       System.out.print(Data.data);
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
        Data.data.setANDROID_APP_PACKAGE_NAME(packageNameField.getText());
        Data.data.setDYNAMIC_LINK_DOMAIN(dynamicLinkDomainField.getText());
        Data.data.setSERVICE_ACCOUNT_KEY_PATH(serviceAccountKeyPathField.getText());
        Data.data.setWEB_API_KEY(webAPIKeyField.getText());
        Data.data.setSYNC_CLIENT_URL(syncClientURLField.getText());
        Data.data.setDATABASE_URL(databaseURLField.getText());
        Data.data.saveKeys();
    }
}
