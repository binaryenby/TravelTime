// Basic stuff we need to know about a single vertex -- its name and whether
// it's been marked for some purpose (useful when we traverse vertices).
// The level atribute is used in breadth-first search.
public class Vertex
{
  private String name;
  private boolean marked;
  private int level;
  private int distance = Integer.MAX_VALUE;

  public Vertex(String n)
  {
    name = n;
    marked = false;
  }

  public void mark()
  {
    marked = true;
  }

  public void unmark()
  {
    marked = false;
  }

  public boolean isMarked()
  {
    return marked;
  }

  public void setLevel(int n)
  {
    level = n;
  }

  public int getLevel()
  {
    return level;
  }
  
  public void setDistance(int n)
  {
	  distance = n;
  }
  
  public int getDistance()
  {
	  return distance;
  }

  // we need to get the name when we're doing the graph traversals
  // (BFS/DFS) and need to call the neighbor iterator constructor, which
  // expects a string.
  public String getName()
  {
    return name;
  }

  // For equals() just check to see if it's the same name.
  public boolean equals(Vertex v)
  {
    return name.equals(v.name);
  }

  // When printing out the vertices, it's probably not important
  // to say if they are marked, so give only the name.
  public String toString()
  {
    return name;
  }
}
