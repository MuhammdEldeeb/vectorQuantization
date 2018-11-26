
package mainPackage;

public class Node {
    public Integer counter;
    public Character symbol;
    public Node parent;
    //public Node right;
    //public Node left;
    public String code;
   
    public Node(Character c , Integer i){
        counter = i;
        symbol = c;
        parent = null;
        //right = null;
        //left = null;
        code = "";
    }
}
