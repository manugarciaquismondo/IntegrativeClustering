package org.feffermanlab.epidemics.cluster;

import weka.clusterers.SimpleKMeans;

/**
 * A fitness function class calculated as the product of the summing of the square error by the number of clusters
 * @author manu_
 *
 */
public class SquaredErrorTimesPowerNumClusters implements FitnessFunction {
	private final double EVALUATION_OFFSET = 1, defaultKExponent=1d, defaultDistanceExponent=1.0d;
	private double kExponent=1.0d;
	private double distanceExponent=1.0d;
	public SquaredErrorTimesPowerNumClusters(double kExponent, double distanceExponent) {
		super();
		this.kExponent = kExponent;
		this.distanceExponent=distanceExponent;
	}
	public SquaredErrorTimesPowerNumClusters() {
		this(1.0d, 1.0d);
		// TODO Auto-generated constructor stub
	}
	public SquaredErrorTimesPowerNumClusters(boolean useOptimizedValues) {
		this();
		if(useOptimizedValues){
			this.kExponent=defaultKExponent;
			this.distanceExponent=defaultDistanceExponent;
		}
		// TODO Auto-generated constructor stub
	}
	@Override
	public double getFitnessValue(SimpleKMeans kmeansAlgorithm){
		return Math.pow((kmeansAlgorithm.getSquaredError()+EVALUATION_OFFSET), distanceExponent)*Math.pow(kmeansAlgorithm.getNumClusters(),kExponent);
	}

}
