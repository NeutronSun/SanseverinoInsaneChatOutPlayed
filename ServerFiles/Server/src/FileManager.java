import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {
    File userFile;
    BufferedReader readerUser;
    BufferedWriter writeUser;

    public FileManager() throws IOException{
        userFile = new File("./ServerFiles/Files/UsersData/data.txt");
        readerUser = new BufferedReader(new FileReader(userFile));
        writeUser = new BufferedWriter(new FileWriter(userFile, true));
    }


    public boolean checkUser(String name) throws IOException{
        String line;
        String[] data = new String[3];
        int cont = 0;
        while((line = readerUser.readLine()) != null){
            cont = 0;
            for(String ss : line.split("/")){
                data[cont] = ss;
                cont++;
            }
            System.out.println(name + ": " + data[0]);
            if(name.equals(data[0]))
                return false;
            
        }
        return true;
    }

    public synchronized void addUser(String name, String password, String key) throws IOException{
        String data = (name + "/" + password + "/" + key);
        writeUser.write(data);
        writeUser.newLine();
        writeUser.flush();
    }
}
