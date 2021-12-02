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
    private boolean canRead;
    private int contReaders;

    /**
     * costruttore di defualt
     */
    public KeySorter(){
        numberKeys = 0;
        keys = new HashMap<String, String>();
        ready = false;
        actualKeys = 0;
        canRead = true;
        contReaders = 0;
    }

    public synchronized void setNKeys(int numberKeys){
        canRead = false;
        this.numberKeys = numberKeys;
        canRead = false;
    }

    /**
     * aggiunge la {@code chiave pubblica} nella {@link KeySorter#keys mappa}
     * @param user
     * nome del client
     * @param key
     * chiave del client
     */
    public synchronized void setKey(String user, String key){
        canRead = false;
        keys.put(user, key);
        actualKeys++;
        if(numberKeys == actualKeys){
            ready = true;
            canRead = true;
            notifyAll();
        }
        canRead = true;
    }

    public synchronized void wrongName(String user){
        canRead = false;
        numberKeys--;
        keys.remove(user);
        if(numberKeys == actualKeys){
            ready = true;
            canRead = true;
        }
        canRead = true;
        notifyAll();
    }

    public String[] getNames(){
        try {
            synchronized(this){
                while(!ready && !canRead){wait();}
                return keys.keySet().toArray(new String[keys.size()]);
            }
        } catch (Exception e) {
            return null;
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
                while(!ready && !canRead){wait();}
                String key = keys.get(name);
                System.out.println(name + " " + keys.size());
                keys.remove(name);
                System.out.println(name + " " + keys.size());
                if(keys.size() == 0){
                    ready = false;
                    actualKeys = 0;
                }
                return key;
            }
        } catch (Exception e) {
            return null;
        }
    }

}
