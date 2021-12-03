/**
 *  Il {@code Message} e' una classe che si occupa di memorizzare tutti i dati di un singolo messaggio:
 * <p><ul>
 * <li> {@link Message#receiver ricevente}
 * <li> {@link Message#msg il messggio}
 * <li> {@link Message#data la data}
 * </ul><p>
 * @author Sanseverino Lorenzo
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

    /**
     * Costruttore default
     * @param receiver
     * {@code String} ricevente
     * @param msg
     * {@code String} messaggio
     * @param data
     * {@code Date} data di invio
     */
    public Message(String receiver, String msg, String data) {
        this.receiver = receiver;
        this.msg = msg;
        this.data = data;
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
        return ("[" + data + "]<" + this.receiver + ">" + ":" + this.msg);
    }
}
