package org.feffermanlab.epidemics.cluster;

import weka.clusterers.SimpleKMeans;

/**
 * A fitness function class calculated as the product of the summing of the square error by the power of 2 of the number of clusters
 * @author manu_
 *
 */
public class SquaredErrorTimesSquaredNumClusters implements FitnessFunction {

	private final double EVALUATION_OFFSET = 1;

	@Override
	public double getFitnessValue(SimpleKMeans kmeansAlgorithm) {
		// TODO Auto-generated method stub
		return (kmeansAlgorithm.getSquaredError()+EVALUATION_OFFSET)*(Math.pow(kmeansAlgorithm.getNumClusters(), 2));
	}

}
