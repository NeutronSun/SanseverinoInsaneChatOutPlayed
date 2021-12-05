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
 * Classe sincronizzato 
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a> 
 * @author <a href="https://github.com/Leon412">Leon412</a> 
 * @version 1
 * @since 2021-11-29 (aaaa-mm-gg)
 */
public class FileManager {
    File userFile;
    //BufferedReader readerUser;
    BufferedWriter writeUser;
    private int contReaders;
    private boolean canRead;
    private String salt;
    Random rnd;

    public FileManager() throws IOException{
        rnd = new Random();
        String pathF = "./ServerFiles/Files";
        String pathUD = "./ServerFiles/Files/UsersData";
        String pathFile = "./ServerFiles/Files/UsersData/data.txt";
        File directory = new File(pathF);
        File directoryUD = new File(pathUD);

        if(!directory.exists()){
            directory.mkdir();
        }
        if(!directoryUD.exists()){
            directoryUD.mkdir();
        }
        userFile = new File("./ServerFiles/Files/UsersData/data.txt");
        if(!userFile.exists()){
            userFile.createNewFile();
        }
        //readerUser = new BufferedReader(new FileReader(userFile));
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
     * true se l'utente e' presente nel file
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
                    if(password.equals(data[1]))
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
    private String getSalt() {
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
