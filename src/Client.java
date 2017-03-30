
import java.io.*;
import java.net.*;

/*
 * Client class for the server based chat project
 */

public class Client {
    private Socket socket = null;
    private BufferedReader console = null;
    private DataOutputStream output = null;
    
    public Client(String clientName, String serverName, int serverPort){
        System.out.println("Connecting...");
        
        while(!connect(clientName, serverName, serverPort)) {
            System.out.println("Try to log on again");
        }
        
        System.out.println("Connected");
        /*try{
            socket = new Socket(serverName, serverPort);
            System.out.println("Connected");
            console = new BufferedReader(new InputStreamReader(System.in));
            output = new DataOutputStream(socket.getOutputStream());
        }catch(UnknownHostException uhe){
            System.out.println("Host unknown: " + uhe.getMessage());
        }
        catch(Exception e){
            System.out.println(e);
        }
        
        String line = "";
        while(!line.equals("bye")){
            try{
                System.out.println("Send message to server....");
                line = console.readLine();
                System.out.println(line);
                output.writeUTF(line);
                output.flush();
            }catch(Exception e){
                System.out.println(e);
            }
        }
        try{
            if(socket != null) socket.close();
            if(console != null) console.close();
            if(output != null) output.close();
        }catch(Exception e){
            System.out.println(e);
        }*/
        
        
    }
    
    private boolean connect(String clientName, String serverName, int serverPort) {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];
        
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName(serverName);
            
            String sentence = inFromUser.readLine();
            
            if(sentence.toUpperCase().equals("LOG ON")) {
                String sendString = "HELLO (" + clientName + ")";
                sendData = sendString.getBytes();
                
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, serverPort);
                clientSocket.send(sendPacket);
                
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
            
                String response = new String(receivePacket.getData());
                System.out.println("FROM SERVER:" + response);
                
                String[] dataArray = response.split("[()]");
                for(int a = 0; a < dataArray.length; a ++) {
                    dataArray[a] = dataArray[a].trim().toUpperCase();
                }
                
                if(dataArray[0].equals("CHALLENGE")) {
                    //TODO response
                }
            }
            else {
                System.out.println("Please type log in");
                clientSocket.close();
                return false;
            }
            
            clientSocket.close();
            
        }catch(Exception e) {
            System.out.println(e);
        }
        return true;
    }
    
    public static void main(String args[]){
        Client client = null;
        
        if(args.length != 3)
            System.out.println("Use correct input, client name, host name, port number");
        else
            client = new Client(args[0], args[1], Integer.parseInt(args[2]));
    }
}
