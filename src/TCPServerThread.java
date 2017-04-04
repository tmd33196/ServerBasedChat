/*
 * Thread for TCP server
 */

import java.io.*;
import java.net.*;

public class TCPServerThread implements Runnable{
    ServerSocket welcomeSocket;
    private final int port;
    private final String CK_A;
    
    public TCPServerThread(int port, String CK_A) {
        this.port = port;
        this.CK_A = CK_A;
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
                inFromClientString = decrypt(inFromClientString, CK_A);
                System.out.println("Decrypted Received: " + inFromClientString);
                
                outToClientString = "CONNECTED";
                outToClientString = encrypt(outToClientString, CK_A);
                outToClient.writeUTF(outToClientString);
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
