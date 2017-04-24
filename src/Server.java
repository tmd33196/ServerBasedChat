
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Random;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

/*
 * Server class for the server based chat project
 */

public class Server {
    
    /*private HashMap<String, String> secretKeys;
    private HashMap<String, String> xRES;
    private HashMap<String, String> CK_A;*/
    
    private DatagramSocket serverSocket;
    byte[] receiveData = new byte[1024];
    byte[] sendData = new byte[1024];
    
    public Server(int port) { 
        /*secretKeys = new HashMap<>();
        xRES = new HashMap<>();
        CK_A = new HashMap<>();
        secretKeys.put("A", "1234");
        secretKeys.put("B", "1234");*/
        
        ConnectedClients cc = new ConnectedClients();
        cc.addSecretKey("A", "1234");
        cc.addSecretKey("B", "1234");
        
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
                        //String secretKey = secretKeys.get(dataArray[1]);
                        String secretKey = cc.getSecretKey(dataArray[1]);
                        if(secretKey != null) {
                            String ran = Integer.toString(random.nextInt(1000));
                            //xRES.put(dataArray[1], A3(ran, secretKey));
                            //CK_A.put(dataArray[1], A8(ran, secretKey));
                            cc.addXRES(dataArray[1], A3(ran, secretKey));
                            cc.addCKA(dataArray[1], A8(ran, secretKey));
                            System.out.println(ran + secretKey);
                            output = "CHALLENGE(" + ran + ")";
                        }
                        else {
                            System.out.println("ERROR: client not in the list of clients");
                            output = "ERROR: Not on client list";
                        }
                        
                        break;
                    case ("RESPONSE"):
                        //dataArray[1] contains the clientID
                        //dataArray[2] contains the RES
                        
                        //if(xRES.get(dataArray[1]).equals(dataArray[2])) {
                        if(cc.getXRES(dataArray[1]).equals(dataArray[2])) {
                            TCPServerThread tcp = new TCPServerThread(nextPort, dataArray[1], cc);//CK_A.get(dataArray[1]));
                            Thread thread = new Thread(tcp);
                            thread.start();

                            output = "AUTH_SUCCESS(" + random.nextInt(1000) + ", " + nextPort++ + ")";
                            output = encrypt(output, cc.getCKA(dataArray[1]));//CK_A.get(dataArray[1]));
                        }
                        else {
                            System.out.println("ERROR: Client response did not match xRES");
                            output = "AUTH_FAIL";
                        }
                        
                        //TODO: encrypt string
                        break;
                }
                
                //Send a packet back
                InetAddress returnIPAddress = receivePacket.getAddress();
                int returnPort = receivePacket.getPort();
                System.out.println(output);
                sendData = output.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(output.getBytes(), output.getBytes().length, returnIPAddress, returnPort);
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		m.reset();
		m.update(CK_A.getBytes());
		byte[] digest = m.digest();
		return digest;
	}
    //Whats currently not working
    private String encrypt(String strClearText,byte[] digest) throws Exception{
		/*
		 * String strData="";
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
		 */
		return strClearText;
	}
    
    public static void main(String args[]){
        Server server = new Server(9879);
        if(args.length != 1)
            System.out.println("Use correct input of a port number");
        else
            server = new Server(Integer.parseInt(args[0]));
    }
}
