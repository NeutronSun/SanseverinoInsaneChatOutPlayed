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
    private String receiver;
    /**
     * messaggio
     */
    private String msg;
    /**
     * data di invio del messaggio
     */
    private String data;

    private String color;

    /**
     * Costruttore default
     * @param receiver
     * {@code String} ricevente
     * @param msg
     * {@code String} messaggio
     * @param data
     * {@code Date} data di invio
     */
    public Message(String receiver, String msg, String data, String color) {
        this.receiver = receiver;
        this.msg = msg;
        this.data = data;
        this.color = color;
    }

    
    public String getreceiver() {
        return this.receiver;
    }

    public String getMsg() {
        return this.msg;
    }

    /**
     * Stampa di tuti i dati del messaggio in uno specifico modo:
     * <p>
     * {@code [data]<sender>:messaggio}
     * @return
     * {@code String}
     */
    public String getPerfectMessage() {
        return (color + "[" + data + "]<" + this.receiver + ">" + ":" + this.msg);
    }
}
