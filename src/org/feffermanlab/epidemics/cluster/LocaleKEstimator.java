package org.feffermanlab.epidemics.cluster;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;


/**
 * An estimator for the best K value using an ad-hoc implementation of the Gap Statistic
 * @author manu_
 *
 */
public class LocaleKEstimator extends KEstimator{
	

	private ClusterIntegrator clusterIntegrator;
	private final int CLUSTER_MAX_ITERATIONS=10;
	private BasicStatistics statistics;
	private Set<List<double[]>> samples;
	private Map<Integer, Double> gapValues;
	private double[][] WLogValues;
	private double[] sdValues;
	
	public LocaleKEstimator() {
		super();
		
		// TODO Auto-generated constructor stub
	}

	
	
	/**
	 * Calculate the distances to the centroid of each cluster
	 * @param instanceIDs IDs of the clustered instances
	 * @param clusterValues values associated with each one of the features for each instance
	 * @return The summing of the distances to the centroid for each one of the clusters
	 */
	private double[] calculateDistancesToCentroid(List<Cluster<Integer>> instanceIDs, Map<Integer, double[]> clusterValues){
		List<Double> distances= new LinkedList<Double>();
		for(Cluster<Integer> cluster:instanceIDs){
			double[] centroid=formater.calculateCentroid(cluster, clusterValues);
			distances.add(cluster.getInstances().stream().mapToDouble(item->calculateDistance(clusterValues.get(item), centroid)).sum());
		}
		double[] distancesAsArray=new double[distances.size()];
		for (int i = 0; i < distancesAsArray.length; i++) {
			distancesAsArray[i]=distances.get(i);
		}
		return distancesAsArray;
	}

	
	/**
	 * Calculate distances to the centroid and the WLog value
	 * @param instanceIDs IDs of the clustered instances
	 * @param clusterValues values associated with each one of the features for each instance
	 * @returnthe the WLog value associated with the partition
	 */
	private double calculateDistancesAndWLogValue(List<Cluster<Integer>> instanceIDs, Map<Integer, double[]> clusterValues){
		double[] distancesToCentroid=calculateDistancesToCentroid(instanceIDs, clusterValues);
		int[] clusterSizes=new int[instanceIDs.size()];
		for (int i = 0; i < instanceIDs.size(); i++) {
			clusterSizes[i]=instanceIDs.get(i).size();
		}
		return calculateWLogValue(distancesToCentroid, clusterSizes);
	}
	
	private double calculateWLogValue(double[] distances, int[] instancesPerCluster){
		double wValue= 0;
		for (int i = 0; i < instancesPerCluster.length; i++) {
			wValue+=distances[i]/instancesPerCluster[i];
		}
		return Math.log10(wValue);
	}
	
	/**
	 * Calculate the Euclidean distance between vectors
	 * @param x first vector
	 * @param y second vector
	 * @return Euclidean distance between {@code x} and {@code y}
	 */
	private double calculateDistance(double[] x, double[] y){
		double distance=0;
		for (int i = 0; i < y.length; i++) {
			distance+=Math.pow(x[i]-y[i],2);
		}
		return Math.sqrt(distance);
	}


	@Override
	public int estimateBestK(String directoryRoute) throws UnsupportedEncodingException, Exception{
		Map<Integer, double[]> instancesMapping = getInstancesMapping(directoryRoute);
		List<String> features=clusterApplier.getAllAttributeNames();
		gapValues = new HashMap<Integer, Double>();
		samples=generateSamples(features.size(), instancesMapping.size(), calculateDimensionRanges(instancesMapping));
		WLogValues= new double[MAX_K-MIN_K+1][samples.size()];
		sdValues=new double[MAX_K-MIN_K+1];
		calculateParametersForKValues(instancesMapping, features);
		int bestKValue = getBestKValue();
		return bestKValue;

	}



	/**
	 * Calculate parameters for a set of K values in the interval MIN_K and MAX_K
	 * @param map A mapping relating each instance integer key with the feature values for the instance
	 * @param features A list containing the names of the features 
	 * @throws Exception If errors occurred during clustering
	 * @throws UnsupportedEncodingException If Weka did not recognize the string representation of a sample
	 */
	private void calculateParametersForKValues(Map<Integer, double[]> map, List<String> features)
					throws Exception, UnsupportedEncodingException {
		for (int i = MIN_K; i <=MAX_K; i++) {
			generateAndProcessSamples(features, samples, WLogValues[i-MIN_K], i);
			this.clusterIntegrator=new ClusterIntegrator(clusterApplier.getInstancesMapping(), clusterIntersector.getClusters(), clusterApplier.getAllAttributeNames());
			this.clusterIntegrator.integrateClusters(i);
			double clusteredWLog=calculateDistancesAndWLogValue(clusterIntegrator.getClusters(),map);
			gapValues.put(i, calculateGapStatistics(WLogValues[i-MIN_K],clusteredWLog));
			sdValues[i-MIN_K]=calculateSKValue(WLogValues[i-MIN_K]);
		}
	}



