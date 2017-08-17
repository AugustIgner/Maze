import java.awt.Point;
import java.util.ArrayList;

public class Node{
    private Point cordinates;
    private Node parent;
    ArrayList<Node> children;
    private int depth;
    private int status; //1 visited; 2 explored;
    private int fromDirection;
    private int enteredFromDirection;
    private int weight;

    public Node(Point cordinates, int depth, Node parent, int fromDirection, int enteredFromDirection, int weight){
        this.cordinates = cordinates;
        this.depth = depth;
        this.parent = parent;
        this.fromDirection = fromDirection;
        this.enteredFromDirection = enteredFromDirection;
        this.status = 1;
        this.weight = weight;
    }

    public Node(Point cordinates){
        this.cordinates = cordinates;
        this.depth = 0;
        this.parent = null;
        this.fromDirection = 0;
        this.enteredFromDirection = 0;
        this.status = 1;
        this.weight = 0;
    }

    public Point getLocation(){
        return cordinates;
    }

    public int getEnteredFrom(){
        return enteredFromDirection;
    }

    public int getFromDirection(){
        return fromDirection;
    }

    public Node getParent(){
        return parent;
    }

    public void addChild(Node node){
        if (children == null) children = new ArrayList<Node>();
        children.add(node);
    }

    public boolean isEqual(Node node){
        return cordinates.equals(node.getLocation());
    }

    public ArrayList getChildren(){
        return children;
    }

    public boolean isChildOf(Node node){
        return parent.isEqual(node);
    }

    public boolean hasChildNode(Node node){
        return children.contains(node);
    }


    public void changeStatus(int status){
        this.status = status;
    }

    public int getStatus(){
        return status;
    }
}