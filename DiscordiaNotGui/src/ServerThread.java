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
    public static ArrayList<UserData> users = new ArrayList<UserData>();

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
            out.println("//" + rsa.getD());
            out.println("//" + rsa.getN());
            out.println("Do you have already an account?");
            String password;
            if(in.readLine().equals("y")){
                do{
                    userName = in.readLine();
                    password = in.readLine();
                }while(!getData(password));
            }else{
                createNewAccount();
            }

            synchronized(this){
                sendUserName();
                getRoomsFromFile();
            }
            users.add(new UserData(userName, "nowhere"));
            setRoom(in.readLine());
            String line;
            synchronized(this){
            while ((line = in.readLine()) != null) {
                if(!line.startsWith("/"))
                sendMessage(line);
                if(line.startsWith("/changeRoom")){
                    sendMessage("has left the chat");
                    this.chattingInto = "nowhere";
                    getRoomsFromFile();
                    setRoom(in.readLine());
                }
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

    public boolean getData(String password) throws IOException{
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
            if(userName.equals(data[1]) && password.equals(data[2])){
                out.println(userName + " welcome back");
                return true;
            }
        }
        out.println(userName + " doesn't exist");
        return false;
    }

    public void createNewAccount() throws IOException{
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
        userName = usr;
        out.println("swemps");
    }

    public void getRoomsFromFile() throws IOException{
        File roomFile = new File("./FileServer/RoomSetting/roomsList.txt");
        BufferedReader br = new BufferedReader(new FileReader(roomFile));
        String line;
        String[] data = new String[2];
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

    public void setRoom(String s){
        for(UserData arr : users){
            if(arr.userName.equals(this.userName))
                arr.chattingInto = s; 
        }
        chattingInto = s;
        sendMessage("has joined the chat");
    }

    public void sendMessage(String msg){
        msg = "<" + userName + ">" + msg;
        for(ServerThread user : clients){
            if(user.chattingInto.equals(this.chattingInto) && !user.userName.equals(this.userName))
            user.out.println(msg);
        }
    }


}


