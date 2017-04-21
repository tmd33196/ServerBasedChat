/*
 * Thread for TCP server
 */

import java.io.*;
import java.net.*;

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
        
        while(true)
        {
            try {
                Socket connectionSocket = welcomeSocket.accept();
                System.out.println("Client accepted: " + connectionSocket);
                DataInputStream inFromClient = new DataInputStream(new BufferedInputStream(connectionSocket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                
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
                        String user = inFromClientString.split("[()]")[1];
                        String userCKA = cc.getCKA(user);
                        
                        if(userCKA == null) {
                            System.out.println("User " + user + " is not currently online, please try again later");
                            outToClientString = "UNREACHABLE(" + user + ")";
                            outToClientString = encrypt(outToClientString, cc.getCKA(client));
                            outToClient.writeUTF(outToClientString);
                        } else {
                            System.out.println("Create chat with " + user + " with secret cka of *" + cc.getCKA(user));
                            outToClientString = " CHAT_STARTED(" + "1" + ", " + user + ")";
                            outToClientString = encrypt(outToClientString, cc.getCKA(client));
                            outToClient.writeUTF(outToClientString);
                            
                            while(true) {
                                inFromClientString = inFromClient.readUTF();
                                System.out.println("Received: " + inFromClientString);
                                inFromClientString = decrypt(inFromClientString, cc.getCKA(client));
                                System.out.println("Decrypted Received: " + inFromClientString);
                                
                                if(inFromClientString.contains("END_REQUEST")) {
                                    break;
                                } else {
                                    //Send to clientB
                                }
                            }
                        }
                        
                    }
                }
                
                connectionSocket.close();
            } catch(Exception e) {
                System.out.println(e);
            }
        }
    }
    
    private String encrypt(String message, String CKA) {
        return message;
    }
    
    private String decrypt(String message, String CKA) {
        return message;
    }
}
