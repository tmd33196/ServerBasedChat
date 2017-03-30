
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * Server class for the server based chat project
 */

public class Server {
    private Socket socket = null;
    private ServerSocket ss = null;
    private DataInputStream input = null;
    
    public Server(int port) {
        try {
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
