
package mainPackage;

public class Node {
    public Integer counter;
    public Character symbol;
    public Node parent;
    public String code;
   
    public Node(Character c , Integer i){
        counter = i;
        symbol = c;
        parent = null;
        code = "";
    }
}
