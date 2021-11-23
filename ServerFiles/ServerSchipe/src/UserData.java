public class UserData {
    private String userName;
    private Boolean online = false;
    private String publicKey;

    public UserData(String userName, String pk){
        this.userName = userName;
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
