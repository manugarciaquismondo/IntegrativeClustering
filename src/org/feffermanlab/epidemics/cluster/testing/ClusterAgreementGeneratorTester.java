package org.feffermanlab.epidemics.cluster.testing;

import static org.junit.Assert.*;

import java.io.File;

import org.feffermanlab.epidemics.cluster.ClusterAgreementGenerator;
import org.junit.Before;
import org.junit.Test;

public class ClusterAgreementGeneratorTester {

	
	ClusterAgreementGenerator clusterAgreementGenerator;
	TestDirectoryProvider directoryProvider;
	MatrixAuxiliaryClass matrixAuxiliaryClass;
	private String bootstrapClusteringFile;
	@Before
	public void setUp() throws Exception {
		clusterAgreementGenerator= new ClusterAgreementGenerator();
		clusterAgreementGenerator.setAgreementMatrixFilename("Environments-Epidemics-Ecology-Gene.csv");
		directoryProvider = new TestDirectoryProvider();
		bootstrapClusteringFile=directoryProvider.getExamplesDirectory()+"/SameClusterBootstrap.csv";
		matrixAuxiliaryClass = new MatrixAuxiliaryClass();
	}

	@Test
	public void testGenerateAgreementMatrices() {
		clusterAgreementGenerator.generateAgreementMatrices(directoryProvider.getProductionDirectory(), bootstrapClusteringFile);
	}
	
	@Test
	public void testAverageAgreement(){
		double[][] averageMatrix=clusterAgreementGenerator.averageAgreementMatrices(directoryProvider.getProductionDirectory());
		double matrixAverageValue = averageMatrix(averageMatrix);
		assertTrue(matrixAverageValue>=0.5f&&matrixAverageValue<=1.0f);
	}


	
	private double averageMatrix(double[][] averageMatrix) {
		// TODO Auto-generated method stub
		return matrixAuxiliaryClass.averageMatrix(averageMatrix);
	}

	@Test
	public void testGenerateAverageAndWriteAgreementMatrices() {
		clusterAgreementGenerator.generateAverageAndWriteAgreementMatrices(directoryProvider.getProductionDirectory(), bootstrapClusteringFile, directoryProvider.getResultsDirectory()+"/clusteringAverageMatrix.csv");
		File testedFile=new File(directoryProvider.getResultsDirectory()+"/clusteringAverageMatrix.csv");
		assertTrue(testedFile.exists()&&testedFile.isFile());
	}

}
