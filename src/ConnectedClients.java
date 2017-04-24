
import java.util.HashMap;

/*
 * Maintains a list of currently connected clients and their keys
 */

public class ConnectedClients {
    private HashMap<String, String> secretKeys;
    private HashMap<String, String> xRES;
    private HashMap<String, byte[]> CK_A;
    
    public ConnectedClients() {
        secretKeys = new HashMap<>();
        xRES = new HashMap<>();
        CK_A = new HashMap<>();
    }
    
    public void addSecretKey(String key, String value) {
        secretKeys.put(key, value);
    }
    
    public String getSecretKey(String key) {
        return secretKeys.get(key);
    }
    
    public void addXRES(String key, String string) {
        xRES.put(key, string);
    }
    
    public String getXRES(String key) {
        return xRES.get(key);
    }
    
    public void addCKA(String key, byte[] bs) {
        CK_A.put(key, bs);
    }
    
    public byte[] getCKA(String key) {
        return CK_A.get(key);
    }

}
