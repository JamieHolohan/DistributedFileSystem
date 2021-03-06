package Server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
    private List<ClientThread> clients; // or "protected static List<ClientThread> clients;"
    private List<ProjectFile> files; // or "protected static List<ClientThread> clients;"
    
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
    
    public List<ClientThread> getClients(){
        return clients;
    }
    
    private void runServer(){
    	 clients = new ArrayList<ClientThread>();
         ServerSocket serverSocket = null;
         try{
             serverSocket = new ServerSocket(serverPort);
             acceptClients(serverSocket);
         }catch (IOException e){
             System.err.println("Could not listen on port: "+serverPort);
             System.exit(1);
         }
     }
    
   // @SuppressWarnings("resource")
	String read(String file, String fileEncoding){
		String fileCont = null;
		StringBuilder fileContents = new StringBuilder();
		String newLine = System.getProperty("line.separator");
    	try {
			Scanner scanner = new Scanner(new FileInputStream(file), fileEncoding);
			while (scanner.hasNextLine()){
				fileContents.append(scanner.nextLine() + newLine);
			}
	    	fileCont = fileContents.toString();
	    	scanner.close();
		}catch (FileNotFoundException e){
			e.printStackTrace();
			fileContents.append("The file " + file + " does not exist!");
			fileCont = fileContents.toString();
		}
    	//encrypt(fileCont);
		return fileCont;
    }
    
    void write(String file, String fileEncoding, String msg){
    	String newLine = System.getProperty("line.separator");
    	//msg = decrypt(msg);
		try {
			Writer out = new OutputStreamWriter(new FileOutputStream(file, false), fileEncoding);
			out.write(msg + newLine);
			out.flush();
			out.close();
	    }catch (Exception e) {
	    	e.printStackTrace();
	    }
	 }
    
    void clear(String file, String fileEncoding){
		try {
			Writer out = new OutputStreamWriter(new FileOutputStream(file, false), fileEncoding);
			out.write("");
			out.flush();
			out.close();
	    }catch (Exception e) {
	    	e.printStackTrace();
	    }
	 }
    
    private void acceptClients(ServerSocket serverSocket){
        System.out.println("server starts port = " + serverSocket.getLocalSocketAddress());
        while(true){
            try{
                Socket socket = serverSocket.accept();
                System.out.println("accepts : " + socket.getRemoteSocketAddress());
                ClientThread client = new ClientThread(this, socket);
                Thread thread = new Thread(client);
                thread.start();
                clients.add(client);
            }catch (IOException ex){
                System.out.println("Accept failed on : "+serverPort);
            }
        }
    } 
    
    private String encrypt(String msg) {
		String encryptedMSG;
		char letter;
		StringBuilder fileBuilder = new StringBuilder();
		
		while(msg != null) {
			letter = msg.charAt(0);
			//System.out.println(letter);
			msg = msg.substring(1);
			//System.out.println(msg);
			letter = (char)(letter + 5) ;
			//letter.toString();
			//System.out.println(letter);
			
			fileBuilder.append(letter);
		}
		System.out.println("Yes");
		encryptedMSG = fileBuilder.toString();
		return encryptedMSG;	
    }
    
    private String decrypt(String msg) {
    	String decryptedMSG;
		char letter;
		StringBuilder fileBuilder = new StringBuilder();
		
		while(msg != null) {
			letter = msg.charAt(0);
			msg = msg.substring(1);
			letter = (char)(letter - 5) ;
			
			fileBuilder.append(letter);
		}
		decryptedMSG = fileBuilder.toString();
    	return decryptedMSG;	
    }
}
