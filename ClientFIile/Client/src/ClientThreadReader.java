import java.io.*;

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
                    return;
                else if(s.startsWith("/ready"))
                    out.println(encr.getKey());
                else if(s.contains("@dec")){
                    String start = s.substring(0, s.indexOf("@dec"));
                    String[] shit = s.split("-");
                    String msgToDec = shit[1];
                    System.out.println(start+encr.decrypt(msgToDec));
                }else
                System.out.println(s);
            } catch (IOException e) {
                System.out.println("wegweg");
                return;
            }
        }
    }
    
}
