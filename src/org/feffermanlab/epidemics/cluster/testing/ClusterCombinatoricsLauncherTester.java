package org.feffermanlab.epidemics.cluster.testing;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.feffermanlab.epidemics.cluster.ClusterApplier;
import org.feffermanlab.epidemics.cluster.ClusterCombinatoricsLauncher;
import org.feffermanlab.epidemics.cluster.MatrixCSVIO;
import org.feffermanlab.epidemics.symmetrychecker.SymmetryMatrix;
import org.junit.Before;
import org.junit.Test;

public class ClusterCombinatoricsLauncherTester {

	private TestDirectoryProvider testDirectoryProvider;
	protected String baseDirectory;
	private ClusterCombinatoricsLauncher clusterCombinatoricsLauncher;

	@Before
	public void setUp() throws Exception {
		testDirectoryProvider = new TestDirectoryProvider();
		clusterCombinatoricsLauncher = new ClusterCombinatoricsLauncher();
	}

	@Test
	public void testWriteClusteringForFeatureCombinations() {
		clusterCombinatoricsLauncher.writeClusteringForFeatureCombinations(getTestDirectory());
	}

	protected String getTestDirectory() {
		return testDirectoryProvider.getTestDirectory();
	}
	
	@Test
	public void testIfMatricesAreSymmetric() throws IOException {
		clusterCombinatoricsLauncher.writeClusteringForFeatureCombinations(getTestDirectory());
		baseDirectory=getTestDirectory();
		testMatrixSymmetry("belongingmatrices");
		testMatrixSymmetry("probabilitymatrices");
		
	}

	protected void testMatrixSymmetry(String fileDirectoryRoute) throws IOException {
		File[] belongingMatrixFiles = getListOfFiles(fileDirectoryRoute, "csv");
		for (int fileIndex = 0; fileIndex < belongingMatrixFiles.length; fileIndex++) {
			float[][] belongingMatrix= readMatrixFromCSV(belongingMatrixFiles[fileIndex].getAbsolutePath());
			assertTrue(SymmetryMatrix.isSymmetric(belongingMatrix));
		}
	}

	protected File[] getListOfFiles(String fileDirectoryRoute, String extension) {
		ClusterApplier clusterApplier = new ClusterApplier();
		File[] belongingMatrixFiles = clusterApplier.getListOfFiles(String.join("/", baseDirectory, fileDirectoryRoute), "csv");
		return belongingMatrixFiles;
	}


	
	
	@Test
	public void testIfIntersectionWorks() throws IOException {
		baseDirectory=getTestDirectory();
		clusterCombinatoricsLauncher.writeClusteringForFeatureCombinations(getTestDirectory());
		List<File> fileList=new LinkedList<File>(Arrays.asList(getListOfFiles("belongingmatrices", "csv")));
		float[][] geneMatrix= readMatrixFromCSV(getTestDirectory()+"/belongingmatrices/Gene.csv");
		Set<String> testedFeatures = new HashSet<String>();
		testedFeatures.add("Gene");
		while(testedFeatures.size()<4){
			fileList=extractFilesWhichContainAllTestedFeatures(fileList, testedFeatures);
			File selectedFile=extractFilesWhichContainOneNewFeature(fileList, testedFeatures.size()).get(0);
			String testedFileRoute = selectedFile.getName();
			testedFileRoute=testedFileRoute.substring(0, testedFileRoute.length()-4);
			testedFeatures.addAll(Arrays.asList(testedFileRoute.split("-")));
			assertTrue(isMatrixSubset(readMatrixFromCSV(selectedFile.getAbsolutePath()), geneMatrix));
		}
		System.out.println("Test finished");
	}

	private float[][] readMatrixFromCSV(String string) {
		// TODO Auto-generated method stub
		MatrixCSVIO matrixCSVIO= new MatrixCSVIO();
		try {
			return matrixCSVIO.readFloatMatrixFromCSV(string);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Unable to read matrix from file "+string);
		}
		return null;
	}

	private List<File> extractFilesWhichContainOneNewFeature(
			List<File> fileList, int numberOfTestedFeatures) {
		// TODO Auto-generated method stub
		return fileList.stream()
				.filter(f-> f.getName().substring(0, f.getName().length()-4).split("-").length==numberOfTestedFeatures+1)
				.collect(Collectors.toList());
	}

	private boolean isMatrixSubset(float[][] readMatrixFromCSV,
			float[][] geneMatrix) {
		// TODO Auto-generated method stub
		for (int i = 0; i < geneMatrix.length; i++) {
			for (int j = 0; j < geneMatrix.length; j++) {
				if(geneMatrix[i][j]==0&&readMatrixFromCSV[i][j]!=0)
					return false;
			}
		}
		return true;
	}

	private List<File> extractFilesWhichContainAllTestedFeatures(List<File> fileList,
			Set<String> testedFeatures) {
		// TODO Auto-generated method stub
		return fileList.stream()
				.filter(f -> Arrays.asList(f.getName().substring(0, f.getName().length()-4).split("-")).containsAll(testedFeatures))
				.filter(f -> f.getName().substring(0, f.getName().length()-4).split("-").length>testedFeatures.size())
				.collect(Collectors.toList());
	}
	
	@Test
	public void testRunSubClusterings(){
		clusterCombinatoricsLauncher.runSubClusterings(getProductionDirectory());
	}
	
	@Test
	public void testSymmetryOnProductionSubClusterings() throws IOException{
		for(File file: Arrays.asList(new File(getProductionDirectory()).listFiles()).stream().filter(f->f.isDirectory()).collect(Collectors.toList())){
			baseDirectory=file.getAbsolutePath();
			testMatrixSymmetry("belongingmatrices");
			testMatrixSymmetry("probabilitymatrices");
		}
	}

	protected String getProductionDirectory() {
		return this.testDirectoryProvider.getProductionDirectory();
	}

}
