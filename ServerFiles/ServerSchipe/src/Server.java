import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    public static void main(String[] args) throws Exception {
        int portNumber = 77;
        ServerSocket serverSocket = new ServerSocket(portNumber);
        ArrayList<Thread> threads = new ArrayList<Thread>();
        ArrayList<ServerThread> clients = new ArrayList<ServerThread>();
        System.out.println("ip: " + Inet4Address.getLocalHost().getHostAddress());
        System.out.println("port: " + portNumber);
        System.out.println("Waiting for user..." );
        MessageBox mailBox = new MessageBox();
        ArrayList<UserData> users = new ArrayList<UserData>();
        int contThread = 0;
        while (true) {
            Socket clientSocket = serverSocket.accept();
            clients.add(new ServerThread(clientSocket, mailBox, users));
            threads.add(new Thread(clients.get(contThread)));
            threads.get(contThread).setName(String.valueOf(contThread));
            threads.get(contThread).start();
            System.out.println("Connection Accepted with client-" + threads.get(contThread).getName());
            contThread++;
        }
    }

}
