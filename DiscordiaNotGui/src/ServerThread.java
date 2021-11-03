import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServerThread implements Runnable {
    public Socket socket;
    public PrintWriter out;
    private BufferedReader in;
    private ArrayList<ServerThread> clients;
    private static File dataFile;

    public String userName;
    public String chattingInto = "nowhere";
    public Encryptor rsa = new Encryptor();

    public static ArrayList<String> rooms = new ArrayList<String>();
    public UserData data = new UserData("bot", "nowhere");

    ServerThread(Socket socket, ArrayList<ServerThread> clients) throws IOException{
        this.socket = socket;
        this.clients = clients;
        dataFile = new File("./FileServer/DataUser/data.txt");
        if(!dataFile.exists()){
            dataFile.createNewFile();
        }
    }

    public void run() {
        try{
            out = new PrintWriter(socket.getOutputStream(), true);
        	in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //send the privateKey to the client and he will storage it
            out.println("//" + rsa.getD());
            out.println("//" + rsa.getN());

            //the first phase, the user has to login or signin
            out.println("Do you have already an account?");
            String usr = "w", password;
            if(in.readLine().equals("y")){
                do{
                    out.println("username:");
                    usr = in.readLine();
                    out.println("password");
                    password = in.readLine();
                }while(!getData(usr, password));
                //set the first values: the username
                data.setUserName(usr);
            }else{
                createNewAccount();
            } 
            //second phase, now the user has logged into is profile and has to choose the room where chat into 
            sendRoomsToUser();
            setRoom();
            String line;
            //and finally he can chat
            synchronized(this){
            while ((line = in.readLine()) != null) {
                if(!line.startsWith("/"))
                sendMessage(line);
                if(line.startsWith("/changeRoom")){
                    sendMessage("has left the chat");
                    this.chattingInto = "nowhere";
                    sendRoomsToUser();
                    setRoom();
                }
                if(line.startsWith("/help"))
                  listCommands();
            }
        }


        }catch(IOException e){
            System.out.println(userName + " has left the game");
            clients.remove(Integer.parseInt(Thread.currentThread().getName()));
            String msg = (userName + " has left the game\nThere are " + clients.size() + "player left.");

        }
    }


    public void sendUserName() throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(dataFile));
        String line;
        String[] data = new String[3];
        int cont = 0;
        while((line = br.readLine()) != null){
            cont = 0;
            for(String s : line.split("/")){
                data[cont] = s;
                cont++;
            }
            out.println(data[1]);
        }
        out.println("end");
    }

    public boolean getData(String usr, String password) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(dataFile));
        String line;
        String[] data = new String[3];
        int cont = 0;
        while((line = br.readLine()) != null){
            cont = 0;
            for(String s : line.split("/")){
                data[cont] = s;
                cont++;
            }
            if(usr.equals(data[1]) && password.equals(data[2])){
                out.println(usr + " welcome back");
                return true;
            }
        }
        out.println(usr + " doesn't exist");
        return false;
    }

    public synchronized void createNewAccount() throws IOException{
        String email, usr, pass;
        email = in.readLine();
        usr = in.readLine();
        pass = in.readLine();

        BufferedWriter fw= new BufferedWriter(new FileWriter(dataFile, true));
        String data = (email + "/" + usr + "/" + pass);
        fw.write(data);
        fw.newLine();
        fw.flush();
        fw.close();
        out.println("logged into your account correctly");
        this.data.setUserName(usr);
    }

    public void sendRoomsToUser() throws IOException{
        File roomFile = new File("./FileServer/RoomSetting/roomsList.txt");
        BufferedReader br = new BufferedReader(new FileReader(roomFile));
        String line;
        String[] data = new String[3];
        int cont = 0;
        while((line = br.readLine()) != null){
            cont = 0;
            for(String s : line.split("-")){
                data[cont] = s;
                cont++;
            }
            out.println("Room: " + data[0]);
            rooms.add(line);
        }
    }

    public void setRoom() throws IOException{
        File roomFile = new File("./FileServer/RoomSetting/roomsList.txt");
        BufferedReader br = new BufferedReader(new FileReader(roomFile));
        String line;
        String[] data = new String[3];
        int cont = 0;
        while(true) {
            String s = in.readLine();
            while((line = br.readLine()) != null){
                cont = 0;
                for(String ss : line.split("-")){
                    data[cont] = ss;
                    cont++;
                }
                System.out.println("s: " + s + " data[0]: " + data[0]);
                if(s.equalsIgnoreCase(data[0])){
                this.data.setRoom(data[0]);
                out.println("welcome in " + this.data.getName() + ", " + data[2]);
                out.println("There are " + getNumberUserInRoom(data[0]) + " users online");
                sendMessage("has joined the room");
                return;
                }
            }
            out.println("room doesn't exist");
            sendRoomsToUser();
        }
    }

    public void sendMessage(String msg){
        msg = "<" + this.data.getName() + ">" + msg;
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


        for(ServerThread user : clients){
            if(user.data.getRoom().equalsIgnoreCase(this.data.getRoom()) && !user.data.getName().equals(this.data.getName()))
            user.out.println(msg);
        }
    }

    public void listCommands(){
        out.println("[/changeRoom] --> You will be disconnected from your actual room");
        out.println("[/op] --> You will request to become an operator of Discordia");
        out.println("[/changeUserName] --> You will change your userName");
        out.println("-----OP COMMANDS-----");
        out.println("[/ban @user] --> You will ban the user from the actual room");
        out.println("[/destroy @user] --> You will ban the user from every room :D");
        out.println("[/createNewRoom] --> You will create a new room");
        
    }

    public int getNumberUserInRoom(String roomName){
        int cont = 0;
        for(ServerThread user : clients){
            if(user.data.chattingInto.equalsIgnoreCase(roomName))
            cont++;
        }
        return cont;
    }

}


