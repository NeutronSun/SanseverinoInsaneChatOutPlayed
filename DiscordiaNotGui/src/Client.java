import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.ArrayList;



import java.awt.*;


public class Client {
    
    private static PrintWriter putInServer;
    static Socket echoSocket;

    public static void main(String[] args) throws Exception{
        int portNumber = 77;
        try{
            String hostName = Inet4Address.getLocalHost().getHostAddress();
            Socket echoSocket = new Socket(hostName, portNumber);
            putInServer = new PrintWriter(echoSocket.getOutputStream(), true);
            BufferedReader inKey = new BufferedReader(new InputStreamReader(System.in));
            ClientThreadReader read = new ClientThreadReader(echoSocket);
            new Thread(read).start();
            
            String userInput;
            while ((userInput = inKey.readLine()) != null) {
                putInServer.println(userInput);
            }

            putInServer.close();
            inKey.close();
        } catch (IOException e) {
            System.out.println("you got banned");
            return;
        }
    }

}
