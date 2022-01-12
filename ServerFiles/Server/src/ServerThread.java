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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Istanza della classe {@link Server}.
 * <p>Ogni volta che il {@code server} accetta una connessione con il
 * {@code client} questa classe viene istanziata e le vengono passsati
 * tutti gli oggetti condivisi ed il {@linl ServerThread#socket socket} con 
 * cui effettua la connessione con il client.
 * <p>La connessione nella fase iniziale avviene in maniera sequenziale: 
 * fase di login o di registrazione, dove vengono estrapolati {@code username} e
 * {@code password} dell'utente e vengono settati nei vari oggetti {@link FileManager} e
 * {@link UserManager}.
 * <p>Solo ora viene avviato il thread secondario {@link TheWaiter} che si occupera' di stare 
 * in attesa e, una volta presente un nuovo messaggio, inviarli al client.
 * La classe, in oltre, si occupa di gestire tutti i diversi protocolli, tra cui:
 * <p><ul>
 * <li>{@code /help} -> [/help](nome comando), viene richiesta una descrizione del comando
 * <li> {@code /list} -> viene richiesta la lista di tutti gli utenti collegati
 * <li> {@code /all} -> [/all][messaggio], il client desidera inviare un messaggio a tutti gli utenti connessi
 * <li> {@code /set color} -> [/set][color][colore], il client desidera cambiare colore con cui sara' visto dagli altri
 * <li> {@code @} -> [@user](@user2...userN)[messaggio], il client desidera inviare un messaggio a specifici utenti
 * <li> {@code pk}, il client manda la {@code chiave pubblica} al server. [avviete in automatico] e
 * viene settata nella {@code mappa} {@link UserManager}
 * <li> {@code msg}, il client invia il messaggio criptato contenente il destinatario e corpo del messaggio
 * <li> {@code /set offline|online} -> [/set][status], il client desidera cambiare il sui status di collegamento
 * </ul><p>
 * Se il messaggio inizia con "{@code /}" significa che e' un comando inviato dall'utente 
 * altrimenti e' un protocollo stabilito tra client e server.
 */
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
    /**{@code mappa} contenente tutti i colori utilizzabili dagli utenti */
    private HashMap <String, String> colors = new HashMap<String, String>();
    /**Thread, {@link TheWaiter}, che sara' istanziato una volta ricevuti tutti i dati */
    private Thread t;

    public ServerThread(Socket sck, MessageBox mailBox, UserManager um, FileManager fm, int contT){
        this.socket = sck;
        this.mailBox = mailBox;
        this.um = um;
        this.fileManager = fm;
        this.cont = contT;
        this.commandList = new HashMap<String, String>();

        commandList.put("list", "Get the list of all the online users.\r\n\r\nLIST");
        commandList.put("all", "Send a message to all the users.\r\n\r\nALL [message]\r\n\r\n\tmessage - text to be sent");
        commandList.put("@send", "Send a message to one/more specific user/s.\r\n\r\n@user@user1@user2...@userN [message]\r\n\r\n\tmessage - text to be sent");
        commandList.put("quit", "Terminate the program.\r\n\r\nQUIT");
        commandList.put("help", "Get a specific description of a command.\r\n\r\nHELP [command]\r\n\r\n\t");
        commandList.put("set status", "Set the current status to offline or online.\r\n\r\nSET [OFFLINE/ONLINE]\r\n\r\nONLINE - Every one can see you as connected and text to you\r\n\r\nOFFLINE - No one can see you as connected or text to you but you can\r\n\r\n\t");
        commandList.put("set color", "Set the the color of the text of your message.\r\r\nSET COLOR [COLOR NAME]" 
        + "\r\033[30mBLACK\033[0m - rgb(0, 0, 0);"
        + "\r\033[31mRED\033[0m - rgb(255, 0, 0);"
        + "\r\033[32mGREEN\033[0m - IM A HACKER 243424 MATRID274T G4."
        + "\r\033[33mYELLOW\033[0m - beeeeeees buzzzzz."
        + "\r\033[34mBLUE\033[0m - rgb(0, 0, 255);"
        + "\r\033[35mMAGENTA\033[0m - is it a color?"
        + "\r\033[95mBRIGHTMAGENTA\033[0m - The upper one but brighter."
        + "\rWHITE - You are normal."
        + "\rCUSTOM - With the syntax SET COLOR RGB [R][G][B] you can set a custom rgb color."
        + "\r\n\r\n\t");
        
        
        colors.put("black","\033[30m");
        colors.put("red","\033[31m");
        colors.put("green","\033[32m");
        colors.put("yellow","\033[33m");
        colors.put("blue","\033[34m");
        colors.put("magenta","\033[35m");
        colors.put("brightmagenta","\033[95m");
        colors.put("white","\033[0m");
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
            out.println("Do you already have an account? Yes - No");
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
            out.println("To send a message -> [@username][message]");
            out.println("/ready");
            notifyAll("server", (um.getName(String.valueOf(cont)) + " is now online"));
            um.toObject(String.valueOf(cont)).setColor("\033[0m");
            while (!(line = in.readLine()).equals("quit")) {
                if(line.equals("/help")){
                    out.println("FOR MORE INFOMATION DIGIT [/help][name-command]\r\n"
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
                else if(line.equals("/get color picker"))
                    colorPicker();
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


    /**
     * Metodo che viene richiamato ogni volta che l'utente desidera
     * inviare un messaggio a qualcuno.
     * <p>Nel caso in cui il messaggio sia {@code /all} il server inviare tutte
     * le chiavi disponibili. Se invece il messaggio inizia con {@code "@user..."} saranno inviate
     * solo le chiavi degi utenti desiderati.
     * <p>Il metodo si occupa in oltre di controllare che i nomi siano corretti/esistenti e
     * che lo stesso sia collegato al server.
     * <p>Se tutti i controlli sono stati passati positivamente
     * viene inviata una chiave si utilizza la sintassi {@code [pk][nome]-[key]} e,
     * una volta inviate tutte quante, il server manda "@end" in modo tale che il client
     * sappia che ha tutte le chiavi necessarie.
     * @param line
     * Messaggio inviato dal client contenenti i nomi.
     */
    public void sendKeys(String line){
        String[] names = null;
        try {
            if(line.startsWith("/all"))
                names = um.namesToArray(String.valueOf(cont));
            else
                names = line.substring(1, line.indexOf(" ")).split("@");
        }catch (Exception e) {out.println("Wrong syntax for sending messages");out.println("@end");return;}
        for(String name : names){
            if(name.equals(um.getName(String.valueOf(cont))))
                out.println("<Server> Avoid writing to yourself, not funny :L");
            else if(um.isConnected(name)){
                out.println("pk" + name + "-" +  um.getPk(name));
                System.out.println("log<" + um.getName(String.valueOf(cont)) + ">RECIEVES " + name + " KEY");
            }
            else
                out.println("<Server>" + name + " not found.");
        }
        out.println("@end");
    }

    /**
     * Metodo che si occupa di stampare a schermo la descrizione del comando
     * specifico contenuto dentro {@link ServerThread#commandList commandList}.
     * @param line
     * Messaggio contente il nome del comando.
     */
    public void command(String line){
        line = line.substring(line.indexOf(" ") + 1);
        out.println((commandList.get(line) != null) ? commandList.get(line) : "Command not found.");
    }

    /**
     * Il metodo si occupa di settare il colore deciso dall'utente.
     * <p>Il messaggio ricevuto e' del tipo {@code [/set][color]["colore"]}.
     * Il codice del colore viene preso dalla {@link ServerThread#colors mappa} e, se
     * esiste, viene impostato nel oggetto condiviso {@link ServerThread#um UsersManager}.
     * @param line
     * Messaggio contente il colore.
     */
    public void setColor(String line){
        String[] s = line.split(" ");
        try {
            if(s[2].equals("rgb")){
                String[] rgbCode = line.substring(line.indexOf("rgb")+4).split(" ");
                new ArrayList<String>(Arrays.asList(rgbCode)).forEach(i ->{
                    if(!Pattern.matches("([01]?[0-9][0-9]?|2[0-4][0-9]|25[0-5])", i))
                        throw new IllegalArgumentException("wrong syntax: use [/set][color][rgb][0-255][0-255][0-255]");
                });
                String prefix = "\033[38;2;" + rgbCode[0] + ";"+rgbCode[1]+";"+rgbCode[2]+"m";
                um.toObject(String.valueOf(cont)).setColor(prefix);
                out.println("Now your color is " + prefix + "this\033[0m");
                return;
            }
        } catch (Exception e) {out.println(e.getMessage());}
        if(colors.containsKey(s[2])){
            um.toObject(String.valueOf(cont)).setColor(colors.get(s[2]));
            out.println("Now your color is " + s[2].toUpperCase());
        }else{
            out.println("This color isnt avaible, use command /help to see which color use");
        }
    }

    /**
     * IL metodo si occupa di inviare un messaggio a tutti i client collegati escluso chi
     * ha effettuato il metodo.
     * <p>Viene in oltre utilizzato dal server per inviare messaggi di sistema.
     * @param sender
     * Nome di chi manda i messaggio
     * @param msg
     * Messaggio
     */
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

    /**
     * Metodo che invia al client che ne ha richiesto l'esecuzione una lista
     * di tutti gli utenti collegati.
     * <p>In caso in cui sia presente solamente un client il metodo {@link UserManager#toArray toArray}
     * lanciera' un'eccezzione e restituisce un {@code null}. Allora il metodo entrera' in un catch che
     * inviera' un messaggio all'utente dicendo che e' l'unico connesso. 
     */
    public void getListUser(){
        try{
            for(UserData dt : um.toArray(String.valueOf(cont)))
                out.println("<Server>" + dt.getName() + " is online");
        }catch (Exception e) {out.println("You are the only one connected.");}
    }

    public void sendMessage(String line) throws InterruptedException, IOException{
        String[] ss = line.split("-");
        String msg = ss[2];
        msg = "@dec-" + msg; 
        DateTimeFormatter dtf =  DateTimeFormatter.ofPattern("HH:mm");
        mailBox.writeMessage(
            new Message(
                um.getName(String.valueOf(cont)), 
                msg,dtf.format(LocalDateTime.now()), 
                um.toObject(String.valueOf(cont)).getColor()
            ), 
            ss[1]
        );
    }


    /**
     * EASTER EGG. 
     * <p>Una volta ricevuto il messaggio [divideandconquer][numero] il metodo
     * dira' il numero di cifre del numero.
     * <p>In oltre, il {@code divideandconquer}, e' il metodo piu'
     * veloce che esista per contare il numero di cifre.
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

    /**
     * Consente di stampare tutte le combinazioni
     * dei colori RGB -> {@code 255*255*255} combinazioni.
     * <p>Non e' molto ma e' un lavoro onesto.
     */
    public void colorPicker(){
        for(int r = 0; r < 255; r++){
            for(int g = 0; g < 255; g++){
                for(int b = 0; b <255; b++){
                    String prefix = "\033[38;2;" + r + ";"+g+";"+b+"m";
                    out.println(prefix + "rgb(" +r +"," + g + "," + b + ")");
                }
            }
        }
    }

}
