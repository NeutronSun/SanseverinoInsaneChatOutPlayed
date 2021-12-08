/**
 * Copyright (c) 22 Giugno anno 0, 2021, SafJNest and/or its affiliates. All rights reserved.
 * SAFJNEST PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * 
 * 
 * 
 * 
 */

import java.util.*;

/**
 * Classe che si occupa di gestire e smistare tutti i messaggi. I vari
 * sotto-thread di {@link ServerThread}({@link TheWaiter}) staranno sempre in attesa di una
 * possibile modifica della mappa {@link MessageBox#messageMap messageMap} e, in tal caso, 
 * leggeranno il messaggio. La mappa memorizza i messaggi {@link Message} per gli utenti connessi
 * al sistema. La mappa è indicizzata con lo username del destinatario del messaggio.
 * <p>Allo stesso modo ogni volta che un client vuole inviare un messaggio sara' il {@link ServerThread} 
 * a salvare il messaggio tramite il metodo {@link MessageBox#writeMessage writeMessage()}.
 * 
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/XOShu4">XOShu4</a>
 * @since 2021-11-29 (aaaa-mm-gg)
 * @version 1
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
     * @param <<ArrayList<Message>>
     * il messaggio che ricevera'
     */
    private  HashMap<String, ArrayList<Message>> messageMap;
    /**
     * Flag che diventa {@code TRUE} o {@code FALSE} a seconda della disponibilita' della {@link MessageBox#messageMap mappa}
     */
    private boolean canRead;

    /**
     * Defaul constructor
     */
    public MessageBox(){
        contReader = 0;
        canRead = true;
        messageMap = new HashMap<String, ArrayList<Message>>();
    }

    /**
     * Metodo sincronizzato che, ogni volta che un utente si registra/esegue il login nella mappa viene
     * aggiunto un indice con il suo nome
     * @param name
     * nome del ricevente a cui fare riferimento
     */

    public synchronized void addUser(String name){
        try{
            while(contReader > 0){wait();}
            canRead = false;
            messageMap.put(name, new ArrayList<Message>());
            canRead = true;
            notifyAll();
        }catch(Exception e){e.getCause();}
    }

    /**
     * Metodo sincronizzato che consente la scrittura da parte di {@link ServerThread} di un singolo messaggio alla volta
     * @param msg
     * messaggio di tipo {@link Message}
     * @param recivier
     * ricevente del messaggio, chiave/indice della mappa {@link MessageBox#messageMap messageMap}
     */
    public synchronized void writeMessage(Message msg, String recivier){
        try{
            while(contReader > 0){wait();}
            canRead = false;
            messageMap.get(recivier).add(msg);
            canRead = true;
            notifyAll();
        }catch(Exception e){e.printStackTrace();}
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
            while(messageMap.get(receiver).size() == 0 && !canRead){wait();}
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
