package cf.dejf.utility;

import xyz.ashleyz.JavaProcess;

public class UserInfo {

    private static String username = "";
    private static String accessToken = null;
    private static String clientToken = null;

    private UserInfo() {}

    public static void setUserInfo(String name, String token, String clToken) {
        username = name;
        accessToken = token;
        clientToken = clToken;
    }

    public static String getAccessToken() {
        return accessToken;
    }

    public static String getClientToken() {
        return clientToken;
    }

    public static String getUsername() {
        return username;
    }
}
