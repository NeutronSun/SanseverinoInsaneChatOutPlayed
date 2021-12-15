/**
 * Copyright (c) 22 Giugno anno 0, 2021, SafJNest and/or its affiliates. All rights reserved.
 * SAFJNEST PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * 
 * 
 * 
 * 
 */

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * 
 * Classe che si occupa di generare le due {@code chiavi} per ogni client
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @version 1.2
 * @since 2021-12-02 (aaaa-mm-gg)
 */
public class Encryptor {

    /**
     * indica l'ultimo numero primo utilizzato, ogni istanza della classe parte da {@code this} 
     * e ne calcola uno nuovo 
     */
    private static BigInteger lastPrime = new BigInteger("150000000073150000000073150000000073150000000073150000000073");
    /**
     * primo numero per il calcolo delle {@code chiavi RSA}
     */
    private BigInteger p = new BigInteger("47");
    /**
     * secondo numero per il calcolo delle {@code chiavi RSA}
     */
    private BigInteger q = new BigInteger("71");
    /**
     * prodotto di {@link Encryptor#q q} e {@link Encryptor#p p}
     */
    private BigInteger n = new BigInteger("0");
    /**
     * {@code esponente pubblico} per criptare. 
     * <p> Deve essere minore di {@link Encryptor#phi phi(n)} e coprimo con {@link Encryptor#phi phi(n)}
     */
    private BigInteger e = new BigInteger("0");
    /**
     * {@code esponente privato} per decriptare. 
     * <p> si calcola facendo {@code this = e-1 mod n}
     */
    private BigInteger d = new BigInteger("0");
    /**
     * valore del risultato della {@code funzione toziente} di Eulero.
     */
    private BigInteger phi = new BigInteger("0");


    /**
     * Default constructor.
     * <p>Le chiavi generate seguiranno lo standard {@code RSA 2048}, quindi
     * le chiavi saranno composte da numeri a 2048 bits.
     */
    public Encryptor(){
        lastPrime = getRandomBigInteger(2048);
        p = newPrime(lastPrime);
        q = newPrime(p);
        n = p.multiply(q);
        e = newPrime(q);
        lastPrime = e;
        phi = phi(p).multiply(phi(q));
        d = e.modInverse(phi);
    }

    /**
     * Le chiavi generate avranno una lunghezza di bits specificata
     * dal parametro del costruttore.
     * @param bits
     * numero di bits con cui rappresentare i {@code BigInteger}.
     */
    public Encryptor(int bits){
        lastPrime = getRandomBigInteger(bits);
        p = newPrime(lastPrime);
        q = newPrime(p);
        n = p.multiply(q);
        e = newPrime(q);
        lastPrime = e;
        phi = phi(p).multiply(phi(q));
        d = e.modInverse(phi);
    }

