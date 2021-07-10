import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;


public class Driver {
	
  private boolean readFile(String fileName, Graph g) {
    Scanner sc;
    
    //Creates scanner on the data file
    try {
      String path = Paths.get(fileName).toString();
      File file = new File(path);
      sc = new Scanner(file);
    }
  
    catch(IOException e) {
    	e.printStackTrace();
    	return false;
    }
    
    
    Vertex station = null;
    
    //Goes through the data file once and adds all stations as vertices
    while (sc.hasNextLine()) {
    	String line = sc.nextLine();
    		
    	if(!line.isBlank() && Character.isAlphabetic(line.charAt(0))) {
    		Vertex v = new Vertex(line);
    		g.addVertex(v);
    	}
    }
    
    sc.close();
    
    //Creates a new scanner back at the beginning of the file for a second iteration
    try {
        String path = Paths.get(fileName).toString();
        File file = new File(path);
        sc = new Scanner(file);
      }
    
      catch(IOException e) {
      	e.printStackTrace();
      	sc.close();
        return false;
      }
    
    while (sc.hasNextLine()) {
      String line = sc.nextLine();
      //Saves the station all the edges in this section of the data file stem from
      if(!line.isBlank() && Character.isAlphabetic(line.charAt(0))) {
    	  station = new Vertex(line);
      }
      //Adds an edge between the saved station and each station it is connected to
      
      //Slightly redundant. With how the data file is structured, addEdge is called
      //twice for each edge, i.e. it adds an edge for vertex1 - vertex2 and 
      //vertex2 - vertex1. edgeExists is no faster than addEdge though so I don't 
      //see a way to improve it.
      if(!line.isBlank() && Character.isDigit(line.charAt(0))) {
        
    	  g.addEdge(station, new Vertex(line.substring(2)), 
    			 Character.getNumericValue(line.charAt(0)));
      }
    }
    
    sc.close();
    return true;
  }
  
  private Vertex[] userInput(Graph g) {
	  String station1;
	  String station2;
	  Vertex[] vertices = new Vertex[2];
	  Scanner userIn = new Scanner(System.in);
	  
	  System.out.println("Enter the station you intend to start from: ");
	  station1 = userIn.nextLine();
	  
	  //Confirms the station entered exists in the graph and adds it to the
	  //array that will be returned
	  Vertex s1 = new Vertex(station1);
	  if(!g.vertexExists(s1)) {
		  System.out.println("The station you entered does not exist. Double "
		  		+ "check the station name and run the program again.");
		  System.exit(0);
	  }
	  vertices[0] = s1;
	  
	  //Same as the last chunk but for the destination station
	  System.out.println("Enter the station you intend to travel to: ");
	  station2 = userIn.nextLine();
	  
	  Vertex s2 = new Vertex(station2);
	  if(!g.vertexExists(s2)) {
		  System.out.println("The station you entered does not exist. Double "
		  		+ "check the station name and run the program again.");
		  System.exit(0);
	  }
	  vertices[1] = s2;
	  
	  userIn.close();
	  return vertices;
  }
  /*
   * Uses Dijkstra's algorithm to figure out the travel time in minutes between
   * the two given stations in the graph.
   */
  private int shortestPath(Vertex v1, Vertex v2, Graph g) {
	  
	  //Keeps track of vertices that have a distance value < infinity but are not marked
	  HashSet<Vertex> edgeVertices = new HashSet<Vertex>();
	  //Sets the starting vertex as the current one
	  Vertex current = g.getVertex(v1);
	  //Retrieves the destination vertex from the graph
	  v2 = g.getVertex(v2);
	  current.setDistance(0);
	  current.mark();
	  
	  //Loops until the destination vertex has been marked
	  while(v2.isMarked() == false) {
		  //Iterator from g that iterates through all vertices connected to current
		  Iterator<Vertex> ni = g.neighborIterator(current);
		  
		  /*
		   * Goes through all the neighbors of the current vertex and updates
		   * their distance values if the neighbor hasn't been marked already,
		   * then adds it to the edgeVertices hashSet. Because of how the add
		   * method works, if the set already contains that vertex it won't be 
		   * added again.
		   */
		  while(ni.hasNext()) {
			  Vertex neighbor = ni.next();
			  
			  if(neighbor.isMarked() == false) {
				  int newDistance = current.getDistance() + 
						  g.getEdge(current, neighbor);
				  
				  if(newDistance < neighbor.getDistance()) {
					  neighbor.setDistance(newDistance);
					  
				  }
				  
				  edgeVertices.add(neighbor);
			  }
		  }
		  
		  //Iterator to go through all edge vertices and find the lowest distance
		  Iterator<Vertex> setIterator = edgeVertices.iterator();
		  Vertex minDistance = setIterator.next();
		  
		  while(setIterator.hasNext()) {
			  Vertex nextVertex = setIterator.next();
			  
			  if(nextVertex.getDistance() < minDistance.getDistance()) {
				  minDistance = nextVertex;
			  }
		  }
		  //Marks the vertex with lowest distance value and sets it as current
		  minDistance.mark();
		  current = minDistance;
		  //Removes the newly marked vertex from the edgeVertices set
		  edgeVertices.remove(current);
	  }
	  
	  return v2.getDistance();
  }
  
  public static void main(String[] args) {
    Graph g = new Graph();
    Driver d = new Driver();
    d.readFile("stations.txt", g);
    
    //Calls userInput to collect origin and destination names
    Vertex[] userIn = d.userInput(g);
    
    int distance = d.shortestPath(userIn[0], userIn[1], g);
    
    System.out.println("The shortest travel time between " + userIn[0].toString()
    		+ " and " + userIn[1].toString() + " is " + distance + " minutes.");
    
    
    System.exit(0);
    
  }
}
