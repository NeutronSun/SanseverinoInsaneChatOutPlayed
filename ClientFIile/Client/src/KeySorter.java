import java.util.HashMap;

/**
 * Classe che si occupa di smistare le chiavi pubbliche tra l'istanza del {@code client} {@link Client} ed il 
 * {@code ClientThreadReader} {@link ClientThreadReader}
 *  
 * @author Sanseverino Lorenzo
 * @version 1
 * @since 2021-12-2 (aaaa-mm-gg)
 */
public class KeySorter {
    /**
     * numero totali di chiavi che servono per criptare il messaggio
     */
    private int numberKeys;
    /**
     * numero di chiavi attualmente presenti nella {@code mappa} {@link KeySorter#keys}
    */
    private int actualKeys;
    /**
     * mappa contenente le chiavi pubbliche degli utenti
     * @param <String>
     * nome del client
     * @param <String>
     * chiave del client
     */
    private HashMap<String, String> keys;
    /**
     * variabile booleana che diventa true quando <p>{@link KeySorter#numberKeys} = {@link KeySorter#actualKeys}
     */
    private boolean ready;

    /**
     * costruttore di defualt
     */
    public KeySorter(){
        numberKeys = 0;
        keys = new HashMap<String, String>();
        ready = false;
        actualKeys = 0;
    }

    public synchronized void setNKeys(int numberKeys){
        this.numberKeys = numberKeys;
    }

    /**
     * aggiunge la {@code chiave pubblica} nella {@link KeySorter#keys mappa}
     * @param user
     * nome del client
     * @param key
     * chiave del client
     */
    public synchronized void setKey(String user, String key){
        keys.put(user, key);
        actualKeys++;
        if(numberKeys == actualKeys){
            ready = true;
            notifyAll();
        }
    }

    /**
     * Restituisce la chiave del utente specificato
     * @param name
     * nome del client
     * @return
     * String
     */
    public String getKey(String name) {
        try {
            
            synchronized(this){
                while(!ready){wait();}
                return keys.get(name);
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Restituisce una mappa contentente tutte le chiavi richieste
     * @return
     * HashMap
     */
    public synchronized HashMap<String, String> getKeys() {
        try{
            while(!ready){wait();}
            return this.keys;
        }catch(InterruptedException e){return null;}
    }
}
