import java.util.*;

public class MessageBox {
    private  HashMap<String, HashMap<String, ArrayList<String>>> messageMap;

    public MessageBox(){
        messageMap = new HashMap<String, HashMap<String, ArrayList<String>>>();
    }

    public synchronized void writeMessage(String msg, String sender, String receiver){

        if(!messageMap.get(sender).containsKey(receiver))
            messageMap.get(sender).put(receiver, new ArrayList<String>());
        messageMap.get(sender).get(receiver).add(msg);
        notifyAll();
    }

    public synchronized void addUser(String sender){
        messageMap.put(sender, new HashMap<String, ArrayList<String>>());
    }

    public String getMessage(String sender, String receiver) throws InterruptedException{
        String msg = "";
        synchronized(this) {
            while(messageMap.get(sender).size() == 0){wait();}
            for(String s : messageMap.get(sender).get(receiver))
                msg = msg + "//" + s;
            return msg;
        }
        
    }
    
}
