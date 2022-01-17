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
 * {@code dati utenti} e' una classe che si occupa di memorizzare tutti i dati di un singolo utente:
 * <p><ul>
 * <li> {@link UserData#userName username}
 * <li> {@link UserData#password password}
 * <li> {@link UserData#online stato di online}
 * <li> {@link UserData#publicKey chiave pubblica }
 * <li> {@link UserData#color codice del colore}
 * </ul><p>
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @version 1
 * @since 2021-12-1(aaaa-mm-gg)
 */

public class UserData {
    /**
     * {@code username} dell'utente, con cui sara' visualizzato da tutti gli altri client
     */
    private String userName;
    /**
     * {@code password} dell'utente
     */
    private String password;
    /**
     * Stato di {@code online} dell'utente, con cui sara' visualizzato da tutti gli altri client.
     * <p>Se online tutti i client potranno vederlo mentre se offline nessuno potra'.
     * Cio' non implica che non sia connesso al server, per quanto ne sanno gli altri client
     * puo' non esiste, essere disconnesso o in modalita' fantasma(connesso ma offline).
     */
    private Boolean online;
    /**
     * {@code publicKey} dell'utente, condivisa con tutti
     */
    private String publicKey;
    /**
     * {@code Stringa} contenente il codice del colore dell'utente. Di
     * default il colore e' impostato sul bianco, il cui codice e' {@code "\033[0m"}
     */
    private String color;

    /**
     * Costruttore di default
     * @param userName
     * @param password
     * @param pk
     */
    public UserData(String userName, String password, String pk){
        this.userName = userName;
        this.password = password;
        online = false;
        publicKey = pk;
        color = "\033[0m";
    }


    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getName(){
        return userName;
    }

    public String getPassword(){
        return password;
    }

    public void setOnline(boolean online){
        this.online = online;
    }

    public boolean isOnline() {
        return online;
    }
    
    public void setPk(String pk){
        this.publicKey = pk;
    }

    public String getPk(){
        return publicKey;
    }

    public String getColor(){
        return color;
    }

    public void setColor(String color){
        this.color = color;
    }
}
