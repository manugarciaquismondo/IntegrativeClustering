package org.feffermanlab.epidemics.cluster.testing;

import static org.junit.Assert.*;

import org.feffermanlab.epidemics.cluster.ClusterProbabilisticCalculator;
import org.feffermanlab.epidemics.cluster.FrequentialProbabilityProvider;
import org.feffermanlab.epidemics.cluster.PoissonCDFProbabilityProvider;
import org.junit.Before;
import org.junit.Test;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import cern.jet.random.engine.DRand;

public class ClusterProbabilisticCalculatorTester {

	ClusterProbabilisticCalculator clusterCalculator;
	
	
	
	public ClusterProbabilisticCalculatorTester() throws Exception {
		super();
		

		// TODO Auto-generated constructor stub
	}

	@Before
	public void setUp() throws Exception {
		TestDirectoryProvider directoryProvider= new TestDirectoryProvider();
		clusterCalculator= new ClusterProbabilisticCalculator();
		PoissonCDFProbabilityProvider provider=new FrequentialProbabilityProvider(new DRand());
		provider.setInterval(500);
		clusterCalculator.setProbabilityProvider(provider);
		clusterCalculator.setReplicatesDirectory(directoryProvider.getReplicatesDirectory());
		clusterCalculator.generateAndCalculateBestKs();
	}

	@Test
	public void testCalculateProbabilityForSpecificK() throws Exception {
		double returnedValue=clusterCalculator.calculateProbabilityForSpecificK(5);
		System.out.println("Returned value for specific K: "+returnedValue);
		assertTrue("The returned value "+returnedValue+" is not a probability", returnedValue>0.0f&&returnedValue<1.0f);
	}
	
	@Test
	public void testCalculateProbabilityForSpecificDataset() throws Exception {
		int[] instancesVector = getInstanceVector();
		double returnedValue=clusterCalculator.calculateProbabilityForData(instancesVector);
		System.out.println("Returned value for specific dataset: "+returnedValue);
		assertTrue("The returned value "+returnedValue+" is not a probability", returnedValue>0.0f&&returnedValue<1.0f);
	}

	protected int[] getInstanceVector() throws Exception {
		String dataSetRoute="./bootstrapping-prob/test_replicates/test-replicate.arff";
		Instances instances = new DataSource(dataSetRoute).getDataSet();
		int instancesVector[]= new int[instances.numInstances()];
		for (int instanceIndex = 0; instanceIndex < instancesVector.length; instanceIndex++) {
			instancesVector[instanceIndex]=(int)instances.instance(instanceIndex).value(0);
		}
		return instancesVector;
	}

	
	@Test
	public void testCalculateProbabilityForSpecificDatasetGivenK() throws Exception {
		int[] instancesVector = getInstanceVector();
		double returnedValue=clusterCalculator.calculateProbabilityForDataGivenK(instancesVector, 5);
		System.out.println("Returned value for specific dataset given K: "+returnedValue);
		assertTrue("The returned value "+returnedValue+" is not a probability", returnedValue>0.0f&&returnedValue<1.0f);
	}
	
	@Test
	public void testCalculateProbabilityForSpecificKGivenDataset() throws Exception {
		int[] instancesVector = getInstanceVector();
		double returnedValue=clusterCalculator.calculateProbabilityForKToBeBestGivenDataset(instancesVector, 5);
		System.out.println("Returned value for specific Kgiven dataset: "+returnedValue);
		assertTrue("The returned value "+returnedValue+" is not a probability", returnedValue>0.0f&&returnedValue<1.0f);
	}
	
	@Test
	public void testCalculateProbabilityForSpecificKOnCDF() throws Exception {
		setCDFProvider();
		double returnedValue=clusterCalculator.calculateProbabilityForSpecificK(5);

		System.out.println("Returned value for specific K: "+returnedValue);
		assertTrue("The returned value "+returnedValue+" is not a probability", returnedValue>0.0f&&returnedValue<1.0f);
	}

	protected void setCDFProvider() {
		PoissonCDFProbabilityProvider provider= new PoissonCDFProbabilityProvider(new DRand());
		provider.setInterval(2000);
		clusterCalculator.setProbabilityProvider(provider);
	}
	
	@Test
	public void testCalculateProbabilityForSpecificDatasetOnCDF() throws Exception {
		setCDFProvider();
		int[] instancesVector = getInstanceVector();
		double returnedValue=clusterCalculator.calculateProbabilityForData(instancesVector);
		System.out.println("Returned value for specific dataset: "+returnedValue);
		assertTrue("The returned value "+returnedValue+" is not a probability", returnedValue>0.0f&&returnedValue<1.0f);
	}
	
	@Test
	public void testCalculateProbabilityForSpecificDatasetGivenKOnCDF() throws Exception {
		setCDFProvider();
		int[] instancesVector = getInstanceVector();
		double returnedValue=clusterCalculator.calculateProbabilityForDataGivenK(instancesVector, 5);
		System.out.println("Returned value for specific dataset given K: "+returnedValue);
		assertTrue("The returned value "+returnedValue+" is not a probability", returnedValue>0.0f&&returnedValue<1.0f);
	}
	
	@Test
	public void testCalculateProbabilityForSpecificKGivenDatasetOnCDF() throws Exception {
		setCDFProvider();
		int[] instancesVector = getInstanceVector();
		double returnedValue=clusterCalculator.calculateProbabilityForKToBeBestGivenDataset(instancesVector, 5);
		System.out.println("Returned value for specific Kgiven dataset: "+returnedValue);
		assertTrue("The returned value "+returnedValue+" is not a probability", returnedValue>0.0f&&returnedValue<1.0f);
	}
}
