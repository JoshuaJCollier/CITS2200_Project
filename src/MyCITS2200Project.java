import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

// Only required for testing
import CITS2200.Graph;

/**
 * Our CITS2200 Project for 2019
 * @author Joshua Collier (22503577)
 * @author Deepali Rajawat (22496421)
 */
public class MyCITS2200Project implements CITS2200Project {
	int numNodes;
	long timeOfLastMethod;

	int visitedAll;
	int[][] dp;
	Stack<Integer> returnStack;

	LinkedList<Integer> adjList[];
	LinkedList<Integer> transposeList[];
	HashMap<String, Integer> dictionary;
	HashMap<Integer, String> intToString;
	private Queue<Integer> list;
	private Stack<Integer> stack;
	private boolean[] visited;

	@SuppressWarnings("unchecked")
	/**
	 * Constructor for CITS Project
	 */
	public MyCITS2200Project() {
		numNodes = 0;
		dictionary = new HashMap<String, Integer>();
		intToString = new HashMap<Integer, String>();
		adjList = new LinkedList[16];
		transposeList = new LinkedList[16];
	}
	
	// Only required for testing
	/**
	 * getTime() returns the time it took for the last method to run in nanoseconds
	 * @return long time in nanoseconds it took for the last method to run
	 */
	public long getTime() {
		return timeOfLastMethod;
	}
	
