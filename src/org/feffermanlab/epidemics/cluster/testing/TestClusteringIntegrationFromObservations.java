package org.feffermanlab.epidemics.cluster.testing;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.feffermanlab.epidemics.cluster.LocaleKEstimator;
import org.feffermanlab.epidemics.cluster.Cluster;
import org.feffermanlab.epidemics.cluster.ClusterAgreementGenerator;
import org.feffermanlab.epidemics.cluster.ClusterApplier;
import org.feffermanlab.epidemics.cluster.ClusterIntersector;
import org.feffermanlab.epidemics.cluster.ClusterReader;
import org.feffermanlab.epidemics.cluster.ClusteringDirectoryRunner;
import org.feffermanlab.epidemics.cluster.ClusterIntegrator;
import org.feffermanlab.epidemics.cluster.ClusteringFramework;
import org.feffermanlab.epidemics.cluster.ClusteringInfo;
import org.feffermanlab.epidemics.cluster.JSATKEstimator;
import org.feffermanlab.epidemics.cluster.KEstimator;
import org.feffermanlab.epidemics.cluster.MatrixCSVIO;
import org.feffermanlab.epidemics.cluster.Pair;
import org.junit.Before;
import org.junit.Test;

import weka.core.Instances;


public class TestClusteringIntegrationFromObservations {

	private FileFilter directoryFilter;
	private ClusterReader clusterReader;
	private ClusteringDirectoryRunner directoryRunner;
	
