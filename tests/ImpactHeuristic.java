package tests;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

import networks.Network;
import networks.Person;

public class ImpactHeuristic {
	
	static double[][] weights;
	static double[][][] PandQ;
	static double[] impactAll;

	public static void main(String[] args) throws NumberFormatException, IOException {
		// TODO Auto-generated method stub
		
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
		
		double[][] tempweights = makeClone(weights);
		
		int trials = 10; // number of trials
		int methods = 7; // number of methods
		int iterations = 15; // number of times a node can reduce weight
		double increment = 0.003;
		
		double[] record = new double[methods];
		//double[] recordSim = new double[methods];
		long[] times = new long[methods];
		double[] margins = new double[methods];
		//double optimal = 0.0;
		
		// calculate the closeness centrality once because it is static
		impactAll = Dijkstra.generateCloseness(startingNetwork);
		
		System.out.println(size);
		
		for (int r = 0; r < trials; r++) {
			System.out.println(r);
			
			// SELECTING V_I
			Random generator = new Random();
			int marker = generator.nextInt(size);
			Person center = startingNetwork.vertices.get(marker);
			while (center.friends.size() < 5) {
				marker = generator.nextInt(size);
				center = startingNetwork.vertices.get(marker);
			}
			
			for (int m = 0; m < methods; m++) {
				//System.out.println(m);
				weights = makeClone(tempweights);
				PandQ = GeneratePQnew.generatePQ(startingNetwork, weights, initialP, timeLim);
				//System.out.println("HERE IT IS: " + weights[center.friends.get(3)][marker]);
				
				long time = System.nanoTime();
				
				double start = PandQ[1][timeLim-1][marker];
				
				//double startSim = repeatedSampling(weights, initialP, 200)[marker];
				
				for (int k = 0; k < iterations; k++) {
					
					if (m < 6) {
					
						double[] dQdW = impactMetr(startingNetwork, marker, timeLim, m);
						
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
						
						/*
						if (m == 0) {
							optimal += dQdW[index];
						}
						*/
						
						/*
						System.out.println("Node: " + center.friends.get(index));
						double prev1 = weights[center.friends.get(index)][marker]; // previous weight
						weights[center.friends.get(index)][marker] = Math.max(0, weights[center.friends.get(index)][marker] - increment);
						System.out.println("Prev W: " + prev1);
						System.out.println("Margin: " + (prev1 - weights[center.friends.get(index)][marker]));
						double prev2 = PandQ[1][timeLim-1][marker]; // previous q
						PandQ = GeneratePQnew.generatePQ(startingNetwork, weights, initialP, timeLim);
						System.out.println("Resultant Q: " + PandQ[1][timeLim-1][marker]);
						System.out.println("Effective Impact: " + (prev2 - PandQ[1][timeLim-1][marker]) 
								/ (prev1 - weights[center.friends.get(index)][marker]));
						*/
						
						double prev1 = weights[center.friends.get(index)][marker];
						
						weights[center.friends.get(index)][marker] = Math.max(0, weights[center.friends.get(index)][marker] - increment);
						margins[m] += (prev1 - weights[center.friends.get(index)][marker]);
						//System.out.println(margins[m]);
					}
					
					PandQ = GeneratePQnew.generatePQ(startingNetwork, weights, initialP, timeLim);
					
				}
				
				if (m < 6) {
				
					double margin = (start - PandQ[1][timeLim-1][marker]) / margins[m] * increment * iterations;
					//double marginSim = (startSim - repeatedSampling(weights, initialP, 200)[marker]) / margins[m] * increment * iterations;
					//System.out.println(margin);
					//System.out.println(marginSim);
					record[m] += (margin);
				}
				//recordSim[m] += (marginSim);
				margins[m] = 0;
				times[m] += (System.nanoTime() - time);
				// System.out.println(m + " " + margin);
			}
		}
		
		
		System.out.println(times[0] / 1000000000.0 / trials);
		System.out.println(times[1] / 1000000000.0 / trials);
		System.out.println(times[2] / 1000000000.0 / trials);
		System.out.println(times[3] / 1000000000.0 / trials);
		System.out.println(times[4] / 1000000000.0 / trials);
		System.out.println(times[5] / 1000000000.0 / trials);
		System.out.println(times[6] / 1000000000.0 / trials);
		
		
		
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/Users/stevenqu/Documents/Network Project/tests/Reduction/Barabasi-Albert.txt")));
		
		out.println(size);
		out.println();
		
		out.println(record[0] / trials / increment / iterations);
		out.println(record[1] / trials / increment / iterations);
		out.println(record[2] / trials / increment / iterations);
		out.println(record[3] / trials / increment / iterations);
		out.println(record[4] / trials / increment / iterations);
		out.println(record[5] / trials / increment / iterations);
		//System.out.println(optimal / trials / iterations);
		out.println();
		
		/*
		out.println(recordSim[0] / trials / increment / iterations);
		out.println(recordSim[1] / trials / increment / iterations);
		out.println(recordSim[2] / trials / increment / iterations);
		out.println(recordSim[3] / trials / increment / iterations);
		out.println(recordSim[4] / trials / increment / iterations);
		out.println(recordSim[5] / trials / increment / iterations);
		
		out.println();
		*/
		
		out.println(times[0] / 1000000000.0 / trials);
		out.println(times[1] / 1000000000.0 / trials);
		out.println(times[2] / 1000000000.0 / trials);
		out.println(times[3] / 1000000000.0 / trials);
		out.println(times[4] / 1000000000.0 / trials);
		out.println(times[5] / 1000000000.0 / trials);
		
		out.close();
		
	}
	
