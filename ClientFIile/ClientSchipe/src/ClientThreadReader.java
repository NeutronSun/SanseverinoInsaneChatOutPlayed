import java.io.*;
import java.math.BigInteger;
import java.net.*;

public class ClientThreadReader implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private BigInteger[] privateKey = {BigInteger.ZERO,BigInteger.ZERO};
    public ClientThreadReader(Socket s) throws IOException{
        socket = s;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    public void run() {
        while (true) {
            String s;
            try {
                s = in.readLine();
                if(s.startsWith("/"))
                    setPK(s);
                else
                    System.out.println(s);
            } catch (IOException e) {
                System.out.println("wegweg");
                return;
            }
        }
    }

    public void setPK(String s){
        if(privateKey[0].equals(BigInteger.ZERO)){
            privateKey[0] = new BigInteger(s.substring(2));
            return;
        }
        privateKey[1] = new BigInteger(s.substring(2));
        System.out.println("your pk is (" + privateKey[0] + ", " + privateKey[1] + ")");
    }

    
}
