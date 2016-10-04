package org.feffermanlab.epidemics.cluster.testing;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.feffermanlab.epidemics.cluster.Cluster;
import org.feffermanlab.epidemics.cluster.ClusterApplier;
import org.feffermanlab.epidemics.cluster.ClusterIntersector;
import org.feffermanlab.epidemics.cluster.ClusterIntegrator;
import org.feffermanlab.epidemics.cluster.ClusteringInfo;
import org.junit.Before;
import org.junit.Test;

public class ClusterCheckerTester {

	ClusterIntersector clusterChecker;
	ClusterApplier clusterApplier;
	@Nonnull
	TestDirectoryProvider testDirectoryProvider= new TestDirectoryProvider();
	@Before
	public void setUp() throws Exception {
		clusterChecker = new ClusterIntersector();
		clusterApplier= new ClusterApplier();
		testDirectoryProvider = new TestDirectoryProvider();
	}


	public void setTestDirectoryProvider(TestDirectoryProvider testDirectoryProvider) {
		if (testDirectoryProvider == null) {
			throw new NullPointerException("The argument testDirectoryProvider of type TestDirectoryProvider cannot be null");
		}
		this.testDirectoryProvider = testDirectoryProvider;
	}


	@Test
	public void testIntegrateClusters() {

		clusterApplier.applyClusteringAndProcessExceptions(getTestDirectory());
		clusterChecker.intersectClusters(clusterApplier.getBestClusterings());
	}
	
	private String getTestDirectory() {
		// TODO Auto-generated method stub
		return testDirectoryProvider.getTestDirectory();
	}


	@Test
	public void clustersDoNotOverlap(){
		
		clusterApplier.applyClusteringAndProcessExceptions(getTestDirectory());
		clusterChecker.intersectClusters(clusterApplier.getBestClusterings());
		Set<Integer> originalInstancesSet=new HashSet<Integer>();
		for(Cluster<Integer> cluster:clusterChecker.getClusters()){
			Set<Integer> intersectedInstances=cluster.getInstances();
			intersectedInstances.retainAll(originalInstancesSet);
			assertEquals(0, intersectedInstances.size());
			intersectedInstances.addAll(cluster.getInstances());
		}
		
	}
	
	@Test
	public void unionOfClustersContainExactlyDataInstances(){
		
		clusterApplier.applyClusteringAndProcessExceptions(getTestDirectory());
		clusterChecker.intersectClusters(clusterApplier.getBestClusterings());
		Set<Integer> targetInstancesSet=clusterApplier.getInstances(), originalInstancesSet=new HashSet<Integer>();
		for(Cluster<Integer> cluster:clusterChecker.getClusters()){
			originalInstancesSet.addAll(cluster.getInstances());
		}
		assertEquals(targetInstancesSet, originalInstancesSet);
		
	}
	
	@Test
	public void allFeaturesHaveBeenClustered(){
		clusterApplier.applyClusteringAndProcessExceptions(getTestDirectory());
		clusterChecker.intersectClusters(clusterApplier.getBestClusterings());
		int numberOfInstances=clusterApplier.getNumberOfFeatures();
		for(Cluster<Integer> cluster:clusterChecker.getClusters()){
			assertEquals(numberOfInstances,cluster.getFeatureName().split("\\+").length);
		}
		
	}
	
	@Test
	public void testWriteIncidenceMatrix(){
		clusterApplier.applyClusteringAndProcessExceptions(getTestDirectory());
		clusterChecker.intersectClusters(clusterApplier.getBestClusterings());
		clusterChecker.writeIncidenceMatrix(String.join("/",getTestDirectory(),"belongingmatrices"));
	}
	


}
