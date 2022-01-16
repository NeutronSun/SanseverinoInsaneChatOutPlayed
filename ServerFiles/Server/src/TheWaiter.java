/**
 * Copyright (c) 22 Giugno anno 0, 2021, SafJNest and/or its affiliates. All rights reserved.
 * SAFJNEST PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * 
 * 
 * 
 * 
 */

import java.io.*;
import java.net.*;

/**
 * Classe che si occupa di "aspettare" una qualsiasi modifica dell'oggetto
 * {@link TheWaiter#mailBox mailbox}
 * e contattare poi il client corrispettivo
 * <p> Non e' un cameriere ma {@code TheOneWhoWaits}.
 * <p> O MAGARI E' UN CAMERIERE CHE SERVE AI CLIENT(I) I MESSAGGI?!?!?!?!
 * 
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 */
public class TheWaiter implements Runnable {
    /**
     * socket connesso con il corrispettivo client
     */
    private Socket socket;
    /**
     * oggeto printwriter che si occupa di scrivere nel buffer del client, tramite
     * la connessione
     * con il socket
     */
    private PrintWriter out;
    /**
     * Oggetto di tipo {@link UserData} contenente tutti i dati del client
     */
    private UserData data;
    /**
     * Oggetto di tipo {@link MessageBox} che si occupa solo di richiamare il metodo
     * {@link MessageBox#getMessage(String) getMessage(String)}
     */
    private MessageBox mailBox;

    /**
     * Defaul constructor
     * 
     * @param s
     *                socket
     * @param mailBox
     *                {@link MessageBox} condiviso dal thread principale
     * @param data
     *                {@link UserData}
     * @throws IOException
     */
    public TheWaiter(Socket s, MessageBox mailBox, UserData data) throws IOException {
        socket = s;
        this.data = data;
        this.mailBox = mailBox;
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    /**
     * Metodo standard dell'interfaccia runnable
     */
    public void run() {
        while (true) {
            try {
                Message[] msg = mailBox.getMessage(data.getName());
                for (Message m : msg) {
                    out.println(m.getPerfectMessage());
                }
            } catch (Exception e) {return;}
        }
    }

}
