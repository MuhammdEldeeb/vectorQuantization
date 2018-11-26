package mainPackage;

import java.io.File;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class Decoder {
	private ArrayList<String> dict;
	private ArrayList<Integer> code;
	private Formatter newFile;
        private Scanner reader;
        private File fileObj;
	
	public Decoder(String path) {
            // initialize dictionary with ascii code
            dict = new ArrayList<String>();
            for(int i=0 ; i<= 127; i++) {
                    char ch = (char)i;
                    dict.add(ch+"");
            }

            code = new ArrayList<Integer>();
                
            try {
                fileObj = new File(path);
                reader = new Scanner(new File(fileObj.getPath()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "somthing went wrong with opening file", "Erorr", JOptionPane.PLAIN_MESSAGE);
            }
	}
	
	public void getCode() {
            // get all compressed data 
            while(reader.hasNext()){
                String a = reader.next();
                int aa = Integer.parseInt(a);
                code.add(aa);
                System.out.printf("<%d>\n" , aa );
            }
            reader.close();
	}
	
	public void decompress() {
            getCode();
            String newFilePath = fileObj.getPath().substring(0, (int) (fileObj.getPath().length()-15)) + "_decompressed.txt";
            try {
            newFile = new Formatter(newFilePath);
            }
            catch(Exception e) {
                JOptionPane.showMessageDialog(null, "somthing went wrong with creating a new file", "Erorr", JOptionPane.PLAIN_MESSAGE);
            }
            int c , p = -1; // current , previous code
            c = code.get(0);
            System.out.print(dict.get(c));
            newFile.format("%s", dict.get(c) );
            for(int i=1 ; i<code.size(); i++) {
                    // update the previous character to the current character
                    p = c;
                    c = code.get(i);

                    if(c < dict.size()) { // check if current code in the dictionary
                            // if it is,
                            // output the string of the current value
                            System.out.print(dict.get(c));
                            newFile.format("%s", dict.get(c) );
                            // add new entry ==> string of previous code + first character of current code
                            String str = dict.get(p) + dict.get(c).charAt(0);
                            dict.add(str);

                    }else {
                            // if not,
                            // create string of previous code + first character of previous code
                            String str = dict.get(p) + dict.get(p).charAt(0);
                            // output the string of the current value & add it as a new entry
                            System.out.print(str);
                            newFile.format("%s", str );
                            dict.add(str);
                    }
		}
                newFile.close();
	}
	
}