	@Before
	public void setUp() throws Exception {

		directoryFilter=new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				return pathname.isDirectory()&&!pathname.getName().startsWith(".");
			}};
        clusterReader= new ClusterReader();
        directoryRunner = new ClusteringDirectoryRunner();
        directoryRunner.setUpDirectoryClustering("./relevantgenes");
	}
	

	@Test
	public void testIntegrateClusters() {
		directoryRunner.integrateClustersAndWriteIncidenceData("./observations");
	}
	
	@Test
	public void testIntegrateClustersFromCPUData() {
		directoryRunner.integrateClustersAndWriteIncidenceData("./cpudata");
	}
	
	@Test
	public void testIntegrateClustersFromGeneData() throws Exception {
		String geneDirectoryRoute="./geneclustering/relevantgenes";
		double agreementBetweenClusterings = getAgreementBetweenClusters(geneDirectoryRoute);
		System.out.println("The agreement between clusterings is "+agreementBetweenClusterings);
		System.out.println("The proportion of instances incorrectly clustered together and separately are "+getSameClusterMetrics());
		assertTrue(agreementBetweenClusterings>0.5f);
	}


	private Pair<Double,Double> getSameClusterMetrics() {
		return directoryRunner.getFalseSameClusterMetrics();
	}


	private double getAgreementBetweenClusters(String geneDirectoryRoute) throws Exception {
		return getAgreementBetweenClusters(geneDirectoryRoute, "./geneclustering/ClusteringForComparison.csv");
	}
	




	




	private double getAgreementBetweenClusters(String geneDirectoryRoute, String string) throws Exception {
		// TODO Auto-generated method stub
		return directoryRunner.getAgreementBetweenClusterings(geneDirectoryRoute, string);
	}


	@Test
	public void testMultipleGeneClusterings() throws Exception{
		String geneDirectoryRoute="./genesampling";
		File testDirectory= new File(geneDirectoryRoute);
		List<Pair<String, Double>> agreements = new LinkedList<Pair<String, Double>>();
		for(File directory : testDirectory.listFiles(directoryFilter)){
			System.out.println("Directory: "+directory.getName());
			agreements.add(new Pair<String, Double>(directory.getName(), getAgreementBetweenClusters(geneDirectoryRoute+"/"+directory.getName()+"/relevantgenes")));
		}
		System.out.println("Checking "+agreements.size()+" elements");
		for(int agreementIndex=1; agreementIndex<agreements.size(); agreementIndex++){			
			Pair<String, Double> currentElem=agreements.get(agreementIndex);
			Pair<String, Double> previousElem=agreements.get(agreementIndex-1);
			System.out.println("Comparing directory "+currentElem.getFirst()+" with value "+currentElem.getSecond()+" with directory "+previousElem.getFirst()+" with value "+previousElem.getSecond());
			assertTrue(currentElem.getSecond()<previousElem.getSecond());
		}
	}
	
	@Test
	public void testEquivalentAlgorithmicAndDirectClusterings() throws Exception{
		int[][] assignments = extractAssignments("./genesampling/generep1/", "202363_at.arff");
		int numberOfAssignments=assignments[0].length;
		for (int i = 0; i < numberOfAssignments; i++) {
			for (int j = 0; j < numberOfAssignments; j++) {
				assertTrue((assignments[0][i]==assignments[0][j]&&assignments[1][i]==assignments[1][j])||
						(assignments[0][i]!=assignments[0][j]&&assignments[1][i]!=assignments[1][j]));
			}
		}
		System.out.println("Test finished");
		
	}


	private int[][] extractAssignments(String kMeansRoute, String kMeansFile) throws Exception {
		return directoryRunner.extractAssignments(kMeansRoute, kMeansFile);
	}
	
	@Test
	public void testMissingParameterEstimation() throws Exception{
		int selectedSample=50;
		applyClusteringOnDirectory("geneestimation/relevantgenes");
		Cluster<Integer> selectedCluster=getClusters().stream().filter((Cluster<Integer> cluster)->cluster.containsInstance(selectedSample)).findFirst().get();
		selectedCluster.removeInstance(selectedSample);
		System.out.println("Cluster is "+selectedCluster);
	}
	
	@Test
	public void testMammalsMilkClustering() throws Exception{
		applyClusteringOnDirectory("mammalsmilk");
	}



	@Test
	public void test50WordsClustering() throws Exception{
		applyClusteringOnDirectory("largesamples/50words");
	}



	private void applyClusteringOnDirectory(String string) throws Exception {
		directoryRunner.applyClusteringOnDirectory("./"+string);
		
	}


	@Test
	public void testGlassClustering() throws Exception{
		applyClusteringOnDirectory("largesamples/glass");
	}
	
	@Test
	public void testGlassIterationsNonIntegrative() throws Exception{
		setIntegrateClusteringsAndEstimateBestK(false, false);
		testGlassIterations();
	}

	@Test
	public void testGlassIterationsIntegrative() throws Exception{
		setIntegrateClusteringsAndEstimateBestK(true, false);
		testGlassIterations();
	}

	private void setIntegrateClusteringsAndEstimateBestK(boolean integrateClusterings, boolean estimateBestK){
		directoryRunner.setIntegrateClusteringsAndEstimateBestK(integrateClusterings, estimateBestK);
	}

	
	private void testGlassIterations() throws Exception {
		String glassRepetitionsRoute="./largesamples/glass/repetitions";
		File glassDirectory = new File(glassRepetitionsRoute);
		File[] glassDirectories=glassDirectory.listFiles(directoryFilter);
		double[][] glassScores=new double[glassDirectories.length][2];
		int scoreIndex=0;
		for(File directory : glassDirectories){
			Pair<Double, Integer> clusteringApplication =directoryRunner.applyClusteringOnDirectory(glassRepetitionsRoute+"/"+directory.getName());
			glassScores[scoreIndex][0]=clusteringApplication.getFirst();
			glassScores[scoreIndex][1]=clusteringApplication.getSecond();
			scoreIndex++;
			System.out.println("Finished iteration "+scoreIndex);
		}
		directoryRunner.writeDoubleMatrix("./largesamples/glass/scores/scores.csv", glassScores);
	}
	
	@Test
	public void testEstimateBestK() throws Exception{
		setIntegrateClusteringsAndEstimateBestK(true, true);
		testGlassIterations();
		System.out.println("Test finished");

	}
	
	@Test
	public void runEpidemiologyIntegration() throws Exception{
		runDirectoryEpidemiologyIntegration("./epidemic_dataset", true, "clusteringknotset");
	}


	@Test
	public void runEpidemiologyClusteringOnFeatureCombination() throws Exception{
		String epidemiolgyCombinationsRoute="./epidemic_dataset/feature_combinations";
		File epidemiolgyCombinationsDirectory = new File(epidemiolgyCombinationsRoute);
		File[] epidemiolgyFiles=epidemiolgyCombinationsDirectory.listFiles(directoryFilter);
		for(File directory : epidemiolgyFiles){
			String directoryName=directory.getName();
			runDirectoryEpidemiologyIntegration(epidemiolgyCombinationsRoute+"/"+directoryName, true, "clusteringknotset");
			runDirectoryEpidemiologyIntegration(epidemiolgyCombinationsRoute+"/"+directoryName, false, "clusteringkset");
			System.out.println("clustering on directory "+directoryName);
		}
	}
	
	private void runDirectoryEpidemiologyIntegration(String baseDirectory, boolean estimateBestK, String clusteringFilename) throws Exception, IOException {
		setIntegrateClusteringsAndEstimateBestK(true, estimateBestK);
		
		applyClusteringOnDirectory(baseDirectory+"/relevantgenes");
		checkIntegrationAndWriteClustering(baseDirectory, clusteringFilename);
	}
	
	private void checkIntegrationAndWriteClustering(String baseDirectory, String clusteringFilename) throws IOException {
		directoryRunner.checkIntegrationAndWriteClustering(baseDirectory, clusteringFilename);
		
	}


	private void runBabyDataset(String babyRoute, String outputRoute) throws Exception{
		File[] babyFiles = new File(babyRoute).listFiles();
		Map<String, ClusteringInfo> clusterings= new HashMap<String, ClusteringInfo>();
		for(File babyFile : babyFiles){
			clusterings.put(babyFile.getName(), clusterReader.readCluster(babyFile.getAbsolutePath()));
		}
		intersectClusters(clusterings);
		writeClusters(outputRoute);
		System.out.println("Intersection finished");
	}
	
	private void writeClusters(String outputRoute) throws IOException {
		// TODO Auto-generated method stub
		directoryRunner.writeClusters(outputRoute);
	}


	private void intersectClusters(Map<String, ClusteringInfo> clusterings) {
		directoryRunner.intersectClusters(clusterings);
		
	}
	
	private List<Cluster<Integer>> getClusters() {
		return directoryRunner.getClusters();
		
	}


	@Test
	public void runBabyDatasetAllVariables() throws Exception{
		runBabyDataset("./baby_dataset/all_variables", "./baby_dataset/all_variable_clusters.csv");
	}
	
	@Test
	public void runBabyDatasetLastThreeVariables() throws Exception{
		runBabyDataset("./baby_dataset/last_three_variables", "./baby_dataset/last_three_variable_clusters.csv");
	}
}
