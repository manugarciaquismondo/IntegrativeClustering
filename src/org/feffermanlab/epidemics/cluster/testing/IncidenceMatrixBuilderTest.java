package org.feffermanlab.epidemics.cluster.testing;

import static org.junit.Assert.*;

import org.feffermanlab.epidemics.cluster.ClusterAgreementGenerator;
import org.junit.Test;

public class IncidenceMatrixBuilderTest {

	@Test
	public void buildIncidenceMatrix() {
		ClusterAgreementGenerator generator = new ClusterAgreementGenerator();
		int[][] incidenceMatrix=generator.buildIncidenceMatrixFromClustering("C:/Users/manu_/localdata/workspaces/eclipse/workspace/EpidemicModelingShared/clusteringTestData/tempClustering.csv");
		System.out.println("Clustering finished");
	}

}
