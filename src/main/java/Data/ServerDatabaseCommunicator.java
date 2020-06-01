package Data;

import Model.Group;
import Model.Notification;
import Model.Response;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.opendatakit.aggregate.odktables.rest.entity.Column;
import org.opendatakit.aggregate.odktables.rest.entity.Row;
import org.opendatakit.sync.client.SyncClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;


public class ServerDatabaseCommunicator {

    private static SyncClient syncClient;

    private static final String APP_ID = "default";

    private static final Data data = new Data();

    private static final String SERVER_URL = data.getSYNC_CLIENT_URL() + "/odktables";

    private static final String NOTIFICATIONS_TABLE_ID = "NotificationsTable";
    private static final String RESPONSES_TABLE_ID = "ResponsesTable";
    private static final String GROUPS_TABLE_ID = "GroupsTable";
    private static final String USERS_TABLE_ID = "UsersTable";

    private static final List<String> GROUPS_TABLE_COLUMNS_LIST = Arrays.asList("GroupId","GroupName",
            "NotificationsList","UsersList","PendingRequestsList");

    private static final List<String> NOTIFICATIONS_TABLE_COLUMNS_LIST = Arrays.asList("NotificationId",
            "NotificationTitle","NotificationMessage","GroupId","NotificationType","ResponseList");

    private static final List<String> RESPONSES_TABLE_COLUMNS_LIST = Arrays.asList("ResponseId",
            "ResponseText","NotificationId","UserName");

    private static final List<String> USERS_TABLE_COLUMNS_LIST = Arrays.asList("UserId",
            "UserName","GroupList","DeviceRegistrationToken");

    // JSON constants
    private static final String RESPONSES_LIST_KEY = "Responses";
    private static final String USERS_LIST_KEY = "Users";
    private static final String NOTIFICATIONS_LIST_KEY = "Notifications";
    private static final String PENDING_REQUESTS_LIST_KEY = "PendingRequests";

    private static final String RELATIVE_PATH_FOR_ATTACHMENT = "/attachment";



    /**
     * Initializes SyncClient with given username and password
     * This method should be called only after verifying the credentials
     *
     * @param username
     *              the user name to use for initializing SyncClient
     * @param password
     *              the password to use for initializing SyncClient
     *
     * @throws IOException
     *              Input errors while calling SyncClient methods
     * @throws JSONException
     *              JSON error while parsing the data
     */

    public static void init(String username , String password) throws IOException, JSONException {
        syncClient = new SyncClient();
        syncClient.init(getServerHost() , username ,password);
        checkTables();
    }

    /**
     * Initializes SyncClient with default parameters
     *
     */
    public static void initAnonymous(){
        syncClient = new SyncClient();
        syncClient.initAnonymous(getServerHost());
        //TODO:Check if anonymous user can create tables or not
    }

    /**
     * Uploads the notification to the server database
     * Adds notification Id to the group's notifications list
     *
     * @param notification
     *              the Notification object to upload
     *
     * @throws IOException
     *              Due to input errors while calling SyncClient methods
     * @throws JSONException
     *              Due to JSON errors while parsing the data
     */
    public static void uploadNotification(Notification notification) throws JSONException, IOException {
        Row row = new Row();

        String rowId = "notification:" + UUID.randomUUID().toString();
        row.setRowId(rowId);

        JSONObject responseList = new JSONObject();
        JSONArray responses = new JSONArray();

        responseList.put(RESPONSES_LIST_KEY,responses);

        Map<String,String> map = new HashMap<>();

        List<String> columnValues = Arrays.asList(rowId,notification.getTitle(),notification.getMessage(),
                notification.getGroup_id(),notification.getType(),responseList.toString());

        for(int i=0;i<columnValues.size();i++){
            map.put(NOTIFICATIONS_TABLE_COLUMNS_LIST.get(i),columnValues.get(i));
        }

        row.setValues(Row.convertFromMap(map));

        ArrayList<Row> rowArrayList = new ArrayList<>();
        rowArrayList.add(row);

        String schemaETag = syncClient.getSchemaETagForTable(SERVER_URL,APP_ID,NOTIFICATIONS_TABLE_ID);

        syncClient.createRowsUsingBulkUpload(SERVER_URL,APP_ID,NOTIFICATIONS_TABLE_ID,schemaETag,rowArrayList,1);

        //uploads the attachment
        if(!notification.getAttachmentPath().equals("") && notification.getAttachmentPath() != null){
            syncClient.putFileForRow(SERVER_URL,APP_ID,NOTIFICATIONS_TABLE_ID,schemaETag,rowId,notification.getAttachmentPath(),RELATIVE_PATH_FOR_ATTACHMENT);
        }
        addNotificationToGroup(notification.getGroup_id() ,rowId);
    }

