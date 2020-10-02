package Controller;

import Data.Data;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    public static LoginPageController loginPageController;
    public TextField serviceAccountKeyPathField;
    public TextField webAPIKeyField;
    public TextField firebaseDatabaseURLField;
    public TextField dynamicLinkDomainField;
    public TextField packageNameField;
    public ImageView folderIcon;
    public TextField syncClientURLField;
    public TextField storageBucketField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        readKeyData();
    }

    private void readKeyData() {
        if (Data.data == null) Data.data = new Data();

        serviceAccountKeyPathField.setText(Data.data.getSERVICE_ACCOUNT_KEY_PATH());
        webAPIKeyField.setText(Data.data.getWEB_API_KEY());
        firebaseDatabaseURLField.setText(Data.data.getFIREBASE_DATABASE_URL());
        dynamicLinkDomainField.setText(Data.data.getDYNAMIC_LINK_DOMAIN());
        packageNameField.setText(Data.data.getANDROID_APP_PACKAGE_NAME());
        syncClientURLField.setText(Data.data.getSYNC_CLIENT_URL());
        storageBucketField.setText(Data.data.getSTORAGE_BUCKET());
        System.out.print(Data.data);
    }

    @FXML
    private void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extensionFilter);
        String userDirectoryString = System.getProperty("user.home");
        File userDirectory = new File(userDirectoryString);
        if (!userDirectory.canRead()) {
            userDirectory = new File("c:/");
        }
        fileChooser.setInitialDirectory(userDirectory);
        File chosenFile = fileChooser.showOpenDialog(null);
        String path;
        if (chosenFile != null) {
            path = chosenFile.getPath();
            serviceAccountKeyPathField.setText(path);
        }
    }

    public void saveButtonClicked() {
        Data.data.setANDROID_APP_PACKAGE_NAME(packageNameField.getText());
        Data.data.setDYNAMIC_LINK_DOMAIN(dynamicLinkDomainField.getText());
        Data.data.setSERVICE_ACCOUNT_KEY_PATH(serviceAccountKeyPathField.getText());
        Data.data.setWEB_API_KEY(webAPIKeyField.getText());
        Data.data.setSYNC_CLIENT_URL(syncClientURLField.getText());
        Data.data.setFIREBASE_DATABASE_URL(firebaseDatabaseURLField.getText());
        Data.data.setSTORAGE_BUCKET(storageBucketField.getText());
        Data.data.saveKeys();
        loginPageController.switchToLoginPage();
    }
}
