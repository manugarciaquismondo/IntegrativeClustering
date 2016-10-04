package org.feffermanlab.epidemics.cluster;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * A class to integrate clusters resulting from intersective clustering
 * @author manu_
 *
 * @param <E> The class of the elements in the clusters
 */
public class ClusterIntegrator implements ClusteringFramework{
	
	private Map<Integer, double[]> instancesMapping;
	private List<Cluster<Integer>> clusters;
	private List<String> attributeNames;
	private IncidenceMatrixBuilder matrixBuilder;
	private final int MAX_ITERATIONS=30;
	private List<Cluster<Integer>> rebuiltClusters;
	private ClusterAuxiliaryClass dataFormatter;
	
	
	public ClusterIntegrator(Map<Integer, double[]> instancesMapping, List<Cluster<Integer>> clusters, List<String> attributeNames) {
		super();
		this.instancesMapping = instancesMapping;
		this.clusters = clusters;
		this.attributeNames=attributeNames;
		this.dataFormatter= new ClusterAuxiliaryClass();
	}

	/**
	 * Integrate clusters from intersection clustering re-clustering the clusters' centroids
	 * @param Kvalue the number of centroid clusters to cluster
	 * @return a new partition integrating the clusters from integrated clustering
	 * @throws Exception if the clustering algorithm from Weka fails
	 */
	public void integrateClusters(int Kvalue) throws Exception{
		List<double[]> centroids=clusters.stream().map((Cluster<Integer> cluster)-> calculateCentroid(cluster)).collect(Collectors.toList());
		String attrFileContent=setCentroidsAsString(centroids);
		int[] assignments=applyClustering(Kvalue, attrFileContent);
		rebuiltClusters= rebuildClusters(assignments);
		
	}

	private List<Cluster<Integer>> rebuildClusters(int[] assignments) {
		// TODO Auto-generated method stub
		String featureNames=attributeNames.stream().reduce((s1, s2) ->s1+"-"+s2).get();
		List<Cluster<Integer>> rebuiltCluster=new LinkedList<Cluster<Integer>>();
		Map<Integer, List<Integer>> assignmentsAsMappings=getAssignmentsAsMapping(assignments);
		for (Entry<Integer, List<Integer>> entry :assignmentsAsMappings.entrySet()) {
			Cluster<Integer> cluster= new Cluster<Integer>(featureNames, attributeNames.size());
			entry.getValue().stream()
			.forEach(clusterIndex ->cluster
					.addInstanceSet(clusters.get(clusterIndex).getInstances()));
			rebuiltCluster.add(cluster);
			
		}
		return rebuiltCluster;
	}

	private Map<Integer, List<Integer>> getAssignmentsAsMapping(int[] assignments) {
		Map<Integer, List<Integer>> assignmentsAsMappings= new HashMap<Integer, List<Integer>>();
		for (int i = 0; i < assignments.length; i++) {
			List<Integer> centroidsInCluster=assignmentsAsMappings.containsKey(assignments[i])?assignmentsAsMappings.get(assignments[i]):new LinkedList<Integer>();
			centroidsInCluster.add(i);
			assignmentsAsMappings.put(assignments[i],centroidsInCluster);
		}
		return assignmentsAsMappings;
	}

	private int[] applyClustering(int Kvalue, String attrFileContent) throws Exception, UnsupportedEncodingException {
		return dataFormatter.applyClustering(Kvalue, attrFileContent, MAX_ITERATIONS);
	}

	private String setCentroidsAsString(List<double[]> centroids) {
		return dataFormatter.formatClusterData(centroids, attributeNames);
	}

	private double[] calculateCentroid(Cluster<Integer> cluster) {
		return dataFormatter.calculateCentroid(cluster, instancesMapping);
	}

	@Override
	public void buildIncidenceMatrix(String string){
		matrixBuilder= new IncidenceMatrixBuilder(rebuiltClusters.stream().flatMapToInt(x -> IntStream.of(x.size())).sum(), rebuiltClusters);
		matrixBuilder.buildIncidenceMatrix(string);
	}
	
	@Override
	public void buildIncidenceMatrix(){
		buildIncidenceMatrix(null);
	}
	@Override	
	public void writeClustering(String clusteringRoute) throws IOException {
		matrixBuilder.writeClusters(rebuiltClusters, clusteringRoute);
		
	}

	@Override
	public List<Cluster<Integer>> getClusters() {
		// TODO Auto-generated method stub
		return new LinkedList<Cluster<Integer>>(rebuiltClusters);
	}

	@Override
	public void writeIncidenceMatrix(String outputDirectory, String outputFile) {
		matrixBuilder.writeIncidenceMatrices(outputDirectory, outputFile);
		
	}

	@Override
	public int getNumberOfClusters() {
		// TODO Auto-generated method stub
		return rebuiltClusters.size();
	}
}
