package org.feffermanlab.epidemics.cluster.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;

import org.feffermanlab.epidemics.cluster.Cluster;
import org.feffermanlab.epidemics.cluster.ClusterApplier;
import org.feffermanlab.epidemics.cluster.ClusterIntersector;
import org.feffermanlab.epidemics.cluster.ClusterIntegrator;
import org.feffermanlab.epidemics.cluster.IncidenceMatrixBuilder;
import org.feffermanlab.epidemics.cluster.MatrixCSVIO;
import org.feffermanlab.epidemics.cluster.Pair;
import org.junit.Before;
import org.junit.Test;

public class ClusterIntegratorTester{
	public ClusterIntersector clusterChecker;
	public ClusterApplier clusterApplier;
	private MatrixCSVIO matrixReader;
	@Nonnull
	TestDirectoryProvider testDirectoryProvider= new TestDirectoryProvider();

	
	@Before
	public void setUp() throws Exception {
		clusterChecker = new ClusterIntersector();
		clusterApplier= new ClusterApplier();
		testDirectoryProvider = new TestDirectoryProvider();
	}
	
	@Test
	public void testIntegrativeClustering() throws Exception{
		int numberOfClusters=2;
		clusterApplier.applyClusteringAndProcessExceptions(getTestDirectory());
		clusterChecker.intersectClusters(clusterApplier.getBestClusterings());
		ClusterIntegrator clusterIntegrator=new ClusterIntegrator(clusterApplier.getInstancesMapping(), clusterChecker.getClusters(), clusterApplier.getAllAttributeNames());
		clusterIntegrator.integrateClusters(numberOfClusters);
		List<Cluster<Integer>> integratedPartition=clusterIntegrator.getClusters();
		assertEquals(integratedPartition.size(),numberOfClusters);
		assertTrue("Clusters in the intersected partition must be in the same integrated cluster",checkAllIntegratedItemsInSameIntegratedCluster(integratedPartition));
		System.out.println("Integrative clustering finished");
	}


	private boolean checkAllIntegratedItemsInSameIntegratedCluster(List<Cluster<Integer>> integratedPartition) {
		boolean allInSameCluster=true;
		for(Cluster<Integer> cluster: clusterChecker.getClusters()){
			Cluster<Integer> integratedCluster=findClusterContainingInstance(cluster.getInstances().iterator().next(), integratedPartition);
			allInSameCluster=allInSameCluster&&cluster.getInstances().stream().allMatch(x->integratedCluster.getInstances().contains(x));
		}
		return allInSameCluster;
	}


	private Cluster<Integer> findClusterContainingInstance(Integer next, List<Cluster<Integer>> integratedPartition) {
		// TODO Auto-generated method stub
		return integratedPartition.stream().filter((Cluster<Integer> cluster)->cluster.getInstances().contains(next)).findAny().get();
	}
	
	private String getTestDirectory() {
		// TODO Auto-generated method stub
		return testDirectoryProvider.getTestDirectory();
	}
}
