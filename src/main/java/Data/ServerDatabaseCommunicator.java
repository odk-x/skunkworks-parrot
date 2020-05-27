package Data;

import Model.Group;
import Model.Notification;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.opendatakit.aggregate.odktables.rest.entity.Column;
import org.opendatakit.sync.client.SyncClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ServerDatabaseCommunicator {

    private static SyncClient syncClient;

    private static final String APP_ID = "default";

    private static Data data = new Data();

    private static final String SERVER_URL = data.getSYNC_CLIENT_URL() + "/odktables";

    private static final String NOTIFICATIONS_TABLE_ID = "NotificationsTable";
    private static final String RESPONSES_TABLE_ID = "ResponsesTable";
    private static final String GROUPS_TABLE_ID = "GroupsTable";
    private static final String USERS_TABLE_ID = "UsersTable";

    private static final List<String> GROUPS_TABLE_COLUMNS_LIST = Arrays.asList("GroupId","GroupName",
            "NotificationsList","UsersList","PendingRequestsList");

    private static final List<String> NOTIFICATIONS_TABLE_COLUMNS_LIST = Arrays.asList("NotificationId",
            "NotificationTitle","NotificationText","GroupName","NotificationType","ResponseList");

    private static final List<String> RESPONSES_TABLE_COLUMNS_LIST = Arrays.asList("ResponseId",
            "ResponseText","NotificationId","UserId");

    private static final List<String> USERS_TABLE_COLUMNS_LIST = Arrays.asList("UserId",
            "Username","GroupList","DeviceRegistrationToken");

    /**
     * Initialize SyncClient with given username and password
     * This method should be called only after verifying the credentials
     *
     * @param username
     *              the user name to use for initializing SyncClient
     * @param password
     *              the password to use for initializing SyncClient
     */
    public static void init(String username , String password){
        syncClient = new SyncClient();
        syncClient.init(getServerHost() , username ,password);
        checkTables();
    }

    /**
     * Initialize SyncClient with default parameters
     *
     */
    public static void initAnonymous(){
        syncClient = new SyncClient();
        syncClient.initAnonymous(getServerHost());

        //TODO:Check if anonymous user can create tables or not
    }
    public static void uploadNotification(Notification notification){

    }
    public static void uploadGroup(Group group){

    }
    public static void uploadAttachment(String filepath, String tableId , String rowId){

    }


    /**
     * Check if server database has all the required tables or not
     * If some table is not present on server creates that table
     *
     */
    private static void checkTables (){
        try {
            JSONObject tablesObject = syncClient.getTables(SERVER_URL,APP_ID);
            JSONArray tablesArray = tablesObject.getJSONArray("tables");
            ArrayList<String> tablesList = new ArrayList<>();

            for(int i = 0; i<tablesArray.length(); i++){
                tablesList.add((String) tablesArray.getJSONObject(i).get("tableId"));
            }

            if(!tablesList.contains(USERS_TABLE_ID))createTable(USERS_TABLE_ID,USERS_TABLE_COLUMNS_LIST);

            if(!tablesList.contains(NOTIFICATIONS_TABLE_ID))createTable(NOTIFICATIONS_TABLE_ID,NOTIFICATIONS_TABLE_COLUMNS_LIST);

            if(!tablesList.contains(RESPONSES_TABLE_ID))createTable(RESPONSES_TABLE_ID,RESPONSES_TABLE_COLUMNS_LIST);

            if(!tablesList.contains(GROUPS_TABLE_ID))createTable(GROUPS_TABLE_ID,GROUPS_TABLE_COLUMNS_LIST);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create table on server database with given tableId and column list
     * @param tableId
     *              Id of table to create
     * @param columnsList
     *              List of column names for a table
     *
     */
    private static void createTable(String tableId , List<String>columnsList) throws IOException, JSONException {
        ArrayList<Column>columns = new ArrayList<>();

        for(int i=0;i<columnsList.size();i++){
            Column column = new Column(columnsList.get(i), columnsList.get(i),
                    "String",null);
            columns.add(column);
        }
        syncClient.createTable(SERVER_URL,APP_ID,tableId,null,columns);
    }

    private static String getServerHost(){
        String url = data.getSYNC_CLIENT_URL();
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        return uri.getHost();
    }
}
