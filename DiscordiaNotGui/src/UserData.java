public class UserData {
    public String userName;
    public String chattingInto;

    public UserData(String userName, String room){
        this.userName = userName;
        chattingInto = room;
    }


    public void setUserName(String userName){
        this.userName = userName;
    }

    public void setRoom(String room){
        this.chattingInto = room;
    }

    public String getName(){
        return userName;
    }

    public String getRoom(){
        return chattingInto;
    }
    
}