	/**
	 * Obtain the best K value as the minimum K such that Gap(K)>=Gap(K+1)-sk(k+1)
	 * @return The best K value as the minimum K such that Gap(K)>=Gap(K+1)-sk(k+1)
	 */
	private int getBestKValue() {
		int bestKValue=MAX_K;
		for (int i = MIN_K; i <MAX_K; i++) {
			if(gapValues.get(i)>=gapValues.get(i+1)-sdValues[i-MIN_K+1]){
				bestKValue=i;
				break;
			}
		}
		return bestKValue;
	}



	/**
	 * Calculate sk value as the formula sd({@code ds})*sqrt(1+1/size(ds))
	 * @param ds the sample for which calculate the sk value
	 * @return the sk value for {@code sd}
	 */
	private double calculateSKValue(double[] ds) {
		// TODO Auto-generated method stub
		statistics=new BasicStatistics(ds);
		return statistics.getStdDev()*Math.sqrt(1+1/ds.length);
	}



	private void generateAndProcessSamples(List<String> features, Set<List<double[]>> samples, double[] WLogValues,
			int i) throws Exception, UnsupportedEncodingException {
		int sampleIndex=0;
		for(List<double[]> sample:samples){
			Map<Integer, double[]> sampleAsMap=transformToMap(sample);
			List<Cluster<Integer>> clustersAsList=transformToList(formater.applyClustering(i, formater.formatClusterData(sample, features), CLUSTER_MAX_ITERATIONS),features);
			WLogValues[sampleIndex]=calculateDistancesAndWLogValue(clustersAsList, sampleAsMap);
			sampleIndex++;
		}
	}
	
	private double calculateGapStatistics(double[] ds, double clusteredWLog) {
		// TODO Auto-generated method stub
		return Arrays.stream(ds).average().getAsDouble()-clusteredWLog;
	}



	private Map<Integer, double[]> transformToMap(List<double[]> sample) {
		// TODO Auto-generated method stub
		Map<Integer, double[]> sampleAsMap =new HashMap<Integer, double[]>();
		int instanceIndex=0;
		for(double[] instance: sample){
			sampleAsMap.put(instanceIndex, instance);
			instanceIndex++;
		}
		return sampleAsMap;
	}



	/**
	 * Transform a partition in array form to a list of clusters
	 * @param partition Partition resulting from clustering
	 * @param features A list containing the names of the features 
	 * @return The partition {@code partition} in the form of a list of clusters
	 */
	private List<Cluster<Integer>> transformToList(int[] partition, List<String> features) {
		// TODO Auto-generated method stub
		Map<Integer,Cluster<Integer>> clustersAsMap= new HashMap<Integer,Cluster<Integer>>();
		for (int i = 0; i < partition.length; i++) {
			Cluster<Integer> cluster=clustersAsMap.containsKey(partition[i])?clustersAsMap.get(partition[i]): new Cluster<Integer>(features.stream().reduce((x, y)->x+"-"+y).get(),features.size());
			cluster.addInstance(i);
			clustersAsMap.put(partition[i], cluster);
		}
		List<Cluster<Integer>> listTransformed=new LinkedList<Cluster<Integer>>();
		List<Integer> clusterKeys=new LinkedList<Integer>(clustersAsMap.keySet());
		clusterKeys.sort(new Comparator<Integer>(){
			@Override
			public int compare(Integer o1, Integer o2) {
				// TODO Auto-generated method stub
				return o1-o2;
			}
			
		});
		clusterKeys.stream().forEach(x->listTransformed.add(clustersAsMap.get(x)));
		return listTransformed;
	}



	/**
	 * Calculate the ranges for each dimension
	 * @param map A mapping associating each instance with the values of its features
	 * @return A bi-dimensional matrix containing the min ([0]) and max ([1]) values for each feature
	 */
	private double[][] calculateDimensionRanges(Map<Integer, double[]> map) {
		// TODO Auto-generated method stub
		double[][] maxMinValues=new double[map.values().iterator().next().length][2];
		for (int i = 0; i < maxMinValues.length; i++) {
			maxMinValues[i][0]=Double.MAX_VALUE;
			maxMinValues[i][1]=Double.MIN_VALUE;
		}
		
		for(double[] instanceValues : map.values()){
			for (int i = 0; i < instanceValues.length; i++) {
				maxMinValues[i][0]=Math.min(maxMinValues[i][0],instanceValues[i]);
				maxMinValues[i][1]=Math.max(maxMinValues[i][1],instanceValues[i]);
			}
		}
		return maxMinValues;
	}
	
	private Set<List<double[]>> generateSamples(int numberOfFeatures, int instancesPerSample,double[][] maxMinValues){
		Set<List<double[]>> samples= new HashSet<List<double[]>>();
		for (int i = 0; i < NUM_B; i++) {
			List<double[]> sample= new LinkedList<double[]>();
			for (int j = 0; j < instancesPerSample; j++) {
				double[] instance= new double[numberOfFeatures];
				for (int k = 0; k < numberOfFeatures; k++) {
					instance[k]=ThreadLocalRandom.current().nextDouble(maxMinValues[k][0], maxMinValues[k][1]);
				}
				sample.add(instance);
			}
			samples.add(sample);
		}
		return samples;
	}
	


}
