import java.util.*;

/**
 * Classe che si occupa di gestire e smistare tutti i messaggi. I vari
 * sotto-thread di {@link ServerThread}({@link TheWaiter}) staranno sempre in attesa di una
 * possibile modifica della mappa {@link MessageBox#messageMap messageMap} e, in tal caso, 
 * leggeranno il messaggio.
 * <p>Allo stesso modo ogni volta che un client vuole inviare un messaggio sara' il {@link ServerThread} 
 * a scrivere il messaggio tramite il metodo {@link MessageBox#writeMessage writeMessage()}.
 * 
 * 
 * @author Sunyx
 */
public class MessageBox {
    /**
     * Segna il numero di {@link TheWaiter} in lettura. Se >0 non è possibile scrivere in {@link MessageBox#messageMap messageMap}.
     */
    private int contReader;
    /**
     * Mappa contenente il messaggio e, come chiave, il ricevente.
     * 
     * @param <String>
     * String key, il ricevente(es "client 1")
     * @param <ArrayList<Message>>
     * il messaggio che ricevera'
     */
    private  HashMap<String, ArrayList<Message>> messageMap;

    /**
     * Defaul constructor
     */
    public MessageBox(){
        contReader = 0;
        messageMap = new HashMap<String, ArrayList<Message>>();
    }

    /**
     * Metodo sincronizzato che, ogni volta che un utente si registra/esegue il login nella mappa viene
     * aggiunto un indice con il suo nome
     * @param name
     * nome del ricevente a cui fare riferimento
     */

    public synchronized void addUser(String name){
        messageMap.put(name, new ArrayList<Message>());
    }

    /**
     * Metodo sincronizzato che consente la scrittura da parte di {@link ServerThread} di un singolo messaggio alla volta
     * @param msg
     * messaggio di tipo {@link Message}
     * @param recivier
     * ricevente del messaggio, chiave/indice della mappa {@link MessageBox#messageMap messageMap}
     * @throws InterruptedException
     * non deve succedere e.e
     */
    public synchronized void writeMessage(Message msg, String recivier) throws InterruptedException{
        while(contReader > 0){wait();}
        messageMap.get(recivier).add(msg);
        notifyAll();
    }
    
    /**
     * Metodo che consente la lettura dei messaggi da parte del sottoThread {@link TheWaiter} di un singolo utente.
     * Il metodo consente la lettura simultanea da parte di piu' thread ma, se i thread leggono gli altri non possono scrivere.
     * In caso in cui l'utente non abbia messaggi {@link TheWaiter il thread} andra' in attesa.
     * @param receiver
     * Stringa contente il nome del ricevente di cui leggere i messaggi
     * @return
     * restituisce un array di {@link Message messaggi} che l'utente ha ricevuto
     * @throws InterruptedException
     * non succede e.e
     */
    public Message[] getMessage(String receiver) throws InterruptedException{
        synchronized(this){
            while(messageMap.get(receiver).size() == 0){wait();}
        }
        
        synchronized(this){
            contReader++;
        }
        
        Message[] message = new Message[messageMap.get(receiver).size()];
        message = messageMap.get(receiver).toArray(message);
        synchronized(this){
            contReader--;
            messageMap.remove(receiver);
            messageMap.put(receiver, new ArrayList<Message>());
            notifyAll();
        }
        return message;
    }
    
}