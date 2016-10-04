package org.feffermanlab.epidemics.cluster;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * A class to build, store and handle the incidence matrix
 * @author manu_
 *
 */
public class IncidenceMatrixBuilder {
	
	private int numberOfInstances;
	private List<Cluster<Integer>> intersectedClusters;
	private int[][] incidenceMatrix;
	private MatrixCSVIO matrixCSVIO;
	
	/**
	 * Create a builder for the incidence matrix
	 * @param numberOfInstances number of instances in the incidence matrix
	 * @param inputClusters partition for which to build the incidence matrix
	 */
	public IncidenceMatrixBuilder(int numberOfInstances, List<Cluster<Integer>> inputClusters) {
		super();
		this.numberOfInstances = numberOfInstances;
		this.intersectedClusters = inputClusters;
		this.incidenceMatrix = new int[numberOfInstances][numberOfInstances];
		this.matrixCSVIO=new MatrixCSVIO();
	}
	/**
	 * Build the incidence matrix for the number of instances and intersected clusters given
	 * @param string Route of the original order of the clustered instances
	 */
	public void buildIncidenceMatrix(String string){
		List<Integer> instanceOrder= new LinkedList<Integer>();
		if(string!=null){
			instanceOrder=matrixCSVIO.getInstanceOrder(string);
		}
		else{
			for (int i = 0; i < numberOfInstances; i++) {
				instanceOrder.add(i);
			}
		}
		for (int instanceIndex1 = 0; instanceIndex1 < numberOfInstances; instanceIndex1++) {
			incidenceMatrix[instanceIndex1][instanceIndex1]=1;
			for (int instanceIndex2 = instanceIndex1+1; instanceIndex2 < numberOfInstances; instanceIndex2++) {
				incidenceMatrix[instanceIndex1][instanceIndex2]=incidenceMatrix[instanceIndex2][instanceIndex1]=instancesInSameCluster(instanceOrder.get(instanceIndex1), instanceOrder.get(instanceIndex2));
			}
		}
		incidenceMatrix[numberOfInstances-1][numberOfInstances-1]=1;
		
	}
	private byte instancesInSameCluster(int instanceIndex1, int instanceIndex2) {
		// TODO Auto-generated method stub
		for(Cluster<Integer> cluster:intersectedClusters){
			if(cluster.containsInstance(instanceIndex1)&&cluster.containsInstance(instanceIndex2))
				return 1;
			if(cluster.containsInstance(instanceIndex1)||cluster.containsInstance(instanceIndex2))
				return 0;
		}
		throw new IllegalArgumentException("Neither the instance "+instanceIndex1+" nor "+instanceIndex2+" were found in the clustering");
	}
	

	
	/**
	 * Write the incidence matrix into a file 
	 * @param outputDirectory The directory where to write the indicence matrix
	 * @param outputFile The file where to write the matrix
	 */
	public void writeIncidenceMatrices(String outputDirectory, String outputFile) {
		matrixCSVIO.writeIntegerMatrix(outputDirectory+"/"+outputFile, incidenceMatrix);
		
	}
	/**
	 * Write the intersected clustering in CSV file, including a header
	 * @param clusteringRoute The file where to write the clustering
	 * @throws IOException if an error occurs while writing the clustering
	 */
	public void writeClusters(List<Cluster<Integer>> intersectedClusters2, String clusteringRoute) throws IOException {
		matrixCSVIO.writeClusters(intersectedClusters, clusteringRoute);
		
	}
}
