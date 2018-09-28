package tests;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

import networks.Network;
import networks.Person;

public class Predictive {

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
		
		f = new BufferedReader(new FileReader("/Users/stevenqu/Documents/Network Project/tests/initialP.txt"));
		f.readLine();
		double[] initialP = new double[size];
		for (int i = 0; i < size; i++) {
			initialP[i] = Double.parseDouble(f.readLine());
		}
		
		// model
		int timeLim = 100;
		Network startingNetwork = GenerateNetwork.generateFromWeights(weights);
		double[][][] out = GeneratePQnew.generatePQ(startingNetwork, weights, initialP, timeLim);
		
		// actually counting
		int repeats = 1000;
		double[] timeP = new double[size];
		for (int i = 0; i < repeats; i++) {
			ArrayList<Integer> activated = sampleInfluence(weights, initialP);
			for (int j = 0; j < activated.size(); j++) {
				timeP[activated.get(j)]++;
			}
			// System.out.println(i);
		}
		
		// output
		for (int i = 0; i < size; i++) {
			timeP[i] /= repeats;
			System.out.print(timeP[i] + " ");
			System.out.print(out[1][timeLim-1][i] + " ");
			System.out.print(out[1][timeLim-1][i] / timeP[i]);
			System.out.println();
		}
		
		

	}
	
	public static ArrayList<Integer> sampleInfluence(double[][] weights, double[] initialP) {
		
		Network graph = GenerateNetwork.generateFromWeights(weights);
		
		ArrayList<Integer> activated = new ArrayList<Integer>();
		Random generator = new Random();
		
		for (int i = 0; i < initialP.length; i++) {
			 if (generator.nextDouble() < initialP[i]) {
				 graph.vertices.get(i).activate();
				 activated.add(i);
			 }
		}
		
		int acquiesce = 0;
		while (true) {
			acquiesce = activated.size();
			double[] influence = new double[graph.size()];
			for (int j = 0; j < graph.size(); j++){
				if (graph.vertices.get(j).isActivated()){
					for (int k = 0; k < graph.vertices.get(j).friends.size(); k++){
						if (graph.vertices.get(graph.vertices.get(j).friends.get(k)).isActivated() == false){
							influence[graph.vertices.get(j).friends.get(k)] += graph.vertices.get(j).weights.get(k);						}
					}
				}
			}
			for (int j = 0; j < graph.size(); j++){
				if (influence[j] > graph.vertices.get(j).getThreshold()){
					graph.vertices.get(j).activate();
					activated.add(graph.vertices.get(j).ID);
				}
			}
			if (activated.size() == acquiesce) break;
		}
		
		for (Person i : graph.vertices){
			i.unactivate();
		}
		
		return activated;
		
	}

}