	/*
	 * 0 - proposed impact metric
	 * 1 - probability stuff (q_a, t-1)
	 * 2 - weight (w_a to i)
	 * 3 - distance centrality (of a)
	 * 4 - degree centrality (degree of a)
	 * 5 - random
	 * 
	 * interesting observation: probability  seems to do just as well as proposed
	 */
	
	public static double[] impactMetr(Network startingNetwork, int marker, int timeLim, int mode) {
		Person center = startingNetwork.vertices.get(marker);
		if (mode == 0) {
			return impactIter(startingNetwork, marker, timeLim);
		}
		else if (mode == 1) {
			double[] impact = new double[center.friends.size()];
			for (int i = 0; i < center.friends.size(); i++) {
				impact[i] = PandQ[1][timeLim-1][center.friends.get(i)];
			}
			return impact;
		}
		else if (mode == 2) {
			double[] impact = new double[center.friends.size()];
			for (int i = 0; i < center.friends.size(); i++) {
				impact[i] = weights[center.friends.get(i)][marker];
			}
			return impact;
		}
		else if (mode == 3) {
			double[] impact = new double[center.friends.size()];
			for (int i = 0; i < center.friends.size(); i++) {
				impact[i] = impactAll[center.friends.get(i)];
			}
			return impact;
		}
		else if (mode == 4) {
			double[] impact = new double[center.friends.size()];
			for (int i = 0; i < center.friends.size(); i++) {
				Person friend = startingNetwork.vertices.get(center.friends.get(i));
				impact[i] = friend.friends.size();
			}
			return impact;
		}
		else if (mode == 5) {
			double[] impact = new double[center.friends.size()];
			Random generator = new Random();
			for (int i = 0; i < center.friends.size(); i++) {
				impact[i] = generator.nextDouble();
			}
			return impact;
		}
		return null;
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
				}
			}
			firstTerm[i] = 1 - Math.min(tempSum1, 1 - 0.000000001);
			secondTerm[i] = tempSum2;
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
					System.out.println(firstTerm[i]);
					System.out.println(PandQ[0][i-1][j]);
					System.out.println(PandQ[1][i-2][j]);
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
		}
		
		//System.out.println(marker);
		for (int i = 0; i < deltaQdeltaW.length; i++) {
			//System.out.println(center.friends.get(i) + " " + deltaQdeltaW[i]);
		}
		
		return deltaQdeltaW;
	}
	
	public static double[][] makeClone(double[][] original){
		double[][] copy = new double[original.length][original[0].length];
		for (int i = 0; i < original.length; i++) {
			for (int j = 0; j < original[0].length; j++) {
				copy[i][j] = original[i][j];
			}
		}
		return copy;
	}
	
	public static double[] repeatedSampling(double[][] weights, double[] initialP, int repeats) {
		int size = initialP.length;
		double[] timeP = new double[size];
		for (int i = 0; i < repeats; i++) {
			ArrayList<Integer> activated = sampleInfluence(weights, initialP);
			for (int j = 0; j < activated.size(); j++) {
				timeP[activated.get(j)]++;
			}
		}
		
		for (int i = 0; i < size; i++) {
			timeP[i] /= repeats;
		}		
		
		return timeP;
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
