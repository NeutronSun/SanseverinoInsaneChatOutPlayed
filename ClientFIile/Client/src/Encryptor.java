/**
 * Copyright (c) 22 Giugno anno 0, 2021, SafJNest and/or its affiliates. All rights reserved.
 * SAFJNEST PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * 
 * 
 * 
 * 
 */

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
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

    {lastPrime = new BigInteger(lastPrime.bitLength(), new Random());}

    /**
     * Default constructor
     */
    public Encryptor() {
        p = newPrime(lastPrime);
        q = newPrime(lastPrime);
        n = p.multiply(q);
        e = newPrime(lastPrime);
        phi = phi(p).multiply(phi(q));
        d = e.modInverse(phi);
    }

    /**
     * Funzione toziente di {@code Eulero}.
     * <p>
     * La funzione restituisce i<l {@code numero} dei numeri {@code coprimi} con n.
     * Nel caso in cui n sia primo, allora il l {@code numero} dei numeri {@code coprimi} con n e'
     * semplicemente {@code n-1}
     * @param n
     * bigInteger da analizzare
     * @return
     * il numero di numero coprimi con n
     */
    public BigInteger phi(BigInteger n) {
        return n.subtract(BigInteger.ONE);
    }

    public BigInteger newPrime(BigInteger prime) {
        BigInteger a = new BigInteger("2");
        if(prime.mod(BigInteger.TWO).equals(BigInteger.ZERO))
        prime = prime.add(BigInteger.ONE);
        do {
            prime = prime.add(BigInteger.TWO);
        } while (!a.modPow(prime.subtract(BigInteger.ONE), prime).equals(BigInteger.ONE));
        lastPrime = prime;
        return prime;
    }


    public String getD() {
        return d.toString();
    }

    public String getN() {
        return n.toString();
    }

    public String encrypt(String ss, String keyFriend) throws UnsupportedEncodingException {
        String[] supOne = keyFriend.split("/");
        BigInteger publicExp = new BigInteger(supOne[0]);
        BigInteger publicN = new BigInteger(supOne[1]); 
        BigInteger plainText;
        boolean lengthPrime = false;
        byte[] erbite = ss.getBytes("UTF-8");
        byte[] toSend = new byte[erbite.length+1];
        toSend[0] = 0;
        for(int i = 0; i<erbite.length; i++) 
            toSend[i+1] = erbite[i];
        
        plainText = new BigInteger(toSend);
        ss = new String(plainText.toByteArray());
        int l = ss.length();
        int part = 1, divisor, cont = 0;
        if(BigInteger.TWO.modPow(new BigInteger(String.valueOf(l-1)), new BigInteger(String.valueOf(ss.length()))).equals(BigInteger.ONE)){
            lengthPrime = true;
            l = l+1;
        }

        for(int i = 2; i <= l/2; i++) {
            if(l % i == 0){
                part = i;
                cont++;
            }
        }
        
        int everyN = l/part;
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
