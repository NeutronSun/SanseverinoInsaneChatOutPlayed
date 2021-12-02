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
             * --------------------------
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
             * -----------------------------
             * settings data user
             */
            um.addUser(String.valueOf(cont), new UserData(n,p,"0000"));
            um.setOnline(String.valueOf(cont));
            mailBox.addUser(um.getName(String.valueOf(cont)));
            System.out.println("log<" + um.getName(String.valueOf(cont)) + "> LOGGED CORRECTLY.");
            new Thread(new TheWaiter(socket,mailBox,um.toObject(String.valueOf(cont)))).start();
            /**
             * ----------------------------
             * now the user can send messages or digits commands
             */
            String line = "";
            out.println("Welcome in, digits /help for more infomation");
            out.println("/ready");
            notifyAll("server", (um.getName(String.valueOf(cont)) + " is now online"));
            while (!(line = in.readLine()).equals("quit")) {
                if(line.equals("/help"))
                    getListCommand();
                else if(line.equals("/list"))
                    getListUser();
                else if(line.startsWith("/all"))
                    sendKeys(line);
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
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
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


    public void getListCommand(){
        out.println("LIST     get a full list of online users");
        out.println("OFFLINE      set your current state to offline");
        out.println("ONLINE       set your current state to online");
        out.println("@user      send a message to @user");
        out.println("@user@user1@user2    send a message to more users");
    }

    public void notifyAll(String sender, String msg) throws InterruptedException{
        //msg = checkEmoji(msg);
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
        String[] ss = line.split("-");
        String msg = ss[2];
        System.out.println("arr: " + ss);
        msg = "@dec-" + msg; 
        DateTimeFormatter dtf =  DateTimeFormatter.ofPattern("HH:mm");
        mailBox.writeMessage(new Message(um.getName(String.valueOf(cont)), msg,dtf.format(LocalDateTime.now())), ss[1]);
        //msg = msg.replaceAll("<3", new StringBuilder().appendCodePoint(0x1F62C).toString());
        /*
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
        */
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
