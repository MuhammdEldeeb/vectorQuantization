package mainPackage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Encoder {
        private HashMap<Character , Integer>freq; // freq of each cahr in the file
        private HashMap<Character , String>code; // code of each cahr in the file
        private ArrayList<Node> list;
	private FileReader reader;
        private File fileObj;
        private Formatter newFile1;
	
	
    public Encoder(String str) {
        freq = new HashMap<Character , Integer>();
        list = new ArrayList<Node>();
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
            code.put(c, s);
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
                    buffer += code.get(ch);
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
        

    }
    
}