package org.feffermanlab.epidemics.cluster;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class ClusterAuxiliaryClass {

	SimpleKMeans kmeansApplier;
	
	/**
	 * Format cluster data as a string so that it can be read by Weka
	 * @param instances list of instances comprising the cluster data
	 * @param attributeNames names of the data features
	 * @return the cluster data formatted for Weka
	 */
	public String formatClusterData(List<double[]> instances, List<String> attributeNames) {
		// TODO Auto-generated method stub
		String centroidsAsString="@RELATION centroids\n";
		centroidsAsString+=attributeNames.stream()
				.map((String elem)-> "@ATTRIBUTE "+elem+" REAL")
				.reduce((String s1, String s2)->s1+"\n"+s2).get()+"\n\n";
		centroidsAsString+="@DATA\n";
		String centroidData="";
		for(double[] centroid: instances){
			for (int i = 0; i < centroid.length; i++) {
				centroidData+=centroid[i]+",";
			}
			centroidData=centroidData.substring(0, centroidData.length()-1)+"\n";
		}
		return centroidsAsString+centroidData;
	}
	
	/**
	 * Apply clustering to a dataset encoded as a string with a predefined number of clusters
	 * @param Kvalue K-value for clustering
	 * @param attrFileContent String encoding a cluster data set in Weka format
	 * @param MAX_ITERATIONS maximum number of clustering iterations
	 * @return the clustering assignments
	 * @throws Exception if a general error occurs during clustering
	 * @throws UnsupportedEncodingException if Weka does not recognize {@code attrFileContent}
	 */
	public int[] applyClustering(int Kvalue, String attrFileContent, int MAX_ITERATIONS) throws Exception, UnsupportedEncodingException {
		Instances structure=new DataSource(new ByteArrayInputStream(attrFileContent.getBytes("UTF-8"))).getDataSet();
		kmeansApplier=new SimpleKMeans();
		kmeansApplier.setPreserveInstancesOrder(true);
		kmeansApplier.setNumClusters(Kvalue);
		kmeansApplier.setMaxIterations(MAX_ITERATIONS);
		kmeansApplier.buildClusterer(structure);
		return kmeansApplier.getAssignments();
	}
	
	/**
	 * Calculate the centroid of a cluster
	 * @param cluster cluster of which calculate the centroid
	 * @param instancesMapping mapping with the feature values of each instance
	 * @return an array with the distance of each instance to the centroid
	 */
	public double[] calculateCentroid(Cluster<Integer> cluster, Map<Integer, double[]> instancesMapping) {
		
		// TODO Auto-generated method stub
		double[] centroid=null;
		for(Integer instance: cluster.getInstances()){
			double[] values=instancesMapping.get(instance);
			if(centroid==null){
				centroid=new double[values.length];
				for (int i = 0; i < centroid.length; i++) {
					centroid[i]=0;
				}
			}
			for (int i = 0; i < centroid.length; i++) {
				centroid[i]+=values[i];
			}
		}
		for (int i = 0; i < centroid.length; i++) {
			centroid[i]/=cluster.size();
		}
		return centroid;
	}
	
	
}
