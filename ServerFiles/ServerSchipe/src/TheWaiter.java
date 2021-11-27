import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TheWaiter implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private UserData data;
    private MessageBox mailBox;
    public TheWaiter(Socket s,  MessageBox mailBox, UserData data)throws IOException{
        socket = s;
        this.data = data;
        this.mailBox = mailBox;
        out = new PrintWriter(socket.getOutputStream(), true);
    }
    public void run() {
        while(true){
            try {
                System.out.println(data.getName());
                Message[] msg = mailBox.getMessage(data.getName());
                for(Message m : msg){
                    out.println(m.getPerfectMessage());
                }
            }catch(InterruptedException e){e.printStackTrace();}
        }
    }


}
