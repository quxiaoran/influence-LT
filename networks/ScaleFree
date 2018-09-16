package networks;

import java.util.ArrayList;
import java.util.Random;

public class ScaleFree {

	public static void main(String[] args) {
		
		
	}
	
	
	
	
	public static void build(Network graph, int size, double connectivity){
		while (graph.size() < size){
			int i = graph.addVertex();
			for (Person p : graph.vertices){
				if (i != p.ID && binChoice(p.getFriends().size() * connectivity / graph.edges.size())){
					graph.addEdge(i, p.ID, 1, 1);
				}
			}
		}
	}
	
	public static boolean binChoice(double chance){
		Random generator = new Random();
		double out = generator.nextDouble();
		if (out < chance) return true;
		else return false;
	}


}
