package Data;

import Model.Group;
import Model.Notification;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.Calendar;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;

public class DatabaseCommunicatorTest {
    private DatabaseCommunicator dc;

    @Before
    public void setUp(){
        dc = new DatabaseCommunicator();
        dc.clearTable(DatabaseCommunicator.TABLE_GROUPS);
        dc.clearTable(DatabaseCommunicator.TABLE_NOTIFICATIONS);
    }

    @After
    public void finish() {
        dc.clearTable(DatabaseCommunicator.TABLE_GROUPS);
        dc.clearTable(DatabaseCommunicator.TABLE_NOTIFICATIONS);
        dc.closeConnection();
    }

    @Test
    public void testPreConditions() {
        assertNotNull(dc);
    }

    @Test
    public void addNewGroupTest() {
        Group group = new Group("test_group","Test Group","http://test_group_link.com");
        dc.insertGroup(group);
        List<Group> groups = dc.getGroups();
        Assert.assertTrue(EqualsBuilder.reflectionEquals(group,groups.get(0)));
    }

    @Test
    public void addNotificationTest() {
        Notification notification = new Notification("test_notification","This is a test notification", Calendar.getInstance().getTimeInMillis(),"test_group",null);
        dc.addNotification(notification);
        List<Notification> notifications = dc.getNotificationsList("test_group");
        Assert.assertTrue(EqualsBuilder.reflectionEquals(notification,notifications.get(0)));
    }
}
