/** Implementation of a Graph data structure.  Here we have the ability
 *  to add vertices and edges, check to see if an edge exists, and list all
 *  the neighbors of some vertex.  The underlying representation is a 2-D
 *  array for the adjacency matrix.  We keep track of the names of the 
 *  vertices in a separate array of Vertex objects.
 *  To make the Graph class flexible, we implement it as a weighted graph,
 *  but also provide a constructor without a weight parameter in case the
 *  user wants all the edge weights to be 1 (to have an unweighted graph).
 */
import java.util.Iterator;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class Graph
{
  private int numVertices;
  private int numEdges;
  private Vertex [] vertex;
  private int [][] matrix;

  public Graph()
  {
    numVertices = 0;
    numEdges = 0;
    vertex = new Vertex[numVertices];
    matrix = new int[numVertices][numVertices];
  }

  public void addVertex(Vertex v)
  {
    ++numVertices;
    Vertex [] newVertex = new Vertex[numVertices];
    for (int i = 0; i < numVertices - 1; ++i)
      newVertex[i] = vertex[i];
    newVertex[numVertices - 1] = new Vertex(v.getName());
    vertex = newVertex;

    // We also need to expand the adjacency matrix.
    int [][] newMatrix = new int[numVertices][numVertices];
    for (int i = 0; i < numVertices - 1; ++i)
      for (int j = 0; j < numVertices - 1; ++j)
        newMatrix[i][j] = matrix[i][j];

    // Use -1 to represent no connection to the other existing vertices
    // And 0 to represent no distance to same vertex.
    matrix = newMatrix;
    for (int i = 0; i < numVertices - 1; ++i)
    {
      matrix[i][numVertices - 1] = -1;
      matrix[numVertices - 1][i] = -1;
    }
    matrix[numVertices - 1][numVertices - 1] = 0;
  }

  // Let's make sure the adjacency matrix is symmetric -- initialize
  // both the ij and ji elements.
  public void addEdge(Vertex a, Vertex b)
  {
    int i = findVertex(a);
    int j = findVertex(b);
    matrix[i][j] = 1;
    matrix[j][i] = 1;
  }

  public void addEdge(Vertex a, Vertex b, int weight)
  {
    int i = findVertex(a);
    int j = findVertex(b);
    matrix[i][j] = weight;
    matrix[j][i] = weight;
  }

  public int getNumVertices()
  {
    return numVertices;
  }

  public int getNumEdges()
  {
    return numEdges;
  }

  // Find which vertices correspond to the strings.  Both vertex names
  // must exist before we can look up if there is an edge connecting them.
  public boolean edgeExists(Vertex a, Vertex b)
  {
    int i = findVertex(a);
    int j = findVertex(b);
    return matrix[i][j] > 0;
  }
  
  public int getEdge(Vertex a, Vertex b)
  {
	  int i = findVertex(a);
	  int j = findVertex(b);
	  return matrix[i][j];
  }
  
  public Vertex getVertex(Vertex v)
  {
	  int vertexNum = findVertex(v);
	  return vertex[vertexNum];
  }
  
  public boolean vertexExists(Vertex v)
  {
	  for(int i = 0; i < numVertices; i++)
		  if(vertex[i].equals(v))
			  return true;
	  
	  return false;
  }

  private int findVertex(Vertex v)
  {
    for (int i = 0; i < numVertices; ++i)
      if (vertex[i].equals(v))
        return i;

    throw new NoSuchElementException();
  }

  public int degree(Vertex v)
  {
    int vertexNum = findVertex(v);

    int count = 0;
    for (int i = 0; i < numVertices; ++i)
      if (matrix[vertexNum][i] > 0)
        ++count;

    return count;
  }

  public String toString()
  {
    String build = "vertices:\n";

    for (int i = 0; i < numVertices; ++i)
      build += "\t" + vertex[i] + "\n";

    build += "\nAdjacency matrix:\n";
    for (int i = 0; i < numVertices; ++i)
    {
      build += "\t";
      for (int j = 0; j < numVertices; ++j)
        build += matrix[i][j] + " ";
      build += "\n";
    }
    return build;
  }

  // Give us a way to find neighboring vertices we're connected to.
  // startingIndex = row number, the starting place
  // currentIndex = column number, the next neighbor we'll output
  class NeighborIterator implements Iterator
  {
    int startingIndex;
    int currentIndex;

    // constructor needs to know which vertex we start from
    public NeighborIterator(Vertex v)
    {
      startingIndex = findVertex(v);
      
      // begin the currentIndex at the first column in the matrix
      // that has a non-zero value in this row.  If there isn't any,
      // then currentIndex will point to the end of the row.
      for (currentIndex = 0; currentIndex < numVertices; ++currentIndex)
        if (matrix[startingIndex][currentIndex] > 0)
          break;
    }

    public boolean hasNext()
    {
      return currentIndex < numVertices;
    }

    // Return the vertex pointed to by currentIndex, and then advance
    // currentIndex to the next neighbor or to the end of the row.
    public Object next()
    {
      if (! hasNext())
        throw new NoSuchElementException();

      Object retVal = vertex[currentIndex];
      for (++currentIndex; currentIndex < numVertices; ++currentIndex)
        if (matrix[startingIndex][currentIndex] > 0)
          break;

      return retVal;
    }

    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }

  // Now, let's give a user class a way of creating an iterator based
  // from one vertex.
  public Iterator neighborIterator(Vertex v)
  {
    return new NeighborIterator(v);
  }

  // Let's give the user a way to visit all the vertices:
  // Breadth-first search.
  // We'll put the vertices into an ArrayList, one level at a time.
  class BFSIterator implements Iterator
  {
    private ArrayList list;
    private int currentIndex;

    public BFSIterator(Vertex start)
    {
      // Start by putting the starting vertex in the list (level 0).
      int level = 0;
      list = new ArrayList();

      int startingIndex = findVertex(start);
      Vertex startingVertex = vertex[startingIndex];
      startingVertex.setLevel(0);
      list.add(startingVertex);

      // Initially mark only the starting vertex; unmark all others.
      for (int i = 0; i < numVertices; ++i)
        vertex[i].unmark();
      startingVertex.mark();

      // loop until the size of the list no longer changes
      // each iteration will do one level
      for (level = 0; ; ++level)
      {
        System.out.println("level = " + level);
        int beginSize = list.size();

        // For each vertex in the ArrayList at this level, find the neighbors
        // and add them in, marked 1 level higher
        for (int i = 0; i < list.size(); ++i)
        {
          System.out.println("looking at #" + i + " in arraylist");
          Vertex v = (Vertex) list.get(i);
          if (v.getLevel() == level)
          {
            System.out.println("About to look at neighbors of " + v);
            Iterator iter = neighborIterator(v);
            while (iter.hasNext())
            {
              Vertex neighbor = (Vertex) iter.next();

              // Need to be sure we don't add same vertex twice.
              if (! neighbor.isMarked())
              {
                neighbor.setLevel(level + 1);
                list.add(neighbor);
                neighbor.mark();
                System.out.println("Just added " + neighbor + " to arraylist");
              }
            }
          }
        }

        int endSize = list.size();

        System.out.println("size was " + beginSize + " and is now " + endSize);
        if (beginSize == endSize)
          break;
      }
      int currentIndex = 0;
    }

    public boolean hasNext()
    {
      return currentIndex < list.size();
    }

    // Return the current vertex pointed by currentIndex, and also
    // increment currentIndex to point to the next vertex in the list.
    public Object next()
    {
      return list.get(currentIndex++);
    }

    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }

  // Now that we have an inner iterator class, we need to have a way
  // of returning an iterator object that belongs to the graph.
  public Iterator BFSIterator(Vertex v)
  {
    return new BFSIterator(v);
  }

  // The structure of our DFS iterator is similar to the BFS iterator.
  // For instance, we want to store vertices in an ArrayList.
  // Here, we need to recursively visit vertices, and backtrack when we
  // encounter a dead end.  The recursion is handled in a special function
  // visitNextVertex.
  class DFSIterator implements Iterator
  {
    private ArrayList list;
    private int currentIndex;

    public DFSIterator(Vertex start)
    {
      list = new ArrayList();
      int startingIndex = findVertex(start);
      Vertex startingVertex = vertex[startingIndex];

      // unmark all vertices, then mark starting vertex & add to arraylist
      for (int i = 0; i < numVertices; ++i)
        vertex[i].unmark();
      startingVertex.mark();
      list.add(startingVertex);
      
      visitNextVertex(startingVertex);

      currentIndex = 0;
    }

    // recursive function that continues down a path from v, and
    // backtracks when we reach dead end.
    public void visitNextVertex(Vertex v)
    {
      System.out.println("visitNextVertex(" + v + "):  start");
      Iterator iter = neighborIterator(v);

      // find a "next" vertex that hasn't already been marked, and
      // continue from there.
      while (iter.hasNext())
      {
        Vertex neighbor = (Vertex) iter.next();
        System.out.println("visitNextVertex(" + v + "):  neighbor is " + 
                           neighbor);

        if (neighbor.isMarked())
          continue;

        System.out.println("visitNextVertex(" + v + "):  adding " + 
                           neighbor + " to list & calling myself");
        neighbor.mark();
        list.add(neighbor);
        visitNextVertex(neighbor);
      }
      System.out.println("visitNextVertex(" + v + "):  returning");
    }

    public boolean hasNext()
    {
      return currentIndex < list.size();
    }

    public Object next()
    {
      return list.get(currentIndex++);
    }

    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }

  public Iterator DFSIterator(Vertex v)
  {
    return new DFSIterator(v);
  }
}
