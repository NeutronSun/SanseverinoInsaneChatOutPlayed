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
    private int contReaders;
    private boolean canRead;

    public FileManager() throws IOException{
        userFile = new File("./ServerFiles/Files/UsersData/data.txt");
        //readerUser = new BufferedReader(new FileReader(userFile));
        writeUser = new BufferedWriter(new FileWriter(userFile, true));
        contReaders = 0;
        canRead = true;
    }

    /**
     * Metodo che controlla che nel file {@code data.txt} sia presente il nome
     * dell'utente richiesto
     * @param name
     * nome da controllare
     * @return
     * true se l'utente e' presente nel file
     */
    public boolean checkUser(String name) {
        String line;
        String[] data = new String[3];
        int cont = 0;
        try(BufferedReader readerUser = new BufferedReader(new FileReader(userFile));){
            synchronized(this){
                while(!canRead){wait();}
                contReaders++;
            }
            while((line = readerUser.readLine()) != null){
                cont = 0;
                for(String ss : line.split("/")){
                    data[cont] = ss;
                    cont++;
                }
                System.out.println(name + ": " + data[0]);
                if(name.equals(data[0])){
                    synchronized(this){
                        contReaders--;
                        notifyAll();
                    }
                    return false;
                }
                
            }
        synchronized(this){
            contReaders--;
            notifyAll();
        }
        return true;
        }catch(IOException | InterruptedException e){}
        return true;
    }

    /**
     * Metodo che controlla che nel file {@code data.txt} l'utente selezionato corrisponda la password inserita
     * @param name
     * nome dell'utente
     * @param password
     * password da controllare
     * @return
     * true se nel file le password corrispondono
     */
    public boolean checkPassword(String name, String password){
        String line;
        String[] data = new String[3];
        int cont = 0;
        try(BufferedReader readerUser = new BufferedReader(new FileReader(userFile));){
            synchronized(this){
                while(!canRead){wait();}
                contReaders++;
            }
            while((line = readerUser.readLine()) != null){
                cont = 0;
                for(String ss : line.split("/")){
                    data[cont] = ss;
                    cont++;
                }
                if(name.equals(data[0]) && password.equals(data[1])){
                    synchronized(this){
                        contReaders--;
                        notifyAll();
                    }
                    return true;
                }
                
            }
        synchronized(this){
            contReaders--;
            notifyAll();
        }
        return false;
        }catch(IOException | InterruptedException e){}
        return true;
    }

    /**
     * Metodo che consente la scrittura dei dati di un nuovo utente nel file
     * @param name
     * nome utente
     * @param password
     * password dell'utente
     * @param key
     * chiave pubblica dell'utente
     * @throws IOException
     */
    public synchronized void addUser(String name, String password, String key){
        canRead = false;
        String data = (name + "/" + password + "/" + key);
        try {
            writeUser.write(data);
            writeUser.newLine();
            writeUser.flush();
            canRead = true;
            notifyAll();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
