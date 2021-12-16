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
                    msg = checkEmoji(msg);
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

    public static String checkEmoji(String msg){
        msg = msg.replaceAll("<3", new StringBuilder().appendCodePoint(0x1F497).toString());
        msg = msg.replaceAll(":143:", new StringBuilder().appendCodePoint(0x1F618).toString());
        msg = msg.replaceAll(":pantano:", new StringBuilder().appendCodePoint(0x1F62C).toString());
        msg = msg.replaceAll(":mario:", new StringBuilder().appendCodePoint(0x1F921).toString());
        msg = msg.replaceAll(":safj:", new StringBuilder().appendCodePoint(0x1F41D).toString());
        msg = msg.replaceAll(":skull:", new StringBuilder().appendCodePoint(0x1F480).toString());
        msg = msg.replaceAll(":sad:", new StringBuilder().appendCodePoint(0x1F614).toString());
        msg = msg.replaceAll(":merio:", new StringBuilder().appendCodePoint(0x1F533).toString());
        msg = msg.replaceAll(":baco:", new StringBuilder().appendCodePoint(0x1F41B).toString());
        msg = msg.replaceAll(":swag:", new StringBuilder().appendCodePoint(0x1F60E).toString());
        msg = msg.replaceAll(":stonks:", new StringBuilder().appendCodePoint(0x1F4C8).toString());
        msg = msg.replaceAll(":diablo:", new StringBuilder().appendCodePoint(0x1F608).toString());
        msg = msg.replaceAll(":deltoide:", new StringBuilder().appendCodePoint(0x00394).toString());
        msg = msg.replaceAll(":squidgame:", (new StringBuilder().appendCodePoint(0x1F991).toString() + new StringBuilder().appendCodePoint(0x1F3B2).toString()));
        return msg;
    }

}
