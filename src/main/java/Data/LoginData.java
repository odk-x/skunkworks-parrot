package Data;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LoginData {
    private String SUPERVISOR_ID;
    private String WEB_API_KEY;
    private String ANDROID_APP_PACKAGE_NAME;
    private String PLAYSTORE_URL;
    private String FIREBASE_INVITES_URL = "https://firebasedynamiclinks.googleapis.com/v1/shortLinks?key=" + WEB_API_KEY;
    private String DYNAMIC_LINK_DOMAIN = "odknotifications.page.link";
    private String SERVICE_ACCOUNT_KEY_PATH;
    private String SYNC_CLIENT_URL;
    private static final String FILE_NAME = "keys.json";

    public static LoginData loginData;

   public LoginData(){
      try {
          String content = new String(Files.readAllBytes(Paths.get(FILE_NAME)));
          JSONObject mainObject = new JSONObject(content);
          System.out.println("$$$"+mainObject.toString());
          try {
              setSERVICE_ACCOUNT_KEY_PATH((String) mainObject.getString("serviceAccountKeyPath"));
              setWEB_API_KEY((String) mainObject.getString("webAPIKey"));
              setDYNAMIC_LINK_DOMAIN((String) mainObject.getString("dynamicLinkDomain"));
              setANDROID_APP_PACKAGE_NAME((String) mainObject.getString("packageName"));
              setSYNC_CLIENT_URL((String) mainObject.getString("syncClientURL"));
          }catch (Exception e){
              e.printStackTrace();
          }
      } catch (IOException | JSONException e) {
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

      try {
          BufferedWriter out = new BufferedWriter(new FileWriter(FILE_NAME));
          out.write(obj.toString());
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
        return FIREBASE_INVITES_URL;
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

    @Override
    public String toString() {
        return "LoginData{" +
                "SUPERVISOR_ID='" + SUPERVISOR_ID + '\'' +
                ", WEB_API_KEY='" + WEB_API_KEY + '\'' +
                ", ANDROID_APP_PACKAGE_NAME='" + ANDROID_APP_PACKAGE_NAME + '\'' +
                ", PLAYSTORE_URL='" + PLAYSTORE_URL + '\'' +
                ", FIREBASE_INVITES_URL='" + FIREBASE_INVITES_URL + '\'' +
                ", DYNAMIC_LINK_DOMAIN='" + DYNAMIC_LINK_DOMAIN + '\'' +
                ", SERVICE_ACCOUNT_KEY_PATH='" + SERVICE_ACCOUNT_KEY_PATH + '\'' +
                ", SYNC_CLIENT_URL='" + SYNC_CLIENT_URL + '\'' +
                '}';
    }
}
