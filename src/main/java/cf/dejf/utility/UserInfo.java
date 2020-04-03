package cf.dejf.utility;

public class UserInfo {

    private String username;
    private String accessToken;
    private String clientToken;

    public UserInfo() {
    }

    public UserInfo(String username, String accessToken, String clientToken) {
        this.username = username;
        this.accessToken = accessToken;
        this.clientToken = clientToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getClientToken() {
        return clientToken;
    }

    public String getUsername() {
        return username;
    }
}
