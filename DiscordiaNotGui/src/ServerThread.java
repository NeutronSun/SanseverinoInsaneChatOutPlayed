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
    public Encryptor rsa = new Encryptor();

    ServerThread(Socket socket, ArrayList<ServerThread> clients) throws IOException{
        this.socket = socket;
        this.clients = clients;
        dataFile = new File("data.txt");
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
            }
            String line;
            synchronized(this){
            while ((line = in.readLine()) != null) {
                
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

}


