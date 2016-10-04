package org.feffermanlab.epidemics.cluster;

import weka.clusterers.SimpleKMeans;

public interface FitnessFunction {
	/** Get the fitness value of a clustering
	 * @param kmeansAlgorithm Algorithm producing the clustering
	 * @return the fitness value of the clustering
	 */
	public double getFitnessValue(SimpleKMeans kmeansAlgorithm);
}
