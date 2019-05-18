import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;



public class CITS2200ProjectTester {
	public static void loadGraph(CITS2200Project project, String path) {
		// The graph is in the following format:
		// Every pair of consecutive lines represent a directed edge.
		// The edge goes from the URL in the first line to the URL in the second line.
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			while (reader.ready()) {
				String from = reader.readLine();
				String to = reader.readLine();
				System.out.println("Adding edge from " + from + " to " + to);
				project.addEdge(from, to);
			}
		} catch (Exception e) {
			System.out.println("There was a problem:");
			System.out.println(e.toString());
		}
	
		
	}
	public static void testShortestPath(CITS2200Project project) {
		
		//assertEquals(2,project.getShortestPath("/wiki/Flow_network","/wiki/Minimum_cut"));
		
	}
	
	public static void main(String[] args) {
		// Change this to be the path to the graph file.
		String pathToGraphFile = "src/example_graph.txt";
		// Create an instance of your implementation.
		CITS2200Project proj = new MyCITS2200Project(pathToGraphFile);
		// Load the graph into the project.
		loadGraph(proj, pathToGraphFile);
		testShortestPath(proj);
		String[] printStr = (proj.getHamiltonianPath());
		if (printStr==null) {
			System.out.println("heck");
		} else {
			for (String i : printStr) {
				System.out.println(i);
			}	
		}
		

		// Write your own tests!
	}
}