
import java.io.*;
import java.net.*;
import java.util.Arrays;

/*
 * Client class for the server based chat project
 */

public class Client {
    
    private final String clientName;
    private final String serverName;
    private final int serverPort;
    
    BufferedReader inFromUser;
    
    private DatagramSocket clientSocket;
    private InetAddress IPAddress;
    
    //Sets the class variables and starts the log on process
    public Client(String _clientName, String _serverName, int _serverPort){
        this.clientName = _clientName;
        this.serverName = _serverName;
        this.serverPort = _serverPort;
        
        System.out.println("Connecting...");
        
        //Continues to try to log on until it is successful
        while(!connect()) {
            System.out.println("Try to log on again");
        }
        
        //Client is done with the chat server
        System.out.println("Done");
    }
    
    /*Method to connect to the server
     *Returns:  false on an error connecting
     *          true when the process is done
     */
    private boolean connect() {
        //Creates the user input reader
        inFromUser = new BufferedReader(new InputStreamReader(System.in));
        
        try {
            clientSocket = new DatagramSocket();
            IPAddress = InetAddress.getByName(serverName);
            
            String sentence = inFromUser.readLine();
            if(sentence.toUpperCase().equals("LOG ON")) {
                //HELLO to the server
                String sendString = "HELLO (" + clientName + ")";
                sendString(sendString);
            
                String response = receiveString();
                System.out.println("FROM SERVER:" + response);
                
                String[] dataArray = response.split("[()]+");
                for(int a = 0; a < dataArray.length; a ++) {
                    dataArray[a] = dataArray[a].trim().toUpperCase();
                }
                
                //RESPONSE to server
                if(dataArray[0].equals("CHALLENGE")) {
                    //TODO calculate response
                    sendString = "RESPONSE(" + 1 + ")";
                }
                else {
                    System.out.println("Error in server response");
                    return false;
                }
                
                sendString(sendString);
                response = receiveString();
                System.out.println("FROM SERVER:" + response);
                
                dataArray = response.split("[(), ]+");
                for(int a = 0; a < dataArray.length; a ++) {
                    dataArray[a] = dataArray[a].trim().toUpperCase();
                }
                
                //Switches to TCP
                if(dataArray[0].equals("AUTH_SUCCESS")) {
                    System.out.println(Arrays.toString(dataArray));
                    runTCPClient(Integer.parseInt(dataArray[2]));
                }
                else {
                    System.out.println("Error in server response");
                    return false;
                }
                
            }
            else {
                System.out.println("Please type log on");
                clientSocket.close();
                return false;
            }
            
            clientSocket.close();
            
        }catch(IOException | NumberFormatException e) {
            System.out.println(e);
        }
        return true;
    }
    
    //Sends a string to the UDP server
    private void sendString(String message) throws IOException {
        byte[] sendData = message.getBytes();      
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, serverPort);
        clientSocket.send(sendPacket);
    }
    
    //Receives a string from the UDP server
    private String receiveString() throws IOException {
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
            
        return new String(receivePacket.getData());
    }
    
    private void runTCPClient(int port) {
        String sendString;
        String receiveString;
        
        try {
            System.out.println("Connecting to: " + port);
            Socket TCPClientSocket = new Socket(serverName, port);
            DataOutputStream outToServer = new DataOutputStream(TCPClientSocket.getOutputStream());
            DataInputStream inFromServer = new DataInputStream(new BufferedInputStream(TCPClientSocket.getInputStream()));
            
            sendString = "CONNECT(123)";
            outToServer.writeUTF(sendString);
            outToServer.flush();
            
            receiveString = inFromServer.readUTF();
            System.out.println("FROM SERVER: " + receiveString);
            TCPClientSocket.close();   
        }catch(Exception e) {
            System.out.println(e);
        }
    }
    
    public static void main(String args[]){
        Client client = null;
        
        if(args.length != 3)
            System.out.println("Use correct input, client name, host name, port number");
        else
            client = new Client(args[0], args[1], Integer.parseInt(args[2]));
    }
}
