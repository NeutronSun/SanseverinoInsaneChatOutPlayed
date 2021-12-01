import java.io.*;
import java.math.BigInteger;
import java.net.*;

public class ClientThreadReader implements Runnable {
    private BufferedReader in;
    private KeySorter ks;
    public ClientThreadReader(BufferedReader in, KeySorter ks)throws IOException{
        this.in = in;
        this.ks = ks;
    }

    public void run() {
        while (true) {
            String s;
            try {
                s = in.readLine();
                if(s.startsWith("pk"))
                
                System.out.println(s);
            } catch (IOException e) {
                System.out.println("wegweg");
                return;
            }
        }
    }
    
}
