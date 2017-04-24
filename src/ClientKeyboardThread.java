
import java.io.*;
import java.net.*;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/*
 * Client thread to listen to the keyboard
 */

public class ClientKeyboardThread extends Thread {
    Client client;
    DataOutputStream outToServer;
    BufferedReader inFromUser;
    byte[] CK_A;
    
    public ClientKeyboardThread(Client client, DataOutputStream outToServer, byte[] CK_A) {
        this.client = client;
        this.outToServer = outToServer;
        this.inFromUser = new BufferedReader(new InputStreamReader(System.in));
        this.CK_A = CK_A;
    }
    
    public void run() {
        String outToServerString;
        while(true) {
            try {
                String line = inFromUser.readLine();
                switch(client.getState()) {
                    case ("IDLE"):
                        if(line.toUpperCase().equals("LOG OFF")) {
                            outToServerString = encrypt2(line, CK_A);
                            outToServer.writeUTF(outToServerString);
                            outToServer.flush();
                            outToServer.close();
                            inFromUser.close();
                            client.stop();
                            break;
                        }else if(line.contains("Chat")) {
                            client.setState("REQUEST");
                            outToServerString = "CHAT_REQUEST(" + line.split("[ ]")[1] + ")";
                            outToServerString = encrypt2(outToServerString, CK_A);
                            outToServer.writeUTF(outToServerString);
                            outToServer.flush();
                        }
                        else {
                           	if(line.toUpperCase().equals("HISTORY")) {                          
                                outToServerString = encrypt2("HISTORY_REQUEST(" + client.getSessionID() + ")", CK_A);
                                outToServer.writeUTF(outToServerString);
                                outToServer.flush();
                                break;
                            }
                        	System.out.println("Please type Log Off or Chat [Client-ID]");
                        }
                        break;
                    case ("CHAT"):
                        if(line.toUpperCase().equals("END CHAT")) {
                            client.setState("IDLE");
                            outToServerString = encrypt2("END_REQUEST(" + client.getSessionID() + ")", CK_A);
                            outToServer.writeUTF(outToServerString);
                            outToServer.flush();
                            break;
                        }
                        else {
                        	outToServerString = encrypt2("CHAT(" + client.getSessionID() + ", " + line + ")", CK_A);
                            outToServer.writeUTF(outToServerString);
                            outToServer.flush();
                            History history = new History(client.getClientName());
                            history.addMessage(line, client.getClientName());
                        }
                        break;
                }
            } catch(Exception e){
                System.out.println(e);
                break;
            }
        }
    }
    
    public void startChat(String message) {
        try {
            client.setState("CHAT");
            String outToServerString = message;
            outToServerString = encrypt2(outToServerString, CK_A);
            outToServer.writeUTF(outToServerString);
            outToServer.flush();
        }catch(Exception e) {
            System.out.println(e);
        }
    }
    
        //Used for TCP connections, does not currently work for UDP
    private String encrypt2(String strClearText,byte[] digest) throws Exception{
        String strData="";
        byte [] encrypted = null;

        try {
            SecretKeySpec skeyspec=new SecretKeySpec(digest,"AES");
            Cipher cipher=Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeyspec);
            encrypted=cipher.doFinal(strClearText.getBytes());
            strData=new String(encrypted, "ISO-8859-1");

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
        return strData;
    }
}
