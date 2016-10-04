package org.feffermanlab.epidemics.cluster.testing;

import static org.junit.Assert.assertTrue;
import static org.feffermanlab.epidemics.cluster.ClusterAgreementGenerator.*;

/**
 * A class for auxiliary operations with matrices
 * @author manu_
 *
 */
public class MatrixAuxiliaryClass {
	

	public MatrixAuxiliaryClass() {
		super();
		falseSameCluster=falseDifferentCluster=-1.0d;
		// TODO Auto-generated constructor stub
	}
	private double falseSameCluster, falseDifferentCluster;
	/**
	 * Average a matrix of double values
	 * @param averageMatrix matrix to average
	 * @return the average value of {@code averageMatrix}
	 */
	public double averageMatrix(double[][] averageMatrix) {
		double matrixAverageValue=0.0;
		for (int i = 0; i < averageMatrix.length; i++) {
			for (int j = 0; j < averageMatrix.length; j++) {
				assertTrue(averageMatrix[i][j]>=0.0f&&averageMatrix[i][j]<=1.0f);
				matrixAverageValue+=averageMatrix[i][j];
			}
		}
		matrixAverageValue/=Math.pow(averageMatrix.length, 2);
		return matrixAverageValue;
	}

	/**
	 * Binarize an input matrix so that if the cell is greater to 0, the position is set to 1. Otherwise, it is set to 0. Also, calculate false discovery rates from the matrix
	 * @param inputMatrix matrix to binarize
	 * @return the binarized matrix
	 */
	public double[][] binarizeMatrixAndCalculateFailureRates(double[][] inputMatrix) {
		// TODO Auto-generated method stub
		falseSameCluster=falseDifferentCluster=0.0d;
		double binarizedMatrix[][]=new double[inputMatrix.length][inputMatrix.length];
		for (int i = 0; i < inputMatrix.length; i++) {
			for (int j = 0; j < inputMatrix.length; j++) {
				int inputItem=(int) inputMatrix[i][j];
				switch(inputItem){
					case(SAME_CLUSTER_FOR_BOOTSTRAP): falseDifferentCluster++; break;
					case(DIFFERENT_CLUSTER_FOR_BOOTSTRAP): falseSameCluster++; break;
				}
				binarizedMatrix[i][j]=inputItem>0?1:0;
			}
		}
		falseDifferentCluster/=Math.pow(inputMatrix.length, 2);
		falseSameCluster/=Math.pow(inputMatrix.length, 2);
		return binarizedMatrix;
	}

	/**
	 * Get the proportion of disagreement in which two instances which are not in the same cluster in the bootstrap clustering are in the same cluster in the integrative clustering
	 * @return the proportion of disagreement in which two instances which are not in the same cluster in the bootstrap clustering are in the same cluster in the integrative clustering
	 */
	public double getFalseSameCluster() {
		return falseSameCluster;
	}

	/**
	 * Get the proportion of disagreement in which two instances which are in the same cluster in the bootstrap clustering are not in the same cluster in the integrative clustering
	 * @return the proportion of disagreement in which two instances which are in the same cluster in the bootstrap clustering are not in the same cluster in the integrative clustering
	 */
	public double getFalseDifferentCluster() {
		return falseDifferentCluster;
	}

}
