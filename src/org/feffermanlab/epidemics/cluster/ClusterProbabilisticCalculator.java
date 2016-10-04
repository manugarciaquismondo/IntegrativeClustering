package org.feffermanlab.epidemics.cluster;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import cern.jet.random.engine.DRand;
import cern.jet.random.sampling.RandomSampler;

/**
 * Class to calculate probabilities for a value K to be the best K-value
 * @author manu_
 *
 */
public class ClusterProbabilisticCalculator {
	
	private FitnessFunction fitnessFunction;
	private Set<Integer> ksamples, kvaluesToEvaluate;
	private Map<Integer, KData> clusterAverages;
	private Map<Integer, Set<Integer>> bestKMapping;
	private final int MIN_K=2, MAX_K=30, NUMBER_OF_KS=20;
	private Map<Integer, Instances> replicates;
	private boolean generateKValues;
	private String replicatesDirectory;
	private ProbabilityProvider probabilityProvider;

	
	public ClusterProbabilisticCalculator() {
		super();
		this.ksamples = new HashSet<Integer>();
		this.kvaluesToEvaluate = new HashSet<Integer>();
		clusterAverages = new HashMap<Integer, KData>();
		bestKMapping = new HashMap<Integer, Set<Integer>>();
		fitnessFunction = new SquaredErrorTimesPowerNumClusters(true);
		generateKValues=true;
		replicatesDirectory="";
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Calculate unconditional probability for <i>Kvalue</i> to be the best K--value 
	 * @param Kvalue Kvalue for which to calculate the probability
	 * @return unconditional probability for <i>Kvalue</i> to be the best K--value
	 * @throws Exception if any arithmetic error occurs
	 */
	public float calculateProbabilityForSpecificK(int Kvalue) throws Exception{
		generateKValues=ksamples==null||ksamples.isEmpty();
		if(ksamples==null||!ksamples.contains(Kvalue)){
			kvaluesToEvaluate.add(Kvalue);
			if(ksamples==null)
				ksamples= new HashSet<Integer>();
			ksamples.add(Kvalue);
		}
		generateAndCalculateBestKs(MIN_K, MAX_K, NUMBER_OF_KS);
		if(!bestKMapping.containsKey(Kvalue)) return 0.0f;
		float denominator = bestKMapping.values().stream().mapToInt(set->set.size()).sum();
		float numerator = bestKMapping.get(Kvalue).size();
		return numerator/denominator;
		
	}
	/**
	 * Calculate conditional probability for <i>Kvalue</i> to be the best K--value given the dataset <i>dataSet</i>
	 * @param dataSet Known dataset
	 * @param Kvalue Kvalue for which to calculate the probability
	 * @return conditional probability for <i>Kvalue</i> to be the best K--value given the dataset <i>dataSet</i>
	 * @throws Exception if any arithmetic error occurs
	 */
	public double calculateProbabilityForKToBeBestGivenDataset(int[] dataSet, int Kvalue) throws Exception{
		float probabilityForSpecificK=calculateProbabilityForSpecificK(Kvalue);
		if(probabilityForSpecificK==0.0f) return 0.0f;
		double logValue= Math.log(probabilityForSpecificK)+calculateProbabilityForDataGivenK(dataSet, Kvalue, false)-calculateProbabilityForData(dataSet, false);
		return Math.exp(logValue);
	}
	
	/**
	 * Calculate unconditional probability for <i>dataSet</i> to occur
	 * @param dataValues Dataset on which to calculate the probability
	 * @return unconditional probability for <i>dataSet</i> to occur
	 */
	public double calculateProbabilityForData(int[] dataValues){
		return calculateProbabilityForData(dataValues, true);
	}
	
	private double calculateProbabilityForData(int[] dataValues, boolean exp){
		Collection<Instances> instances= replicates.values();
		return calculateProbabilityForGivenData(dataValues, clusterAverages.values(), exp, instances);
	}
	
	private double calculateProbabilityForDataGivenK(int[] dataValues, int k, boolean exp){
		if(!bestKMapping.containsKey(k)){
			return 0.0d;
		}
		Collection<Instances> instances= bestKMapping.get(k).stream().map(v->replicates.get(v)).collect(Collectors.toSet());
		double result= calculateProbabilityForGivenData(dataValues, bestKMapping.get(k).stream().map(value->clusterAverages.get(value)).collect(Collectors.toSet()), exp, instances);
		return result;
	}
	
	/**
	 * Calculate conditional probability for {@code dataSet} to occur given that {@code k} is the best K--value
	 * @param dataSet dataset to occur
	 * @param k Best K-value
	 * @return conditional probability for {@code dataSet} to occur given that {@code k} is the best K--value
	 */
	public double calculateProbabilityForDataGivenK(int[] dataSet, int k){
		return calculateProbabilityForDataGivenK(dataSet, k, true);
	}
	
	private double checkExponential(double value, boolean exp){
		return exp?Math.exp(value):value;
	}
	
	private double calculateProbabilityForGivenData(int[] dataValues,
			Collection<KData> values, boolean exp, Collection<Instances> instances) {
		// TODO Auto-generated method stub
		int denominator = values.stream().flatMap(k->k.averages.stream()).mapToInt(v->v.getSecond()).sum();
		double returnedValue= Arrays.stream(dataValues).mapToDouble(v->calculateProbabilityForGivenData(v, values, instances)).sum()-dataValues.length*Math.log(denominator);
		return checkExponential(returnedValue, exp);
	}

	

	private double calculateProbabilityForGivenData(int dataValue,
			Collection<KData> values, Collection<Instances> instances) {
		Set<Double> instancesFlattened=flattenInstances(instances);
		probabilityProvider.setDataset(instancesFlattened);
		double probabilityNumerator=probabilityProvider.calculateProbability(dataValue, values);
		return Math.log(probabilityNumerator);
		// TODO Auto-generated method stub
		
	}


	private Set<Double> flattenInstances(Collection<Instances> values) {
		// TODO Auto-generated method stub
		Set<Double> instancesFlattened = new HashSet<Double>();
		for(Instances instances: values){
			for (int instanceIndex = 0; instanceIndex < instances.numInstances(); instanceIndex++) {
				instancesFlattened.add(instances.instance(instanceIndex).value(0));
			}
			
		}
		return instancesFlattened;
	}




	private void generateAndCalculateBestKs(int minK, int maxK, int numberOfKs) throws Exception{
		if(ksamples==null||ksamples.isEmpty()||generateKValues)
			generateKs(minK, maxK, numberOfKs);
		if(replicates==null||replicates.isEmpty()){
			replicates = new HashMap<Integer, Instances>();
			readReplicates();
		}
		if(kvaluesToEvaluate!=null&&!kvaluesToEvaluate.isEmpty())
			calculateBestKs();
	}
	
	/** Generate and calculate the best K values given the dataset in the directory
	 * @throws Exception if any arithmetic error occurs
	 */
	public void generateAndCalculateBestKs() throws Exception{
		generateAndCalculateBestKs(this.MIN_K, this.MAX_K, this.NUMBER_OF_KS);
	}
	
	private void readReplicates() throws IOException, Exception {
		// TODO Auto-generated method stub
		File[] files = new File(replicatesDirectory).listFiles(f-> f.isFile());
		int replicateIndex=0;
		for (File file: files) {
			Instances instances = new DataSource(file.getCanonicalPath()).getDataSet();
			replicates.put(replicateIndex++, instances);
		}
	}


	private void calculateBestKs() throws Exception{
		SimpleKMeans kmeans = new SimpleKMeans();
		kmeans.setPreserveInstancesOrder(true);
		int instanceIndex=0;
		for (Instances instances: replicates.values()) {
			KData bestkdata = new KData();
			for (int kvalue: kvaluesToEvaluate) {	
				kmeans.setNumClusters(kvalue);
				kmeans.buildClusterer(instances);
				KData localeKData = new KData(kvalue, calculateFitness(kmeans));
				localeKData.registerClusterAverages(kmeans.getClusterCentroids(), kmeans.getClusterSizes());
				if(localeKData.fitnessValue<bestkdata.fitnessValue){
					bestkdata = (KData) localeKData.clone();
				}
			}
			updateClusterAverages(instanceIndex++, (KData)bestkdata.clone());
		}
		kvaluesToEvaluate.clear();
		registerBestKFrequencies();
	}

	private void updateClusterAverages(int i, KData clone) {
		double refValue=clusterAverages.containsKey(i)?clusterAverages.get(i).fitnessValue:Double.MAX_VALUE;
		if(refValue>clone.fitnessValue){
			clusterAverages.put(i, clone);
		}
		
	}


	private void registerBestKFrequencies() {
		bestKMapping.clear();
		for(Entry<Integer, KData> kentry: clusterAverages.entrySet()){
			registerBestKFrequencies(kentry.getValue().Kvalue, kentry.getKey());
		}
			
	}
	
	


	private void registerBestKFrequencies(int kvalue, int replicateIndex) {
		Set<Integer> bestClassifiedDatasetsForK=bestKMapping.containsKey(kvalue)?bestKMapping.get(kvalue):new HashSet<Integer>();
		bestClassifiedDatasetsForK.add(replicateIndex);
		bestKMapping.put(kvalue, bestClassifiedDatasetsForK);
		
	}
	
	


	private void generateKs(int minK, int maxK, int numberOfKs) {
		RandomSampler sampler = new RandomSampler(numberOfKs, maxK-minK, minK, new DRand());
		long[] ksamples = new long[numberOfKs];
		sampler.nextBlock(numberOfKs, ksamples, 0);
		this.ksamples.addAll(transformArrayToSet(ksamples));
		this.kvaluesToEvaluate.addAll(this.ksamples);
		generateKValues=false;
	}
	
	

	private Set<Integer> transformArrayToSet(long[] ksamples) {
		// TODO Auto-generated method stub
		Set<Integer> returnedValue= new HashSet<Integer>();
		Arrays.stream(ksamples).forEach(v->returnedValue.add((int)v));
		return returnedValue;
	}

	private double calculateFitness(SimpleKMeans kmeans) {
		// TODO Auto-generated method stub
		return fitnessFunction.getFitnessValue(kmeans);
	}

	public void setFitnessFunction(FitnessFunction fitnessFunction) {
		this.fitnessFunction = fitnessFunction;
	}


	public String getReplicatesDirectory() {
		return replicatesDirectory;
	}


	public void setReplicatesDirectory(String replicatesDirectory) {
		this.replicatesDirectory = replicatesDirectory;
	}


	public void setProbabilityProvider(ProbabilityProvider probabilityProvider) {
		this.probabilityProvider = probabilityProvider;
	}
}
