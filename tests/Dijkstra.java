package tests;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

import networks.Network;
import networks.Person;

public class Dijkstra {

	public static void main(String[] args) throws NumberFormatException, IOException {
		
		BufferedReader f = new BufferedReader(new FileReader("/Users/stevenqu/Documents/Network Project/tests/weights.txt"));
		int size = Integer.parseInt(f.readLine());
		double[][] weights = new double[size][size];
		for (int i = 0; i < size; i++) {
			StringTokenizer st = new StringTokenizer(f.readLine());
			for (int j = 0; j < size; j++) {
				weights[i][j] = Double.parseDouble(st.nextToken());
			}
		}
		f.close();
		
		Network network = GenerateNetwork.generateFromWeights(weights);
		
		double[] closeness = new double[network.size()];
		for (int i = 0; i < network.size(); i++) {
			double[] results = dijkstra(network, network.vertices.get(i));
			double total = 0.0;
			for (int j = 0; j < results.length; j++) {
				total += results[j];
			}
			closeness[i] = 1.0 / total;
		}
	}
	
	public static double[] generateCloseness(Network network) {
		
		double[] closeness = new double[network.size()];
		for (int i = 0; i < network.size(); i++) {
			double[] results = dijkstra(network, network.vertices.get(i));
			double total = 0.0;
			for (int j = 0; j < results.length; j++) {
				total += results[j];
			}
			closeness[i] = 1.0 / total;
		}
		
		return closeness;
	}
	
	public static double[] dijkstra(Network network, Person source) {
		
		double[] distance = new double[network.size()];
		distance[source.ID] = 0;
		
		int[] previous = new int[network.size()];
		
		PriorityQueue<Person> explored = new PriorityQueue<Person>();
		
		for (Person person : network.vertices) {
			if (person.ID != source.ID) {
				distance[person.ID] = 1000000000.0;
			}
			previous[person.ID] = -1;
			person.priority = distance[person.ID];
			explored.add(person);
		}
		
		while (!explored.isEmpty()) {
			Person current = explored.poll();
			for (int i = 0; i < current.friends.size(); i++) {
				double altDistance = distance[current.ID] + .01 / current.weights.get(i);
				if (altDistance < distance[current.friends.get(i)]) {
					distance[current.friends.get(i)] = altDistance;
					previous[current.friends.get(i)] = current.ID;
					Person friend = network.vertices.get(current.friends.get(i));
					explored.remove(friend);
					friend.priority = altDistance;
					explored.add(friend);
				}
			}
		}
		
		return distance;
	}
}