    /**
     * Uploads the notification to the server database
     * Adds notification Id to the group's notifications list
     *
     * @param group
     *            The Group object to upload
     *
     * @throws IOException
     *            Due to input errors while calling SyncClient methods
     * @throws JSONException
     *            Due to JSON errors while parsing the data
     */
    public static void uploadGroup(Group group) throws JSONException, IOException {
        Row row = new Row();

        String rowId = "group:" + UUID.randomUUID().toString();
        row.setRowId(rowId);

        JSONObject usersList = new JSONObject();
        JSONArray users = new JSONArray();
        usersList.put(USERS_LIST_KEY,users);

        JSONObject notificationsList = new JSONObject();
        JSONArray notifications = new JSONArray();
        notificationsList.put(NOTIFICATIONS_LIST_KEY,notifications);

        JSONObject pendingRequestsList = new JSONObject();
        JSONArray pendingRequests = new JSONArray();
        pendingRequestsList.put(PENDING_REQUESTS_LIST_KEY,pendingRequests);

        Map<String,String>map = new HashMap<>();

        List<String>columnValues = Arrays.asList(rowId,group.getName(),notificationsList.toString(),
                usersList.toString(), pendingRequestsList.toString());

        for(int i=0;i<columnValues.size();i++){
            map.put(GROUPS_TABLE_COLUMNS_LIST.get(i),columnValues.get(i));
        }

        row.setValues(Row.convertFromMap(map));
        ArrayList<Row>rowArrayList = new ArrayList<>();
        rowArrayList.add(row);

        String schemaETag = syncClient.getSchemaETagForTable(SERVER_URL,APP_ID,GROUPS_TABLE_ID);

        syncClient.createRowsUsingBulkUpload(SERVER_URL,APP_ID,GROUPS_TABLE_ID,schemaETag,rowArrayList,1);

    }

    /**
     * Adds created notifications ID to corresponding Notifications List of group
     *
     */
    private static void addNotificationToGroup(String groupId , String notificationId) throws IOException, JSONException {
        String schemaETag = syncClient.getSchemaETagForTable(SERVER_URL,APP_ID,GROUPS_TABLE_ID);

        JSONObject rowObject = syncClient.getRow(SERVER_URL,APP_ID,GROUPS_TABLE_ID,schemaETag,groupId);
        JSONArray data = rowObject.getJSONArray("orderedColumns");

        Map<String,String> map = new HashMap<>();
        for(int i=0;i<data.length();i++){
            map.put(data.getJSONObject(i).get("column").toString(),data.getJSONObject(i).get("value").toString());
        }

        JSONObject temp = new JSONObject(map.get("NotificationsList"));
        JSONArray tempArray = temp.getJSONArray(NOTIFICATIONS_LIST_KEY);
        tempArray.add(notificationId);
        temp.put(NOTIFICATIONS_LIST_KEY,tempArray);

        map.put("NotificationsList",temp.toString());

        Row row = getRowFromJSON(rowObject);
        row.setValues(Row.convertFromMap(map));
        ArrayList<Row> rowArrayList = new ArrayList<>();
        rowArrayList.add(row);

        String dataETag = syncClient.getTableDataETag(SERVER_URL,APP_ID,GROUPS_TABLE_ID);
        schemaETag = syncClient.getSchemaETagForTable(SERVER_URL,APP_ID,GROUPS_TABLE_ID);

        syncClient.updateRowsUsingBulkUpload(SERVER_URL,APP_ID,GROUPS_TABLE_ID,schemaETag,dataETag,rowArrayList,1);
    }

    /**
     * Checks if server database has all the required tables or not
     * If some tables are not present on server then creates that table
     *
     */
    private static void checkTables () throws JSONException, IOException {

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
    }

