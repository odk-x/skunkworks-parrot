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
import java.util.*;


public class ServerDatabaseCommunicator {

    private SyncClient syncClient;

    private static final String APP_ID = "default";

    private static final Data data = new Data();

    private static final String SERVER_URL = data.getSYNC_CLIENT_URL() + "/odktables";

    private static final String NOTIFICATIONS_TABLE_ID = "NotificationsTable";
    private static final String RESPONSES_TABLE_ID = "ResponsesTable";
    private static final String GROUPS_TABLE_ID = "GroupsTable";
    private static final String USERS_TABLE_ID = "UsersTable";

    private static final List<String> GROUPS_TABLE_COLUMNS_LIST = Arrays.asList("GroupId","GroupName",
            "GroupLink","NotificationsList");

    private static final List<String> NOTIFICATIONS_TABLE_COLUMNS_LIST = Arrays.asList("NotificationId",
            "NotificationTitle","NotificationMessage","GroupId","NotificationType","NotificationTime","ResponseList");

    private static final List<String> RESPONSES_TABLE_COLUMNS_LIST = Arrays.asList("ResponseId",
            "ResponseText","NotificationId","UserName","ResponseTime");

    private static final List<String> USERS_TABLE_COLUMNS_LIST = Arrays.asList("UserId",
            "UserName","GroupList");

    private static final String RELATIVE_PATH_FOR_ATTACHMENT = "/attachment";

    private static ServerDatabaseCommunicator serverDatabaseCommunicator;

    private ServerDatabaseCommunicator(){

    }

    public static ServerDatabaseCommunicator getInstance(){

        if(serverDatabaseCommunicator == null){
            serverDatabaseCommunicator = new ServerDatabaseCommunicator();
        }
        return serverDatabaseCommunicator;
    }

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

    public void init(String username , String password) throws IOException, JSONException {
        syncClient = new SyncClient();
        syncClient.init(getServerHost() , username ,password);
        checkTables();
        uploadDefaultGroups();
    }

    /**
     * Uploads the notification to the server database
     * Adds notification Id to the group's notifications list
     * Returns Id for uploaded notification
     *
     * @param notification
     *              the Notification object to upload
     *
     * @throws IOException
     *              Due to input errors while calling SyncClient methods
     * @throws JSONException
     *              Due to JSON errors while parsing the data
     */
    public String uploadNotification(Notification notification) throws JSONException, IOException {
        Row row = new Row();

        String rowId;

        if(notification.getId() != null && !notification.getId().equals("")){
            rowId = notification.getId();
        }
        else {
            rowId = UUID.randomUUID().toString();
        }

        row.setRowId(rowId);
        String responseList = "";

        Map<String,String> map = new HashMap<>();

        List<String> columnValues = Arrays.asList(rowId,notification.getTitle(),notification.getMessage(),
                notification.getGroup_id(),notification.getType(),String.valueOf(notification.getDate()),responseList);

        for(int i=0;i<columnValues.size();i++){
            map.put(NOTIFICATIONS_TABLE_COLUMNS_LIST.get(i),columnValues.get(i));
        }

        row.setValues(Row.convertFromMap(map));

        ArrayList<Row> rowArrayList = new ArrayList<>();
        rowArrayList.add(row);

        String schemaETag = syncClient.getSchemaETagForTable(SERVER_URL,APP_ID,NOTIFICATIONS_TABLE_ID);

        syncClient.createRowsUsingBulkUpload(SERVER_URL,APP_ID,NOTIFICATIONS_TABLE_ID,schemaETag,rowArrayList,1);

        //uploads the attachment
        if(notification.getAttachmentPath() != null && !notification.getAttachmentPath().equals("")){
            syncClient.putFileForRow(SERVER_URL,APP_ID,NOTIFICATIONS_TABLE_ID,schemaETag,rowId,notification.getAttachmentPath(),RELATIVE_PATH_FOR_ATTACHMENT);
        }
        addNotificationToGroup(notification.getGroup_id() ,rowId);

        return rowId;
    }

