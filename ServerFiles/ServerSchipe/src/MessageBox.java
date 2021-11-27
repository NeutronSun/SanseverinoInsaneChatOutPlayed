import java.util.*;

public class MessageBox {
    private int contReader;
    private  HashMap<String, ArrayList<Message>> messageMap;

    public MessageBox(){
        contReader = 0;
        messageMap = new HashMap<String, ArrayList<Message>>();
    }

    public synchronized void addUser(String name){
        messageMap.put(name, new ArrayList<Message>());
    }

    public synchronized void writeMessage(Message msg, String recivier) throws InterruptedException{
        while(contReader > 0){wait();}
        System.out.println(msg.getPerfectMessage());
        messageMap.get(recivier).add(msg);
        notifyAll();
    }
    
    public Message[] getMessage(String sender) throws InterruptedException{
        while(messageMap.get(sender).size() == 0){wait();
            System.out.println("ciao");}
        synchronized(this){
            contReader++;
        }
        Message[] message = new Message[messageMap.get(sender).size()];
        message = messageMap.get(sender).toArray(message);
        synchronized(this){
            contReader--;
            messageMap.remove(sender);
            messageMap.put(sender, new ArrayList<Message>());
            notifyAll();
        }
        return message;
    }
    
}
