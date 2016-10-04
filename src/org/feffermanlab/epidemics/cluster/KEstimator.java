package org.feffermanlab.epidemics.cluster;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * A general class to estimate the best K value for clustering
 * @author manu_
 *
 */
public abstract class KEstimator {

	protected final int MIN_K=2, MAX_K=5, NUM_B=20;
	
	protected ClusterApplier clusterApplier;
	protected ClusterIntersector clusterIntersector;
	protected ClusterAuxiliaryClass formater;
	
	/**
	 * Constructor that initializes the delegated objects for the estimator
	 */
	public KEstimator() {
		super();
		clusterApplier= new ClusterApplier();
		clusterIntersector = new ClusterIntersector();
		formater = new ClusterAuxiliaryClass();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Estimate the best <i>K</i> value for the dataset encoded in the file {@code directoryRoute}
	 * @param directoryRoute The route where the file encoding the dataset is
	 * @return The best estimation for the <i>K</i> value
	 * @throws UnsupportedEncodingException If the file {@code directoryRoute} does not contain a valid dataset
	 * @throws Exception If errors ocurred during clustering
	 */
	abstract public int estimateBestK(String directoryRoute) throws UnsupportedEncodingException, Exception;

	/**
	 * Get the mapping of the dataset encoded in the file {@code directoryRoute} 
	 * @param directoryRoute The route where the file encoding the dataset is
	 * @return The mapping of the dataset encoded in the file {@code directoryRoute} 
	 */
	protected Map<Integer, double[]> getInstancesMapping(String directoryRoute){
		clusterApplier.applyClusteringAndProcessExceptions(directoryRoute, "probabilitymatrix.csv");
		clusterIntersector.intersectClusters(clusterApplier.getBestClusterings());
		Map<Integer, double[]> instancesMapping=clusterApplier.getInstancesMapping();
		return instancesMapping;
	}

}
