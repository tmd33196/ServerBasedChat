
import java.util.HashMap;

/*
 * Maintains a list of currently connected clients and their keys
 */

public class ConnectedClients {
    private HashMap<String, String> secretKeys;
    private HashMap<String, String> xRES;
    private HashMap<String, String> CK_A;
    
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
    
    public void addXRES(String key, String value) {
        xRES.put(key, value);
    }
    
    public String getXRES(String key) {
        return xRES.get(key);
    }
    
    public void addCKA(String key, String value) {
        CK_A.put(key, value);
    }
    
    public String getCKA(String key) {
        return CK_A.get(key);
    }

}
