/*
 * Thread for TCP server
 */

import java.io.*;
import java.net.*;

public class TCPServerThread implements Runnable{
    String clientSentence;
    String capitalizedSentence;
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
    
    /*try {
            ss = new ServerSocket(port);
            System.out.println("Server started:" + ss);
            System.out.println("Waiting for client...");
            socket = ss.accept();
            System.out.println("Client accepted: " + socket);
            input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            
            boolean done = false;
            
            while(!done){
                try{
                    String line = input.readUTF();
                    System.out.println(line);
                    done = line.equals("bye");
                }catch(Exception e){
                    System.out.println(e);
                }
            }
            if(socket!=null) socket.close();
            if(ss!=null) ss.close();
        }catch(Exception e){
            System.out.println(e);
        }*/
    
    public void run() {
        System.out.println("Listening on: " + port);
        while(true)
        {
            try {
                Socket connectionSocket = welcomeSocket.accept();
                System.out.println("Client accepted: " + connectionSocket);
                DataInputStream inFromClient = new DataInputStream(new BufferedInputStream(connectionSocket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                clientSentence = inFromClient.readUTF();
                System.out.println("Received: " + clientSentence);
                capitalizedSentence = clientSentence.toUpperCase();
                outToClient.writeUTF(capitalizedSentence);
            } catch(Exception e) {
                System.out.println(e);
            }
        }
    }
}
