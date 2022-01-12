/**
 * Copyright (c) 22 Giugno anno 0, 2021, SafJNest and/or its affiliates. All rights reserved.
 * SAFJNEST PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * 
 * 
 * 
 * 
 */

/**
 *  Il {@code Message} e' una classe che si occupa di memorizzare tutti i dati di un singolo messaggio:
 * <p><ul>
 * <li> {@link Message#receiver ricevente}
 * <li> {@link Message#msg il messggio}
 * <li> {@link Message#data la data}
 * </ul><p>
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @version 1
 * @since 2021-12-1(aaaa-mm-gg)
 */
public class Message {
    /**
     * ricevente del messaggio
     */
    private String sender;
    /**
     * messaggio
     */
    private String msg;
    /**
     * data di invio del messaggio
     */
    private String data;
    /**
     * {@code Stringa} rappresentante il colore che sara' interpretato dal cmd.
     */
    private String color;

    /**
     * Costruttore default
     * @param sender
     * {@code String} sender
     * @param msg
     * {@code String} messaggio
     * @param data
     * {@code Date} data di invio
     * @param color
     * {@code colore} colore del sender
     */
    public Message(String sender, String msg, String data, String color) {
        this.sender = sender;
        this.msg = msg;
        this.data = data;
        this.color = color;
    }

    
    public String getSender() {
        return this.sender;
    }

    public String getMsg() {
        return this.msg;
    }

    /**
     * Stampa di tuti i dati del messaggio in uno specifico modo:
     * <p>
     * {@code color[data]<sender>:messaggio}
     * <p>Il colore non sara' visibe nel testo nel messaggio ma sara' solo
     * un effetto grafico interpretato dal cmd.
     * @return
     * {@code String}
     */
    public String getPerfectMessage() {
        return (color + "[" + data + "]<" + this.sender + ">" + ":" + this.msg);
    }
}
