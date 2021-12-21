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

/**
 * Thread principale del {@link Client} che si occupa di leggere i messaggi inviati dal 
 * Server e scriverli a schermo.
 * <p> La classe serve per rendere dinamica la comunicazione, così mentre 
 * l'utente scrive allo stesso tempo e' capace di ricevere messaggi o notizie dal server.
 * <p> I {@code protocolli di comunicazione} sono svariati, diversi in base al compito da svolgere:
 * <p><ul>
 * <li> {@code "pk"} -> [pk][user][key], la chiave viene inviata insieme al nome utente 
 * del proprietario.
 * <li>{@code "@end"} -> tutte le chiavi sono state inviate correttamente ed il client
 * ora puo' crittografare i messaggi e inviarli.
 * <li>{@code "/ready"} -> il server e' pronto a ricevere la chiave pubblica del client.
 * <li>{@code "@dec"} -> [colore][data][mit][@dec][messaggio da crittografare], messaggio da decriptare.
 * </ul></p>
 * Tutti questi messaggi iniziano, o contengono, simboli privati (@,/,#) in quanto
 * i nomi utenti non contengono simboli, i messaggi in arrivo sono crittografati e quindi
 * e' garantita integrita'.
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a> 
 * @author <a href="https://github.com/Leon412">Leon412</a> 
 * @version 1
 * @since 2021-11-29 (aaaa-mm-gg)
 */
public class ClientThreadReader implements Runnable {
    private BufferedReader in;
    private KeySorter ks;
    private Encryptor encr;
    private PrintWriter out;
    public ClientThreadReader(BufferedReader in, PrintWriter out, KeySorter ks, Encryptor e)throws IOException{
        this.in = in;
        this.ks = ks;              
        this.encr = e;
        this.out = out;
    }

    public void run() {
        loadingBee();
        while (true) {
            String s;
            try {
                s = in.readLine();
                if(s.startsWith("pk")){
                    String name = s.substring(2, s.indexOf("-"));
                    String key = s.substring(s.indexOf("-")+1);
                    ks.setKey(name, key);
                }
                else if(s.startsWith("@end"))
                    ks.isReady();
                else if(s.equals("@quit"))
                    Thread.currentThread().interrupt();
                else if(s.startsWith("/ready"))
                    out.println(encr.getKey());
                else if(s.contains("@dec")){
                    String start = s.substring(0, s.indexOf("@dec"));
                    String[] ee = s.split("-");
                    String msgToDec = ee[1];
                    String msg = checkEmoji(encr.decrypt(msgToDec));
                    typeWriterEffect(start+msg+"\033[0m", 8);
                }else
                typeWriterEffect(s, 8);
            } catch (Exception e) {
                return;
            }
        }
    }


    /**
     * Metodo che si occupa di simulare l'effetto di una macchina da scrivere.
     * @author <a href="https://github.com/ "
     * @param line
     * messaggio
     * @param speed
     * velocita' di stampa.
     */
    public void typeWriterEffect(String line, int speed){
        try {
            for(char c : line.toCharArray()){
                System.out.print(c);
                Thread.sleep(speed);
            }
            System.out.println();
        } catch (Exception e) {e.printStackTrace();}
    } 

    /**
     * THE SAFJNEST LOADING SCREEN
     * @author <a href="https://github.com/orgs/SafJNes">SafJNest</a>
     */
    public void loadingBee(){
        typeWriterEffect("\033[46;30m "
                + "                                                      __            \t\n"
                + "                                                      // \\           \t\n"
                + "                                                      \\\\_/ //        \t\n"
                + "                                     '-.._.-''-.._.. -(||)(')        \t\n"
                + "                                                       '''           \t", 2);
        typeWriterEffect(""
                + "\033[90m███████╗\033[93m █████╗ \033[90m███████╗\033[93m   ██╗    \033[90m███╗   ██╗\033[93m███████╗\033[90m███████╗\033[93m████████╗\033[40m\n"
                + "\033[90m██╔════╝\033[93m██╔══██╗\033[90m██╔════╝\033[93m   ██║    \033[90m████╗  ██║\033[93m██╔════╝\033[90m██╔════╝\033[93m╚══██╔══╝\n"
                + "\033[90m███████╗\033[93m███████║\033[90m█████╗  \033[93m   ██║    \033[90m██╔██╗ ██║\033[93m█████╗  \033[90m███████╗\033[93m   ██║   \n"
                + "\033[90m╚════██║\033[93m██╔══██║\033[90m██╔══╝\033[93m██   ██║    \033[90m██║╚██╗██║\033[93m██╔══╝  \033[90m╚════██║\033[93m   ██║   \n"
                + "\033[90m███████║\033[93m██║  ██║\033[90m██║   \033[93m╚█████╔╝    \033[90m██║ ╚████║\033[93m███████╗\033[90m███████║\033[93m   ██║   \n"
                + "\033[90m╚══════╝\033[93m╚═╝  ╚═╝\033[90m╚═╝   \033[93m ╚════╝     \033[90m╚═╝  ╚═══╝\033[93m╚══════╝\033[90m╚══════╝\033[93m   ╚═╝\n\033[0m", 2);
    }   

    public static String checkEmoji(String msg){
        if(System.getProperty("os.name").startsWith("Windows"))
            return msg;
        msg = msg.replaceAll("<3", new StringBuilder().appendCodePoint(0x1F497).toString());
        msg = msg.replaceAll(":143:", new StringBuilder().appendCodePoint(0x1F618).toString());
        msg = msg.replaceAll(":pantano:", new StringBuilder().appendCodePoint(0x1F62C).toString());
        msg = msg.replaceAll(":mario:", new StringBuilder().appendCodePoint(0x1F921).toString());
        msg = msg.replaceAll(":safj:", new StringBuilder().appendCodePoint(0x1F41D).toString());
        msg = msg.replaceAll(":skull:", new StringBuilder().appendCodePoint(0x1F480).toString());
        msg = msg.replaceAll(":sad:", new StringBuilder().appendCodePoint(0x1F614).toString());
        msg = msg.replaceAll(":merio:", new StringBuilder().appendCodePoint(0x1F533).toString());
        msg = msg.replaceAll(":baco:", new StringBuilder().appendCodePoint(0x1F41B).toString());
        msg = msg.replaceAll(":swag:", new StringBuilder().appendCodePoint(0x1F60E).toString());
        msg = msg.replaceAll(":stonks:", new StringBuilder().appendCodePoint(0x1F4C8).toString());
        msg = msg.replaceAll(":diablo:", new StringBuilder().appendCodePoint(0x1F608).toString());
        msg = msg.replaceAll(":deltoide:", new StringBuilder().appendCodePoint(0x00394).toString());
        msg = msg.replaceAll(":squidgame:", (new StringBuilder().appendCodePoint(0x1F991).toString() + new StringBuilder().appendCodePoint(0x1F3B2).toString()));
        return msg;
    }
}
