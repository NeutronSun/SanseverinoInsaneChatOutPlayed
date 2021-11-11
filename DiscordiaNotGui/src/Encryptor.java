import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

public class Encryptor {
    private static BigInteger lastPrime = new BigInteger("150000000000000000000000000000000000000000000000973");
    private static int cont = 0;
    private BigInteger p = new BigInteger("47");
    private BigInteger q = new BigInteger("71");
    private BigInteger n = new BigInteger("0");
    private BigInteger e = new BigInteger("0");
    private BigInteger d = new BigInteger("0");
    private BigInteger phi = new BigInteger("0");

    {
        lastPrime = newPrime(lastPrime);
        cont++;
    }

    public Encryptor() {
        p = newPrime(lastPrime);
        q = newPrime(lastPrime);
        n = p.multiply(q);
        e = coprimes();
        phi = phi();
        d = e.modInverse(phi);
        System.out.println("contatore: " + cont);
    }

    public BigInteger findD() {
        BigInteger k = BigInteger.TEN.pow(e.toString().length());
        BigInteger phi = phi();
        while (true) {
            if (k.multiply(e).mod(phi).equals(BigInteger.ONE))
                return k;
            k = k.add(BigInteger.ONE);
        }
    }

    public BigInteger phi() {
        return p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
    }

    /*
     * public BigInteger mcm(BigInteger a, BigInteger b){ if
     * (b.equals(BigInteger.ZERO)) return a; return mcm(b,a.mod(b)); }
     */

    public BigInteger coprimes() {
        BigInteger prm = q;
        return newPrime(prm);
    }

    public BigInteger newPrime(BigInteger prime) {
        BigInteger a = new BigInteger("2");
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

    public String encrypt(String ss) throws UnsupportedEncodingException {
        BigInteger plainText, cypher = new BigInteger("0");
        boolean lengthPrime = false;
        byte[] erbite = ss.getBytes("UTF-8");
        byte[] toSend = new byte[erbite.length+1];
        toSend[0] = 0;
        for(int i = 0; i<erbite.length; i++) 
            toSend[i+1] = erbite[i];
        
        System.out.println(new String(erbite));
        plainText = new BigInteger(toSend); // <n
        if (plainText.min(n).equals(plainText)){
            plainText = plainText.modPow(e, n); // still <n
            return plainText.toString();
        }
        ss = plainText.toString();
        int part = 0, divisor, cont = 0;
        if(BigInteger.TWO.modPow(new BigInteger(String.valueOf(ss.length())), new BigInteger(String.valueOf(ss.length()))).equals(BigInteger.ZERO))
            lengthPrime = true;
        if(lengthPrime){
            for(int i = 2; i < Math.ceil(Math.sqrt(ss.length()-1)); i++) {
                if(ss.length() % i == 0){
                    part = i;
                    cont++;
                }
            }
        }else{
            for(int i = 2; i < Math.ceil(Math.sqrt(ss.length()-1)); i++) {
                if(ss.length() % i == 0){
                    part = i;
                    cont++;
                }
            }
        }
        int everyN = ss.length()/part;
        cont = 0;
        String cypherText = "";
        String supp = "";
        for(char c : ss.toCharArray()){
            if(cont == everyN){
                plainText = new BigInteger(supp);
                cypherText = cypherText + "|" + plainText.modPow(e, n);
            }
            supp = supp + c;
        }
        return ss;
    }

    public String decrypt(BigInteger msg) {
        if (msg.min(n).equals(msg))
            System.out.println("2:ok bro");
        msg = msg.modPow(d, n);
        byte[] erbite = msg.toByteArray();
        String ss = new String(erbite);
        // System.out.println("msg^n: " + msg.pow(Integer.valueOf(d.toString())));
        return ss;

    }

}
