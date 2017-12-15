package Server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Scanner;

public class ProjectFile {

	public String file;
	private String fileEncoding = "UTF8";
	private boolean isLocked = false;
	
	public ProjectFile(String fileName) {
		file = fileName;
	}

	public void writeToFile(String msg) {
		String newLine = System.getProperty("line.separator");
    	//msg = decrypt(msg);
		try {
			Writer out = new OutputStreamWriter(new FileOutputStream(this.file, false), fileEncoding);
			out.write(msg + newLine);
			out.flush();
			out.close();
	    }catch (Exception e) {
	    	e.printStackTrace();
	    }
	}

	public String readFromFile() {
		String fileCont = null;
		StringBuilder fileContents = new StringBuilder();
		String newLine = System.getProperty("line.separator");
    	try {
			Scanner scanner = new Scanner(new FileInputStream(this.file), fileEncoding);
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
	
	public void setIsLocked(boolean value) {
		isLocked = value;
	}
}