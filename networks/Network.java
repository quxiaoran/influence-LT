package networks;

import java.util.ArrayList;

public class Network {
	public ArrayList<Person> vertices = new ArrayList<Person>();
	public ArrayList<double[]> edges;
	
	public Network(){
		vertices.add(new Person(0));
		edges = new ArrayList<double[]>();
	}
	
	public int addVertex(){
		vertices.add(new Person(vertices.size()));
		return vertices.size()-1;
	}
	
	public void addEdge(int a, int b, double aw, double bw){ // takes in ID's
		vertices.get(a).getFriends().add(b);
		vertices.get(a).getWeights().add(aw); // a's influence on b
		vertices.get(b).getFriends().add(a);
		vertices.get(b).getWeights().add(bw);
		double[] pair = {a, b, aw, bw};
		edges.add(pair);
	}
	
	public int size(){
		return vertices.size();
	}
	
	public void changeThresholds(){
		for (Person p : vertices){
			p.randomThreshold();
		}
	}
	
	public static double[][] generateAdjMatr(Network graph){
		double[][] adjMatr = new double[graph.vertices.size()][graph.vertices.size()];
		for (double[] edge : graph.edges){
			adjMatr[(int) edge[0]][(int) edge[1]] = edge[2];
			adjMatr[(int) edge[1]][(int) edge[0]] = edge[3];
		}
		return adjMatr;
	}
}
