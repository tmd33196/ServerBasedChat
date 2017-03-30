
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Random;

/*
 * Server class for the server based chat project
 */

public class Server {
    private Socket socket = null;
    private ServerSocket ss = null;
    private DataInputStream input = null;
    
    private DatagramSocket serverSocket;
    byte[] receiveData = new byte[1024];
    byte[] sendData = new byte[1024];
    
    public Server(int port) {
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
        
        try {
            serverSocket = new DatagramSocket(port);
        }catch(Exception e){
            System.out.println(e);
        }
        
        int nextPort = port + 1;
        Random random = new Random(System.nanoTime());
        
        while(true) {
            try {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                String data = new String( receivePacket.getData());
                String output = data;
                System.out.println("RECEIVED: " + data);
                
                //Parse the input
                String[] dataArray = data.split("[()]");
                for(int a = 0; a < dataArray.length; a ++) {
                    dataArray[a] = dataArray[a].trim().toUpperCase();
                }
                
                switch(dataArray[0]) {
                    case ("HELLO"):
                        //TODO verify the client is on the list of clients
                        output = "CHALLENGE(" + random.nextInt(1000) + ")";
                        break;
                    case ("RESPONSE"):
                        //TODO: do authentication
                        output = "AUTH_SUCCESS(" + random.nextInt(100) + ", " + nextPort++ + ")";
                        //TODO: encrypt string
                        break;
                }
                
                InetAddress returnIPAddress = receivePacket.getAddress();
                int returnPort = receivePacket.getPort();
                sendData = output.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, returnIPAddress, returnPort);
                serverSocket.send(sendPacket);
            }catch(Exception e) {
                System.out.println(e);
            }
        }
    }
    
    public static void main(String args[]){
        Server server = null;
        if(args.length != 1)
            System.out.println("Use correct input of a port number");
        else
            server = new Server(Integer.parseInt(args[0]));
    }
}
