package org.feffermanlab.epidemics.cluster;

import java.util.HashSet;
import java.util.Set;

/**
 * A cluster of instances
 * @author manu_
 *
 * @param <E> Instance type
 */
public class Cluster<E> {
	
	private String featureName;
	private Set<E> instances;
	private int numberOfFeatures;
	
	/**
	 * @param featureName Features represented
	 * @param numberOfFeatures The number of features in {@code featureName}
	 */
	public Cluster(String featureName, int numberOfFeatures) {
		super();
		if (featureName == null) {
			throw new IllegalArgumentException("Argument of type "
					+ "String"
					+ " cannot be null when creating an object of type "
					+ getClass());
		}
		this.featureName = featureName;
		this.numberOfFeatures = numberOfFeatures;
		instances= new HashSet<E>();
	}
	
	@Override
	protected Object clone(){
		// TODO Auto-generated method stub
		Cluster<E> clone = new Cluster<E>(getFeatureName(), getNumberOfFeatures());
		for(E instance : getInstances()){
			clone.addInstance(instance);
		}
		return clone;
	}

	public int getNumberOfFeatures() {
		// TODO Auto-generated method stub
		return numberOfFeatures;
	}

	/** Add an instance to the cluster
	 * @param instanceID the instance to be added
	 */
	public void addInstance(E instanceID){
		this.instances.add(instanceID);
	}
	
	/** Get all instances from the cluster
	 * @return A set with all the cluster instances
	 */
	public Set<E> getInstances(){
		return new HashSet<E>(instances);
	}
	
	/**
	 * Check if the cluster contains an instance
	 * @param instanceID The instance to check
	 * @return True if the cluster contains {@code instanceID}, false otherwise
	 */
	public boolean containsInstance(E instanceID){
		return instances.contains(instanceID);
	}

	/** Get the feature name of the clustering
	 * @return the feature name of the clustering
	 */
	public String getFeatureName() {
		return featureName;
	}
	/** 
	 * Intersect this cluster with {@code inputCluster}
	 * @param inputCluster Cluster to be intersected
	 * @return Intersection of this and inputCluster
	 */
	public Cluster<E> intersectCluster(Cluster<E> inputCluster){
		Cluster<E> intersectionCluster = new Cluster<E>(getFeatureName()+"+"+inputCluster.getFeatureName(), getNumberOfFeatures()+inputCluster.getNumberOfFeatures());
		for(E instanceID: getInstances()){
			if(inputCluster.containsInstance(instanceID)){
				intersectionCluster.addInstance(instanceID);
			}
		}
		return intersectionCluster;
	}

	/**
	 * Get the number of instances
	 * @return Number of instances
	 */
	public int size() {
		// TODO Auto-generated method stub
		return instances.size();
	}
	
	/**
	 * Add a set of instances
	 * @param instanceSet Set with instances to add to the cluster
	 */
	public void addInstanceSet(Set<E> instanceSet){
		instances.addAll(instanceSet);
	}
	
	/**
	 * Retain all instances from {@code instanceSet}
	 * @param instanceSet Set with instances to retain to the cluster
	 */
	public void retainInstanceSet(Set<E> instanceSet){
		instances.retainAll(instanceSet);
	}
	
	/**
	 * Remove a set of instances
	 * @param instanceSet Set of instances to remove
	 */
	public void removeinstanceSet(Set<E> instanceSet){
		instances.removeAll(instanceSet);
	}
	
	/**
	 * Remove an instance
	 * @param E instance to remove
	 */
	public void removeInstance(E instance){
		instances.remove(instance);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Feature: "+getFeatureName()+", feature count: "+getNumberOfFeatures()+", instances: "+getInstances();
	}
	
}
