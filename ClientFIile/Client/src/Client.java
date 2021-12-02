import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
    
    private static PrintWriter putInServer;
    static Socket echoSocket;

    public static void main(String[] args) throws Exception{
        int portNumber = 77;
        Encryptor encrypt = new Encryptor();
        KeySorter ks = new KeySorter();
        try{
            String hostName = Inet4Address.getLocalHost().getHostAddress();
            Socket echoSocket = new Socket(hostName, portNumber);
            putInServer = new PrintWriter(echoSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            BufferedReader inKey = new BufferedReader(new InputStreamReader(System.in));
            ClientThreadReader read = new ClientThreadReader(in, putInServer, ks, encrypt);
            new Thread(read).start();
           // putInServer.println(encrypt.getKey());
            String userInput;
            while ((userInput = inKey.readLine()) != null) {
                if(userInput.startsWith("@")) {
                    String[] names = userInput.substring(1, userInput.indexOf(" ")).split("@");
                    String msg = userInput.substring(userInput.indexOf(" ") + 1);
                    msg = checkEmoji(msg);
                    ks.setNKeys(names.length);
                    System.out.println(names.length);
                    putInServer.println(userInput);
                    if(ks.allOkay()){   
                        String[] correctName = ks.getNames();
                        for(String name : correctName){
                            putInServer.println("msg-" + name + "-" + encrypt.encrypt(msg, ks.getKey(name)));
                        }
                    }
                }else{
                    putInServer.println(userInput);
                }
            }

            putInServer.close();
            inKey.close();
        } catch (IOException e) {
            System.out.println("you got banned");
            return;
        }
    }

    public static String checkEmoji(String msg){
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