    /**
     * Returns a List of groups present in a server Database
     *
     * @throws JSONException
     *              Due to JSON errors while parsing the data
     */
    public static ArrayList<Group>getGroups() throws JSONException {
        ArrayList<Group>groupArrayList = new ArrayList<>();
        String schemaETag = syncClient.getSchemaETagForTable(SERVER_URL,APP_ID,GROUPS_TABLE_ID);
        JSONObject jsonObject = syncClient.getRows(SERVER_URL,APP_ID,GROUPS_TABLE_ID,schemaETag,null,null);

        JSONArray groupObjectsArray = jsonObject.getJSONArray("rows");
        for(int i=0;i<groupObjectsArray.size();i++){
            groupArrayList.add(getGroupFromJson(groupObjectsArray.getJSONObject(i).getJSONArray("orderedColumns")));
        }
        return groupArrayList;
    }

    /**
     * Returns a notification object for a given notification Id
     *
     * @param notificationId
     *                  Id for a Notification
     *
     * @throws IOException
     *             Due to input errors while calling SyncClient methods
     * @throws JSONException
     *             Due to JSON errors while parsing the data
     *
     */
    public static Notification getNotification(String notificationId) throws IOException, JSONException {
        String schemaETag = syncClient.getSchemaETagForTable(SERVER_URL,APP_ID,NOTIFICATIONS_TABLE_ID);

        JSONObject notificationObject = syncClient.getRow(SERVER_URL,APP_ID,NOTIFICATIONS_TABLE_ID,schemaETag,notificationId);
        JSONArray notificationArray = notificationObject.getJSONArray("orderedColumns");

        Notification notification =  getNotificationFromJSON(notificationArray);
        LocalDateTime localDateTime = LocalDateTime.parse(notificationObject.get("savepointTimestamp").toString());
        notification.setDate(localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

        return notification;
    }

    /**
     * Returns a Response object for a given response Id
     *
     * @param responseId
     *              Id for a Response
     *
     *  @throws IOException
     *             Due to input errors while calling SyncClient methods
     *  @throws JSONException
     *             Due to JSON errors while parsing the data
     *
     */
    public static Response getResponse(String responseId) throws IOException, JSONException {
        String schemaETag = syncClient.getSchemaETagForTable(SERVER_URL,APP_ID,RESPONSES_TABLE_ID);

        JSONObject responseObject = syncClient.getRow(SERVER_URL,APP_ID,RESPONSES_TABLE_ID,schemaETag,responseId);

        Response response = getResponseFromJSON(responseObject.getJSONArray("orderedColumns"));

        LocalDateTime localDateTime = LocalDateTime.parse(responseObject.get("savepointTimestamp").toString());
        response.setTime(localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

        return response;
    }

    /**
     * Creates a table on server database with given tableId and column list
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

    /**
     * Converts JSONObject with rowData to row object
     * Use for updating the row
     * Does not copy columns data from JSONObject to row
     *
     */
    private static Row getRowFromJSON(JSONObject jsonObject) throws JSONException {
        Row row = new Row();
        if(jsonObject.get("formId") != null)row.setFormId(jsonObject.get("formId").toString());
        if(jsonObject.get("locale") != null)row.setLocale(jsonObject.get("locale").toString());
        if(jsonObject.get("rowETag") != null)row.setRowETag(jsonObject.get("rowETag").toString());
        if(jsonObject.get("dataETagAtModification") != null)row.setDataETagAtModification(jsonObject.get("dataETagAtModification").toString());
        if(jsonObject.get("savepointCreator") != null)row.setSavepointCreator(jsonObject.get("savepointCreator").toString());
        if(jsonObject.get("createUser") != null)row.setCreateUser(jsonObject.get("createUser").toString());
        if(jsonObject.get("id") != null)row.setRowId(jsonObject.get("id").toString());
        if(jsonObject.get("savepointTimestamp") != null)row.setSavepointTimestamp(jsonObject.get("savepointTimestamp").toString());
        return row;
    }

    /**
     * Converts a JSONArray containing group data to Group object
     *
     */
    private static Group getGroupFromJson(JSONArray jsonArray) throws JSONException {
        Group group = new Group();
        for(int i=0;i<jsonArray.size();i++){
            if(jsonArray.getJSONObject(i).get("column").toString().equals("GroupId")){
                group.setId(jsonArray.getJSONObject(i).get("value").toString());
            }
            if(jsonArray.getJSONObject(i).get("column").toString().equals("GroupName")){
                group.setName(jsonArray.getJSONObject(i).get("value").toString());
            }
            if(jsonArray.getJSONObject(i).get("column").toString().equals("NotificationsList")){
                JSONObject jsonObject = new JSONObject(jsonArray.getJSONObject(i).get("value").toString());
                group.setNotificationsList(getListFromJsonArray(jsonObject.getJSONArray(NOTIFICATIONS_LIST_KEY)));
            }
            if(jsonArray.getJSONObject(i).get("column").toString().equals("UsersList")){
                JSONObject jsonObject = new JSONObject(jsonArray.getJSONObject(i).get("value").toString());
                group.setUsersList(getListFromJsonArray(jsonObject.getJSONArray(USERS_LIST_KEY)));
            }
            if(jsonArray.getJSONObject(i).get("column").toString().equals("PendingRequestsList")){
                JSONObject jsonObject = new JSONObject(jsonArray.getJSONObject(i).get("value").toString());
                group.setPendingRequestsList(getListFromJsonArray(jsonObject.getJSONArray(PENDING_REQUESTS_LIST_KEY)));
            }
        }
        return group;
    }

    /**
     * Converts a JSONArray containing Notification data to Notification object
     *
     */
    private static Notification getNotificationFromJSON(JSONArray jsonArray) throws JSONException {
        Notification notification = new Notification();

        for(int i=0;i<jsonArray.size();i++){
            if(jsonArray.getJSONObject(i).get("column").toString().equals("NotificationId")){
               notification.setId(jsonArray.getJSONObject(i).get("value").toString());
            }
            if(jsonArray.getJSONObject(i).get("column").toString().equals("NotificationTitle")){
                notification.setTitle(jsonArray.getJSONObject(i).get("value").toString());
            }
            if(jsonArray.getJSONObject(i).get("column").toString().equals("NotificationMessage")){
                notification.setMessage(jsonArray.getJSONObject(i).get("value").toString());
            }
            if(jsonArray.getJSONObject(i).get("column").toString().equals("GroupId")){
                notification.setGroup_id(jsonArray.getJSONObject(i).get("value").toString());
            }
            if(jsonArray.getJSONObject(i).get("column").toString().equals("NotificationType")){
                notification.setType(jsonArray.getJSONObject(i).get("value").toString());
            }
            if(jsonArray.getJSONObject(i).get("column").toString().equals("ResponseList")) {
                JSONObject jsonObject = new JSONObject(jsonArray.getJSONObject(i).get("value").toString());
                notification.setResponseList(getListFromJsonArray(jsonObject.getJSONArray(RESPONSES_LIST_KEY)));
            }
        }
        return notification;
    }

    /**
     * Converts a JSONArray containing Response data to Response object
     *
     */
    private static Response getResponseFromJSON(JSONArray jsonArray) throws JSONException {
        Response response = new Response();
        for(int i=0;i<jsonArray.size();i++){
            if(jsonArray.getJSONObject(i).get("column").toString().equals("ResponseText")){
                response.setResponse(jsonArray.getJSONObject(i).get("value").toString());
            }
            if(jsonArray.getJSONObject(i).get("column").toString().equals("NotificationId")){
                response.setNotificationId(jsonArray.getJSONObject(i).get("value").toString());
            }
            if(jsonArray.getJSONObject(i).get("column").toString().equals("UserName")){
                response.setSenderName(jsonArray.getJSONObject(i).get("value").toString());
            }
        }
        return response;
    }

    /**
     * Returns server host
     *
     */
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

    /**
     * Converts a JSONArray to ArrayList
     *
     */
    private static ArrayList<String> getListFromJsonArray(JSONArray jsonArray){
        ArrayList<String>arrayList = new ArrayList<>();
        for(int i=0;i<jsonArray.size();i++){
            arrayList.add(jsonArray.get(i).toString());
        }
        return arrayList;
    }
}
