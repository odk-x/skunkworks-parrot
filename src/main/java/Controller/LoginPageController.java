package Controller;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginPageController implements Initializable {

    public ImageView loginIcon;
    public TitledPane loginText;
    public ImageView configureIcon;
    public TitledPane configureText;
    public AnchorPane centerPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/LoginForm.fxml"));
        try {
            setPane(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void configureButtonClicked(javafx.scene.input.MouseEvent mouseEvent) {

       FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Settings.fxml"));
        try {
          setPane(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchToLoginPage(javafx.scene.input.MouseEvent mouseEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/LoginForm.fxml"));
        try {
            setPane(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setPane(Pane pane){
        AnchorPane.setTopAnchor(pane,0.0);
        AnchorPane.setBottomAnchor(pane,0.0);
        AnchorPane.setLeftAnchor(pane,0.0);
        AnchorPane.setRightAnchor(pane,0.0);
        centerPane.getChildren().clear();
        centerPane.getChildren().add(pane);
    }

    public void openHelpPage(javafx.scene.input.MouseEvent mouseEvent) {
        String url = "https://github.com/opendatakit/skunkworks-parrot/wiki";

        if(Desktop.isDesktopSupported()){
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }else{
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("xdg-open " + url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
