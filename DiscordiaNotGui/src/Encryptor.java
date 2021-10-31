import java.io.IOException;
import java.math.BigInteger;

public class Encryptor {
    private static BigInteger lastPrime = new BigInteger("93199");
    private BigInteger p = new BigInteger("7");
    private BigInteger q = new BigInteger("11");
    private BigInteger n = new BigInteger("0");
    private BigInteger e = new BigInteger("0");
    private BigInteger d = new BigInteger("0");

    {lastPrime = newPrime(lastPrime);}

    public Encryptor(){
        p = newPrime(lastPrime);
        System.out.println("setted");
        q = newPrime(lastPrime);
        System.out.println("setted");
        n = p.multiply(q);
        System.out.println("setted");
        e = coprimes();
        System.out.println("setted");
        //d = findD();
        d = e.modInverse(phi());
        System.out.println("phi(n): " + phi() + "|q: " + q + "|p: " + p + "|n: " + n + "|e: " + e + "|d: " + d);
    }
    public BigInteger findD(){
        BigInteger k = BigInteger.TEN.pow(e.toString().length());
        BigInteger phi = phi();
        while(true) {
            if(k.multiply(e).mod(phi).equals(BigInteger.ONE))
                return k;
            k = k.add(BigInteger.ONE);
        }
    } 
    public BigInteger phi(){
        return p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
    }

    /*
    public BigInteger mcm(BigInteger a, BigInteger b){
        if (b.equals(BigInteger.ZERO)) return a;
        return mcm(b,a.mod(b));
    }
    */

    public BigInteger coprimes(){
        BigInteger phi = phi();
        int digits = phi.toString().length();
        BigInteger prm = new BigInteger(BigInteger.TEN.pow(digits).toString());
        if(prm.mod(BigInteger.TWO).equals(BigInteger.ZERO))
            prm = prm.add(BigInteger.ONE);
        return newPrime(prm);
    }
    public BigInteger newPrime(BigInteger prime){
        BigInteger a = new BigInteger("2");
        do{
            prime = prime.add(BigInteger.TWO);
        }while(!a.modPow(prime.subtract(BigInteger.ONE), prime).equals(BigInteger.ONE));
        return prime;
    }

    public BigInteger encrypt(int x){
        BigInteger msg = new BigInteger(String.valueOf(x));
        System.out.println(msg);
        System.out.println(e);
        System.out.println("msg^n: " + msg.pow(Integer.valueOf(e.toString())));
        return msg.modPow(e, n);
    }

    public BigInteger decrypt(BigInteger msg){
        System.out.println(msg);
        System.out.println(d);
        System.out.println("msg^n: " + msg.pow(Integer.valueOf(d.toString())));
        return msg.modPow(d,n);

    }

    public String getD(){
        return d.toString();
    }

    public String getN(){
        return n.toString();
    }


}
