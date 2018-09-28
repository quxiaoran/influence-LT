package tests;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import networks.*;

public class GeneratePQnew {

	public static void main(String[] args) {
		
	}
	
	// first P/Q, second by time, third by node
	public static double[][][] generatePQ(Network startingNetwork, double[][] weights, double[] initialP, int timeLim) throws IOException {
				
		double[][] adjMatr = Network.generateAdjMatr(startingNetwork);
		
		ArrayList<double[]> pStar = new ArrayList<double[]>(); // prob. i is infected AT time t
		ArrayList<double[]> qStar = new ArrayList<double[]>(); // prob. i is infected BY time t
		
		pStar.add(initialP);
		qStar.add(initialP);
		
		// generates p^1 and q^1
		double[] nextP = new double[adjMatr.length];
		for (int i = 0; i < adjMatr.length; i++){
			double sum = 0;
			for (int j = 0; j < adjMatr.length; j++){
				if (adjMatr[j][i] > 0){
					sum += weights[j][i] * pStar.get(0)[j];
				}
			}
			nextP[i] = Math.min(1, sum);
		}
		pStar.add(nextP.clone());
		
		double[] nextQ = new double[adjMatr.length];
		for (int i = 0; i < adjMatr.length; i++){
			nextQ[i] = Math.min(1, qStar.get(0)[i] + (1 - qStar.get(0)[i]) * pStar.get(1)[i]); 
		}
		qStar.add(nextQ.clone());
		
		// generates p and q up to time limit
		for (int k = 2; k < timeLim; k++){
			
			for (int i = 0; i < adjMatr.length; i++){
				double botSum = 0; // bottom
				double topSum = 0; // top
				for (int j = 0; j < adjMatr.length; j++){
					if (adjMatr[j][i] > 0){
						botSum += qStar.get(k-2)[j] * weights[j][i];
						topSum += pStar.get(k-1)[j] * weights[j][i] * (1 - qStar.get(k-2)[j]);
					}
				}
				botSum = 1 - Math.min(1 - 0.0000000001, botSum);
				
				nextP[i] = Math.min(1, topSum / botSum);
			}
			pStar.add(nextP.clone());
			
			for (int i = 0; i < adjMatr.length; i++){
				nextQ[i] = Math.min(1, qStar.get(k-1)[i] + (1 - qStar.get(k-1)[i]) * pStar.get(k)[i]);
			}
			qStar.add(nextQ.clone());
			
			// System.out.println(k);
		}
		
		// generate output
		double[][][] out = new double[2][timeLim][adjMatr.length];
		
		for (int i = 0; i < timeLim; i++) {
			for (int j = 0; j < adjMatr.length; j++) {
				out[0][i][j] = pStar.get(i)[j];
				out[1][i][j] = qStar.get(i)[j];
			}
		}
		
		/*
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("/Users/stevenqu/Documents/Network Project/tests/PandQ.txt")));
		for (int i = 0; i < timeLim; i++) {
			for (int j = 0; j < adjMatr.length; j++) {
				writer.println(out[0][i][j] + " " + out[1][i][j]);
			}
		}
		writer.close();
		*/
		
		return out;
		
	}

}
