import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.loadui.testfx.controls.Commons.hasText;

public class LaunchTest extends GuiTest {

    private Parent mainNode;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {

    }

    @Override
    protected Parent getRootNode() {
        try {
            mainNode = FXMLLoader.load(Launch.class.getResource("fxml/LoginPage.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mainNode;
    }


    @Test
    public void loginTest () {
        click("#usernameField");
        type("hgupta");
        click("#passwordField");
        type("admin");
        click("#loginButton");
        Label statusLabel = find("#statusLabel");
        waitUntil(statusLabel,hasText("Login Successful"),10);

        //assertEquals(,statusLabel.getText());
    }

    @Test
    public void createGroupTest() throws TimeoutException {
        click("#createGroup_tp");
        click("#name_field");
        type("test_group");
        click("#createButton");
        Label status = find("#statusLabel");
        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS,()-> !status.getText().equals("Please Wait..."));
        String grouplink = status.getText();
        assertEquals("Group Link: ",grouplink.substring(0,12));
    }

    @Test
    public void createNotificationTest() throws TimeoutException {
        click("#createNotification_tp");
        click("#title_field");
        type("Test Notification");
        click("#message_field");
        type("This is a test notification");
        click("#comboBox");
        click("test group");
        click("#send_button");
        Label statusLabel = find("#statusLabel");
        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS,()->statusLabel.getText().equals("Message sent successfully."));
        String status = statusLabel.getText();
        assertEquals("Message sent successfully.",status);
    }




}