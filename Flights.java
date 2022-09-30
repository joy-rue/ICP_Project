import java.util.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
/**
 * uses collected information on airlines (respective airports, countries and cities involved)
 * to recommend a route to the destination country for  the user
 * @author: Ruvimbo Joy Sithole
 */
public class Flights{

    HashMap<String, String> city_countries = new HashMap(); //stores the citycountry  and the corresponding code
    HashMap<String, ArrayList<Node>> all_Routes = new HashMap<>(); //stores all the routes associated with a certain airport code


/**
 * Takes city names, country names and codes
 * @param file contains cities names, country names, codes, etc required
 * @throws FileNotFoundException if file provided doesn't exist
 */
    public void set_CountryCity(File file) throws FileNotFoundException {
        Scanner scanner_object = new Scanner(new FileReader(file));

        while (scanner_object.hasNextLine()){
            String[] line = scanner_object.nextLine().split(",");
            if(line.length == 14) { //skips the inconsistent data
                String temp = line[2] + line[3];// takes the city and country name from the read line
                city_countries.put(temp, line[4]); //value for the (citycountry) key will be the airport's code
            }
        }
    }

    /**
     * Reads from the user's file
     * @param user_input contains the user's desired source and destination
     * @return the list containing city-country  for source and for the destination
     * @throws FileNotFoundException
     */
    public ArrayList<String> userInput(File user_input) throws FileNotFoundException{
        Scanner scanner = new Scanner(new FileReader(user_input));
        ArrayList<String> source_dest = new ArrayList<>();

        while (scanner.hasNextLine()){
           String [] temp= scanner.nextLine().split(",");
            source_dest.add(temp[0] + temp[1]);
        }
        return source_dest;
    }

    /**
     * read information on routes available from the file
     * creates Nodes to represent the flights, airport destination and airline associated with each source airport
     * @param routes file containing all available routes
     * @throws FileNotFoundException if there exists no route from stated city to destination
     */
    public void routesInfor(File routes) throws FileNotFoundException{

        Scanner sc = new Scanner(new FileReader(routes));

        while (sc.hasNextLine()){
            String[] line = sc.nextLine().split(",");
            if(line.length < 9)
                continue;
            ArrayList<Node> temp = new ArrayList<>();
            Node new_destination = new Node(line[4], line[0], line[7]); //create Node  with destination code ,airline ID, No.stops

            //if the key already exists, add the new destination code found to the list
            if(all_Routes.containsKey(line[2])){
                temp = all_Routes.get(line[2]);
                temp.add(new_destination);
                all_Routes.put(line[2], temp);
            }
            temp.add(new_destination);
            all_Routes.put(line[2], temp); //adding an array to hold the Node destinations associated with the current airport code

        }
    }

    /**
     * Bakctracks Nodes from the found destination to the start airport
     * @param end the found destination airport
     * @return a list/ generations of Nodes that lead to the destination
     */
    public ArrayList<Node> solution(Node end){
        ArrayList<Node> result = new ArrayList<>();
        Node child = end;

        while (child.getParent()!= null){
            result.add(child);
            child = child.getParent();
        }

        Collections.reverse(result);

        return result;
    }


    /**
     * Find the route from the given source to destination
     * @param source_destination provided by user
     * @return the route found
     */
    public ArrayList<Node> generateRoute(ArrayList<String> source_destination)
    {
        LinkedList<Node> frontier = new LinkedList<>();
        HashSet<String> explored = new HashSet<>();
        String source_code = "";
        String destination_code = "";

        if(city_countries.containsKey(source_destination.get(0))){
            source_code = city_countries.get(source_destination.get(0));
            Node curr_node = new Node(source_code, null,null);
            frontier.add(curr_node); //add start airport Node to the frontier
        }
        else{
            return null;//source airport does not exist
        }
        if(city_countries.containsKey(source_destination.get(1))) {
            destination_code = city_countries.get(source_destination.get(1));
        }
        else{
            return null; //destination airport does not exit
        }

        //To find the route to user's destination airport:
        //explore the breadth of all the destination nodes connected to the source airport
        while (!frontier.isEmpty())
        {
            Node src = frontier.removeFirst(); //explore nodes in FIFO sequence
            ArrayList<Node> src_routes = all_Routes.get(src.getCode());
            explored.add(src.getCode()); // +1 explored Node

            //if any destination node is == to user's destination return solution
            if(src_routes!= null) {
                for (Node potential : src_routes) {
                    System.out.println(potential.getCode() + " the parent  " + potential.getParent() + "");
                    potential.setParent(src);
                    if (potential.getCode().equals(destination_code)) {
                        return solution(potential);
                    }

                    if (!explored.contains(potential.getCode()) && !frontier.contains(potential)) {
                        frontier.add(potential); //to be explored later
                    }
                }
            }
        }
        return null; //failed: no path/route found
    }

//-------------------------------------------DRIVER'S CODE -----------------------------------------------------------//

    public static void main(String[] args)
    {
        Flights demoProgram = new Flights();

        // Program files: Access airport, airline, and routes data
        try {
            demoProgram.set_CountryCity(new File("airports.csv"));
            demoProgram.routesInfor(new File("routes.csv"));
        }

        catch (FileNotFoundException fnfe){
            fnfe.getMessage();
        }

        //USER
        try{
            ArrayList<String> source_destination = demoProgram.userInput(new File("userFile.txt"));
            ArrayList<Node> result = demoProgram.generateRoute(source_destination);

            if (result == null)
                System.out.println(" There exists no route from " + source_destination.get(0) +
                        " to your desired destination " + source_destination.get(1));
            else{
                int stops = 0;
                for(int r = 0 ; r < result.size(); r++){
                    System.out.println(r + 1  + ". " + result.get(r));
                }
                System.out.println("Total flights: "+ result.size()+ "\n" + " Total additional stops: " + stops);
            }
        }

        catch (FileNotFoundException fnfe){
            System.out.println("File passed could not be found");
        }
    }
}