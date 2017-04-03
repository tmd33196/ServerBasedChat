
import java.net.*;
import java.util.Random;

/*
 * Server class for the server based chat project
 */

public class Server {
    
    private DatagramSocket serverSocket;
    byte[] receiveData = new byte[1024];
    byte[] sendData = new byte[1024];
    
    public Server(int port) {        
        try {
            serverSocket = new DatagramSocket(port);
        }catch(Exception e){
            System.out.println(e);
        }
        
        int nextPort = port + 1;
        Random random = new Random(System.nanoTime());
        
        while(true) {
            try {
                //Receive the next packet
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
                
                //Switch on the command
                switch(dataArray[0]) {
                    case ("HELLO"):
                        //TODO verify the client is on the list of clients
                        //TODO authentication
                        output = "CHALLENGE(" + random.nextInt(1000) + ")";
                        break;
                    case ("RESPONSE"):
                        //TODO: if data[1] == calculated authentication value
                        
                        TCPServerThread tcp = new TCPServerThread(nextPort);
                        Thread thread = new Thread(tcp);
                        thread.start();
                        
                        output = "AUTH_SUCCESS(" + random.nextInt(100) + ", " + nextPort++ + ")";
                        
                        //TODO: encrypt string
                        break;
                }
                
                //Send a packet back
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
