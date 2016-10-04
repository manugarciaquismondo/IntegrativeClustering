package org.feffermanlab.epidemics.cluster;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public interface ClusteringFramework {
	
	/**
	 * Build the incidence matrix for the integrated clustering
	 * @param inputOrderRoute Route where the order of the instances is stored
	 * 
	 */
	public void buildIncidenceMatrix(String inputOrderRoute);
	
	/**
	 * Write the incidence matrix into a file 
	 * @param outputDirectory The directory where to write the indicence matrix
	 * @param outputFile The file where to write the matrix
	 */
	public void writeIncidenceMatrix(String outputDirectory, String outputFile);
		
	/**
	 * Write the intersected clustering in CSV file, including a header
	 * @param clusteringRoute The file where to write the clustering
	 * @throws IOException if an error occurs while writing the clustering
	 */
	public void writeClustering(String clusteringRoute) throws IOException;
	
	/**
	 * Get the intersected clusters for all features
	 * @return The intersected clusters for all features
	 */
	public List<Cluster<Integer>> getClusters();
	
	/**
	 * Get the number of clusters in the cluster intersection
	 * @return The number of clusters in the cluster intersection
	 */
	public int getNumberOfClusters();

	/**
	 * Build the incidence matrix for the integrated clustering using the default instance order
	 * 
	 */
	public void buildIncidenceMatrix();

}
