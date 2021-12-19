/**
 * Copyright (c) 22 Giugno anno 0, 2021, SafJNest and/or its affiliates. All rights reserved.
 * SAFJNEST PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * 
 * 
 * 
 * 
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Socket;
/**
* Client che prende in input, per il resto non fa nulla <3.
**/
public class Client {
    
    private static PrintWriter putInServer;
    static Socket echoSocket;
    /**
    *main: la funzione main è il punto di inizio per l'esecuzione di un programma.
    **/
    public static void main(String[] args) throws Exception{
        int portNumber = 11701;
        Encryptor encrypt = new Encryptor(1024);
        KeySorter ks = new KeySorter();
        try{
            String hostName = Inet4Address.getLocalHost().getHostAddress();
            Socket echoSocket = new Socket(hostName, portNumber);
            putInServer = new PrintWriter(echoSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            BufferedReader inKey = new BufferedReader(new InputStreamReader(System.in));
            ClientThreadReader read = new ClientThreadReader(in, putInServer, ks, encrypt);
            Thread t = new Thread(read);
            t.start();
           // putInServer.println(encrypt.getKey());
            String userInput; 
            while (!(userInput = inKey.readLine()).equals("quit")) {
                if(userInput.startsWith("@") || userInput.startsWith("/all")) {
                    String msg = userInput.substring(userInput.indexOf(" ") + 1);
                    putInServer.println(userInput);  
                    String[] names = ks.getNames();
                    if(ks.isOkay()){
                        for(String name : names){
                            putInServer.println("msg-" + name + "-" + encrypt.encrypt(msg, ks.getKey(name)));
                        }
                    }
                }else{
                    putInServer.println(userInput);
                }
            }
            echoSocket.close();
            putInServer.close();
            inKey.close();
            t.interrupt();
        } catch (Exception e) {
            System.out.println("connection lost."); //no way!1!!1! IMPOSSIBLE
            return;
        }
    }

}
