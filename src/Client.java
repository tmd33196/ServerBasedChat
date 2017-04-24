
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/*
 * Client class for the server based chat project
 * Handles the connection phase then create a thread for listening to the keyboard and server input
 */

public class Client {
    
    private final String clientName;
    private final String clientSecretKey;
    private final String serverName;
    private final int serverPort;
    private byte[] CK_A;
    
    BufferedReader inFromUser;
    
    private DatagramSocket clientSocket;
    private InetAddress IPAddress;
    
    private String state;
    private String sessionID;
    
    ClientServerThread cst = null;
    ClientKeyboardThread ckt = null;
    
    //Sets the class variables and starts the log on process
    public Client(String _clientName, String _clientSecretKey, String _serverName, int _serverPort){
        this.clientName = _clientName;
        this.clientSecretKey = _clientSecretKey;
        this.serverName = _serverName;
        this.serverPort = _serverPort;
        
        System.out.println("Connecting...");
        
        //Continues to try to log on until it is successful
        while(!connect()) {
            System.out.println("Try to log on again");
        }
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
            
                String response = receiveString().trim();
                System.out.println("FROM SERVER:" + response);
                
                String[] dataArray = response.split("[()]+");
                for(int a = 0; a < dataArray.length; a ++) {
                    dataArray[a] = dataArray[a].trim().toUpperCase();
                }
                
                //RESPONSE to server
                if(dataArray[0].equals("CHALLENGE")) {
                    String xRES = A3(dataArray[1], clientSecretKey);
                    CK_A = A8(dataArray[1], clientSecretKey);
                    System.out.println(dataArray[1] + clientSecretKey);
                    sendString = "RESPONSE(" + clientName + "," + xRES + ")";
                }
                else {
                    System.out.println("Error in server response\n" + response);
                    return false;
                }
                
                sendString(sendString);
                byte [] responseBytes = receiveRandCookie();
                System.out.println("FROM SERVER:" + responseBytes.toString() + " " + responseBytes.length);
                response = decrypt(responseBytes, CK_A);
                System.out.println("Decrypted FROM SERVER:" + response);
                
                dataArray = response.split("[(), ]+");
                for(int a = 0; a < dataArray.length; a ++) {
                    dataArray[a] = dataArray[a].trim().toUpperCase();
                }
                
                //Switches to TCP
                if(dataArray[0].equals("AUTH_SUCCESS")) {
                    runTCPClient(dataArray[1], Integer.parseInt(dataArray[2]));
                }
                else {
                    System.out.println("Error in server response\n" + response);
                    return false;
                }
                
            }
            else {
                System.out.println("Please type log on");
                clientSocket.close();
                return false;
            }
            
            clientSocket.close();
            
        }catch(Exception e) {
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
    //Where the encryption string gets modified, making it error prone
    private String receiveString() throws IOException {
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        return new String(receivePacket.getData());
    }
    
    private byte [] receiveRandCookie() throws IOException {
        byte[] receiveData = new byte[32];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        return receivePacket.getData();
    }

    //Performs A3 encryption
    private String A3(String random, String secretKey) {
        String plainText = random + secretKey;
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.reset();
        m.update(plainText.getBytes());
        byte[] digest = m.digest();
        BigInteger bigInt = new BigInteger(1,digest);
        String strData = bigInt.toString(16);
        //Padding
        while(strData.length() < 32 ){
            strData = "0"+strData;
        }
        return strData;
    }
    
    //Generate the ciphering key
    //Generated Key needs to be 16 byte length
    private byte[] A8(String ran, String strKey){
        String CK_A = ran + strKey;
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.reset();
        m.update(CK_A.getBytes());
        byte[] digest = m.digest();
        return digest;
    }
    
    //Used for UDP currently
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
    
    //Used for UDP currently
    private String decrypt(byte[] strEncrypted, byte[] digest) throws Exception{
		String strData="";
		byte[] byteEncrypted = strEncrypted;
		try {
			SecretKeySpec skeyspec=new SecretKeySpec(digest,"AES");
			Cipher cipher=Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, skeyspec);
			byte[] decrypted=cipher.doFinal(byteEncrypted);
			strData = new String(decrypted);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		return strData;
    }
    
    //Used for TCP connections, does not currently work for UDP
    private String decrypt2(String strEncrypted, byte[] digest) throws Exception{
        String strData="";
        byte[] byteEncrypted = strEncrypted.getBytes("ISO-8859-1");
        try {
            SecretKeySpec skeyspec=new SecretKeySpec(digest,"AES");
            Cipher cipher=Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeyspec);
            byte[] decrypted=cipher.doFinal(byteEncrypted);
            strData = new String(decrypted);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
        return strData;
    }
    
    //Runs the TCP client
    private void runTCPClient(String cookie, int port) {
        String outToServerString;
        String inFromServerString;
        
        try {
            System.out.println("Connecting to: " + port);
            Socket TCPClientSocket = new Socket(serverName, port);
            DataOutputStream outToServer = new DataOutputStream(TCPClientSocket.getOutputStream());
            DataInputStream inFromServer = new DataInputStream(new BufferedInputStream(TCPClientSocket.getInputStream()));
            
            outToServerString = "CONNECT(" + cookie + ")";
            outToServerString = encrypt2(outToServerString, CK_A);
            outToServer.writeUTF(outToServerString);
            outToServer.flush();
            
            inFromServerString = inFromServer.readUTF();
            System.out.println("FROM SERVER: " + inFromServerString);
            inFromServerString = decrypt2(inFromServerString, CK_A);
            System.out.println("Decrypted FROM SERVER: " + inFromServerString);
            setState("IDLE");
            cst = new ClientServerThread(this, inFromServer, CK_A);
            Thread thread = new Thread(cst);
            thread.start();
            
            ckt = new ClientKeyboardThread(this, outToServer, CK_A);
            Thread thread2 = new Thread(ckt);
            thread2.start();
        }catch(Exception e) {
            System.out.println(e);
        }
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getState() {
        return state;
    }
    
    public void setSessionID(String session) {
        this.sessionID = session;
    }
    
    public String getSessionID() {
        return sessionID;
    }
    
    public void startChat(String message) {
        ckt.startChat(message);
    }
    
    public void stop() {
        cst.stopIt();
    }
    public String getClientName(){
    	return this.clientName;
    }
    
    public static void main(String args[]){
        Client client = null;//new Client("A","1234","localhost",9879);
        
        if(args.length != 4)
            System.out.println("Use correct input, client name, client key, host name, port number");
        else
            client = new Client(args[0], args[1], args[2], Integer.parseInt(args[3]));
    }
}