    /**
     * Uploads the notification to the server database
     * Adds notification Id to the group's notifications list
     * Returns Id for uploaded group
     * If group with the given group name already exists, then returns a null string
     *
     * @param group
     *            The Group object to upload
     *
     * @return String
     *            Group Id of created group
     *
     * @throws IOException
     *            Due to input errors while calling SyncClient methods
     * @throws JSONException
     *            Due to JSON errors while parsing the data
     */
    public String uploadGroup(Group group) throws JSONException, IOException {

        if(isGroupPresent(group)){
            System.out.println("Group with the Group Name: " + group.getName() + " is already present in the database");
            return null;
        }

        Row row = new Row();
        String rowId;
        if(group.getId() == null || group.getId().equals("")) {
            rowId = UUID.randomUUID().toString();
            row.setRowId(rowId);
        }
        else {
            rowId = group.getId();
            row.setRowId(group.getId());
        }

        String notificationList = "";
        Map<String,String>map = new HashMap<>();

        List<String>columnValues = Arrays.asList(rowId,group.getName(),group.getGroupLink(),notificationList);

        for(int i=0;i<columnValues.size();i++){
            map.put(GROUPS_TABLE_COLUMNS_LIST.get(i),columnValues.get(i));
        }

        row.setValues(Row.convertFromMap(map));
        ArrayList<Row>rowArrayList = new ArrayList<>();
        rowArrayList.add(row);

        String schemaETag = syncClient.getSchemaETagForTable(SERVER_URL,APP_ID,GROUPS_TABLE_ID);

        syncClient.createRowsUsingBulkUpload(SERVER_URL,APP_ID,GROUPS_TABLE_ID,schemaETag,rowArrayList,1);
        return rowId;
    }

    /**
     * Checks if given group is already present in database or not
     *
     */
    private boolean isGroupPresent(Group group) throws JSONException {
        ArrayList<Group>groupArrayList = getGroups();
        boolean present = false;
        for(int i=0;i<groupArrayList.size();i++){
            if (groupArrayList.get(i).getName().equals(group.getName())) {
                present = true;
                break;
            }
        }
        return present;
    }

