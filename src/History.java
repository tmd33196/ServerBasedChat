import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class History {
    
    public String filePath;
    public String[] parts;
    
    private HashMap<Integer, ArrayList<String>> history; 
    
    public History(String filePath){
        this.filePath = filePath;
    }
    
    public History() {
        history = new HashMap<>();
    }

    public void addMessage(String msg, String sender, String receiver, int session){

        String save = session + " From: " + sender + " To: " + receiver + " " + msg;

        if(history.get(session) != null) {
            System.out.println("TRUE");
            history.get(session).add(save);
        }
        else {
            System.out.println("FLASE");
            history.put(session, new ArrayList<String>());
            history.get(session).add(save);
        }
    }
    
    public ArrayList<String> getHistory(String clientA, String clientB) {
        ArrayList<String> arr = new ArrayList<>();
        for(Entry<Integer, ArrayList<String>> s : history.entrySet()) {
            for(String str : s.getValue()) {
                if(str.contains("From: " + clientA) && str.contains("To: " + clientB) || str.contains("From: " + clientB) && str.contains("To: " + clientA)) {
                    arr.add(str);
                }
            }
        }
        return arr;
    }
        
        
    public String printToconsole(){
        
        String returnStr = "";
        for(Entry<Integer, ArrayList<String>> s : history.entrySet()) {
            for(String str : s.getValue()) {
                returnStr += str + "\n";
            }
            returnStr += "\n";
        }
        return returnStr;
        
        
        /*String s=null;
	String line = null;
	try {
		s = new String(Files.readAllBytes(Paths.get(filePath)));
	}

	catch (IOException e) {

		e.printStackTrace();}
	return s;*/
    }
}
