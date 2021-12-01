import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class ServerThread implements Runnable{
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private MessageBox mailBox;
    private UserManager um;
    private FileManager fileManager;
    private int cont;

    public ServerThread(Socket sck, MessageBox mailBox, UserManager um, FileManager fm, int contT){
        socket = sck;
        this.mailBox = mailBox;
        this.um = um;
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
            String n,p;
            boolean check = true;
            out.println("Do you have already an account?");
            if(in.readLine().equalsIgnoreCase("yes")) {
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
            }else{
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
                    if(!fileManager.checkUser(n)){
                        out.println(n + " already exists as user");
                        check = false;
                    }else{fileManager.addUser(n, p, "000");}
                }while(!check);
            }
            /**
             * settings data user
             */
            um.addUser(String.valueOf(cont), new UserData(n,p,"0000"));
            um.setOnline(String.valueOf(cont));
            mailBox.addUser(um.getName(String.valueOf(cont)));
            System.out.println("log<" + um.getName(String.valueOf(cont)) + "> LOGGED CORRECTLY.");
            new Thread(new TheWaiter(socket,mailBox,um.toObject(String.valueOf(cont)))).start();
            String line = "";
            /**
             * now the user can send messages or digits commands
             */
            out.println("Welcome in, digits /help for more infomation");
            notifyAll("server", (um.getName(String.valueOf(cont)) + " is now online"));
            while ((line = in.readLine()) != null) {
                if(line.equals("/help"))
                    getListCommand();
                else if(line.equals("/list"))
                    getListUser();
                else if(line.startsWith("/all"))
                    notifyAll(um.getName(String.valueOf(cont)), line.substring(4));
                else if(line.startsWith("divideandconquer"))
                    divideandconquer(line);
                else if(line.startsWith("be fast pls"))
                    theflash(line);
                else if(line.startsWith("@"))
                    sendMessage(line);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void getListCommand(){
        out.println("LIST     get a full list of online users");
        out.println("OFFLINE      set your current state to offline");
        out.println("ONLINE       set your current state to online");
        out.println("@user      send a message to @user");
        out.println("@user@user1@user2    send a message to more users");
    }

    public void notifyAll(String sender, String msg) throws InterruptedException{
        msg = checkEmoji(msg);
        for(UserData dt : um.toArray()){
            if(!dt.getName().equals("@") && !dt.getName().equals(um.getName(String.valueOf(cont)))){
                DateTimeFormatter dtf =  DateTimeFormatter.ofPattern("HH:mm");
                mailBox.writeMessage(new Message(sender, msg, dtf.format(LocalDateTime.now())), dt.getName());
            }
        }
        System.out.println("log<" + um.getName(String.valueOf(cont)) + "> SENT CORRECTLY THE MESSAGE.");
    }

    public void getListUser() throws InterruptedException{
        for(UserData dt : um.toArray()){
            if(!dt.getName().equals("@") && dt.isOnline() && !dt.getName().equals(um.getName(String.valueOf(cont)))){
                String msg = dt.getName() + " is online";
                DateTimeFormatter dtf =  DateTimeFormatter.ofPattern("HH:mm");
                mailBox.writeMessage(new Message("server", msg, dtf.format(LocalDateTime.now())), dt.getName());
            }
        }
    }

    public void sendMessage(String line) throws InterruptedException, IOException{
        String[] names = line.substring(1, line.indexOf(" ")).split("@");
        String msg = line.substring(line.indexOf(" ") + 1);
        msg = checkEmoji(msg);
        //msg = msg.replaceAll("<3", new StringBuilder().appendCodePoint(0x1F62C).toString());
        for(String name : names){
            if(um.isConnected(name)){
                DateTimeFormatter dtf =  DateTimeFormatter.ofPattern("HH:mm");
                mailBox.writeMessage(new Message(um.getName(String.valueOf(cont)), msg,dtf.format(LocalDateTime.now())), name);
                System.out.println("log<" + um.getName(String.valueOf(cont)) + "> SEND CORRECTLY THE MESSAGE TO " + name);
            }else{
                out.println(name + " not exists");
                System.out.println("log<" + um.getName(String.valueOf(cont)) + "> SEND INCORRECTLY THE MESSAGE TO " + name + "[NOT EXISTS]");
            }
        }
    }


    public String checkEmoji(String msg){
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

    public static float invSqrt(float x) {
        float xhalf = 0.5f * x;
        int i = Float.floatToIntBits(x);
        i = 0x5f3759df - (i >> 1);
        x = Float.intBitsToFloat(i);
        x *= (1.5f - xhalf * x * x);
        return x;
    }

}