    /**
     * Adds created notifications ID to corresponding Notifications List of group
     *
     */
    private void addNotificationToGroup(String groupId , String notificationId) throws IOException, JSONException {
        String schemaETag = syncClient.getSchemaETagForTable(SERVER_URL,APP_ID,GROUPS_TABLE_ID);

        JSONObject rowObject = syncClient.getRow(SERVER_URL,APP_ID,GROUPS_TABLE_ID,schemaETag,groupId);
        JSONArray data = rowObject.getJSONArray("orderedColumns");

        Map<String,String> map = new HashMap<>();
        for(int i=0;i<data.length();i++){
            map.put(data.getJSONObject(i).get("column").toString(),data.getJSONObject(i).get("value").toString());
        }

        String temp = map.get("NotificationsList");
        if(!temp.equals("")) temp += ",";
        temp += notificationId;
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
    private void checkTables () throws JSONException, IOException {

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
     * Uploads the user default groups to the database if not present
     *
     */
    private void uploadDefaultGroups() throws IOException, JSONException {
        ArrayList<Map<String, Object>> users = syncClient.getUsers(SERVER_URL, APP_ID);
        for (Map<String, Object> user : users) {
            ArrayList<String> userGroupList = (ArrayList<String>)user.get("roles");
            for(String groupName : userGroupList){
                if((groupName.startsWith("GROUP_") || groupName.startsWith("ROLE_"))){
                    Group group = new Group();
                    group.setName(groupName);
                    group.setId(groupName);
                    group.setGroupLink("");
                    uploadGroup(group);
                }
            }
        }
    }

    /**
     * Returns a List of groups present in a server Database
     *
     * @throws JSONException
     *              Due to JSON errors while parsing the data
     */
    public ArrayList<Group>getGroups() throws JSONException {
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
     * Returns a Array list of notification objects for given group
     *
     * @param group
     *            group object whose notification list is required
     *
     * @throws IOException
     *             Due to input errors while calling SyncClient methods
     * @throws JSONException
     *             Due to JSON errors while parsing the data
     *
     */
    public ArrayList<Notification> getNotificationsList(Group group) throws IOException, JSONException {
        ArrayList<Notification> notificationArrayList = new ArrayList<>();
        ArrayList<String>notificationsIdList = group.getNotificationsList();
        for (String s : notificationsIdList) {
            if(s != null && !s.equals("")){
                notificationArrayList.add(getNotification(s));
            }
        }
        return notificationArrayList;
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
    public Notification getNotification(String notificationId) throws IOException, JSONException {
        String schemaETag = syncClient.getSchemaETagForTable(SERVER_URL,APP_ID,NOTIFICATIONS_TABLE_ID);

        JSONObject notificationObject = syncClient.getRow(SERVER_URL,APP_ID,NOTIFICATIONS_TABLE_ID,schemaETag,notificationId);
        JSONArray notificationArray = notificationObject.getJSONArray("orderedColumns");

        Notification notification =  getNotificationFromJSON(notificationArray);
        return notification;
    }

    /**
     * Returns a array list of responses for given notification
     *
     * @param notification
     *              Notification object whose response list is required
     *
     *  @throws IOException
     *             Due to input errors while calling SyncClient methods
     *  @throws JSONException
     *             Due to JSON errors while parsing the data
     *
     */
    public ArrayList<Response> getResponsesList (Notification notification) throws IOException, JSONException {
        ArrayList<Response> responseArrayList = new ArrayList<>();
        ArrayList<Response> completeResponsesList = getResponses();

        for (Response response : completeResponsesList) {
            if (response.getNotificationId().equals(notification.getId())) {
                responseArrayList.add(response);
                System.out.println(response.getNotificationId());
            }
        }
        System.out.println(responseArrayList.size());
        return responseArrayList;
    }

    /**
     * Returns a List of all the responses present in a database
     *
     *
     *  @throws JSONException
     *             Due to JSON errors while parsing the data
     *
     */
    public ArrayList<Response> getResponses() throws JSONException {

        ArrayList<Response> responseArrayList = new ArrayList<>();
        String schemaETag = syncClient.getSchemaETagForTable(SERVER_URL,APP_ID,RESPONSES_TABLE_ID);

        JSONObject responseObject = syncClient.getRows(SERVER_URL,APP_ID,RESPONSES_TABLE_ID,schemaETag,null,null);

        JSONArray responsesJsonArray = responseObject.getJSONArray("rows");

        for(int i=0;i<responsesJsonArray.size();i++) {
            responseArrayList.add(getResponseFromJSON(responsesJsonArray.getJSONObject(i).getJSONArray("orderedColumns")));
        }
        return responseArrayList;
    }

    /**
     * Creates a table on server database with given tableId and column list
     *
     */
    private void createTable(String tableId , List<String>columnsList) throws IOException, JSONException {
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
    private Row getRowFromJSON(JSONObject jsonObject) throws JSONException {
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
    private Group getGroupFromJson(JSONArray jsonArray) throws JSONException {
        Group group = new Group();
        for(int i=0;i<jsonArray.size();i++){
            if(jsonArray.getJSONObject(i).get("column").toString().equals("GroupId")){
                group.setId(jsonArray.getJSONObject(i).get("value").toString());
            }
            if(jsonArray.getJSONObject(i).get("column").toString().equals("GroupName")){
                group.setName(jsonArray.getJSONObject(i).get("value").toString());
            }
            if(jsonArray.getJSONObject(i).get("column").toString().equals("GroupLink")){
                if(jsonArray.getJSONObject(i).get("value") != null) {
                    group.setGroupLink(jsonArray.getJSONObject(i).get("value").toString());
                }
            }
            if(jsonArray.getJSONObject(i).get("column").toString().equals("NotificationsList")){
                String temp = jsonArray.getJSONObject(i).get("value").toString();
                String[] notificationList = temp.split(",");
                ArrayList<String> notificationArrayList = new ArrayList<>(Arrays.asList(notificationList));
                group.setNotificationsList(notificationArrayList);
            }
        }
        return group;
    }

    /**
     * Converts a JSONArray containing Notification data to Notification object
     *
     */
    private Notification getNotificationFromJSON(JSONArray jsonArray) throws JSONException {
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
                String temp = jsonArray.getJSONObject(i).get("value").toString();
                String[] responseList = temp.split(",");
                ArrayList<String> responseArrayList = new ArrayList<>(Arrays.asList(responseList));
                notification.setResponseList(responseArrayList);
            }
            if(jsonArray.getJSONObject(i).get("column").toString().equals("NotificationTime")){
                notification.setDate(Long.parseLong(jsonArray.getJSONObject(i).get("value").toString()));
            }
        }
        return notification;
    }

    /**
     * Converts a JSONArray containing Response data to Response object
     *
     */
    private Response getResponseFromJSON(JSONArray jsonArray) throws JSONException {
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
            if(jsonArray.getJSONObject(i).get("column").toString().equals("ResponseTime")){
                response.setTime(Long.parseLong(jsonArray.getJSONObject(i).get("value").toString()));
            }
        }
        return response;
    }

    /**
     * Returns server host
     *
     */
    private String getServerHost(){
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
