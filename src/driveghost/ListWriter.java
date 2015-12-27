package driveghost;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileWriter;
import java.io.File;

/**
 *
 * @author Daniel Munkacsi
 */
public class ListWriter{
    
    private String path;
    public static final boolean WRITE = false;
    public static final boolean APPEND = true;
    public static final String STRING_SEPARATOR = " ";
    public static final String NEWLINE_CHAR = "\r\n";
    
    public ListWriter(String p){
        path = p;
    }
   
    /**
     * Write data to file.
     * @param s the data to be written to file (NOTE: a newline character is appended to the string supplied)
     * @param mode the mode for the process. WRITE (false) or APPEND (true)
     */
    public void writeList(String s, boolean mode){
        String lines = "";
        try{
            lines = s.replace(STRING_SEPARATOR,NEWLINE_CHAR);
 
            File file = new File(path);
            //if file doesnt exists, then create it
            if(!file.exists()){
                file.createNewFile();
            }
            //true = append file
            FileWriter fw = new FileWriter(path,mode); // used to be "(file.getName(),mode);"
            BufferedWriter br = new BufferedWriter(fw);
            br.write(lines);
            br.close();
    	}catch(IOException e){
            e.printStackTrace();
    	} 
    }    
    
    /**
     * Write data to file.
     * @param s the data to be written to file (NOTE: a newline character is appended to the string supplied)
     * @param mode the mode for the process. WRITE (false) or APPEND (true)
     */
    public void writeString(String s, boolean mode){
        try{
            String data = s;
            File file = new File(path);
            //if file doesnt exists, then create it
            if(!file.exists()){
                file.createNewFile();
            }
            //true = append file
            FileWriter fw = new FileWriter(path,mode);
            BufferedWriter br = new BufferedWriter(fw);
            br.write(data);
            br.close();
    	}catch(IOException e){
            e.printStackTrace();
    	} 
    }
}