	// Only required for testing
	/**
	 * setRandomGraph(int nodes, double d) uses the CITS2200 package to generate a random graph and allows us to use it for testing 
	 * @param nodes Number of nodes you want
	 * @param d Density of edges
	 */
	public void setRandomGraph(int nodes, double d) {
		Graph g = Graph.randomGraph(nodes, d);
		int[][] matrix = g.getEdgeMatrix();
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				if (matrix[i][j] == 1) {
					addEdge(((Integer) i).toString(),((Integer) j).toString());
				}
			}
		}
	}
	
	/**
	 * addNode(String url) is called to by addEdge to add unknown nodes to the adjacency list and transposed list
	 * @param url The name of the node you want to add
	 */
	private void addNode(String url) {
		if (numNodes==adjList.length) {
			@SuppressWarnings("unchecked")
			LinkedList<Integer>[] tempAdjList = new LinkedList[adjList.length*2];
			@SuppressWarnings("unchecked")
			LinkedList<Integer>[] temptranspose = new LinkedList[transposeList.length*2];
			for (int i = 0; i < adjList.length; i++) {
				tempAdjList[i] = adjList[i];
				temptranspose[i] = transposeList[i];
			}
			LinkedList<Integer> edges = new LinkedList<Integer>();
			LinkedList<Integer> diffedges = new LinkedList<Integer>();
			dictionary.put(url, numNodes);
			intToString.put(numNodes, url);
			tempAdjList[numNodes] = edges;
			temptranspose[numNodes]= diffedges;
			numNodes+=1;
			adjList = tempAdjList;
			transposeList = temptranspose;
		} else {
			LinkedList<Integer> edges = new LinkedList<Integer>();
			LinkedList<Integer> diffedges = new LinkedList<Integer>();
			dictionary.put(url, numNodes);
			intToString.put(numNodes, url);
			adjList[numNodes] = edges;
			transposeList[numNodes] = diffedges;
			numNodes+=1;
		}
	}

	@Override
	/**
	 * addEdge(String urlFrom, String urlTo) adds edge between the two nodes, and adds those nodes if they have not yet been added
	 * @param urlFrom The node the edge is coming from
	 * @param urlTo The node the edge is going to
	 */
	public void addEdge(String urlFrom, String urlTo) {
		if (!dictionary.containsKey(urlFrom)) {
			addNode(urlFrom);
		}

		if(!dictionary.containsKey(urlTo)) {
			addNode(urlTo); 
		}
		int urlFromNode = dictionary.get(urlFrom);
		int urlToNode = dictionary.get(urlTo);

		if(!(adjList[urlFromNode].contains(urlToNode))) {
			adjList[dictionary.get(urlFrom)].add(dictionary.get(urlTo));
		}

		if(!(transposeList[urlToNode].contains(urlFromNode))) {
		transposeList[dictionary.get(urlTo)].add(dictionary.get(urlFrom));
		}
	}

	@Override
	/**
	 * getShortestPath(String urlFrom, String urlTo) will return the shortest distance between two nodes
	 * @param urlFrom The node you start at
	 * @param urlTo The node you finish at
	 * @return int The distance between the two nodes
	 */
	public int getShortestPath(String urlFrom, String urlTo) {
		long startTime = System.nanoTime();
		
		// Use breadth first search
		int vertex1 = 0;
		int vertex2 = 0;
		if(dictionary.containsKey(urlFrom)) {
			vertex1 = dictionary.get(urlFrom);
		}

		if(dictionary.containsKey(urlTo)) {
			vertex2 = dictionary.get(urlTo);
		}
		
		int[] distances = BFS(vertex1);

		timeOfLastMethod = System.nanoTime() - startTime;
		return distances[vertex2];
	}

	@Override
	/**
	 * getCenters() returns the centres of the graph 
	 * @return String[] A list of nodes that are centres of the graph
	 */
	public String[] getCenters() {
		long startTime = System.nanoTime();
		int[] eccentricity = new int[numNodes]; // max shortest path for each vertex

		for (int i = 0; i < numNodes; i++) {
			int[] distances = BFS(i);
			
			int max = distances[0];
			for(int j =0; j<distances.length; j++) {
				if(distances[j]>max) {
					max = distances[j];
				}
			}
	
			eccentricity[i] = max; // Set max shortest path for vertex i
		}
		
		// Find min eccentricity
		int min = eccentricity[0];
		for (int i = 0; i < eccentricity.length; i++) {
			if(eccentricity[i]<min) {
				min = eccentricity[i];
			}
		}
	
		// Find centres with min eccentricity and add them to string array
		List<String> centre = new ArrayList<>();
		for(int i = 0; i <eccentricity.length; i++){
			if(eccentricity[i] == min) {
				centre.add(intToString.get(i));
			}
		}
	
		String[] centres = centre.toArray(new String[0]); 
	
		timeOfLastMethod = System.nanoTime() - startTime;
		return centres;
	}

	/**
	 * BFS(int vertex) is a method that runs a breath first search algorithm on a particular node to find its distance from all other nodes
	 * @param vertex Starting vertex
	 * @return int[] Distance each node is from the starting vertex
	 */
	private int[] BFS(int vertex){
		int[] distance = new int[numNodes];
		list = new LinkedList<Integer>();
		boolean[] visited = new boolean[numNodes];

		for(int i = 0; i < numNodes; i++) {
			visited[i]= false;
			distance[i] = Integer.MAX_VALUE;
		}

		visited[vertex] = true;
		distance[vertex] = 0;
		list.add(vertex);

		while(!list.isEmpty()) {
			Integer top = list.remove();
			LinkedList<Integer> edges = adjList[top];
			for(int i = 0; i < edges.size(); i++) {
			int adjv = edges.get(i);
				if(!visited[adjv]) {
					if(distance[top]+1 < distance[adjv]) {
				    distance[adjv] = distance[top]+1;
					visited[adjv] = true;
					list.add(adjv);
					}
				}
			}
		}
		
		for(int i = 0; i < distance.length; i++) {
			if(distance[i] == Integer.MAX_VALUE) {
				distance[i] = -1;
				}
		}

		return distance;
	}
	
	
	@Override
	/**
	 * getStringlyConnectedComponents() returns a list of lists, each element of the String[] contains a list of nodes in a strongly connected subset
	 * @return String[][] Returns the strongly connected component subsets 
	 */
	public String[][] getStronglyConnectedComponents() {
		long startTime = System.nanoTime();

		int index = 0;
		stack = new Stack<Integer>();
		String[][] scc = new String[dictionary.size()][];
		visited = new boolean[dictionary.size()]; 

		for(int i = 0; i < visited.length; i++) {
			visited[i] = false;
		}

		for (int i = 0; i < visited.length; i++) {
			if(!visited[i]) {
			DFS(i, visited, stack);
			}
		}

		// Reset visited for transpose graph
		for(int i = 0; i< visited.length; i++) {
			visited[i] = false;
		}

		while(!stack.empty()) {
			int top = stack.pop();
			if(!visited[top]) {

				List<String> strongComponent = new ArrayList<String>();

				DFSreversal(top, visited, strongComponent);
				String[] component = strongComponent.toArray(new String[0]); 
			scc[index] = component;
			index++;
			}

		}

		String[][] actualscc = new String[index][];
		for (int i = 0; i < index; i++) {
			actualscc[i] = scc[i];
		}
		timeOfLastMethod = System.nanoTime() - startTime;
		return actualscc;
	}

	/**
	 * DFS(int vertex, boolean visit[], Stack<Integer> st) 
	 * @param vertex Vertex we are looking at
	 * @param visit List of visited verticies
	 * @param st Stack
	 */
	private void DFS(int vertex, boolean visit[], Stack<Integer> st) {
		visit[vertex] = true;

		// Look at adjacent vertices
		LinkedList<Integer> edges = adjList[vertex];
		Iterator<Integer> it = edges.iterator();

		while(it.hasNext()) {
			int adjv = it.next();

			if (!visit[adjv]) {
				DFS(adjv, visit, st);
			}

		}
		stack.push(vertex);
	}

	/**
	 * DFSreversal(int vertex, boolean visited[], List<String> component)
	 * @param vertex Vertex we are looking at
	 * @param visit The visited verticies
	 * @param component List of verticies we have visited
	 */
	private void DFSreversal(int vertex, boolean visit[], List<String> component) {
		visit[vertex] = true;
		component.add(intToString.get(vertex));

		Iterator<Integer> it = transposeList[vertex].iterator();
		while(it.hasNext()) {
			int adjv = it.next();
			if(!visit[adjv]) {
				DFSreversal(adjv, visit, component);

			}
		}


	}

	/**
	 * getHamiltonianPath() returns a hamiltonian cycle of the graph if there is one starting and ending with the starting node, and null if there isnt
	 * @return String[] returns an array of string, being the names of the nodes in the the order of the hamiltonian cycle
	 */
	public String[] getHamiltonianPath() {
		long startTime = System.nanoTime();
		
		// Stack we will use to add all the nodes visited and then show our path
		returnStack = new Stack<Integer>();
		
		// Initialising the array to store all of the bitmask states to -1
		dp = new int[(1<<numNodes)][numNodes];
		for (int i = 0; i < (1<<numNodes); i++) {
			for (int j = 0; j < numNodes; j++) {
				dp[i][j] = -1;
			}
		}

		String[] returnString = new String[numNodes+1];
		// Setting the variable of the full bitmask, (essentially 11111... numNodes times)
		visitedAll = (1<<numNodes)-1;

		// Setting a default bitmask to start with, where we have just visited node 0
		int visitedBitmask = 1;
		int n = 0;

		// Return null if there was not a cycle
		if (p(0, adjList[0], visitedBitmask)==0) {
			return null;
		}

		// If there was no failure, iterate through the stack and add it to the returnString
		while (!returnStack.isEmpty()) {
			String currentStr = intToString.get(returnStack.pop());
			returnString[n] = currentStr;
			n++;
		}
		timeOfLastMethod = System.nanoTime() - startTime;
		return returnString;
	}

	/**
	 * p(int currentNode, LinkedList<Integer> edges, int visitedBitmask)
	 * @param currentNode The node that we are currenting looking at
	 * @param edges The edges branching off the currentNode
	 * @param visitedBitmask The currently visited nodes represented in a bitmask
	 * @return int The lenght of the bitmask value from the currentNode to the origin
	 */
	private int p(int currentNode, LinkedList<Integer> edges, int visitedBitmask) {
		// Check if a path exists from the current node to the visitedBitmask of nodes

		// Have we visited all nodes
		if (visitedBitmask==visitedAll) {
			// If so does the final node link back to 0
			if (adjList[currentNode].contains(0)) {
				// Adding the final value and the 0 to represent to cycle
				returnStack.add(0);
				returnStack.add(currentNode);
				return dp[currentNode][0];
			} else {
				return 1000; // number greater than or equal to the default ans value
			}
		}

		// If we have already done the calculation, then if that path is possible
		if (dp[visitedBitmask][currentNode]!=-1) {
			return dp[visitedBitmask][currentNode];
		}

		// Just a high value, doesn't need to be higher than 20 realistically because there will at no point be more than 20 nodes for this problem
		int ans = 1000;

		// Check all the adjacent nodes
		while (!edges.isEmpty()) {
			// Grab one adjacent node
			int nextNode = edges.pop();
			// Check if that node has been visited
			if ((visitedBitmask&(1<<nextNode))==0) {
				// Check if a path exists from the children of our nextNode to the visitedBitmask plus the nextNode
				
				int newAns = p(nextNode, adjList[nextNode],visitedBitmask|(1<<nextNode));
				// The shortest path would be either the original path or our new path (that may now be one node deeper)
				if (newAns < ans) {
					returnStack.add(currentNode);
				}
				ans = Integer.min(newAns, ans);

			}
		}
		dp[visitedBitmask][currentNode] = ans;
		return ans;
	}
}
