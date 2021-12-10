/**
 * Copyright (c) 22 Giugno anno 0, 2021, SafJNest and/or its affiliates. All rights reserved.
 * SAFJNEST PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * 
 * 
 * 
 * 
 */

import java.io.*;

public class ClientThreadReader implements Runnable {
    private BufferedReader in;
    private KeySorter ks;
    private Encryptor encr;
    private PrintWriter out;
    public ClientThreadReader(BufferedReader in, PrintWriter out, KeySorter ks, Encryptor e)throws IOException{
        this.in = in;
        this.ks = ks;
        this.encr = e;
        this.out = out;
    }

    public void run() {
        loadingBee();
        while (true) {
            String s;
            try {
                s = in.readLine();
                if(s.startsWith("pk")){
                    String name = s.substring(2, s.indexOf("-"));
                    String key = s.substring(s.indexOf("-")+1);
                    ks.setKey(name, key);
                }
                else if(s.startsWith("@end"))
                    ks.isReady();
                else if(s.equals("@quit"))
                    Thread.currentThread().interrupt();
                else if(s.startsWith("/ready"))
                    out.println(encr.getKey());
                else if(s.contains("@dec")){
                    String start = s.substring(0, s.indexOf("@dec"));
                    String[] shit = s.split("-");
                    String msgToDec = shit[1];
                    printDibEraRdini(start+encr.decrypt(msgToDec)+"\033[0m", 12);
                }else
                printDibEraRdini(s, 12);
            } catch (Exception e) {
                return;
            }
        }
    }


    /**
     * @author <a href="https://github.com/ "
     * @param line
     * @throws InterruptedException
     */
    public void printDibEraRdini(String line, int speed){
        try {
            for(char c : line.toCharArray()){
                System.out.print(c);
                Thread.sleep(speed);
            }
            System.out.println();
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    public void loadingBee(){
        printDibEraRdini("\033[46;30m "
                + "                                                      __            \t\n"
                + "                                                      // \\           \t\n"
                + "                                                      \\\\_/ //        \t\n"
                + "                                     '-.._.-''-.._.. -(||)(')        \t\n"
                + "                                                       '''           \t", 8);
        printDibEraRdini(""
                + "\033[90m███████╗\033[93m █████╗ \033[90m███████╗\033[93m   ██╗    \033[90m███╗   ██╗\033[93m███████╗\033[90m███████╗\033[93m████████╗\033[40m\n"
                + "\033[90m██╔════╝\033[93m██╔══██╗\033[90m██╔════╝\033[93m   ██║    \033[90m████╗  ██║\033[93m██╔════╝\033[90m██╔════╝\033[93m╚══██╔══╝\n"
                + "\033[90m███████╗\033[93m███████║\033[90m█████╗  \033[93m   ██║    \033[90m██╔██╗ ██║\033[93m█████╗  \033[90m███████╗\033[93m   ██║   \n"
                + "\033[90m╚════██║\033[93m██╔══██║\033[90m██╔══╝\033[93m██   ██║    \033[90m██║╚██╗██║\033[93m██╔══╝  \033[90m╚════██║\033[93m   ██║   \n"
                + "\033[90m███████║\033[93m██║  ██║\033[90m██║   \033[93m╚█████╔╝    \033[90m██║ ╚████║\033[93m███████╗\033[90m███████║\033[93m   ██║   \n"
                + "\033[90m╚══════╝\033[93m╚═╝  ╚═╝\033[90m╚═╝   \033[93m ╚════╝     \033[90m╚═╝  ╚═══╝\033[93m╚══════╝\033[90m╚══════╝\033[93m   ╚═╝\n\033[0m", 8);
    }   
}
