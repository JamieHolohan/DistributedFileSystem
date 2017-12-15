package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.awt.Desktop;

public class ClientThread implements Runnable {
    private Socket socket;
    private PrintWriter clientOut;
    private BufferedReader br;
    private Server server;
    private int portNumber;
    private String input;
    private String file;
    private String fileName;
    private String filePassword;
    private int fileID;
    private String fileEncoding = "UTF8";
    private String filePath;
    private String filetype;
    private Desktop desktop;
    File tempfile;
    String tempFileContent;

    public ClientThread(Server server, Socket socket) throws IOException{
        this.server = server;
        this.socket = socket;
    }

    public PrintWriter getWriter(){
        return clientOut;
    }
    
    
    public int getPortNum() {
    	return portNumber;
    }

    @Override
    public void run() {
        try{
            // setup
            this.clientOut = new PrintWriter(socket.getOutputStream(), false);
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            print("Welcome to this distributed file system");
            
            if(!Desktop.isDesktopSupported()){
            	print("Desktop is not supported, using CMD");
                while(!socket.isClosed()){
                	getMessageCMD();
                }
            }
            
            desktop = Desktop.getDesktop();
            
            // start communicating
            while(!socket.isClosed()){
            	getMessage();
            }
            
            this.socket.close();
            
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static String killService = "KILL_SERVICE";
    private static String savefile = "SAVEFILE";
    private static String exit = "EXIT";
    private static String read = "READ";
    private static String write = "WRITE";
    private static String clear = "CLEAR";
    private static String overwrite = "OVERWRITE";
    
    
    public void getMessage() throws IOException {
    	while(true) {
    		print("Enter Command (READ/WRITE/CLEAR/OVERWRITE/SAVEFILE/EXIT/KILL_SERVICE):");
        	this.input = this.br.readLine();
        	this.input.trim();
        		
       		//Respond to kill_service
       		if (this.input.regionMatches(0, killService, 0, 12)) {
       			System.out.println("Kill service initiated, server will close now");
       			System.exit(1);
       			
       		//Respond to exit	
       		}else if (this.input.regionMatches(0, exit, 0, 4)) {
       			this.socket.close();
       		
       		//Respond to Read
       		}else if (this.input.regionMatches(0, read, 0, 4)) {
       			this.file = getFileName();
       			print(this.file);
       			
       			try {
       				String cont = server.read(this.file, fileEncoding);
       				tempfile = File.createTempFile(this.file, ".txt");
       				tempfile.deleteOnExit();
           			
           			Writer out = new OutputStreamWriter(new FileOutputStream(tempfile, true), fileEncoding);
        			out.write(cont);
        			out.flush();
        			out.close();
        			
        			log("Temp file : " + tempfile.getAbsolutePath());
           			
        			if(tempfile.exists()) {
           				desktop.open(tempfile);	
           			}
       			} catch (Exception e) {
       				print("ERROR: FILENAME INVALID");
       			}
       			
       			
       		//Respond to write
       		}else if (this.input.regionMatches(0, write, 0, 5)) {
       			this.file = getFileName();
       			print(this.file);
       			
       			//Get Password for Writing
       			print ("Enter file admin password:");
       			this.input = this.br.readLine();
       			this.input.trim();
       			filePassword = this.input;
       			
       			if (filePassword == "password") {
       				try {
       					String cont = server.read(this.file, fileEncoding);
       					File tempfile = File.createTempFile(this.file, ".txt");
       					tempfile.deleteOnExit();
           			
           				Writer out = new OutputStreamWriter(new FileOutputStream(tempfile, true), fileEncoding);
           				out.write(cont);
        				out.flush();
        				out.close();
        			
        				log("Temp file : " + tempfile.getAbsolutePath());
           			
        				if(tempfile.exists()) {
           					desktop.open(tempfile);	
        				}
       				} catch (Exception e) {
       					print("ERROR: FILENAME INVALID");
       				}
       			}
       			
       			
            //Respond to clear
       		}else if (this.input.regionMatches(0, clear, 0, 5)) {
       			this.fileName = getFileName();
       			
       			print ("Enter file admin password:");
       			this.input = this.br.readLine();
       			this.input.trim();
       			this.filePassword = this.input;
       			
       			clearFile(fileName);
       			
       		//Respond to Save file
       		}else if (this.input.regionMatches(0, savefile, 0, 8)) {
       			tempFileContent = readFromTempFile(tempfile);
       			writeToFile(this.file, tempFileContent);
       			tempfile.delete();
       		}
       	}
    }
    
    public void getMessageCMD() throws IOException {
    	while(true) {
    		print("Enter Command (READ/WRITE/CLEAR/OVERWRITE/KILL_SERVICE):");
        	this.input = this.br.readLine();
        	this.input.trim();
        		
       		//Respond to kill_service
       		if (this.input.regionMatches(0, killService, 0, 12)) {
       			System.out.println("Kill service initiated, server will close now");
       			System.exit(1);
       		}else if (this.input.regionMatches(0, read, 0, 4)) {
       			print("Enter fileName:");
       			this.input = this.br.readLine();
       			fileName = this.input;
       			fileName.trim();
       			
       			print ("Enter fileID:");
       			this.input = this.br.readLine();
       			this.input.trim();
       			fileID = Integer.parseInt(this.input);
       			
       			fileName = file(fileName, fileID, filePath, filetype);
           		printFileText(fileName);
           		break;
       		}else if (this.input.regionMatches(0, write, 0, 5)) {
       			print("Enter fileName:");
       			this.input = this.br.readLine();
       			fileName = this.input;
       			fileName.trim();
       			
       			print ("Enter fileID:");
       			this.input = this.br.readLine();
       			this.input.trim();
       			fileID = Integer.parseInt(this.input);
       			
       			fileName = file(fileName, fileID, filePath, filetype);

       			print ("Enter Message:");
       			this.input = this.br.readLine();
       			this.input.trim();
       			String msg = this.input; 
       			
       			writeToFile(fileName, msg);	
       		}else if (this.input.regionMatches(0, clear, 0, 5)) {
       			print("Enter fileName:");
       			this.input = this.br.readLine();
       			fileName = this.input;
       			fileName.trim();
       			
       			print ("Enter fileID:");
       			this.input = this.br.readLine();
       			this.input.trim();
       			fileID = Integer.parseInt(this.input);
       			
       			fileName = file(fileName, fileID, filePath, filetype);
       			clearFile(fileName);
       		}else if (this.input.regionMatches(0, overwrite, 0, 9)) {
       			print("Enter fileName:");
       			this.input = this.br.readLine();
       			fileName = this.input;
       			fileName.trim();
       			
       			print ("Enter fileID:");
       			this.input = this.br.readLine();
       			this.input.trim();
       			fileID = Integer.parseInt(this.input);
       			
       			fileName = file(fileName, fileID, filePath, filetype);
       			clearFile(fileName);
       			
       			print ("Enter Message:");
       			this.input = this.br.readLine();
       			this.input.trim();
       			String msg = this.input; 
       			
       			writeToFile(fileName, msg);	
       		}
       	}
    }
    
    public void print(String msg) {
    	for(ClientThread thatClient : server.getClients()){
    		PrintWriter thatClientOut = thatClient.getWriter();
    		if(thatClientOut != null) {
    			thatClientOut.println(msg);
    			thatClientOut.flush();
    			log(msg);
    		}
    	}
    }
    
    public String readFromTempFile(File tempfile) throws IOException {
    	FileReader f = new FileReader(tempfile);
		BufferedReader b = new BufferedReader(f);
		String fileCont = null;
		StringBuilder fileContents = new StringBuilder();
		String newLine = System.getProperty("line.separator");
		String line;
		while ((line = b.readLine()) != null) {
			fileContents.append(line + newLine);
		}
		fileCont = fileContents.toString();
    	b.close();
    	f.close();
    	if (fileCont != "") {
    		return fileCont;
    	}else{
    		return null;
    	}
	}

    public void printFileText(String fileName) {
    	for(ClientThread thatClient : server.getClients()){
    		PrintWriter thatClientOut = thatClient.getWriter();
    		if(thatClientOut != null) {
    			String cont = server.read(fileName, fileEncoding);
    			thatClientOut.println(cont);
    			thatClientOut.flush();
    			thatClientOut.close();
    			log(cont);
    		}
    	}
    }
    
    public void writeToFile(String fileName,String msg) {
    	server.write(fileName, fileEncoding,  msg);
    }
   
    public void clearFile(String fileName) {
    	server.clear(fileName, fileEncoding);
    }
    
    private String file(String filename, int fileID, String filepath, String filetype) {
    	String fileIDstring = Integer.toString(fileID);
    	StringBuilder fileBuilder = new StringBuilder();
    	
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
    	String filetitle = fileBuilder.toString();
    	filetitle.trim();
    	log(filetitle);
    	return filetitle;
    }
    
    private void log(String aMessage){
        System.out.println(aMessage);
      }
    
    public String getFileName() throws IOException{
    	print("Enter fileName:");
		this.input = this.br.readLine();
		this.fileName = this.input;
		this.fileName.trim();
			
		print ("Enter fileID:");
		this.input = this.br.readLine();
		this.input.trim();
		this.fileID = Integer.parseInt(this.input);

		print ("Enter file path (HIT RETURN FOR DEFAULT):");
		this.input = this.br.readLine();
		if(this.input == null) {
			this.filePath = "files/";
			this.filePath.trim();
		} else {
			this.filePath = "files/";
			this.filePath.trim();
		}
			
		print ("Enter file type (HIT RETURN FOR DEFAULT):");
		this.input = this.br.readLine();
		if(this.input.regionMatches(0, ".", 0, 1)) {
			this.filetype = this.input;
			this.filetype.trim();
		} else {
			this.filetype = ".txt";
			this.filetype.trim();
		}
		file = file(this.fileName, this.fileID, this.filePath, this.filetype);
		print(file);
		return file;
    }
}