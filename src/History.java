import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class History {
    
    public String filePath;
    public String[] parts;
    
    public History(String filePath){
        this.filePath = filePath;
    }
    
    public History() {
		// TODO Auto-generated constructor stub
	}

	public void addMessage(String msg, String sender){
		parts = msg.split("(,)");
		try {
			PrintWriter out = null;
			File f = new File(filePath);
			if ( f.exists() && !f.isDirectory() ) {
				out = new PrintWriter(new FileOutputStream(new File(filePath), true));
				out.append(System.getProperty("file.separator"));
				out.append("\r\n");
				out.append(sender + ":" + msg +System.getProperty("file.separator"));
				out.println(System.getProperty("file.separator"));
				out.close();
			}
			else {
				out = new PrintWriter(filePath);
				out.println(System.getProperty("file.separator"));
				out.append("\r\n");
				out.append("From:" + sender + ":" + msg +System.getProperty("file.separator"));
				out.append(System.getProperty("file.separator"));
				out.close();
			}
		 }catch (IOException e) {

			e.printStackTrace();

		}}
	
public String printToconsole(){
	String s=null;
	try {
	BufferedReader in = new BufferedReader(new FileReader(filePath));
	String line = in.readLine();
	
	while(line != null)
	{
		 s = new StringBuilder()
		           .append(line).toString();    		    
	  line = in.readLine();
	}
	in.close();}
	catch (IOException e) {

		e.printStackTrace();}
	return s;

	
	
}}
