package org.feffermanlab.epidemics.cluster;

import java.util.HashSet;
import java.util.Set;

import weka.core.Instances;

/**
 * A class to store K values
 * @author manu_
 *
 */
public class KData{
	public int Kvalue;
	public double fitnessValue;
	public Set<Pair<Double, Integer>> averages;
	public KData(){
		Kvalue=-1; 
		fitnessValue=Double.MAX_VALUE;
		averages= new HashSet<Pair<Double, Integer>>();
	}
	public KData(int bestK, double bestFit) {
		super();
		this.Kvalue = bestK;
		this.fitnessValue = bestFit;
		averages= new HashSet<Pair<Double, Integer>>();
	}
	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		KData clonedKData= new KData(Kvalue, fitnessValue);
		clonedKData.averages=getClusterInformation();
		return clonedKData;
	}
	
	protected void copyClusterAverages(Set<Pair<Double, Integer>> averages){
		this.averages= new HashSet<Pair<Double, Integer>>();
		this.averages.addAll(averages);
	}
	/**
	 * Register a vector of cluster averages
	 * @param clusterCentroids cluster centroids from which to calculate averages
	 * @param sizes Size of each cluster
	 */
	public void registerClusterAverages(Instances clusterCentroids, int[] sizes) {
		this.averages= new HashSet<Pair<Double, Integer>>();
		for(int instanceIndex=0; instanceIndex<clusterCentroids.numInstances(); instanceIndex++){
			this.averages.add(new Pair<Double, Integer>(clusterCentroids.instance(instanceIndex).value(0), sizes[instanceIndex]));
		}
		
	}
	
	protected Set<Pair<Double, Integer>> getClusterInformation(){
		Set<Pair<Double, Integer>> returnedAverages = new HashSet<Pair<Double, Integer>>();
		returnedAverages.addAll(averages);
		return returnedAverages;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Kvalue: "+Kvalue+"\nfitness: "+fitnessValue+"\naverages: "+averages;
	}
	
}