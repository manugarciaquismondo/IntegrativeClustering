package org.feffermanlab.epidemics.cluster;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class ClusteringInfo {


	private int numClusters;
	private Map<Integer, Integer> assignments;
	private double evaluation;
	private String featureName;
	private Set<Cluster<Integer>> clusters;
	private Set<Integer> instanceSet;
	public ClusteringInfo(int numClusters, int[] assignments, double evaluation, String feature) {
		// TODO Auto-generated constructor stub
		this.numClusters = numClusters;
		copyAssignments(assignments);
		this.evaluation = evaluation;
		this.featureName=feature;
		copyClustersAndCreateInstanceSet();
		
	}
	protected void copyClustersAndCreateInstanceSet() {
		clusters=copyClusters();
		createInstanceSet();
	}
	private HashSet<Cluster<Integer>> copyClusters() {
		Map<Integer, Cluster<Integer>> clusterMap = new HashMap<Integer, Cluster<Integer>>();
		for (Entry<Integer, Integer>  instanceEntry: assignments.entrySet()) {
			Cluster<Integer> copiedCluster= clusterMap.containsKey(instanceEntry.getValue())?copiedCluster=clusterMap.get(instanceEntry.getValue()):new Cluster<Integer>(getFeature(), 1);
			copiedCluster.addInstance(instanceEntry.getKey());
			clusterMap.put(instanceEntry.getValue(), copiedCluster);
		}
		return new HashSet<Cluster<Integer>>(clusterMap.values());
		
	}
	private void createInstanceSet() {
		// TODO Auto-generated method stub
		instanceSet = new HashSet<Integer>();
		for (Integer  instanceID: assignments.keySet()) {
			instanceSet.add(instanceID);
		}
		
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Feature: "+getFeature()+", number of clusters: "+getNumClusters()+", evaluation: "+getEvaluation()+", assignments: "+printAssignments();
	}
	private String printAssignments() {
		String assignmentsString="{";
		for (Entry<Integer, Integer> assignmentIndex :assignments.entrySet()) {
			assignmentsString+=printAssignment(assignmentIndex)+", ";
		}
		assignmentsString=assignmentsString.substring(0,  assignmentsString.length()-2);
		// TODO Auto-generated method stub
		return assignmentsString+"}";
	}
	private String printAssignment(Entry<Integer, Integer> assignmentEntry) {
		// TODO Auto-generated method stub
		return " "+assignmentEntry.getKey()+":"+assignmentEntry.getValue();
	}
	/**
	 * Get the number of clusters in the clustering
	 * @return The number of clusters in the clustering
	 */
	public int getNumClusters() {
		return numClusters;
	}
	/**
	 * Get the quality of the clustering
	 * @return The quality of the clustering
	 */
	public double getEvaluation() {
		return evaluation;
	}
	
	/**
	 * Get the cluster assigned to an instance
	 * @param instanceIndex Instance whose cluster is returned
	 * @return the number identifying the cluster of {@code instanceIndex}
	 */
	public int getAssignment(int instanceIndex){
		if(!assignments.containsKey(instanceIndex)){
			throw new IllegalArgumentException("Instances "+instanceIndex+" not present in clustering");
		}
		return assignments.get(instanceIndex);
	}
	private void copyAssignments(int[] assignments) {
		this.assignments=new HashMap<Integer, Integer>();
		for(int assignmentIndex=0; assignmentIndex<assignments.length; assignmentIndex++){
			this.assignments.put(assignmentIndex,assignments[assignmentIndex]);
		}
		
	}
	
	public String getFeature(){
		return featureName;
	}
	
	private Cluster<Integer> getClustersWhichContainsInstance(Integer instanceID){
		for(Cluster<Integer> cluster: clusters){
			if(cluster.containsInstance(instanceID))
				return cluster;
		}
		throw new IllegalArgumentException("Instance "+instanceID+" not found");
	}
	/**
	 * Return all the instances in the clustering
	 * @return All the instances in the clustering
	 */
	public Set<Integer> getInstances() {
		// TODO Auto-generated method stub
		return new HashSet<Integer>(instanceSet);
	}
	
	/**
	 * Return all the clusters in the clustering
	 * @return All the clusters in the clustering
	 */
	public Set<Cluster<Integer>> getClusters() {
		return new HashSet<Cluster<Integer>>(clusters);
	}
	/**
	 * Remove all the instances in {@code instanceSet}
	 * @param instanceSet the instances to be removed
	 */
	public void removeInstances(Set<Integer> instanceSet) {
		for(Integer instancesID: instanceSet){
			assignments.remove(instancesID);
		}
		copyClustersAndCreateInstanceSet();
		
	}

}
