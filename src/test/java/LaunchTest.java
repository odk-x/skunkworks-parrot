import Data.DatabaseCommunicator;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.loadui.testfx.GuiTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.loadui.testfx.controls.Commons.hasText;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LaunchTest extends GuiTest {

    private Parent mainNode;
    private DatabaseCommunicator dc;
    @Before
    public void setUp() {
        dc = new DatabaseCommunicator();
        dc.clearTable(DatabaseCommunicator.TABLE_GROUPS);
        dc.clearTable(DatabaseCommunicator.TABLE_NOTIFICATIONS);
    }

    @After
    public void tearDown() {
        dc.clearTable(DatabaseCommunicator.TABLE_GROUPS);
        dc.clearTable(DatabaseCommunicator.TABLE_NOTIFICATIONS);
        dc.closeConnection();
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
    public void stage1_loginTest () {
        click("#usernameField");
        type(System.getProperty("username","admin"));
        click("#passwordField");
        type(System.getProperty("password","admin"));
        click("#loginButton");
        Label statusLabel = find("#statusLabel");
        waitUntil(statusLabel,hasText("Login Successful"),10);

        //assertEquals(,statusLabel.getText());
    }

    @Test
    public void stage2_createGroupTest() throws TimeoutException {
        click("#createGroup_tp");
        click("#name_field");
        type("test group");
        click("#createButton");
        Label status = find("#statusLabel");
        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS,()-> !status.getText().equals("Please Wait..."));
        String grouplink = status.getText();
        assertEquals("Group Link: ",grouplink.substring(0,12));
    }

    @Test
    public void stage3_createNotificationTest() throws TimeoutException {
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