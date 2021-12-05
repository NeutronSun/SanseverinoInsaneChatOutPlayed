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
     * Stato di {@code online} dell'utente, con cui sara' visualizzato da tutti gli altri client
     */
    private Boolean online = false;
    /**
     * {@code publicKey} dell'utente, condivisa con tutti
     */
    private String publicKey;

    /**
     * Costruttore di default
     * @param userName
     * @param password
     * @param pk
     */
    public UserData(String userName, String password, String pk){
        this.userName = userName;
        this.password = password;
        publicKey = pk;
    }


    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getName(){
        return userName;
    }

    public void setOnline(boolean online){
        this.online = online;
    }

    public boolean isOnline() {
        return online;
    }
    
    public void setPk(String pk){
        this.publicKey = pk;
        System.out.println(pk);
    }

    public String getPk(){
        return publicKey;
    }
}
