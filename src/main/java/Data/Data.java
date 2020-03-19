package Data;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Data {
    private String SUPERVISOR_ID;
    private String WEB_API_KEY;
    private String ANDROID_APP_PACKAGE_NAME;
    private String PLAYSTORE_URL;
    private String FIREBASE_INVITES_URL;
    private String DYNAMIC_LINK_DOMAIN;
    private String SERVICE_ACCOUNT_KEY_PATH;
    private String SYNC_CLIENT_URL;
    private String FIREBASE_DATABASE_URL;
    private String STORAGE_BUCKET;
    private static final String FILE_NAME = "keys.json";
    public static final String FIREBASE_KEYS_FILE_NAME = "FirebaseKeys.json";

    public static Data data;

   public Data(){
      try {
             if (Files.exists(Paths.get(FILE_NAME))) {
              String content = new String(Files.readAllBytes(Paths.get(FILE_NAME)));
              JSONObject mainObject = new JSONObject(content);
              try {
                  setSERVICE_ACCOUNT_KEY_PATH(mainObject.getString("serviceAccountKeyPath"));
                  setWEB_API_KEY(mainObject.getString("webAPIKey"));
                  setDYNAMIC_LINK_DOMAIN(mainObject.getString("dynamicLinkDomain"));
                  setANDROID_APP_PACKAGE_NAME(mainObject.getString("packageName"));
                  setSYNC_CLIENT_URL(mainObject.getString("syncClientURL"));
                  setFIREBASE_DATABASE_URL(mainObject.getString("firebaseDatabaseURL"));
                  setSTORAGE_BUCKET(mainObject.getString("storageBucket"));
              } catch (Exception e) {
                  e.printStackTrace();
              }
          }
      }
       catch (IOException | JSONException e) {
          e.printStackTrace();
      }
   }

  public void saveKeys(){
      JSONObject obj = new JSONObject();
      obj.put("serviceAccountKeyPath", SERVICE_ACCOUNT_KEY_PATH);
      obj.put("webAPIKey", WEB_API_KEY);
      obj.put("dynamicLinkDomain", DYNAMIC_LINK_DOMAIN);
      obj.put("packageName", ANDROID_APP_PACKAGE_NAME);
      obj.put("syncClientURL", SYNC_CLIENT_URL);
      obj.put("firebaseDatabaseURL",FIREBASE_DATABASE_URL);
      obj.put("storageBucket",STORAGE_BUCKET);

      try {
          BufferedWriter out = new BufferedWriter(new FileWriter(FILE_NAME));
          out.write(obj.toString());
          out.close();
      } catch (IOException e) {
          e.printStackTrace();
      }

      try {
          String FirebaseKeysContent = new String(Files.readAllBytes(Paths.get(SERVICE_ACCOUNT_KEY_PATH)));
          BufferedWriter out = new BufferedWriter(new FileWriter(FIREBASE_KEYS_FILE_NAME));
          out.write(FirebaseKeysContent);
          out.close();
      } catch (IOException e) {
          e.printStackTrace();
      }

  }

    public String getSUPERVISOR_ID() {
        return SUPERVISOR_ID;
    }

    public void setSUPERVISOR_ID(String SUPERVISOR_ID) {
        this.SUPERVISOR_ID = SUPERVISOR_ID;
    }

    public String getWEB_API_KEY() {
        return WEB_API_KEY;
    }

    public void setWEB_API_KEY(String WEB_API_KEY) {
        this.WEB_API_KEY = WEB_API_KEY;
    }

    public String getANDROID_APP_PACKAGE_NAME() {
        return ANDROID_APP_PACKAGE_NAME;
    }

    public void setANDROID_APP_PACKAGE_NAME(String ANDROID_APP_PACKAGE_NAME) {
        this.ANDROID_APP_PACKAGE_NAME = ANDROID_APP_PACKAGE_NAME;
    }

    public String getPLAYSTORE_URL() {
        return PLAYSTORE_URL;
    }

    public void setPLAYSTORE_URL(String PLAYSTORE_URL) {
        this.PLAYSTORE_URL = PLAYSTORE_URL;
    }

    public String getFIREBASE_INVITES_URL() {
        return "https://firebasedynamiclinks.googleapis.com/v1/shortLinks?key=" + WEB_API_KEY;
    }

    public void setFIREBASE_INVITES_URL(String FIREBASE_INVITES_URL) {
        this.FIREBASE_INVITES_URL = FIREBASE_INVITES_URL;
    }

    public String getDYNAMIC_LINK_DOMAIN() {
        return DYNAMIC_LINK_DOMAIN;
    }

    public void setDYNAMIC_LINK_DOMAIN(String DYNAMIC_LINK_DOMAIN) {
        this.DYNAMIC_LINK_DOMAIN = DYNAMIC_LINK_DOMAIN;
    }

    public String getSERVICE_ACCOUNT_KEY_PATH() {
        return SERVICE_ACCOUNT_KEY_PATH;
    }

    public void setSERVICE_ACCOUNT_KEY_PATH(String SERVICE_ACCOUNT_KEY_PATH) {
        this.SERVICE_ACCOUNT_KEY_PATH = SERVICE_ACCOUNT_KEY_PATH;
    }

    public String getSYNC_CLIENT_URL() {
        return SYNC_CLIENT_URL;
    }

    public void setSYNC_CLIENT_URL(String SYNC_CLIENT_URL) {
        this.SYNC_CLIENT_URL = SYNC_CLIENT_URL;
    }

    public String getFIREBASE_DATABASE_URL() {
        return FIREBASE_DATABASE_URL;
    }

    public void setFIREBASE_DATABASE_URL(String FIREBASE_DATABASE_URL) {
        this.FIREBASE_DATABASE_URL = FIREBASE_DATABASE_URL;
    }
    public String getSTORAGE_BUCKET(){
       return STORAGE_BUCKET;
    }
    public void setSTORAGE_BUCKET(String STORAGE_BUCKET){
       this.STORAGE_BUCKET = STORAGE_BUCKET;
    }
    @Override
    public String toString() {
        return "Data{" +
                "SUPERVISOR_ID='" + SUPERVISOR_ID + '\'' +
                ", WEB_API_KEY='" + WEB_API_KEY + '\'' +
                ", FIREBASE_DATABASE_URL='" + FIREBASE_DATABASE_URL + '\'' +
                ", ANDROID_APP_PACKAGE_NAME='" + ANDROID_APP_PACKAGE_NAME + '\'' +
                ", PLAYSTORE_URL='" + PLAYSTORE_URL + '\'' +
                ", FIREBASE_INVITES_URL='" + FIREBASE_INVITES_URL + '\'' +
                ", DYNAMIC_LINK_DOMAIN='" + DYNAMIC_LINK_DOMAIN + '\'' +
                ", SERVICE_ACCOUNT_KEY_PATH='" + SERVICE_ACCOUNT_KEY_PATH + '\'' +
                ", SYNC_CLIENT_URL='" + SYNC_CLIENT_URL + '\'' +
                ", STORAGE_BUCKET='" + STORAGE_BUCKET + '\'' +
                '}';
    }
}
