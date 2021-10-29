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
            BufferedReader inServer = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            ClientThreadReader read = new ClientThreadReader(echoSocket);
            new Thread(read).start();
            //here the user has to answer if he has an account
            inServer.readLine();
            //here the server says what the client has to do
            

            putInServer.close();
            inKey.close();
        } catch (IOException e) {
            System.out.println("you got banned");
            return;
        }
    }

}
