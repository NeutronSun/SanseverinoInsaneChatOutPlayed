import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Classe sincronizzato 
 * 
 * @author Sanseverino Lorenzo
 * @version 1
 * @since 2021-11-29 (aaaa-mm-gg)
 */
public class FileManager {
    File userFile;
    //BufferedReader readerUser;
    BufferedWriter writeUser;

    public FileManager() throws IOException{
        userFile = new File("./ServerFiles/Files/UsersData/data.txt");
        //readerUser = new BufferedReader(new FileReader(userFile));
        writeUser = new BufferedWriter(new FileWriter(userFile, true));
    }


    public boolean checkUser(String name) {
        String line;
        String[] data = new String[3];
        int cont = 0;
        try(BufferedReader readerUser = new BufferedReader(new FileReader(userFile));){
            while((line = readerUser.readLine()) != null){
                cont = 0;
                for(String ss : line.split("/")){
                    data[cont] = ss;
                    cont++;
                }
                System.out.println(name + ": " + data[0]);
                if(name.equals(data[0])){
                    return false;
                }
                
            }
        return true;
        }catch(IOException e){}
        return true;
    }


    public boolean checkPassword(String name, String password){
        String line;
        String[] data = new String[3];
        int cont = 0;
        try(BufferedReader readerUser = new BufferedReader(new FileReader(userFile));){
            while((line = readerUser.readLine()) != null){
                cont = 0;
                for(String ss : line.split("/")){
                    data[cont] = ss;
                    cont++;
                }
                if(name.equals(data[0]) && password.equals(data[1])){
                    return true;
                }
                
            }
        return false;
        }catch(IOException e){}
        return true;
    }

    public synchronized void addUser(String name, String password, String key) throws IOException{
        String data = (name + "/" + password + "/" + key);
        writeUser.write(data);
        writeUser.newLine();
        writeUser.flush();
    }
}
