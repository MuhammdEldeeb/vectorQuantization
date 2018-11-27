package mainPackage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Formatter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Decoder
{
	private HashMap<String , Character> code;
        private File fileObj;
        private FileInputStream fis;
        private ObjectInputStream ois;
        private String buffer;
        private int redendant;
        private int numOfInts;
	
	public Decoder(String path) 
        {
                code = new HashMap<String , Character>();
                buffer = "";
                
            try {
                fileObj = new File(path);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "somthing went wrong with opening compressed file", "Erorr", JOptionPane.PLAIN_MESSAGE);
            }
            
            try {
                fis = new FileInputStream(fileObj.getPath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "somthing went wrong with opening compressed file", "Erorr", JOptionPane.PLAIN_MESSAGE);
            }
            
            try {
                ois = new ObjectInputStream(fis);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "somthing went wrong with opening compressed file", "Erorr", JOptionPane.PLAIN_MESSAGE);
            }
            
	}
        
        public void setCode(String str)
        {
            String arr[] = str.split(":");
            for(int i=0 ; i<arr.length; i +=2){
                code.put(arr[i+1] , arr[i].charAt(0));
            }
            System.out.println(code);// print to the console
        }
	
	public void readCompressedData()
        {
            try {
                // get number of bits of last integer in the compressed data
                redendant = ois.readInt();
                System.out.println(redendant);
                // get number of integers
                numOfInts = ois.readInt();
                // get all characters with thier codes
                char ch;
                String str = "";
                ch = ois.readChar();
                while(ch  != ';'){
                    str += ch;
                    ch = ois.readChar();    
                }
                System.out.println(str);
                /*************************/
                setCode(str);
                /************************/
                int temp;
                // get binary stream
                for(int i=0; i< numOfInts; i++ ){
                    temp = ois.readInt();
                    String s = Integer.toBinaryString(temp);
                    StringBuilder s1 = new StringBuilder(s);
                    s = s1.reverse().toString();
                    for(int j = s.length(); j<31; j++){
                        s += '0';
                    }
                    StringBuilder s2 = new StringBuilder(s);
                    s = s2.reverse().toString();
                    if(redendant>0 && i == numOfInts)
                        buffer += s.substring(0 , redendant);
                    else
                        buffer += s;
                    System.out.println(temp);
                    
                }
                System.out.println("buffer ==> " + buffer);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "somthing went wrong during reading compressed file", "Erorr", JOptionPane.PLAIN_MESSAGE);
            }
            
            try {
                ois.close();
            } catch (IOException ex) {
                Logger.getLogger(Decoder.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(Decoder.class.getName()).log(Level.SEVERE, null, ex);
            }
            
	}
	
        public void displayCode()
        {
            for(String s :code.keySet()){
                Character c = code.get(s);
                System.out.printf("%s ==> %s\n" , s , c);
            }
        
        }
        
        public void decompress()
        {
            // pre functions
            readCompressedData();
            
            String data = ""; // original data
            String codeStr = "";
            for(int i=0;i <buffer.length(); i++){
                codeStr += buffer.charAt(i);
                if(code.containsKey(codeStr)){
                    data += code.get(codeStr);
                    codeStr = "";
                }
            }
            System.out.println("data == > " + data);
            
            // wite the original data on the decompressed file
            Formatter newFile = null;
            String newFilePath = fileObj.getPath().substring(0, (int) (fileObj.getPath().length()-15)) + "_decompressed.txt";
            try {
            newFile = new Formatter(newFilePath);
            }
            catch(Exception e) {
                JOptionPane.showMessageDialog(null, "somthing went wrong with creating a new file", "Erorr", JOptionPane.PLAIN_MESSAGE);
            }
            
            newFile.format("%s", data );
            newFile.close();
        }
	
}
