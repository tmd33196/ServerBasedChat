
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import java.math.BigInteger;

/*
 * Server class for the server based chat project
 */

public class Server {
    
    private DatagramSocket serverSocket;
    byte[] receiveData = new byte[1024];
    byte[] sendData = new byte[1024];
    History history = null;
    
    public Server(int port) { 
        
        ConnectedClients cc = new ConnectedClients();
        cc.addSecretKey("A", "1234");
        cc.addSecretKey("B", "1234");
        history = new History();
        
        try {
            serverSocket = new DatagramSocket(port);
        }catch(Exception e){
            System.out.println(e);
            return;
        }
        
        int nextPort = port + 1;
        Random random = new Random(System.nanoTime());
        
        while(true) {
            try {
                //Receive the next packet
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                String data = new String(receivePacket.getData()).trim();
                
                String output = data;
                byte[] outputBytes = null;
                System.out.println("RECEIVED: " + data);
                
                //Parse the input
                String[] dataArray = data.split("[(), ]+");
                for(int a = 0; a < dataArray.length; a ++) {
                    dataArray[a] = dataArray[a].trim().toUpperCase();
                }
                
                //Switch on the command
                switch(dataArray[0]) {
                    case ("HELLO"):
                        //dataArray[1] contains the clientID
                        String secretKey = cc.getSecretKey(dataArray[1]);
                        if(secretKey != null) {
                            String ran = Integer.toString(random.nextInt(1000));
                            cc.addXRES(dataArray[1], A3(ran, secretKey));
                            cc.addCKA(dataArray[1], A8(ran, secretKey));
                            System.out.println(ran + secretKey);
                            output = "CHALLENGE(" + ran + ")";
                            outputBytes = output.getBytes();
                        }
                        else {
                            System.out.println("ERROR: client not in the list of clients");
                            output = "ERROR: Not on client list";
                            outputBytes = output.getBytes();
                        }
                        
                        break;
                    case ("RESPONSE"):
                        //dataArray[1] contains the clientID
                        //dataArray[2] contains the RES
                        
                        if(cc.getXRES(dataArray[1]).equals(dataArray[2])) {
                            cc.addPort(dataArray[1], nextPort);
                            TCPServerThread tcp = new TCPServerThread(nextPort, dataArray[1], cc, history);
                            Thread thread = new Thread(tcp);
                            thread.start();
                            
                            

                            output = "AUTH_SUCCESS(" + random.nextInt(1000) + ", " + nextPort++ + ")";
                            outputBytes = encrypt(output, cc.getCKA(dataArray[1]));
                        }
                        else {
                            System.out.println("ERROR: Client response did not match xRES");
                            output = "AUTH_FAIL";
                            outputBytes = output.getBytes();
                        }
                        break;
                }
                
                //Send a packet back
                InetAddress returnIPAddress = receivePacket.getAddress();
                int returnPort = receivePacket.getPort();
                System.out.println(output);
                sendData = outputBytes;
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, returnIPAddress, returnPort);
                serverSocket.send(sendPacket);
            }catch(Exception e) {
                System.out.println(e);
            }
        }
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
        //Added in due to input data from client being automatically made uppercase
        strData = strData.toUpperCase();
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
    //Works now
    private byte[] encrypt(String strClearText,byte[] digest) throws Exception{
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
		return encrypted;
	}
    
    public static void main(String args[]){
        Server server = new Server(9879);
        if(args.length != 1)
            System.out.println("Use correct input of a port number");
        else
            server = new Server(Integer.parseInt(args[0]));
    }
}

