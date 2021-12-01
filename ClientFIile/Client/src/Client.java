import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
    
    private static PrintWriter putInServer;
    static Socket echoSocket;

    public static void main(String[] args) throws Exception{
        int portNumber = 77;
        Encryptor encrypt = new Encryptor();
        KeySorter ks = new KeySorter();
        try{
            String hostName = Inet4Address.getLocalHost().getHostAddress();
            Socket echoSocket = new Socket(hostName, portNumber);
            putInServer = new PrintWriter(echoSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            BufferedReader inKey = new BufferedReader(new InputStreamReader(System.in));
            ClientThreadReader read = new ClientThreadReader(in, ks);
            new Thread(read).start();
            
            String userInput;
            while ((userInput = inKey.readLine()) != null) {
                if(userInput.startsWith("@")) {
                    String[] names = userInput.substring(1, userInput.indexOf(" ")).split("@");
                    putInServer.println(userInput.substring(1, userInput.indexOf(" ")));
                    
                }
                
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
