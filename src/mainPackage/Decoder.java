package mainPackage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Decoder
{
	private HashMap<Character , String> code;
        private File fileObj;
        private FileInputStream fis;
        private ObjectInputStream ois;
        private String buffer;
        int redendant;
	
	public Decoder(String path) 
        {
                code = new HashMap<Character ,String>();
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
                code.put(arr[i].charAt(0), arr[i+1]);
            }
            System.out.println(code);// print to the console
        }
	
	public void readCompressedData() {
            try {
                // get number of bits of last integer in the compressed data
                redendant = ois.readInt();
                System.out.println(redendant);
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
                
                for(int i=0; i< 8; i++ ){
                    temp = ois.readInt();
                    System.out.println(temp);
                    
                }
                
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
//	
        public void displayCode()
        {
            for(Character c :code.keySet()){
                String s = code.get(c);
                System.out.printf("%s ==> %s\n" , c , s);
            }
        
        }
        
//	public void decompress() {
//            getCode();
//            String newFilePath = fileObj.getPath().substring(0, (int) (fileObj.getPath().length()-15)) + "_decompressed.txt";
//            try {
//            newFile = new Formatter(newFilePath);
//            }
//            catch(Exception e) {
//                JOptionPane.showMessageDialog(null, "somthing went wrong with creating a new file", "Erorr", JOptionPane.PLAIN_MESSAGE);
//            }
//            int c , p = -1; // current , previous code
//            c = code.get(0);
//            System.out.print(dict.get(c));
//            newFile.format("%s", dict.get(c) );
//            for(int i=1 ; i<code.size(); i++) {
//                    // update the previous character to the current character
//                    p = c;
//                    c = code.get(i);
//
//                    if(c < dict.size()) { // check if current code in the dictionary
//                            // if it is,
//                            // output the string of the current value
//                            System.out.print(dict.get(c));
//                            newFile.format("%s", dict.get(c) );
//                            // add new entry ==> string of previous code + first character of current code
//                            String str = dict.get(p) + dict.get(c).charAt(0);
//                            dict.add(str);
//
//                    }else {
//                            // if not,
//                            // create string of previous code + first character of previous code
//                            String str = dict.get(p) + dict.get(p).charAt(0);
//                            // output the string of the current value & add it as a new entry
//                            System.out.print(str);
//                            newFile.format("%s", str );
//                            dict.add(str);
//                    }
//		}
//                newFile.close();
//	}
	
}
