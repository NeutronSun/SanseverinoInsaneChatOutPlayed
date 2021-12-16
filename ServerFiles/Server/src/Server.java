/**
 * Copyright (c) 22 Giugno anno 0, 2021, SafJNest and/or its affiliates. All rights reserved.
 * SAFJNEST PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * 
 * 
 * 
 * 
 */

import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.lang.StringBuilder;

public class Server {
    public static void main(String[] args) throws Exception {
        int portNumber = 11701, contThread = 0;
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("ip: " + Inet4Address.getLocalHost().getHostAddress());
            System.out.println("port: " + portNumber);
            System.out.println("Waiting for user..." +  new StringBuilder().appendCodePoint(0x1F920).toString());
            
            FileManager fm = new FileManager();
            MessageBox mailBox = new MessageBox();
            UserManager um = new UserManager();
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ServerThread(clientSocket, mailBox, um, fm, contThread)).start();
                System.out.println("Connection Accepted with client-" + contThread);
                contThread++;
            }
        }catch (Exception e) {e.printStackTrace();System.out.println("D:");}
    }

}
