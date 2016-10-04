package org.feffermanlab.epidemics.cluster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ClusterIntersector implements ClusteringFramework{
	
	protected Map<String, ClusteringInfo> featureNamedClusteringInfo;
	protected List<String> clusteringsToProcess;
	private Map<Integer,Set<Cluster<Integer>>> partialClusters;
	private List<Cluster<Integer>> intersectedClusters;
	private int numberOfInstances;
	private IncidenceMatrixBuilder incidenceMatrixBuilder;
	private List<Integer> instanceOrder;
	
	public ClusterIntersector() {
		super();
		instanceOrder= new LinkedList<>();
		// TODO Auto-generated constructor stub
	}

	public void readInstanceOrder(String instanceOrder){
		
	}
	/**
	 * Write the incidence matrix into a file using the feature names
	 * @param outputDirectory The directory where to write the belongning matrix
	 */
	public void writeIncidenceMatrix(String outputDirectory) {
		writeIncidenceMatrix(outputDirectory, asMatrixFilename(featureNamedClusteringInfo.keySet())+".csv");
		
	}

	@Override
	public void writeIncidenceMatrix(String outputDirectory, String outputFile) {
		incidenceMatrixBuilder.writeIncidenceMatrices(outputDirectory, outputFile);
		
	}
	
	private String[] convertToStringArray(byte[] bs) {
		// TODO Auto-generated method stub
		String[] stringArray= new String[numberOfInstances];
		for (int instanceIndex = 0; instanceIndex < numberOfInstances; instanceIndex++) {
			stringArray[instanceIndex]=bs[instanceIndex]+"";
		}
		return stringArray;
	}
	private String asMatrixFilename(Set<String> clusteringsToProcess) {
		// TODO Auto-generated method stub
		return String.join("-", clusteringsToProcess);
	}
	/**
	 * Intersect different clusterings from different features and store the clustering matrix in incidenceMatrix
	 * @param clusteringInfos The clusterings to intersect
	 * @throws IOException If errors occurred while reading the original order of the instances
	 */

	public void intersectClusters(Map<String, ClusteringInfo> clusteringInfos){
		intersectClusters(clusteringInfos, null);
	}
	
	/**
	 * Intersect different clusterings from different features and store the clustering matrix in incidenceMatrix
	 * @param clusteringInfos The clusterings to intersect
	 * @param instancesOrderRoute Original order of the clustered instances
	 * @throws IOException If errors occurred while reading the original order of the instances
	 */
	
	public void intersectClusters(Map<String, ClusteringInfo> clusteringInfos, String instancesOrderRoute){
		partialClusters = new HashMap<Integer,Set<Cluster<Integer>>>();
		intersectedClusters = new LinkedList<Cluster<Integer>>();
		ClusterSelectionCriterion<Integer> largestCluster = new LargestClusterSelectionCriterion<Integer>();
		featureNamedClusteringInfo=clusteringInfos;
		Set<Integer> instances= featureNamedClusteringInfo.values().iterator().next().getInstances();
		numberOfInstances=instances.size();
		while(!instances.isEmpty()){
			initiateClusterings();
			Cluster<Integer> clusterIndex= getLargestCluster(largestCluster);
			while(thereExistClusteringsToProcess()){
				clusterIndex=getLargestCluster(new LargestIntersectionSelectionCriterion<Integer>(clusterIndex));
				addClusterToPartialClusters(partialClusters, clusterIndex);
			}
			instances.removeAll(clusterIndex.getInstances());
			removeClusteredInstances(clusterIndex.getInstances());
			intersectedClusters.add(clusterIndex);
				
		}
		System.out.println("Clustering finished");
		buildIncidenceMatrix(instancesOrderRoute);
	}

	@Override
	public void buildIncidenceMatrix(String samplingOrderRoute){
		incidenceMatrixBuilder = new IncidenceMatrixBuilder(numberOfInstances, intersectedClusters);
		incidenceMatrixBuilder.buildIncidenceMatrix(samplingOrderRoute);
		
	}

	private void removeClusteredInstances(Set<Integer> instanceSet) {
		for(ClusteringInfo clusteringInfo: featureNamedClusteringInfo.values()){
			clusteringInfo.removeInstances(instanceSet);
		}
		// TODO Auto-generated method stub
		
	}

	private void addClusterToPartialClusters(
			Map<Integer, Set<Cluster<Integer>>> partialClusters,
			Cluster<Integer> clusterIndex) {
		int numberOfFeatures=clusterIndex.getNumberOfFeatures();
		Set<Cluster<Integer>> setOfPartialClusters=partialClusters.containsKey(numberOfFeatures)?
			partialClusters.get(numberOfFeatures):
			new HashSet<Cluster<Integer>>();
		setOfPartialClusters.add(clusterIndex);
		partialClusters.put(numberOfFeatures, setOfPartialClusters);
		// TODO Auto-generated method stub
		
	}

	private Cluster<Integer> getLargestCluster(ClusterSelectionCriterion<Integer> clusterSelectionCriterion) {
		// TODO Auto-generated method stub
		Cluster<Integer> largestCluster=null;
		String selectedFeature="";
		for(String iteratedFeature: clusteringsToProcess){
			for(Cluster<Integer> iteratedCluster:featureNamedClusteringInfo.get(iteratedFeature).getClusters()){
				if(clusterSelectionCriterion.selectCluster(largestCluster, iteratedCluster)){
					largestCluster=clusterSelectionCriterion.getSelectedCluster();
					selectedFeature=iteratedFeature;
				}
			}
		}
		clusteringsToProcess.remove(selectedFeature);
		return clusterSelectionCriterion.processCluster(largestCluster);
		
	}



	private void initiateClusterings() {
		clusteringsToProcess = new ArrayList<String>(featureNamedClusteringInfo.keySet());
		
	}

	private boolean thereExistClusteringsToProcess() {
		// TODO Auto-generated method stub
		return !clusteringsToProcess.isEmpty();
	}

	protected Cluster<Integer> getNextCluster() {
		return featureNamedClusteringInfo.get(randomNotProcessedFeatureName()).getClusters().iterator().next();
	}

	private String randomNotProcessedFeatureName() {
		// TODO Auto-generated method stub
		String selectedFeature= clusteringsToProcess.get(clusteringsToProcess.size()*(int)Math.floor(Math.random()));
		clusteringsToProcess.remove(selectedFeature);
		return selectedFeature;
		
	}


	private ClusteringInfo getClusteringInfo(String string) {
		// TODO Auto-generated method stub
		if(featureNamedClusteringInfo.containsKey(string))
			return featureNamedClusteringInfo.get(string);
		throw new IllegalArgumentException("Feature "+string+" is not recognized");
	}
	
	private Set<String> getFeatures(){
		return featureNamedClusteringInfo.keySet();
	}

	private Map<Integer, Set<Cluster<Integer>>> getPartialClusters() {
		if(partialClusters==null||partialClusters.isEmpty()){
			throw new IllegalStateException(getIntegrationPendingMessage());
		}
		return partialClusters;
	}

	private String getIntegrationPendingMessage() {
		// TODO Auto-generated method stub
		return "Cluster integration not performed yet or rendering void cluster mapping";
	}

	@Override
	public List<Cluster<Integer>> getClusters() {
		if(intersectedClusters==null){
			throw new IllegalStateException(getIntegrationPendingMessage());
		}
		return new LinkedList<Cluster<Integer>>(intersectedClusters);
	}

	@Override
	public void writeClustering(String clusteringRoute) throws IOException {
		incidenceMatrixBuilder.writeClusters(intersectedClusters, clusteringRoute);
		
	}
	
	/**
	 * Get the intersected clustering as an array of integers
	 * @return The intersected clustering in the form of an array of integers
	 */
	public int[] getClusteringsAsArray(){
		Map<Integer, Integer> clusteringArray = new HashMap<Integer, Integer>();
		int clusterIndex=0;
		for(Cluster<Integer> cluster:intersectedClusters){
			for(Integer instance:cluster.getInstances()){
				clusteringArray.put(instance, clusterIndex);
			}
			clusterIndex++;
			
		}
		int[] clusteringAsArray = new int[clusteringArray.keySet().size()];
		for (int i = 0; i < clusteringArray.keySet().size(); i++) {
			clusteringAsArray[i]=clusteringArray.get(i);
		}
		return clusteringAsArray;
	}
	
	@Override
	public int getNumberOfClusters(){
		return intersectedClusters.size();
	}

	@Override
	public void buildIncidenceMatrix(){
		buildIncidenceMatrix(null);
		
	}
	
}
