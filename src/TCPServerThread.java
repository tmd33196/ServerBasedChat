/*
 * Thread for TCP server
 */

import java.io.*;
import java.net.*;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class TCPServerThread implements Runnable{
    ServerSocket welcomeSocket;
    private final int port;
    private ConnectedClients cc;
    private final String client;
    
    public TCPServerThread(int port, String client, ConnectedClients cc) {
        this.port = port;
        this.cc = cc;
        this.client = client;
        try {
            welcomeSocket = new ServerSocket(port);
            System.out.println("Created TCP on: " + port);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    public void run() {
        System.out.println("Listening on: " + port);
        String inFromClientString;
        String outToClientString;
        
        Socket connectionSocket = null;

        try {
            connectionSocket = welcomeSocket.accept();
            System.out.println("Client " + client + " accepted: " + connectionSocket);
            DataInputStream inFromClient = new DataInputStream(new BufferedInputStream(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            cc.addStream(client, outToClient);
            System.out.println(outToClient.toString());
            
            inFromClientString = inFromClient.readUTF();
            System.out.println("Received: " + inFromClientString);
            inFromClientString = decrypt(inFromClientString, cc.getCKA(client));
            System.out.println("Decrypted Received: " + inFromClientString);

            outToClientString = "CONNECTED";
            outToClientString = encrypt(outToClientString, cc.getCKA(client));
            outToClient.writeUTF(outToClientString);


            while(true) {
                inFromClientString = inFromClient.readUTF();
                System.out.println("Received: " + inFromClientString);
                inFromClientString = decrypt(inFromClientString, cc.getCKA(client));
                System.out.println("Decrypted Received: " + inFromClientString);

                if(inFromClientString.toUpperCase().equals("LOG OFF")) {
                    break;
                }
                if(inFromClientString.split("[()]")[0].equals("CHAT_REQUEST")) {
                    String clientB = inFromClientString.split("[()]")[1];
                    byte[] clientBCKA = cc.getCKA(clientB);

                    if(clientBCKA == null) {
                        System.out.println("User " + clientB + " is not currently online, please try again later");
                        outToClientString = "UNREACHABLE(" + clientB + ")";
                        outToClientString = encrypt(outToClientString, cc.getCKA(client));
                        outToClient.writeUTF(outToClientString);
                    } else {
                        int session = cc.getSession();
                        int clientBport = cc.getPort(clientB);
                        String outToClientBString = "";
                        //Socket clientBSocket = new Socket("localhost", clientBport, InetAddress.getByName("localhost"), clientBport);
                        //System.out.println(clientBSocket);
                        //DataOutputStream outToClientB = new DataOutputStream(clientBSocket.getOutputStream());
                        DataOutputStream outToClientB = cc.getStream(clientB);
                        System.out.println(outToClientB.toString());
                        
                        System.out.println("Create chat with " + clientB + " with cka of: " + cc.getCKA(clientB) + " on port: " + clientBport);
                        outToClientString = "CHAT_STARTED(" + session + ", " + clientB + ")";
                        outToClientString = encrypt(outToClientString, cc.getCKA(client));
                        outToClient.writeUTF(outToClientString);
                        
                        outToClientBString = "CHAT_STARTED(" + session + ", " + client + ")";
                        outToClientBString = encrypt(outToClientBString, clientBCKA);
                        outToClientB.writeUTF(outToClientBString);
                        

                        while(true) {
                            inFromClientString = inFromClient.readUTF();
                            System.out.println("Received: " + inFromClientString);
                            inFromClientString = decrypt(inFromClientString, cc.getCKA(client));
                            System.out.println("Decrypted Received: " + inFromClientString);

                            if(inFromClientString.contains("END_REQUEST")) {
                                outToClientBString = "END_NOTIF(" + session + ")";
                                outToClientBString = encrypt(outToClientBString, clientBCKA);
                                outToClientB.writeUTF(outToClientBString);
                                //outToClientB.flush();
                                break;
                            } else {
                                //outToClientBString = inFromClientString;
                                outToClientBString = encrypt(inFromClientString, clientBCKA);
                                outToClientB.writeUTF(outToClientBString);
                                //outToClientB.flush();
                            }
                        }
                    }

                }
            }
        } catch(Exception e) {
            System.out.println(e);
        }
        try {
            cc.removeClient(client);
            connectionSocket.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        
    }
    //Encrypts string using AES
    private String encrypt(String strClearText,byte[] digest) throws Exception{
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
    //decrypts string using AES
    private String decrypt(String strEncrypted, byte[] digest) throws Exception{
		String strData="";
		byte[] byteEncrypted = strEncrypted.getBytes("ISO-8859-1");
		try {
			SecretKeySpec skeyspec=new SecretKeySpec(digest,"AES");
			Cipher cipher=Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, skeyspec);
			byte[] decrypted=cipher.doFinal(byteEncrypted);
			strData=new String(decrypted);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		return strData;
	}

}
