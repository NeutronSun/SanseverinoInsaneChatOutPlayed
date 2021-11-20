 import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    public static void main(String[] args) throws Exception {
        int portNumber = 77;
        ServerSocket serverSocket = new ServerSocket(portNumber);
        int contThread = 0;
        System.out.println(new StringBuilder().appendCodePoint(0x1F9DC).toString() + " frake");
        ArrayList<Thread> threads = new ArrayList<Thread>();
        ArrayList<ServerThread> clients = new ArrayList<ServerThread>();
        System.out.println("ip: " + Inet4Address.getLocalHost().getHostAddress());
        System.out.println("port: " + portNumber);
        System.out.println("Waiting for user..." );
        HashMap<String, HashMap<String, ArrayList<String>>> messageMap = new HashMap<String, HashMap<String, ArrayList<String>>>();
        while (true) {
            Socket clientSocket = serverSocket.accept();
            clients.add(new ServerThread(clientSocket, clients, messageMap));
            threads.add(new Thread(clients.get(contThread)));
            threads.get(contThread).setName(String.valueOf(contThread));
            threads.get(contThread).start();
            System.out.println("Connection Accepted with client-" + threads.get(contThread).getName());
            contThread++;
        }
    }
}
