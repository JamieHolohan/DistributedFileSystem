package Server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {

    private static final int portNumber = 4242;

    private int serverPort;
    private static InetAddress serverIP;
    static Server server;
    
    String filename = "file";
    String filepath = "files/";
    String filetype = ".txt";
    String fileEncoding = "UTF8";
    int fileID = 0001;

    public static void main(String[] args) throws UnknownHostException{
        server = new Server(portNumber);
        serverIP = InetAddress.getLocalHost();
        server.startServer();
    }

    public Server(int portNumber){
        this.serverPort = portNumber;
    }

    public int getPortNumber() {
    	return portNumber;
    }
    
    public InetAddress getServerIP() {
    	return serverIP;
    }

    private void startServer(){
      server.runServer();
    }
    
    private void runServer(){
    	String file = file(filename, fileID, filepath, filetype);
    	server.read(file, fileEncoding);
    	server.write(file, fileEncoding);
    	server.read(file, fileEncoding);
     }
    
   // @SuppressWarnings("resource")
	private void read(String file, String fileEncoding){
    	try {
			Scanner scanner = new Scanner(new FileInputStream(file), fileEncoding);
			StringBuilder fileContents = new StringBuilder();
			String newLine = System.getProperty("line.separator");
			while (scanner.hasNextLine()){
				fileContents.append(scanner.nextLine() + newLine);
			}
	    	String fileCont = fileContents.toString();
	    	log(fileCont);
	    	scanner.close();
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}
    }
    
    private void write(String file, String fileEncoding){
    	String newLine = System.getProperty("line.separator");
	    int counter = 0;
    	
		try {
			Writer out = new OutputStreamWriter(new FileOutputStream(file, true), fileEncoding);
			//Writer out = new FileWriter(file, true);
			out.write("Line " + counter + newLine);
			out.close();
	    }catch (Exception e) {
	    	e.printStackTrace();
	    }
	 }


    private void log(String aMessage){
        System.out.println(aMessage);
      }
    
    private String file(String filename, int fileID, String filepath, String filetype) {
    	String fileIDstring = Integer.toString(fileID);
    	StringBuilder fileBuilder = new StringBuilder();
    	
    	//fileBuilder.append(System.getProperty("user.dir"));
    	fileBuilder.append(filepath);
    	fileBuilder.append(filename);
    	if (fileID < 10) {
    		fileBuilder.append("000");
    		fileBuilder.append(fileIDstring);
    	}else if (fileID < 100){
    		fileBuilder.append("00");
    		fileBuilder.append(fileIDstring);
    	}else if (fileID < 1000){
    		fileBuilder.append("0");
    		fileBuilder.append(fileIDstring);
    	}else if (fileID < 10000){
    		fileBuilder.append(fileIDstring);
    	}
    	fileBuilder.append(filetype);
    	String file = fileBuilder.toString();
    	log(file);
    	return file;
    }
}
