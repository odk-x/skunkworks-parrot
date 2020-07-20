package Controller;

import Helper.QRCodeHelper;
import Model.Group;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;

public class QRCodeBoxController implements Initializable {
    private Group group;
    private QRCodeHelper qrCodeHelper = new QRCodeHelper();
    private BufferedImage qrCode;
    @FXML
    private ImageView qrCodeImage;


    QRCodeBoxController(Group group) {
        this.group = group;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        qrCode = qrCodeHelper.createQRCode(group.getGroupLink());
        qrCodeImage.setImage(SwingFXUtils.toFXImage(qrCode, null));
    }

    public void saveImageButtonClicked(MouseEvent mouseEvent) {
        if (qrCode != null) qrCodeHelper.saveQRCodeImage(qrCode, group.getName());
    }

}
