package tests;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

import networks.Network;
import networks.Person;

public class ImpactPQ { // for a specific v_i
	
	static double[][] weights;
	static double[][][] PandQ;
	//static double[] firstTerm; // 1 - sum(q_{j, t-2} * w_{j to i})
	//static double[] secondTerm; // sum(p_{j, t-1} * w_{j to i} * (1 - q_{j, t-2}))

	public static void main(String[] args) throws NumberFormatException, IOException {
		
		BufferedReader f = new BufferedReader(new FileReader("/Users/stevenqu/Documents/Network Project/tests/weights.txt"));
		int size = Integer.parseInt(f.readLine());
		weights = new double[size][size];
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
		
		
		Network startingNetwork = GenerateNetwork.generateFromWeights(weights);
		
		int timeLim = 10;
		PandQ = GeneratePQnew.generatePQ(startingNetwork, weights, initialP, timeLim);
		
		// precompute neighbor independent values
		// firstTerm = new double[timeLim];
		// secondTerm = new double[timeLim];
		
		// SELECTING V_I
		Random generator = new Random();
		int marker = generator.nextInt(size);
		Person center = startingNetwork.vertices.get(marker);
		while (center.friends.size() < 5) {
			marker = generator.nextInt(size);
			center = startingNetwork.vertices.get(marker);
		}
		
		int iterations = 15;
		double increment = 0.01;
		
		for (int k = 0; k < iterations; k++) {
			double[] dQdW = impactIter(startingNetwork, marker, timeLim);
			
			for (int i = 0; i < dQdW.length; i++) {
				if (weights[center.friends.get(i)][marker] == 0) {
					dQdW[i] = 0;
				}
				// System.out.println(center.friends.get(i) + " " + dQdW[i]);
			}
			//System.out.println();
			
			double maxD = dQdW[0];
			int index = 0;
			
			for (int i = 0; i < dQdW.length; i++) {
				if (dQdW[i] > maxD && weights[center.friends.get(i)][marker] != 0) {
					maxD = dQdW[i];
					index = i;
				}
			}
			
			
			for (int j = 0; j < dQdW.length; j++) {
				System.out.println(dQdW[j] + " " + PandQ[1][timeLim-1][center.friends.get(j)]);
			}
			
			
			System.out.println("Node: " + center.friends.get(index));
			double prev1 = weights[center.friends.get(index)][marker]; // previous weight
			weights[center.friends.get(index)][marker] = Math.max(0, weights[center.friends.get(index)][marker] - increment);
			System.out.println("Margin: " + (prev1 - weights[center.friends.get(index)][marker]));
			double prev2 = PandQ[1][timeLim-1][marker]; // previous q
			PandQ = GeneratePQnew.generatePQ(startingNetwork, weights, initialP, timeLim);
			System.out.println("Resultant Q: " + PandQ[1][timeLim-1][marker]);
			System.out.println("Effective Impact: " + (prev2 - PandQ[1][timeLim-1][marker]) 
					/ (prev1 - weights[center.friends.get(index)][marker]));
		}
	}
	
	public static double[] impactIter(Network startingNetwork, int marker, int timeLim) {
		double[] firstTerm = new double[timeLim];
		double[] secondTerm = new double[timeLim];
		
		Person center = startingNetwork.vertices.get(marker);
		ArrayList<Integer> friends = center.friends;
		
		for (int i = 0; i < timeLim; i++) {
			double tempSum1 = 0;
			double tempSum2 = 0;
			for (int j : friends) {
				if (i == 1) {
					tempSum2 += PandQ[0][i-1][j] * weights[j][marker];
				}
				else if (i >= 2) {
					tempSum1 += PandQ[1][i-2][j] * weights[j][marker];
					tempSum2 += PandQ[0][i-1][j] * weights[j][marker] * (1 - PandQ[1][i-2][j]);
					
					/*
					System.out.println("Temp1: " + tempSum1);
					System.out.println("Temp2: " + tempSum2);
					*/
				}
			}
			firstTerm[i] = 1 - Math.min(tempSum1, 1 - 0.000000001);
			secondTerm[i] = tempSum2;
			
			/*
			System.out.println(i);
			System.out.println(firstTerm[i]);
			System.out.println(secondTerm[i]);
			System.out.println();
			*/
		}
		
		// for specific CENTER
		double[][] deltaPdeltaW = new double[timeLim][friends.size()];
		for (int i = 0; i < timeLim; i++) {
			for (int j = 0; j < friends.size(); j++) { // V_A
				int k = friends.get(j);
				if (i == 0) deltaPdeltaW[i][j] = 0;
				else if (i == 1) deltaPdeltaW[i][j] = PandQ[0][i-1][k] / firstTerm[i];
				else {
					deltaPdeltaW[i][j] = (firstTerm[i] * PandQ[0][i-1][k] * (1 - PandQ[1][i-2][k]) + PandQ[1][i-2][k] * secondTerm[i]) / (Math.pow(firstTerm[i], 2));
					
					/*
					System.out.println(i + " " + j + " " + k);
					System.out.println(firstTerm[i]);
					System.out.println(PandQ[0][i-1][k]);
					System.out.println(PandQ[1][i-2][k]);
					System.out.println(secondTerm[i]);
					System.out.println(deltaPdeltaW[i][j]);
					System.out.println();
					*/
					
				}	
			}
		}
		
		/*
		for (int i = 0; i < timeLim; i++) {
			for (int j = 0; j < friends.size(); j++) {
				System.out.println(center.friends.get(j) + " " + deltaPdeltaW[i][j]);
			}
			System.out.println();
		}
		*/
		
		double[] deltaQdeltaW = new double[friends.size()]; // only necessary at t=timeLim
		for (int i = 0; i < friends.size(); i++) {
			double tempSum = 0;
			for (int m = 1; m <= timeLim-1; m++) {
				double product = 1;
				for (int n = m+1; n <= timeLim-1; n++) {
					product *= (1 - PandQ[0][n][marker]);
				}
				tempSum += (1 - PandQ[1][m-1][marker]) * deltaPdeltaW[m][i] * product;
				
				/*
				System.out.println(product);
				System.out.println(deltaPdeltaW[m][i]);
				System.out.println(tempSum);
				System.out.println();
				*/
				
			}
			deltaQdeltaW[i] = tempSum;
			// System.out.println("NEW FRIEND");
		}
		
		//System.out.println(marker);
		for (int i = 0; i < deltaQdeltaW.length; i++) {
			System.out.println(center.friends.get(i));
			System.out.println(deltaQdeltaW[i]);
			System.out.println(PandQ[1][timeLim-1][center.friends.get(i)]);
		}
		
		return deltaQdeltaW;
	}
}
