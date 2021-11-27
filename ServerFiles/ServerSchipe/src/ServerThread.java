import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerThread implements Runnable{
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private MessageBox mailBox;
    private ArrayList<UserData> users;
    private UserData user;
    private FileManager fileManager;
    private String username;

    public ServerThread(Socket sck, MessageBox mailBox, ArrayList<UserData> users, FileManager fm){
        socket = sck;
        this.mailBox = mailBox;
        this.users = users;
        fileManager = fm;
    }

    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            /**
             * input dati utente
             */
            out.println("Do you have already an account?");
            if(in.readLine().equalsIgnoreCase("yes")) {
                out.println("Enter UserName");
                String n = in.readLine();
                out.println("Enter Password");
                String p = in.readLine();
                this.username = n;
                user = new UserData(n, "000");
                //missing get key from the file
                if(fileManager.checkUser(n))
                System.out.println("no bro wait");
                if(AlreadyOnline(n)) {
                    System.out.println("faker");
                }
            }else{
                out.println("Enter UserName");
                String n = in.readLine();
                out.println("Enter Password");
                String p = in.readLine();
                fileManager.addUser(n, p, "0000");
                user = new UserData(n, "000");
            }
            mailBox.addUser(username);
            new Thread(new TheWaiter(socket,mailBox,user)).start();
            String line = "";

            out.println("Welcome in, digits /help for more infomation");
            while ((line = in.readLine()) != null) {
                sendMessage(line);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean AlreadyOnline(String name) {
        for(UserData usr : users){
            if(usr.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

    public void sendMessage(String line) throws InterruptedException{
        String[] names = line.substring(0, line.indexOf(":")).split("/");
        String msg = line.substring(line.indexOf(":") + 1);
        for(String name : names){
            mailBox.writeMessage(new Message(this.username,msg), name);
        }
    }
}
