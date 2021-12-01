import java.util.HashMap;

public class KeySorter {
    private int numberKeys;
    private int actualKeys;
    private HashMap<String, String> keys;
    private boolean ready;

    public KeySorter(){
        numberKeys = 0;
        keys = new HashMap<String, String>();
        ready = false;
    }

    public synchronized void setNKeys(int numberKeys){
        this.numberKeys = numberKeys;
    }

    public synchronized void setKey(String user, String key){
        keys.put(user, key);
        actualKeys++;
        if(numberKeys == actualKeys)
            ready = true;
    }

    public synchronized HashMap<String, String> getKeys() {
        try{
            while(!ready){wait();}
            return this.keys;
        }catch(InterruptedException e){return null;}
    }
}
