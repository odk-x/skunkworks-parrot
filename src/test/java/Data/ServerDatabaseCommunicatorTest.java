package Data;

import Model.Group;
import Model.Notification;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.wink.json4j.JSONException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static junit.framework.TestCase.assertNotNull;

public class ServerDatabaseCommunicatorTest {

    private ServerDatabaseCommunicator serverDatabaseCommunicator;


    private static final String USERNAME = "test";
    private static final String PASSWORD = "test";

    private static final String TEST_GROUP_NAME = "TestGroup";
    private static final String TEST_GROUP_ID = UUID.randomUUID().toString();
    private static final String TEST_GROUP_LINK = "http://test_group_link.com";

    private static final String TEST_NOTIFICATION_ID = UUID.randomUUID().toString();
    private static final String TEST_NOTIFICATION_TITLE = "Test Notification";
    private static final String TEST_NOTIFICATION_MESSAGE = "Test Message";
    private static final Long TEST_NOTIFICATION_DATE = new Date().getTime();

    private static final String SIMPLE_NOTIFICATION_TYPE = "Simple";
    private static final String INTERACTIVE_NOTIFICATION_TYPE = "Interactive";

    private static final List<String> DEFAULT_GROUPS_LIST = Arrays.asList("ROLE_ADMINISTER_TABLES", "ROLE_DATA_COLLECTOR",
            "ROLE_DATA_OWNER", "ROLE_DATA_VIEWER", "ROLE_SITE_ACCESS_ADMIN", "ROLE_SUPER_USER_TABLES",
            "ROLE_SYNCHRONIZE_TABLES", "ROLE_USER");


    @Before
    public void setUp() throws IOException, JSONException {

        serverDatabaseCommunicator = ServerDatabaseCommunicator.getInstance();

        serverDatabaseCommunicator.init(USERNAME, PASSWORD);

    }

    @Test
    public void testPreConditions() {
        assertNotNull(serverDatabaseCommunicator);
    }

    @Test
    public void initializationTest() throws JSONException {

        ArrayList<Group> groupArrayList = serverDatabaseCommunicator.getGroups();

        ArrayList<String> groupNamesList = new ArrayList<>();
        ArrayList<String> groupIdsList = new ArrayList<>();

        for(Group group : groupArrayList) {
            groupNamesList.add(group.getName());
            groupIdsList.add(group.getId());
        }

        for(String s : DEFAULT_GROUPS_LIST) {
            Assert.assertThat(groupNamesList, CoreMatchers.hasItems(s));
            Assert.assertThat(groupIdsList, CoreMatchers.hasItems(s));
        }
    }

    @Test
    public void addNewGroupTest() throws IOException, JSONException {

        Group testGroup = new Group();

        testGroup.setId(TEST_GROUP_ID);
        testGroup.setName(TEST_GROUP_NAME);
        testGroup.setGroupLink(TEST_GROUP_LINK);

        serverDatabaseCommunicator.uploadGroup(testGroup);

        ArrayList<Group> groupArrayList = serverDatabaseCommunicator.getGroups();

        Group actualGroup = new Group();

        for(Group group : groupArrayList) {
            if(group.getId().equals(TEST_GROUP_ID)) {
                actualGroup = group;
            }
        }

        Assert.assertTrue(EqualsBuilder.reflectionEquals(testGroup , actualGroup , "usersList","notificationsList"));

    }

    @Test
    public void addNotificationTest() throws IOException, JSONException {

        Notification testNotification = new Notification(TEST_NOTIFICATION_ID, TEST_NOTIFICATION_TITLE,
                TEST_NOTIFICATION_MESSAGE, TEST_NOTIFICATION_DATE, DEFAULT_GROUPS_LIST.get(0),
                SIMPLE_NOTIFICATION_TYPE, null);

        String [] responseList = {};
        testNotification.setResponseList(new ArrayList<>(Arrays.asList(responseList)));

        serverDatabaseCommunicator.uploadNotification(testNotification);

        Notification actualNotification = serverDatabaseCommunicator.getNotification(TEST_NOTIFICATION_ID);

        Assert.assertTrue(EqualsBuilder.reflectionEquals(testNotification , actualNotification , "responseList"));
    }

    @Test
    public void addInteractiveNotificationTest() throws IOException, JSONException {

        String interactiveNotificationId = UUID.randomUUID().toString();
        Notification testNotification = new Notification(interactiveNotificationId, TEST_NOTIFICATION_TITLE,
                TEST_NOTIFICATION_MESSAGE, TEST_NOTIFICATION_DATE, DEFAULT_GROUPS_LIST.get(0),
                INTERACTIVE_NOTIFICATION_TYPE, null);

        String [] responseList = {};
        testNotification.setResponseList(new ArrayList<>(Arrays.asList(responseList)));

        serverDatabaseCommunicator.uploadNotification(testNotification);

        Notification actualNotification = serverDatabaseCommunicator.getNotification(interactiveNotificationId);

        Assert.assertTrue(EqualsBuilder.reflectionEquals(testNotification , actualNotification , "responseList"));
    }

}
