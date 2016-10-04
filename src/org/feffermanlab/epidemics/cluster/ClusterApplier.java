package org.feffermanlab.epidemics.cluster;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.WekaException;
import weka.core.converters.ConverterUtils.DataSource;


/**
 * A class to apply clustering algorithms to datasets
 * @author manu_
 *
 */
public class ClusterApplier {
	private static final int MAX_ITERATIONS=10, INIT_CLUSTERS=2, MAX_CLUSTERS=7;
	private  SimpleKMeans kmeansAlgorithm;
	private  ClusterEvaluation clusterEvaluation;
	private String latestFile;
	private ClusteringInfo bestClusteringInfo;
	private Map<String, ClusteringInfo> bestClusterings;
	private Map<SymmetricPair<Integer, Integer>,List<String>> featureMap;
	private int numberOfInstances;
	private double neutralExpectation;
	private double[][] probabilityOfAppearance;
	private MatrixCSVIO matrixCSVIO;
	private FitnessFunction fitnessFunction;
	private String probabilityFileName;
	private Map<Integer, double[]> instancesMapping;
	private List<String> attributeNames, allAttributeNames;
	public ClusterApplier() {
		super();
		clusterEvaluation = new ClusterEvaluation();
		numberOfInstances=-1;
		matrixCSVIO = new MatrixCSVIO();
		fitnessFunction = new SquaredErrorTimesPowerNumClusters(true);
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * Apply clustering to a set of instances and process exceptions
	 * @param inputDirectory the directory where the instances data is
	 */
	public void applyClusteringAndProcessExceptions(String inputDirectory){
		File[] listOfFiles = getListOfFiles(inputDirectory, "arff");
		applyClusteringAndProcessExceptions(listOfFiles, inputDirectory);
	}

	/**
	 * Apply clustering to a set of instances and process exceptions
	 * @param inputDirectory the directory where the instances data is
	 * @param probabilityFileName the file where to store the probability matrix indicating the probability of two samples to be in the same cluster
	 */
	public void applyClusteringAndProcessExceptions(String inputDirectory, String probabilityFileName){
		this.probabilityFileName=probabilityFileName;
		applyClusteringAndProcessExceptions(inputDirectory);
	}

	/** Get a list of the files in the directory
	 * @param inputDirectory the files directory
	 * @return A list of the files in the directory
	 */
	public File[] getListOfFiles(String inputDirectory, String extension) {
		File folder = new File(inputDirectory);
		File[] listOfFiles = folder.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathfile) {
				// TODO Auto-generated method stub
				return pathfile.isFile()&&!pathfile.getName().startsWith(".")&&pathfile.getName().endsWith("."+extension);
			}
		});
		if(listOfFiles.length==0)
			throw new IllegalArgumentException("There exist no valid files with extension "+extension+" in directory "+inputDirectory);
		return listOfFiles;
	}
	
	/**
	 * Apply clustering and associated calculations to datasets
	 * @param featureFiles Files where the datasets are
	 * @param baseDirectory File where the integrated clustering is written
	 */
	public void applyClusteringAndProcessExceptions(File[] featureFiles, String baseDirectory){
		try{
		applyKmeansToDirectory(featureFiles);
		neutralExpectation=calculateNeutralExpectation();
		createMapOfClusteringsWithInstancesInSameCluster();
		calculateProbabilityOfAppearance();
		writeProbabilityTable(baseDirectory);
		}
		catch(Exception e){
			System.err.println("Errors occurred while processing base directory "+baseDirectory);
		}
	}

	private void writeProbabilityTable(String baseDirectory) {
		// TODO Auto-generated method stub
		probabilityFileName=probabilityFileName==null?asMatrixFilename(bestClusterings.keySet())+".csv":probabilityFileName;
		matrixCSVIO.writeDoubleMatrix(String.join("/", baseDirectory,"probabilitymatrices", probabilityFileName), probabilityOfAppearance);
		
		
	}


	private String asMatrixFilename(Set<String> set) {
		// TODO Auto-generated method stub
		return String.join("-", set);
	}

	private void calculateProbabilityOfAppearance() {
		probabilityOfAppearance=new double[numberOfInstances][numberOfInstances];
		for (int instanceIterator1 = 0; instanceIterator1 < numberOfInstances; instanceIterator1++) {
			for (int instanceIterator2 = instanceIterator1+1; instanceIterator2 < numberOfInstances; instanceIterator2++) {
				probabilityOfAppearance[instanceIterator1][instanceIterator2]=probabilityOfAppearance[instanceIterator2][instanceIterator1]=calculateProbabilityOfAppearance(instanceIterator1, instanceIterator2);
			}
		}
		probabilityOfAppearance[numberOfInstances-1][numberOfInstances-1]=1.0;
		
	}
	private double calculateProbabilityOfAppearance(int instanceIterator1,
			int instanceIterator2) {
		// TODO Auto-generated method stub
		double probabilityOfAppearance=0.0;
		for(String feature:bestClusterings.keySet()){
			SymmetricPair<Integer, Integer> instancePair= new SymmetricPair<Integer, Integer>(instanceIterator1, instanceIterator2);
			if(featureMap.containsKey(instancePair)&&featureMap.get(instancePair).contains(feature)){
				probabilityOfAppearance+=bestClusterings.get(feature).getNumClusters();
			}
		}
		return probabilityOfAppearance/neutralExpectation;
	}
	private double calculateNeutralExpectation() {
		double localeNeutralExpectation = 0.0;
		for(String featureName: bestClusterings.keySet()){
			ClusteringInfo localeClusteringInfo=bestClusterings.get(featureName);
			for (int clusterIterator = 0; clusterIterator < localeClusteringInfo.getNumClusters(); clusterIterator++) {
				localeNeutralExpectation+=Math.pow(1-choose(numberOfInstances-localeClusteringInfo.getNumClusters(), numberOfInstances), localeClusteringInfo.getNumClusters());
				
			}
		}
		return localeNeutralExpectation;
		
	}
	
	/** Choose <i>choose</i> elements over <i>total</i>
	 * @param total Number of elements to choose from
	 * @param choose Number of chosen elements
	 * @return <i>choose</i> over <i>total</i>
	 */
	public static long choose(long total, long choose){
	    if(total < choose)
	        return 0;
	    if(choose == 0 || choose == total)
	        return 1;
	    return choose(total-1,choose-1)+choose(total-1,choose);
	}
	protected void createMapOfClusteringsWithInstancesInSameCluster() {
		featureMap=new HashMap<SymmetricPair<Integer, Integer>,List<String>>();
		 for (int instanceIndex1 = 0; instanceIndex1 < numberOfInstances; instanceIndex1++) {
			 for (int instanceIndex2 = instanceIndex1+1; instanceIndex2 < numberOfInstances; instanceIndex2++) {
				 for(String feature:bestClusterings.keySet()){
					 ClusteringInfo clusteringInfo=bestClusterings.get(feature);
					 if(clusteringInfo.getAssignment(instanceIndex1)==clusteringInfo.getAssignment(instanceIndex2)){
						 SymmetricPair<Integer,Integer> instancePair = new SymmetricPair<Integer,Integer>(instanceIndex1, instanceIndex2);
						 List<String> featureList=null;
						 if(featureMap.containsKey(instancePair)){
							 featureList=featureMap.get(instancePair);
						 }
						 else{
							 featureList= new LinkedList<String>();
						 }
						 featureList.add(feature);
						 featureMap.put(instancePair, featureList);
					 }
				 }
			 }
		}
	}
	protected void applyKmeansToDirectory(File[] listOfFiles) throws Exception
			{
		 instancesMapping= new HashMap<Integer, double[]>();
		 bestClusterings = new HashMap<String, ClusteringInfo>();
		 allAttributeNames =new LinkedList<String>();
		 for(int fileIndex=0; fileIndex<listOfFiles.length; fileIndex++){
			 String latestFileCompleteRoute=listOfFiles[fileIndex].getAbsolutePath();
			 latestFile=listOfFiles[fileIndex].getName();
			 latestFile=latestFile.substring(0, latestFile.lastIndexOf('.'));
			 double logLikelihood = testDifferentNumberOfClusters(latestFileCompleteRoute, fileIndex);
			 registerBestClustering();
			 System.out.println("\nThe best clustering for feature "+latestFile+" is:\n"+bestClusteringInfo+"\nwith evaluation "+logLikelihood);
		 }
	}
	protected void registerBestClustering() throws WekaException {
		try {
			bestClusterings.put(latestFile, bestClusteringInfo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			WekaException wekaException= new WekaException("Errors ocurred when accessing the assignments for file "+latestFile);
			wekaException.setStackTrace(e.getStackTrace());
			throw wekaException;
		
		}
	}
	protected double testDifferentNumberOfClusters(String latestFileCompleteRoute, int fileIndex)
			throws Exception {
		double bestLogLikelihood=Double.MAX_VALUE;
		bestClusteringInfo=null;
		createInstanceMapping(latestFileCompleteRoute, fileIndex);
		for(int numberOfClusters=INIT_CLUSTERS; numberOfClusters<=MAX_CLUSTERS; numberOfClusters++){
			 bestLogLikelihood = updateKmeansAndTreatExceptions(latestFileCompleteRoute,
					bestLogLikelihood, numberOfClusters);
		 }
		return bestLogLikelihood;
	}
	
	private void createInstanceMapping(String structureRoute, int fileIndex) throws Exception {
		Instances structure=(new DataSource(structureRoute)).getDataSet();
		attributeNames= new LinkedList<String>();
		for (int i = 0; i < structure.numAttributes(); i++) {
			attributeNames.add(structure.attribute(i).name());
		}
		allAttributeNames.addAll(attributeNames.stream().map((String name) ->name+fileIndex).collect(Collectors.toList()));
		for(int instanceIndex=0; instanceIndex<structure.numInstances(); instanceIndex++){
			Instance instance = structure.instance(instanceIndex);
			double[] featureValues=instancesMapping.containsKey(instanceIndex)?instancesMapping.get(instanceIndex): new double[]{};
			instancesMapping.put(instanceIndex, concatenateArrays(featureValues, instance.toDoubleArray()));
		}
		
	}
	
	/**
	 * Concatenate two arrays
	 * @param firstArray The first array
	 * @param secondArray The second array
	 * @return concatenation of two arrays
	 */
	private double[] concatenateArrays(double[] firstArray, double[] secondArray) {
		// TODO Auto-generated method stub
		double[] concatenatedArrays= new double[firstArray.length+secondArray.length];
		for (int i = 0; i < firstArray.length; i++) {
			concatenatedArrays[i]=firstArray[i];
		}
		for (int i = 0; i < secondArray.length; i++) {
			concatenatedArrays[i+firstArray.length]=secondArray[i];
		}
		return concatenatedArrays;
	}


	protected double updateKmeansAndTreatExceptions(String latestFileCompleteRoute,
			double bestFitnessValue, int numberOfClusters) throws Exception {
		Instances structure;
		try{
			 structure = applyKmeans(numberOfClusters, latestFileCompleteRoute);
			 try {
				 
				  double fitnessValue = evaluateCluster(structure);
				  if(fitnessValue<=bestFitnessValue){
					  bestFitnessValue=fitnessValue;
					  bestClusteringInfo=new ClusteringInfo(kmeansAlgorithm.getNumClusters(), kmeansAlgorithm.getAssignments(), bestFitnessValue, latestFile);
				  }
				} catch (Exception e) {
					Exception localeException= new WekaException("There was an error when evaluating clustering for file: "+latestFile);
					localeException.setStackTrace(e.getStackTrace());
					// TODO Auto-generated catch block
					throw localeException;
				}
			 }
			 catch(Exception e){
				 Exception localeException= new IOException("There was an error when processing file: "+latestFile);
				 localeException.setStackTrace(e.getStackTrace());
				 throw localeException;
			 }
		return bestFitnessValue;
	}
	private double evaluateCluster(Instances structure) throws Exception {
		clusterEvaluation.setClusterer(kmeansAlgorithm);
		clusterEvaluation.evaluateClusterer(structure);
		return calculateEvaluation();
		
	}
	protected double calculateEvaluation() {
		return fitnessFunction.getFitnessValue(kmeansAlgorithm);
	}
	/**
	 * Apply K-means given a number of clusters and a path where the data to cluster is
	 * @param inputClusters Number of clusters to set
	 * @param inputPath Path to the dataset to cluster
	 * @return the clustered data structure
	 * @throws Exception if an error occurred during clustering
	 */
	public Instances applyKmeans(int inputClusters, String inputPath) throws Exception{
		DataSource source = new DataSource(inputPath);
		  Instances structure = source.getDataSet();
		 testAndInitializeNumberOfGenes(structure.numInstances());
		 // train Cobweb
		kmeansAlgorithm = new SimpleKMeans();
		kmeansAlgorithm.setPreserveInstancesOrder(true);
		kmeansAlgorithm.setNumClusters(inputClusters);
		kmeansAlgorithm.setMaxIterations(MAX_ITERATIONS);
		 kmeansAlgorithm.buildClusterer(structure);
		 return structure;
	}
	
	/**
	 * Get the assignments on the latest clustering
	 * @return The assignments on the latest clustering
	 * @throws Exception if no assignment has been performed yet
	 */
	public int[] getTemporalAssignments() throws Exception{
		return kmeansAlgorithm.getAssignments();
		
	}
	
	private void testAndInitializeNumberOfGenes(int numInstances) {
		numberOfInstances=Math.max(numberOfInstances, numInstances);
		
	}
	/**
	 * Create clustering matrix
	 * @return clustering matrix
	 */
	public Map<SymmetricPair<String,String>, Set<String>> createClusteringMatrix(){
		Map<SymmetricPair<String,String>, Set<String>> sameClusterMatrix= new HashMap<SymmetricPair<String,String>, Set<String>>();
		return sameClusterMatrix;
		
	}
	
	/**
	 * Get the probability of two instances to be in the same cluster
	 * @param instance1 First instance
	 * @param instance2 Second instance
	 * @return The probability of two instances to be in the same cluster
	 */
	public double getProbabilityOfAppearance(int instance1, int instance2){
		if(probabilityOfAppearance==null){
			throw new IllegalStateException("Probability of appearance not calculated yet");
		}
		try {
			return probabilityOfAppearance[instance1][instance2];
		} catch (IndexOutOfBoundsException e) {
			throw new IllegalArgumentException("Instance 1: "+instance1+" or instance 2: "+instance2+" are out of bounds");
			// TODO: handle exception
		}
		
	}
	public int getNumberOfInstances() {
		return numberOfInstances;
	}
	
	/** Get a list of the features in whose clustering both instances are in the same clustering
	 * @param instance1 First instance
	 * @param instance2 Second instance
	 * @return List of the features in whose clustering both instances are in the same clustering
	 */
	public List<String> commonFeatures( int instance1, int instance2){
		if(featureMap==null){
			throw new IllegalStateException("Feature map not generated yet");
		}
		SymmetricPair<Integer, Integer> instancePair= new SymmetricPair<Integer, Integer>(instance1, instance2);
		return !featureMap.containsKey(instancePair)?new LinkedList<String>():new LinkedList<String>(featureMap.get(instancePair));
	}
	
	/**
	 * Returns true if there exists any feature for which both instances are in the same clustering, false otherwise.
	 * @param instance1 First instance
	 * @param instance2 Second instance
	 * @return True if there exists any feature for which both instances are in the same clustering, false otherwise.
	 */
	public boolean containsAnyCommonFeature(int instance1, int instance2){
		return !commonFeatures(instance1, instance2).isEmpty();
	}
	public Map<String, ClusteringInfo> getBestClusterings() {
		return new HashMap<String, ClusteringInfo>(bestClusterings);
	}
	
	public Set<Integer> getInstances(){
		Set<Integer> instanceSet= new HashSet<Integer>();
		for (int instanceID = 0; instanceID < numberOfInstances; instanceID++) {
			instanceSet.add(instanceID);
			
		}
		return instanceSet;
	}

	public int getNumberOfFeatures(){
		if(bestClusterings==null){
			throw new IllegalStateException("Clustering not applied yet");
		}
		return bestClusterings.size();
	}


	public void setFitnessFunction(FitnessFunction fitnessFunction) {
		this.fitnessFunction = fitnessFunction;
	}


	/**
	 * Get a mapping containing the feature values for each instance numeric ID
	 * @return A mapping containing the feature values for each instance numeric ID
	 */
	public Map<Integer, double[]> getInstancesMapping() {
		return new HashMap<Integer, double[]>(instancesMapping);
	}


	/**
	 * Get the names of all attributes from the latest clustering
	 * @return A list containing the names of all attributes
	 */
	public List<String> getLatestAttributeNames() {
		return new LinkedList<String>(attributeNames);
	}
	
	/**
	 * Get the names of all attributes from all clusterings
	 * @return A list containing the names of all attributes
	 */
	public List<String> getAllAttributeNames() {
		return new LinkedList<String>(allAttributeNames);
	}
	
	
}
