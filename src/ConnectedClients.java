
import java.io.DataOutputStream;
import java.util.HashMap;

/*
 * Maintains a list of currently connected clients and their keys
 */

public class ConnectedClients {
    private HashMap<String, String> secretKeys;
    private HashMap<String, String> xRES;
    private HashMap<String, byte[]> CK_A;
    private HashMap<String, Integer> ports;
    private HashMap<String, DataOutputStream> streams;
    private HashMap<String, Boolean> available;
    private int session;
    
    public ConnectedClients() {
        secretKeys = new HashMap<>();
        xRES = new HashMap<>();
        CK_A = new HashMap<>();
        ports = new HashMap<>();
        streams = new HashMap<>();
        available = new HashMap<>();
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
    
    public void addPort(String key, Integer port) {
        ports.put(key, port);
    }
    
    public int getPort(String key) {
        return ports.get(key);
    }
    
    public void addStream(String key, DataOutputStream dos) {
        streams.put(key, dos);
    }
    
    public DataOutputStream getStream(String key) {
        return streams.get(key);
    }
    
    public void addAvailable(String key, Boolean bool) {
        available.put(key, bool);
    }
    
    public void setAvailable(String key, Boolean bool) {
        available.replace(key, bool);
    }
    
    public Boolean getAvailable(String key) {
        return available.get(key);
    }
    
    public int getSession() {
        return session++;
    }

    public void removeClient(String key) {
        xRES.remove(key);
        CK_A.remove(key);
    }
    
}
