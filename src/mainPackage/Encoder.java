package mainPackage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

public class Encoder 
{
        private HashMap<Character , Integer>freq; // freq of each cahr in the file
        private HashMap<Character , String>code; // code of each cahr in the file
        private ArrayList<Node> list;
	private FileReader reader;
        private File fileObj;
        private Formatter newFile;
	
	
    public Encoder(String str)
    {
        freq = new HashMap<Character , Integer>();
        list = new ArrayList<Node>();
        code = new HashMap<Character , String>();
        try {
            fileObj = new File(str);
            reader = new FileReader(fileObj.getPath());
        }catch (Exception ex) {
        JOptionPane.showMessageDialog(null, "somthing went wrong with opening file", "Erorr", JOptionPane.PLAIN_MESSAGE);
        }

    }
	
    public void getFreq() 
    {
        // iterate all the characters in the charstream to get the freq and store them into map "freq"
        int ch;
        try {
            while((ch = reader.read()) != -1) {
                // get freq of each char in the file i want to compresss
                Character c = (char)ch;
                int x;
                if(freq.containsKey(c)){
                    x = freq.get(c);
                    freq.put(c, x+1);
                }else{
                    freq.put(c, 1);
                }
            }
        } catch (Exception e) {
         JOptionPane.showMessageDialog(null, "somthing went wrong with reading fom file", "Erorr", JOptionPane.PLAIN_MESSAGE);
        }
        try {
            reader.close();
        } catch (Exception ex) {
           JOptionPane.showMessageDialog(null, "somthing went wrong with closing file", "Erorr", JOptionPane.PLAIN_MESSAGE);
        }
        fillList(freq);
    }
        
    public void fillList(Map<Character, Integer> map)  
    { // cope all the element from the map to the arrayList "list"
        if (map.isEmpty())  
        { 
            JOptionPane.showMessageDialog(null, "choosen file is empty", "Erorr", JOptionPane.PLAIN_MESSAGE);
        } 
          
        else
        { 
            for(Character key : map.keySet()){
                Integer value = freq.get(key);
                Node n = new Node(key , value);
                list.add(n);
            }
        } 
        sortList(list);
    }

    public void sortList(ArrayList<Node> arr)
    {
        // sort the arrayList desceding
        int i , j;
        Node temp;
        boolean swapped;
        for(i = 0 ; i<arr.size()-1; i++){
            swapped = false;
            for(j = 0; j < arr.size()-i-1  ; j++){
                if(arr.get(j).counter < arr.get(j+1).counter){
                    temp = arr.get(j);
                    arr.set(j, arr.get(j+1));
                    arr.set(j+1, temp);
                    swapped = true;
                }
            }
            if(swapped == false)
                break;
        }
    }
    
    public void displayList()
    {
        for(Node node : list){
            System.out.printf("%s ==> %d ==> %s\n" , node.symbol , node.counter , node.code);
        }
    }
    
    public void setCode(ArrayList<Node> arr)
    {
        // break condition
        if(arr.size() == 2){
            arr.get(0).code = "0";
            arr.get(1).code = "1";
        }else{
            ArrayList<Node> newArr = new ArrayList<Node>();
            for(int i= 0 ; i< arr.size()-2; i++){
                newArr.add(arr.get(i));
            }
            Integer count = arr.get(arr.size()-1).counter + arr.get(arr.size()-2).counter;
            Node newNode = new Node(null , count);
            arr.get(arr.size()-1).parent = newNode;
            arr.get(arr.size()-2).parent = newNode;
            newArr.add(newNode);
            sortList(newArr);
            /*********************/
            setCode(newArr);
            /*********************/
            int digit = 1;
            for(Node n : arr){
                if(n.parent != null){
                    digit = ++digit %2;
                    if(digit == 0){
                        n.code = n.parent.code + "0";
                        n.parent = null;
                    }else if(digit == 1){
                        n.code = n.parent.code + "1";
                        n.parent = null;
                    }
                }
            }
            
        }
    }
    
    public void fillCode()
    {
        for(Node n : list){
            Character c = n.symbol;
            String s = n.code;
            code.put(c , s);
        }
    }
    
    public String getBinaryStream()
    {
        String buffer = "";
        FileReader re = null; // object to read the original data content
            try {
                re = new FileReader(fileObj.getPath());
                int ch;
                
                while((ch = re.read()) != -1){
                    char c = (char)(ch);
                    buffer += code.get(c);
                }
                re.close();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "somthing went wrong with opening file to store binary stream", "Erorr", JOptionPane.PLAIN_MESSAGE);
            }
            
            
        return buffer;
    }
    
    public void compress()
    {
        // pre functions should be called 
        getFreq();
        setCode(list);
        displayList();
        fillCode();
        String buffer = getBinaryStream();
        
        //calculate number of bits of last integer
        int last_int_bist = buffer.length()%31;
        // calculat number of iteration
        int iterationNumber = buffer.length()/31;
                
        // write the overhead and the binary stream to the compressed file
        String newFilePath = fileObj.getPath().substring(0, (int) (fileObj.getPath().length()-4)) + "_compressed.txt";
        FileOutputStream fos= null;
        ObjectOutputStream oos = null;
            try {
                fos = new FileOutputStream(newFilePath); // create an object of fileOutputStream
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "somthing went wrong with creating an object of fileOutputStream", "Erorr", JOptionPane.PLAIN_MESSAGE);
            }
            try {
                oos = new ObjectOutputStream(fos); // create an object of ObjectOutputStream
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "somthing went wrong with creating an object of ObjectOutputStream", "Erorr", JOptionPane.PLAIN_MESSAGE);
            }
            try {
                // write the overhead
                oos.writeInt(last_int_bist); // #of bits of the last int
                if(last_int_bist == 0)
                    oos.writeInt(iterationNumber); // #of ints
                else
                    oos.writeInt(iterationNumber + 1);
                for(Character ch: code.keySet()){
                    String val = code.get(ch);
                    oos.writeChar(ch); // write all characters
                    oos.writeChar(':');
                    oos.writeChars(val); // write all codes of all characters
                    oos.writeChar(':');
                }
                oos.writeChar(';'); // the end of the overhead
                
                // write the binary stream
                int limit = 0;
                String str ;
                int temp;
                System.out.println("buffer ==> " + buffer);
                System.out.println("size ==> " + buffer.length());
                System.out.println("#iteration ==> " + iterationNumber);
                
                for(int i=0 ; i<iterationNumber; i++){
                    str = buffer.substring(limit, limit+31);
                    //System.out.println(str);
                    limit += 31;
                    //convert every 31 bit into integer
                    temp = Integer.parseInt(str , 2);
                    System.out.println(str + " ==> " + temp);
                    // write the iteger
                    oos.writeInt(temp);
                }
                if(last_int_bist > 0 ){
                    // write last integer
                    str = buffer.substring(limit);
                    System.out.println(last_int_bist);
                    for(int i=last_int_bist ; i<31; i++){
                        str += '0';
                    }
                    temp = Integer.parseInt(str , 2);
                    System.out.println(str + " ==> " + temp);
                    oos.writeInt(temp);
                }
                
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "somthing went wrong during writting the compressed data", "Erorr", JOptionPane.PLAIN_MESSAGE);
            }
            
            // close objects assosiated with files
            try {
                oos.close();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "somthing went wrong with closing an object of ObjectOutputStream", "Erorr", JOptionPane.PLAIN_MESSAGE);
            }
            try {
                fos.close();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "somthing went wrong with closing an object of ObjectOutputStream", "Erorr", JOptionPane.PLAIN_MESSAGE);
            }

    }
    
}