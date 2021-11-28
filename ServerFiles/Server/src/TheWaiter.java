import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Classe che si occupa di "aspettare" una qualsiasi modifica dell'oggetto {@link TheWaiter#mailBox mailbox} 
 * e contattare poi il client corrispettivo
 * 
 * @author
 * sunyx 
 */
public class TheWaiter implements Runnable {
    /**
     * socket connesso con il corrispettivo client
     */
    private Socket socket;
    /**
     * oggeto printwriter che si occupa di scrivere nel buffer del client, tramite la connessione
     * con il socket
     */
    private PrintWriter out;
    /**
     * Oggetto di tipo {@link UserData} contenente tutti i dati del client 
     */
    private UserData data;
    /**
     * Oggetto di tipo {@link MessageBox} che si occupa solo di richiamare il metodo {@link MessageBox#getMessage(String) getMessage(String)}
     */
    private MessageBox mailBox;

    /**
     * Defaul constructor
     * @param s
     * socket 
     * @param mailBox
     * {@link MessageBox} condiviso dal thread principale
     * @param data
     * {@link UserData}
     * @throws IOException
     */
    public TheWaiter(Socket s,  MessageBox mailBox, UserData data)throws IOException{
        socket = s;
        this.data = data;
        this.mailBox = mailBox;
        out = new PrintWriter(socket.getOutputStream(), true);
    }
    /**
     * Metodo standard dell'interfaccia runnable
     */
    public void run() {
        while(true){
            try {
                System.out.println(data.getName());
                Message[] msg = mailBox.getMessage(data.getName());
                for(Message m : msg){
                    out.println(m.getPerfectMessage());
                }
            }catch(InterruptedException e){e.printStackTrace();}
        }
    }


}
