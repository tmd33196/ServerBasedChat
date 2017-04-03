/*
 * Thread for TCP server
 */

import java.io.*;
import java.net.*;

public class TCPServerThread implements Runnable{
    ServerSocket welcomeSocket;
    int port;
    
    public TCPServerThread(int port) {
        this.port = port;
        try {
            welcomeSocket = new ServerSocket(port);
            System.out.println("Created TCP on: " + port);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    public void run() {
        System.out.println("Listening on: " + port);
        String inFromClientSentence;
        String outToClientSentence;
        
        while(true)
        {
            try {
                Socket connectionSocket = welcomeSocket.accept();
                System.out.println("Client accepted: " + connectionSocket);
                DataInputStream inFromClient = new DataInputStream(new BufferedInputStream(connectionSocket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                
                inFromClientSentence = inFromClient.readUTF();
                System.out.println("Received: " + inFromClientSentence);
                
                outToClientSentence = "CONNECTED";
                outToClient.writeUTF(outToClientSentence);
                connectionSocket.close();
            } catch(Exception e) {
                System.out.println(e);
            }
        }
    }
}
