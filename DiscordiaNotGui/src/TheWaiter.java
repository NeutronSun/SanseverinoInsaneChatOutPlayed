import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TheWaiter implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private UserData data;
    private HashMap<String, HashMap<String, ArrayList<String>>> ashe;
    public TheWaiter(Socket s,  HashMap<String, HashMap<String, ArrayList<String>>> ashe, UserData data) throws IOException{
        socket = s;
        this.data = data;
        this.ashe = ashe;
        out = out = new PrintWriter(socket.getOutputStream(), true);
    }
    public void run() {
        while (true) {
                try {
                    synchronized(this) {
                        while(ashe.get(data.getName()).size() == 0) {
                            wait();
                        }
                        out.println("wgwgwg");
                        for(String sender : ashe.get(data.getName()).keySet()) {
                            for(String msg : ashe.get(data.getName()).get(sender))
                                write(msg, sender);
                        }
                    }
                } catch (InterruptedException e) {
                    //TODO: handle exception
                }
            
        }
    }


    public void write(String msg, String sender){
        out.println("<" + sender + ">" + msg);
    }
}