    public BigInteger getRandomBigInteger(int bits){
        bits--;
        int numBytes = (int)(((long)bits+7)/8);
        byte[] bytes = new byte[numBytes];
        try {
            SecureRandom.getInstanceStrong().nextBytes(bytes);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        BigInteger a = new BigInteger(1, bytes);
        return (a.bitLength() == (bits + 1)) ? a : a.add(BigInteger.TWO.pow(bits));
    }
    

    /**
     * Funzione toziente di {@code Eulero}.
     * <p>
     * La funzione restituisce i<l {@code numero} dei numeri {@code coprimi} con n.
     * Nel caso in cui n sia primo, allora il l {@code numero} dei numeri {@code coprimi} con n e'
     * semplicemente n - 1
     * @param n
     * bigInteger da analizzare
     * @return
     * il numero di numero coprimi con n
     */
    public BigInteger phi(BigInteger n) {
        return n.subtract(BigInteger.ONE);
    }

    /**
     * Dato un numero primo p, il metodo si occupa di trocare il prossimo numero 
     * primo, o pseudoprimo, partendo da p.
     * @param prime
     * numero primo di partenza.
     * @return
     * next(p)
     */

    public BigInteger newPrime(BigInteger prime) {
        BigInteger a = new BigInteger("2");
        if(prime.mod(BigInteger.TWO).equals(BigInteger.ZERO))
        prime = prime.add(BigInteger.ONE);
        do {
            prime = prime.add(BigInteger.TWO);
        } while (!a.modPow(prime.subtract(BigInteger.ONE), prime).equals(BigInteger.ONE));
        return prime;
    }


    public String getD() {
        return d.toString();
    }

    public String getN() {
        return n.toString();
    }

    /**
     * Metodo che si occupa di criptare un messaggio data una chiave 
     * pubblica.
     * <p>La stringa da criptare viene "depurata" da qualsiasi impurita' che
     * darebbero errore durante la fase di encryption(Es. le emoji).
     * Dopo viene effettuato il controllo della lunghezza della parola e, nel caso sia primo, 
     * arrotondata aggiungendo 1.
     * <p>Viene calcolato il piu' grande divisore di {@code lunghezza/2} "part" ed adesso
     * ogni {@code lunghezza/part} parole il messaggio viene spezzato e criptato.
     * <p>In caso la lunghezza del testo sia prima, la restante parte di testo non criptata viene aggiunta.
     * <p>Questo procedimento serve per dividere la stringa in parti uguali e proporzionate alla lunghezza del messaggio
     * in modo dale che non importa la lunghezza del testo ma non sara mai maggiore di {@link Encryptor#n n}.
     * @param ss
     * Stringa da criptare
     * @param keyFriend
     * chiave pubblica da utilizzare, scritta nel formato -> e/n
     * @return
     * String
     * @throws UnsupportedEncodingException
     */
    public String encrypt(String ss, String keyFriend) throws UnsupportedEncodingException {
        //istanzio la chiavi pubblica del client "amico"
        String[] supOne = keyFriend.split("/");
        BigInteger publicExp = new BigInteger(supOne[0]);
        BigInteger publicN = new BigInteger(supOne[1]); 
        //stringPlainTextNotYetDecriptedNotBigIntegher
        BigInteger plainText;
        
        /**Flag in caso la lunghezza del testo sia un numero prim0.
         * In tal caso allora la lunghezza sara' l+1.
         */
        boolean lengthPrime = false;
        
        /**Il testo viene inserito in un array di byte e "depurato" aggiungendo
         * uno zero all'inizio.
         * Serve nel caso in cui il testo sia una emoji, quindi in byte -9,-101 ecc...
        */
        byte[] erbite = ss.getBytes("UTF-8");
        byte[] toSend = new byte[erbite.length+1];
        toSend[0] = 0;
        for(int i = 0; i<erbite.length; i++) 
            toSend[i+1] = erbite[i];
        
        
        
        plainText = new BigInteger(toSend);
        ss = new String(plainText.toByteArray());
        int length = ss.length();
        int part = 1, cont = 0;

        //Controllo in caso la lunghezza sia un numero primo, percio' non
        //e' possibile da dividere in parti uguali
        if(BigInteger.TWO.modPow(new BigInteger(String.valueOf(length-1)), new BigInteger(String.valueOf(ss.length()))).equals(BigInteger.ONE)){
            lengthPrime = true;
            length = length+1;
        }

        //ricerca del divisore piu' grande di l/2
        for(int i = 2; i <= length/2; i++) {
            if(length % i == 0){
                part = i;
                cont++;
            }
        }
        
        /**
         * Il testo sara' ora diviso ogni l/n lettere.
         * Es. in caso la lunghezza sia 30, ogni 5 lettere sara' diviso
         * e criptato.
         */
        int everyN = length/part;
        cont = 0;
        String cypherText = "";
        String supp = "";
        for(char c : ss.toCharArray()){
            supp = supp + c;
            cont++;
            if(cont == everyN){
                plainText = new BigInteger(supp.getBytes("UTF-8"));
                cypherText = cypherText + "//" + plainText.modPow(publicExp, publicN).toString();
                supp = "";
                cont = 0;
            }
        }
        /**Se la lunghezza e' prima viene aggiunta la parte mancante.
         * Es. lunghezza sia 31, diventa 32.
         * Ogni 4 lettere sara' divisa e manca il resto.
         */
        if(lengthPrime){
            plainText = new BigInteger(supp.getBytes("UTF-8"));
            cypherText = cypherText + "//" + plainText.modPow(publicExp, publicN).toString();
        }
        return cypherText;
    }

    public String decrypt(String msg) {
        msg = msg.substring(2);
        String decrypted = "";
        String part = "";
        String[] b = msg.split("//");
        
        for(String s : msg.split("//")){
            //System.out.println(s);
            BigInteger supp = new BigInteger(s);
            supp = supp.modPow(d,n);
            byte[] erbite = supp.toByteArray();
            decrypted = decrypted + new String(erbite);
        }
        return decrypted;

    }

    public String getKey() {
        return new String("pk"+ e.toString() + "/" + n.toString());
    }


}
