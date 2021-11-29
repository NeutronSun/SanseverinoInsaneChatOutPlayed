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
    private FileManager fileManager;
    private String username;
    private static int contT = -1;
    private int cont;

    {contT++;}

    public ServerThread(Socket sck, MessageBox mailBox, ArrayList<UserData> users, FileManager fm){
        socket = sck;
        this.mailBox = mailBox;
        this.users = users;
        fileManager = fm;
        cont = contT;
        System.out.println(cont);
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
                users.add(new UserData(n,p,"0000"));
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
                users.add(new UserData(n,p,"0000"));
            }
            /**
             * settings data user
             */
            System.out.println(cont);
            System.out.println(users.size());
            users.get(cont).setOnline(true);
            mailBox.addUser(username);
            new Thread(new TheWaiter(socket,mailBox,users.get(cont))).start();
            String line = "";
            /**
             * now the user can send messages or digits commands
             */
            out.println("Welcome in, digits /help for more infomation");
            while ((line = in.readLine()) != null) {
                if(line.equals("/help"))
                    getListCommand();
                if(line.equals("/list"));
                getListUser();
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

    public void getListCommand(){
        out.println("LIST     get a full list of online users");
        out.println("OFFLINE      set your current state to offline");
        out.println("ONLINE       set your current state to online");
        out.println("@user      send a message to @user");
        out.println("@user@user1@user2    send a message to more users");
    }

    public void getListUser() throws InterruptedException{
        for(UserData dt : users){
            if(dt.isOnline() && !dt.getName().equals(users.get(cont).getName())){
                String msg = dt.getName() + " is online";
                mailBox.writeMessage(new Message("server", msg), dt.getName());
            }
        }
    }

    public void sendMessage(String line) throws InterruptedException{
        String[] names = line.substring(1, line.indexOf(":")).split("/");
        String msg = line.substring(line.indexOf(":") + 1);
        for(String name : names){
            System.out.println("name: " + name);
            mailBox.writeMessage(new Message(this.username,msg), name);
        }
    }
}
