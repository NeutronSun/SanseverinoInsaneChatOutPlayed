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
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.regex.Pattern;


public class ServerThread implements Runnable{
    /** Consente la connessione client-server*/ 
    private Socket socket;
    /**Consente la scrittura del server sul buffer del client */
    private PrintWriter out;
    /**Consente la lettura del proprio buffer per leggere i messaggi da parte del client */
    private BufferedReader in;
    /**Oggetto condiviso di tipo {@link MessageBox} dove saranno scritti i messaggi */
    private MessageBox mailBox;
    /**Oggetto condiviso di tipo {@link UserManager} dove saranno gestiti tutti gli utenti */
    private UserManager um;
    /**Oggetto condiviso di tipo {@link FileManager} dove saranno gestiti tutti i file del server*/
    private FileManager fileManager;
    /**{@code Mappa} contenente una descrizione dettagliata dei comandi */
    private HashMap<String, String> commandList;
    /**Contatore autoincrementante che identifica tutti i vari client/ServerThread */
    private int cont;
    private HashMap <String, String> colors = new HashMap<String, String>();
    private Thread t;

    public ServerThread(Socket sck, MessageBox mailBox, UserManager um, FileManager fm, int contT){
        socket = sck;
        this.mailBox = mailBox;
        this.um = um;
        fileManager = fm;
        cont = contT;
        commandList = new HashMap<String, String>();

        commandList.put("list", "Get the list of all the online users.\r\n\r\nLIST");
        commandList.put("all", "Send a message to all the users.\r\n\r\nALL [message]\r\n\r\n\tmessage - text to be sent");
        commandList.put("@send", "Send a message to one/more specific user/s.\r\n\r\n@user@user1@user2...@userN [message]\r\n\r\n\tmessage - text to be sent");
        commandList.put("quit", "Terminate the program.\r\n\r\nQUIT");
        commandList.put("help", "Get a specific description of a command.\r\n\r\nHELP [command]\r\n\r\n\t");
        commandList.put("set status", "Set the current status to offline or online.\r\n\r\nSET [OFFLINE/ONLINE]\r\n\r\nONLINE - Every one can see you as connected and text to you\r\n\r\nOFFLINE - No one can see you as connected or text to you but you can\r\n\r\n\t");
        commandList.put("set color", "Set the the color of the text of your message.\r\n\r\nSET COLOR [COLOR NAME]" 
        + "\r\n\r\nBLACK - You like the dark, maybe no one would be able to see what you write, but black is black."
        + "\r\n\r\nRED - Wow red im the diablo."
        + "\r\n\r\nGREEN - IM A HACKER 243424 MATRID274T G4."
        + "\r\n\r\nYELLOW - beeeeeees buzzzzz."
        + "\r\n\r\nBLUE - rgb(0, 0, 255);"
        + "\r\n\r\nMAGENTA - is it a color?"
        + "\r\n\r\nBRIGHTMAGENTA - The upper one but brighter."
        + "\r\n\r\nWHITE - You are normal."
        + "\r\n\r\n\t");
       
        colors.put("black","\033[30m");
        colors.put("red","\033[31m");
        colors.put("green","\033[32m");
        colors.put("yellow","\033[33m");
        colors.put("blue","\033[34m");
        colors.put("magenta","\033[35m");
        colors.put("brightmagenta","\033[95m");
        colors.put("white","\033[97m");

        colors.put("purple","\033[38;2;<123>;<0>;<255>m");
        colors.put("water", "\033[38;2;<0>;<255>;<238>m");
    }

    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            /**
             * input dati utente
             * --------------------------
             */
            String n = " ", p = " ", e = " ";
            boolean check = true;
            String answer = "";
            if(!fileManager.isEmpty()){
            out.println("Do you already have an account?");
            answer = in.readLine();
            }else{
                out.println("No account registered, create a new one.");
                answer = "no";
            }
            if(answer.equalsIgnoreCase("yes")) {
                do{
                    check = true;
                    out.println("Enter UserName");
                    n = in.readLine();
                    out.println("Enter Password");
                    p = in.readLine();
                    if(um.isConnected(n)){
                        out.println(n + " is already online");
                        check = false;
                    }
                    if(fileManager.checkUser(n)){
                        out.println(n + " not exists as user");
                        check = false;
                    }
                    else if(!fileManager.checkPassword(n, p)){
                        out.println("wrong password");
                        check = false;
                    }
                }while(!check);
            }else if(answer.equalsIgnoreCase("no")){
                do{
                    check = true;
                    out.println("Enter your mail");
                    e = in.readLine();
                    out.println("Enter UserName");
                    n = in.readLine();
                    out.println("Enter Password");
                    p = in.readLine();
                    if(um.isConnected(n)){
                        out.println(n + " is already online");
                        check = false;
                    }
                    if(!fileManager.checkUser(n)){
                        out.println(n + " already exists as user");
                        check = false;
                    }
                    if(!Pattern.matches("[\\w]+@[\\w]+\\.[a-zA-Z]{2,3}", e)){
                        out.println("wrong format email");
                        check = false;
                    }
                    if(!Pattern.matches("^[A-Za-z0-9]+(?:[ _-][A-Za-z0-9]+)*$", n)){
                        out.println("wrong format unsername");
                        check = false;
                    }
                    if(check)
                    fileManager.addUser(n, p, e);
                
                }while(!check);
            }else{out.println("Invalid answer");}
            /**
             * -----------------------------
             * settings data user
             */
            um.addUser(String.valueOf(cont), new UserData(n,p,"0000"));
            um.setOnline(String.valueOf(cont));
            mailBox.addUser(um.getName(String.valueOf(cont)));
            System.out.println("log<" + um.getName(String.valueOf(cont)) + "> LOGGED CORRECTLY.");
            t =  new Thread(new TheWaiter(socket,mailBox,um.toObject(String.valueOf(cont))));
           t.start();
            /**
             * ----------------------------
             * now the user can send messages or digits commands
             */
            String line = "";
            out.println("Welcome in, digit /help for more infomation");
            out.println("/ready");
            notifyAll("server", (um.getName(String.valueOf(cont)) + " is now online"));
            um.toObject(String.valueOf(cont)).setColor("\033[0m");
            while (!(line = in.readLine()).equals("quit")) {
                if(line.equals("/help")){
                    out.println("Per ulteriori informazioni su uno specifico comando, digitare HELP nome comando.\r\n"
                                    + "LIST\tGet a list of users\r\n"
                                    + "ALL\tSend a message to all the users\r\n"
                                    + "@SEND\tSend a message to a specific user\r\n"
                                    + "QUIT\tTerminate the program\r\n"
                                    + "HELP\tGet a more specific guide for the commands\r\n"
                                    + "SET COLOR\tYou can set custom customization for the color\r\n"
                                    + "SET STATUS\tYou can set your current statu\r\n");
                }
                else if(line.startsWith("/help"))
                    command(line);
                else if(line.equals("/list"))
                    getListUser();
                else if(line.equalsIgnoreCase("/getSalt"))
                    out.println(fileManager.getSalt());
                else if(line.startsWith("/all"))
                    sendKeys(line);
                else if(line.startsWith("/set color"))
                    setColor(line);
                else if(line.startsWith("divideandconquer"))
                    divideandconquer(line);
                else if(line.startsWith("be fast pls"))
                    theflash(line);
                else if(line.startsWith("@"))
                    sendKeys(line);
                else if(line.startsWith("pk"))
                    um.setPk(String.valueOf(cont), line.substring(2));
                else if(line.startsWith("msg"))
                    sendMessage(line);
                else if(line.equalsIgnoreCase("/set offline")){
                    if(um.isConnected(um.getName(String.valueOf(cont)))){
                        out.println("your current status has been changed to offline, now you are a ghost ;D");
                        System.out.println("log<" + um.getName(String.valueOf(cont)) + "> SETTED IS STATUS TO OFFLINE");
                        notifyAll("server", (um.getName(String.valueOf(cont)) + " has left the chat"));
                        um.setStatus(String.valueOf(cont), false);
                    }else
                        out.println("Your status already is offline.");
                }
                else if(line.equalsIgnoreCase("/set online")){
                    if(!um.isConnected(um.getName(String.valueOf(cont)))){
                        out.println("your current status has been changed to online");
                        notifyAll("server", (um.getName(String.valueOf(cont)) + " joined the chat"));
                        System.out.println("log<" + um.getName(String.valueOf(cont)) + "> SETTED IS STATUS TO ONLINE");
                        um.setStatus(String.valueOf(cont), true);
                    }else
                        out.println("Your status already is online");
                }
                else
                    out.println("Wrong command, for more information please digit '/help'.");
            }
        } catch (Exception e) {
            t.interrupt();
            mailBox.removeUser(um.getName(String.valueOf(cont)));
            notifyAll("server", (um.getName(String.valueOf(cont)) + " has left the chat"));
            System.out.println("log<" + um.getName(String.valueOf(cont)) + "> HAS LEFT THE CHAT");
            um.remove(String.valueOf(cont));
        }
    }

    public void sendKeys(String line){
        /**
         * contorllo he eisdstani tutti i nomu
         */
        String[] names;
        if(line.startsWith("/all")){
            names = um.namesToArray(String.valueOf(cont));
        }else
             names = line.substring(1, line.indexOf(" ")).split("@");
        System.out.println(names);
        System.out.println(line);
        for(String name : names){
            if(name.equals(um.getName(String.valueOf(cont))))
                out.println("<Server> Avoid writing to yourself, not funny :L");
            else if(um.isConnected(name))
                out.println("pk" + name + "-" +  um.getPk(name));
            else
                out.println("<Server>" + name + " not exists");
            
        }
        out.println("@end");
    }


    public void command(String line){
        line = line.substring(line.indexOf(" ") + 1);
        System.out.println(line);
        if(commandList.get(line) != null)
            out.println(commandList.get(line));
        else
            out.println("Commnand not found");
    }


    public void setColor(String line){
        String[] s = line.split(" ");
        if(colors.containsKey(s[2])){
            um.toObject(String.valueOf(cont)).setColor(colors.get(s[2]));
            out.println("Now your color is " + s[2].toUpperCase());
        }else{
            out.println("This color isnt avaible, use command /help to see which color use");
        }
    }

    public void notifyAll(String sender, String msg){
        try {
            for(String name : um.namesToArray(String.valueOf(cont))){
                DateTimeFormatter dtf =  DateTimeFormatter.ofPattern("HH:mm");
                mailBox.writeMessage(new Message(sender, msg, dtf.format(LocalDateTime.now()),um.toObject(String.valueOf(cont)).getColor()), name);
            }
            System.out.println("log<" + um.getName(String.valueOf(cont)) + "> SENT CORRECTLY THE MESSAGE.");
        } catch (Exception e) {
            System.out.println("log<" + um.getName(String.valueOf(cont)) + "> DIDNT SENT CORRECTLY THE MESSAGE: ZERO USERS AVAIBLE");
        }
            
    }

    public void getListUser(){
        try{
            for(UserData dt : um.toArray(String.valueOf(cont)))
                out.println("<Server>" + dt.getName() + " is online");
        }catch (Exception e) {out.println("You are the only one connected.");}
    }

    public void sendMessage(String line) throws InterruptedException, IOException{
        String[] ss = line.split("-");
        String msg = ss[2];
        System.out.println("arr: " + ss);
        msg = "@dec-" + msg; 
        DateTimeFormatter dtf =  DateTimeFormatter.ofPattern("HH:mm");
        mailBox.writeMessage(
            new Message(
                um.getName(String.valueOf(cont)), 
                msg,dtf.format(LocalDateTime.now()), 
                um.toObject(String.valueOf(cont)).getColor()), 
            ss[1]
        );
    }


    /**
     * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
     * @author <a href="https://github.com/XOShu4">XOShu4</a>
     * @param msg
     */

    public void divideandconquer(String msg){
        msg = msg.substring(msg.indexOf(" ")+1);
        try {
            int n = Integer.parseInt(msg);
            if (n < 100000) {
                // 5 or less
                if (n < 100){
                    // 1 or 2
                    if (n < 10)
                        out.println("1");
                    else
                    out.println("2");
                } else {
                    // 3 or 4 or 5
                    if (n < 1000)
                    out.println("3");
                    else {
                        // 4 or 5
                        if (n < 10000)
                        out.println("4");
                        else
                        out.println("5");
                    }
                }
            } else {
                // 6 or more
                if (n < 10000000) {
                    // 6 or 7
                    if (n < 1000000)
                        out.println("6");
                    else
                        out.println("7");
                } else {
                    // 8 to 10
                    if (n < 100000000)
                        out.println("8");
                    else {
                        // 9 or 10
                        if (n < 1000000000)
                            out.println("9");
                        else
                            out.println("10");
                    }
                }
            }
        } catch (NumberFormatException e) {
            out.println("digit a number");
        }
    }


    public void theflash(String msg){
        try {
            String[] f = msg.split(" ");
            float x = invSqrt(Float.parseFloat(f[3]));
            out.println(x);
        } catch (NumberFormatException e) {out.println("digit a number");}
    }

    /**
     * Il metodo piu' veloce per calcolare la radice quadrata inversa
     * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
     * @author <a href="https://github.com/Leon412">Leon412</a> 
     * @param x
     * @return
     * the fastest float ever
     */
    public static float invSqrt(float x) {
        float xhalf = 0.5f * x;
        int i = Float.floatToIntBits(x);
        i = 0x5f3759df - (i >> 1);
        x = Float.intBitsToFloat(i);
        x *= (1.5f - xhalf * x * x);
        return x;
    }

}
