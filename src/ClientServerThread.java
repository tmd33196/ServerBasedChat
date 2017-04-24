/*
 * Thread to listen to server for messages
 */

import java.io.*;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class ClientServerThread extends Thread {
    Client client;
    DataInputStream inFromServer;
    byte[] CK_A;
    
    public ClientServerThread(Client client, DataInputStream inFromServer, byte[] CK_A) {
        this.client = client;
        this.inFromServer = inFromServer;
        this.CK_A = CK_A;
        //System.out.println(CK_A);
    }
    
    public void run() {
        while(true) {
            try {
                String inFromServerString = inFromServer.readUTF();
                //System.out.println("FROM SERVER: " + inFromServerString);
                inFromServerString = decrypt2(inFromServerString, CK_A);
                //System.out.println("Decrypted FROM SERVER: " + inFromServerString);
                
                switch(client.getState()) {
                    case ("IDLE"):
                        if(inFromServerString.contains("CHAT_STARTED")){
                            System.out.println("Chat started with " + inFromServerString.split("[(), ]+")[2]);
                            client.setSessionID(inFromServerString.split("[(), ]+")[1]);
                            client.setState("CHAT");
                            client.startChat(inFromServerString);
                        }
                        
                        break;
                    case ("REQUEST"):
                        if(inFromServerString.contains("UNREACHABLE")) {
                            System.out.println("Client " + inFromServerString.split("[()]")[1] + " is currently unreachable");
                        }else {
                            System.out.println("Chat started with " + inFromServerString.split("[(), ]+")[2]);
                            client.setSessionID(inFromServerString.split("[(), ]+")[1]);
                            client.setState("CHAT");
                        }
                        break;
                    case ("CHAT"):
                        if(inFromServerString.contains("END_NOTIF")) {
                            System.out.println("Chat Ended");
                            client.setState("IDLE");
                        }else {
                            String s = inFromServerString.substring(inFromServerString.indexOf(",") + 2, inFromServerString.length() - 1);
                            System.out.println(s);
                        }
                        break;
                }
            } catch(Exception e) {
                System.out.println(e);
                break;
            }
        }
    }
    
    public void stopIt() {
        try {
            inFromServer.close();
        }catch(Exception e) {
            System.out.println(e);
        }
    }
    
    //Used for TCP connections, does not currently work for UDP
    private String decrypt2(String strEncrypted, byte[] digest) throws Exception{
        String strData="";
        byte[] byteEncrypted = strEncrypted.getBytes("ISO-8859-1");
        try {
            SecretKeySpec skeyspec=new SecretKeySpec(digest,"AES");
            Cipher cipher=Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeyspec);
            byte[] decrypted=cipher.doFinal(byteEncrypted);
            strData = new String(decrypted);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
        return strData;
    }
}
