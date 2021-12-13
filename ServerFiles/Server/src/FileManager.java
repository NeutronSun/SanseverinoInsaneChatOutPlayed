/**
 * Copyright (c) 22 Giugno anno 0, 2021, SafJNest and/or its affiliates. All rights reserved.
 * SAFJNEST PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * 
 * 
 * 
 * 
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;
import java.util.Base64.Encoder;

/**
 * 
 * Classe che si occupa di gestire l'accesso concorrente di tutte le istanze del {@link Server server}
 * sui vari file necessari al corretto funzionamento.
 * <p>Attualmente i vari file presenti sono:{@code data.txt} nella directory {@code Files/UsersData}.
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a> 
 * @author <a href="https://github.com/Leon412">Leon412</a> 
 * @version 1
 * @since 2021-11-29 (aaaa-mm-gg)
 */
public class FileManager {
    /**
     * Oggetto contente il file {@code data.txt} nella directory {@code Files/UsersData}.
     */
    private File userFile;
    /**
     * Oggetto che scrive nel file {@link FileManager#userFile dataUser.txt}
     */
    private BufferedWriter writeUser;
    /**
     * Contatore che indica il numero di thread lettori sull'istanza del file
     */
    private int contReaders;
    /**
     * Flag che blocca tutti i lettori in quanto gli scrittori stanno effettundo modifiche
     */
    private boolean canRead;


    public FileManager() throws IOException{
        String pathF, pathUD, pathFile;
        if(System.getProperty("os.name").startsWith("Windows")){
            System.out.println("OS: " + System.getProperty("os.name"));
            pathF = "./ServerFiles/Files";
            pathUD = "./ServerFiles/Files/UsersData";
            pathFile = "./ServerFiles/Files/UsersData/data.txt";
        }else{
            System.out.println("OS: " + System.getProperty("os.name"));
            pathF = "../../Files";
            pathUD = "../../Files/UsersData";
            pathFile = "../../Files/UsersData/data.txt";
        }
        
        File directory = new File(pathF);
        File directoryUD = new File(pathUD);

        if(!directory.exists())
            directory.mkdir();

        if(!directoryUD.exists())
            directoryUD.mkdir();

        userFile = new File(pathFile);
        
        if(!userFile.exists())
            userFile.createNewFile();
    
        writeUser = new BufferedWriter(new FileWriter(userFile, true));
        contReaders = 0;
        canRead = true;
    }


    public boolean isEmpty() {
        try(
        BufferedReader br = new BufferedReader(new FileReader(userFile));
        ){
            synchronized(this){
                while(!canRead){wait();}
                contReaders++;
            }
            if (br.readLine() == null) {
                synchronized(this){
                    contReaders--;
                    notifyAll();
                }
                return true;
            }
            synchronized(this){
                contReaders--;
                notifyAll();
            }
            return false;
        }catch(Exception e){
            return false;
        }
        
    }


    /**
     * Metodo che controlla che nel file {@code data.txt} sia presente il nome
     * dell'utente richiesto
     * @param name
     * nome da controllare
     * @return
     * true se l'utente non e' presente nel file
     */
    public boolean checkUser(String name) {
        String line;
        String[] data = new String[4];
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
     * Metodo che controlla che nel file {@code data.txt} l'utente selezionato corrisponda la password inserita.
     * <p>Essendo la password crittografata tramite l'algoritmo di hashign {@code sha512}, appena 
     * l'utente digita la password e la stessa viene crittografata insieme al {@code sale},
     * preso dal file, e nel caso corrispondano restituisce {@code true}.
     * @param name
     * nome dell'utente
     * @param password
     * password da controllare
     * @return
     * true se nel file le password corrispondono
     */
    public boolean checkPassword(String name, String password){
        String line;
        String[] data = new String[4];
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
                if(name.equals(data[0])){
                    System.out.println(data[3]);
                    password = get_SHA_512_SecurePassword(password, data[3]);
                    if(password.equals(data[1])){
                        synchronized(this){
                            contReaders--;
                            notifyAll();
                        }
                        return true;
                    }
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
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public synchronized void addUser(String name, String password, String key) throws NoSuchAlgorithmException{
        canRead = false;
        String salt = getSalt();
        password = get_SHA_512_SecurePassword(password,salt);
        String data = (name + "/" + password + "/" + key + "/" + salt);
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
    /**
     * cripta la password utilizzando {@link FileManager#gtetSalt il sale}
     * @param passwordToHash
     * password da criptare
     * @param salt
     * l'unico ed il solo sale
     * @return
     * password criptata
     */
    private String get_SHA_512_SecurePassword(String passwordToHash, String salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes());
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16)
                        .substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    /**
     * crea il sale
     * @author <a href="https://github.com/Leon412">Leon412</a> 
     * @return
     * il sale
     */
    public String getSalt() {
        try {
            SecureRandom random = new SecureRandom();
            byte bytes[] = new byte[20];
            random.nextBytes(bytes);
            Encoder encoder = Base64.getUrlEncoder().withoutPadding();
            String salt = encoder.encodeToString(bytes);
            System.out.println(salt);
            return salt;
        } catch (Exception e) {
            return null;
        }
        
    }
}
