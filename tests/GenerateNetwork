package tests;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import networks.Network;
import networks.ScaleFree;

public class GenerateNetwork {

	public static void main(String[] args) throws IOException {
		
		// generating the network
		Network startingNetwork = new Network();
		startingNetwork.addVertex();
		startingNetwork.addEdge(0, 1, 1, 1);
		int size = 1000;
		ScaleFree.build(startingNetwork, size, 3.5); // initial network
		
		double[][] weights = weightedBuild(startingNetwork);
		double[] initialP = generateInitial(startingNetwork);

		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/Users/stevenqu/Documents/Network Project/tests/weights.txt")));
		out.println(size);
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				out.print(weights[i][j] + " ");
			}
			out.println();
		}
		out.close();
		
		out = new PrintWriter(new BufferedWriter(new FileWriter("/Users/stevenqu/Documents/Network Project/tests/initialP.txt")));
		out.println(size);
		for (int i = 0; i < size; i++) {
			out.println(initialP[i]);
		}
		out.close();
		
	}
	
	public static double[][] weightedBuild(Network startingNetwork) {
		
		double[][] adjMatr = Network.generateAdjMatr(startingNetwork);
		double[][] weights = new double[adjMatr.length][adjMatr.length];
		Random generator = new Random();
		
		for (int i = 0; i < adjMatr.length; i++){
			for (int j = 0; j < adjMatr.length; j++){
				weights[i][j] = adjMatr[i][j] * Math.pow(0.15 + 0.05 * generator.nextDouble(), 2);
			}
		}
		
		return weights;
	}
	
	public static double[] generateInitial(Network startingNetwork) {
		
		Random generator = new Random();
		double[] initialP = new double[startingNetwork.size()];
		
		for (int i = 0; i < startingNetwork.size(); i++){
			initialP[i] = 0.04 + 0.04 * generator.nextDouble(); // initial p
		}
		
		return initialP;
	}

	

	public static Network generateFromWeights(double[][] weights) {
		
		Network graph = new Network();
		int size = weights.length;
		for (int i = 1; i < size; i++) graph.addVertex();
		for (int i = 0; i < size; i++) {
			for (int j = i; j < size; j++) {
				if (weights[i][j] != 0) graph.addEdge(i, j, weights[i][j], weights[j][i]);
			}
		}
		
		return graph;
	}
}
