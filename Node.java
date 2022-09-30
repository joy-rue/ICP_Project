/**
 * Class that gives each airport associated with a particular airline, a structure
 * airport is not assignment to parent until a specific route needs to be generated
 */

public class Node{
    private String code;
    private Node parent;
    private String Airline;
    private int num_stops = 0;

    public Node(String code, String Airline, String stops)
    {
        this(code, null, Airline, stops);
    }

    public Node(String code, Node parent,String Airline, String stops)
    {
        this.code = code;
        this.parent = parent;
        this.Airline = Airline;
        if (stops !=null)
            this.num_stops = Integer.parseInt(stops);
    }

    public String getCode(){
        return code;
    }

    public Node getParent(){
        return parent;
    }

    public String getAirlineCode(){
        return Airline;
    }

    public int getNumberStops(){
        return num_stops;
    }

    public void setParent(Node p){
        parent = p;
    }

    @ Override
    public String toString(){
        String n = Airline;
        if(parent != null)
                n += " from " + parent.getCode();
                n += " to " + code + " " + num_stops + " stops";
        return n;
    }

}