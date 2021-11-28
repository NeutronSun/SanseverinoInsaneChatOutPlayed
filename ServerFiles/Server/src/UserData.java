public class UserData {
    private String userName;
    private String password;
    private Boolean online = false;
    private String publicKey;

    public UserData(String userName, String password, String pk){
        this.userName = userName;
        this.password = password;
        publicKey = pk;
    }


    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getName(){
        return userName;
    }

    public void setOnline(boolean online){
        this.online = online;
    }

    public boolean isOnline() {
        return online;
    }
    
}
