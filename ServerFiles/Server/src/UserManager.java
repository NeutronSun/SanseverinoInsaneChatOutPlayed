import java.util.HashMap;
/**
 * Classe che si occupa di gestire tutti i client collegati al server. 
 * I dati saranno memorizzati in una mappa(con chiave il numero del thread istanziato dalla classe {@link Server}) e come
 * valore un oggetto di tipo {@link UserData}.
 * @author Lorenzo Sanseverino
 * @version 1
 * @since 2021-11-30 (aaaa-mm-gg)
 */
public class UserManager {
    /**
     * numero di thread attualmente in lettura
     */
    private int contReader;
    /**
     * flag che indica se c'e' un thread che sta modificando l'attributo {@link UserManager#users users}
     */
    private boolean canRead;
    /**
     * Mappa contenente tutti i dati dei client collegati.
     * Si utilizza una mappa e non un arraylist in quanto il thread 0(es) puo' loggare dopo il thread 1, 
     * quindi in un arraylist il thread 0 si troverebb in indice 1 e viceversa --> in una mappa cio' non ha nessuna
     * ripercussione.
     * @param <String>
     * chiave della mappa
     * @param <UserData>
     * oggetto di tipo {@link UserData}
     */
    private HashMap<String, UserData> users;

    /**
     * Costruttore default
     */
    public UserManager() {
        users = new HashMap<String, UserData>();
        contReader = 0;
    }

    /**
     * Metodo che aggiunge un utente alla mappa {@link UserManager#users users}
     * @param name
     * nome del utente --> 0,1,2...3
     * @param user
     * oggetto di tipo {@link UserData} contenente i dati dell'utente
     * @throws InterruptedException
     * @see 
     * {@link Server}
     */
    public synchronized void addUser(String name, UserData user) throws InterruptedException {
        while(contReader > 0) {wait();}
        canRead = false;
        users.put(name, user);
        canRead = true;
        notifyAll();
    }

    /**
     * Metodo che verifica che un utente sia effettivamente online
     * @param key 
     * nome del client, il nome effettivo --> sunyx, swamp...
     * @return
     * true se il client e' online
     */
    public boolean isConnected(String key){
        try {
            while(!canRead) {wait();}
            synchronized(this) {
                contReader++;
            }
            for(UserData dt : users.values()) {
                if(dt.getName().equals(key) && dt.isOnline()){
                    synchronized(this){
                        contReader--;
                        notifyAll();
                    }
                    return true;
                }
            }
            synchronized(this){
                contReader--;
                notifyAll();
            }
            return false;
        } catch (Exception e) {return false;}
    }

    public void setPk(String key, String pk){
        try {
            while(!canRead) {wait();}
            synchronized(this) {
                contReader++;
            }
            users.get(key).setPk(pk);
            synchronized(this){
                contReader--;
                notifyAll();
            }
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    public String getPk(String name) {
        try {
            while(!canRead) {wait();}
            synchronized(this) {
                contReader++;
            }
            for(UserData dt : users.values()) {
                if(dt.getName().equals(name)){
                    synchronized(this){
                        contReader--;
                        notifyAll();
                    }
                    return dt.getPk();
                }
            }
            synchronized(this){
                contReader--;
                notifyAll();
            }
            return null;
        } catch (Exception e) {return null;}
    }

    /**
     * Setta lo stato del client ad online
     * @param key
     * nome del client --> 0,1,2...3
     * @see
     * {@link UserData Userdata}
     */
    public void setOnline(String key){
        try {
            while(!canRead) {wait();}
            synchronized(this) {
                contReader++;
            }
            users.get(key).setOnline(true);
            synchronized(this){
                contReader--;
                notifyAll();
            }
        } catch (Exception e) {}
    }

    public String getName(String key){
        try {
            while(!canRead) {wait();}
                return users.get(key).getName();
            
        } catch (Exception e) {
            return null;
        }
    }

    public UserData[] toArray(){
        try {
            while(!canRead) {wait();}
            return users.values().toArray(new UserData[users.size()]);
        } catch (Exception e) {return null;}
        
    }

    public String[] namesToArray(String except){
        try {
            while(!canRead) {wait();}
            String[] names = new String[users.size()-1];
            int cont = 0;
            for(UserData user : toArray()){
                if(!user.getName().equals(getName(except)))
                names[cont] = user.getName();
                cont++;
            }
            return names;
        } catch (Exception e) {return null;}
    }

    public UserData toObject(String key){
        try {
            while(!canRead) {wait();}
            return users.get(key);
        } catch (Exception e) {return null;}
    }

}
