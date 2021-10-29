import java.io.*;
import java.net.*;

public class ClientThreadReader implements Runnable {
    private Socket socket;
    private BufferedReader in;
    public ClientThreadReader(Socket s) throws IOException{
        socket = s;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    public void run() {
        while (true) {
            String s;
            try {
                s = in.readLine();
                System.out.println(s);
                if(s.equals("you got banned.")){
                    socket.close();
                    in.close();
                }
            } catch (IOException e) {
                System.out.println("wegweg");
                return;
            }
        }
    }
    
}
