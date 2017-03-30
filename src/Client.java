
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
//import java.io.DataInputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/*
 * Client class for the server based chat project
 */

public class Client {
    private Socket socket = null;
    private BufferedReader console = null;
    private DataOutputStream output = null;
    
    public Client(String serverName, int serverPort){
        System.out.println("Connecting...");
        try{
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
        }
    }
    
    public static void main(String args[]){
        Client client = null;
        
        if(args.length != 2)
            System.out.println("Use correct input, host name, port number");
        else
            client = new Client(args[0], Integer.parseInt(args[1]));
    }
}
