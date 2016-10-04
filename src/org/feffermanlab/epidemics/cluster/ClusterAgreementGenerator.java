package org.feffermanlab.epidemics.cluster;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A class to calculate the level of agreement between the bootstrap and the intersected clusterings
 * @author manu_
 *
 */
/**
 * @author manu_
 *
 */
public class ClusterAgreementGenerator {
	
	private static final String AGREEMENT_MATRIX_FILENAME = "Environments-Epidemics-Ecology-Gene.csv";
	public static final int SAME_CLUSTER_FOR_BOTH_CLUSTERINGS=2,
			DIFFERENT_CLUSTER_FOR_BOTH_CLUSTERINGS=1,
			SAME_CLUSTER_FOR_BOOTSTRAP=0,
			DIFFERENT_CLUSTER_FOR_BOOTSTRAP=-1;
	private MatrixCSVIO matrixCSVIO;
	final private String AGREEMENT_MATRIX_DIRECTORY="/agreementmatrices/";
	private double[][] averageAgreementMatrix;
	public ClusterAgreementGenerator() {
		super();
		matrixCSVIO = new MatrixCSVIO();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Create files encoding the agreement matrices between the matrices from the bootstrap clustering and the clustering to test 
	 * @param replicatesRoute Route of the directory where the matrices of the bootstrap clustering are
	 * @param bootstrapSameClusterFile Route where matrix of the tested clustering is
	 */
	public void generateAgreementMatrices(String replicatesRoute, String bootstrapSameClusterFile){
		int[][] bootstrapSameClusterMatrix=readMatrixFromCSV(bootstrapSameClusterFile);
		List<File> subdirectories = collectSubdirectories(replicatesRoute);
		for(File subdirectory: subdirectories){
			
			String subdirectoryPath=subdirectory.getAbsolutePath();

			int[][] sameClusterMatrix = createAgreementMatrix(
					bootstrapSameClusterMatrix, subdirectoryPath, AGREEMENT_MATRIX_FILENAME);
			checkAgreementDirectoryAndWriteAgreementMatrix(subdirectoryPath,
					sameClusterMatrix, AGREEMENT_MATRIX_FILENAME);

		}
	}
	
	/**
	 * Create file encoding the agreement matrices between the matrices from the bootstrap clustering and the clustering to test 
	 * @param belongingMatrixDirectory Directory where the belonging matrix is
	 * @param bootstrapSameClusterFile Route to the bootstrap clustering file
	 * @param belongingMatrixFilename File name of the belonging matrix for the tested clustering
	 */
	public void generateAgreementMatrix(String belongingMatrixDirectory, String bootstrapSameClusterFile, String belongingMatrixFilename){
		generateAgreementMatrix(belongingMatrixDirectory, bootstrapSameClusterFile, belongingMatrixFilename, AGREEMENT_MATRIX_FILENAME);
	}
	
	/**
	 * Create file encoding the agreement matrices between the matrices from the bootstrap clustering and the clustering to test 
	 * @param belongingMatrixDirectory Directory where the belonging matrix is
	 * @param bootstrapSameClusterFile Route to the bootstrap clustering file
	 * @param belongingMatrixFilename File name of the belonging matrix for the tested clustering
	 */
	public void generateAgreementMatrix(String belongingMatrixDirectory, String bootstrapSameClusterFile, String belongingMatrixFilename, String agreementMatrixInputFilename){
		int[][] bootstrapSameClusterMatrix=readMatrixFromCSV(bootstrapSameClusterFile);
		int[][] sameClusterMatrix = createAgreementMatrix(
				bootstrapSameClusterMatrix, belongingMatrixDirectory, belongingMatrixFilename);
		checkAgreementDirectoryAndWriteAgreementMatrix(belongingMatrixDirectory,
				sameClusterMatrix, agreementMatrixInputFilename);
	}

	protected List<File> collectSubdirectories(String replicatesRoute) {
		return Arrays.asList(new File(replicatesRoute).listFiles()).stream().filter(f->f.isDirectory()).collect(Collectors.toList());
	}

	protected void checkAgreementDirectoryAndWriteAgreementMatrix(
			String subdirectoryPath, int[][] sameClusterMatrix, String agreementMatrixInputFilename) {
		File agreementDir=new File(subdirectoryPath+AGREEMENT_MATRIX_DIRECTORY);
		if(!agreementDir.exists())
			agreementDir.mkdir();
		writeMatrixToCSV(sameClusterMatrix, agreementDir.getAbsolutePath()+"/"+agreementMatrixInputFilename);
	}

	/**
	 * Create a matrix in which each position is equal to 1 if the bootstrap matrix and the intersected matrix coincide, and 0 otherwise
	 * @param bootstrapSameClusterMatrix the intersected matrix 
	 * @param subdirectoryPath the directory where the bootstrap matrix is
	 * @return the agreement matrix
	 */
	protected int[][] createAgreementMatrix(int[][] bootstrapSameClusterMatrix,
			String subdirectoryPath, String belongingMatrixFilename) {
		int[][] replicateBelongingMatrix = readMatrixFromCSV(subdirectoryPath+"/belongingmatrices/"+belongingMatrixFilename);
		int[][] sameClusterMatrix= new int[replicateBelongingMatrix.length][replicateBelongingMatrix.length];
		for (int i = 0; i < sameClusterMatrix.length-1; i++) {
			sameClusterMatrix[i][i]=1;
			for (int j = i+1; j < sameClusterMatrix.length; j++) {
				double bootstrapValue=bootstrapSameClusterMatrix[i][j], replicateValue=replicateBelongingMatrix[i][j];
				if(bootstrapValue==1&&replicateValue==0)
					sameClusterMatrix[i][j]=sameClusterMatrix[j][i]=SAME_CLUSTER_FOR_BOOTSTRAP;
				else if(bootstrapValue==0&&replicateValue==1)
					sameClusterMatrix[i][j]=sameClusterMatrix[j][i]=DIFFERENT_CLUSTER_FOR_BOOTSTRAP;
				else if(bootstrapValue==1&&replicateValue==1)
					sameClusterMatrix[i][j]=sameClusterMatrix[j][i]=SAME_CLUSTER_FOR_BOTH_CLUSTERINGS;
				else if(bootstrapValue==0&&replicateValue==0)
					sameClusterMatrix[i][j]=sameClusterMatrix[j][i]=DIFFERENT_CLUSTER_FOR_BOTH_CLUSTERINGS;
				else throw new IllegalArgumentException("The cell {"+i+", "+j+"} contain the illegal states "+bootstrapSameClusterMatrix[i][j]+" for bootstrap and "+replicateBelongingMatrix[i][j]+" for the replicate.");
			}
		}
		sameClusterMatrix[sameClusterMatrix.length-1][sameClusterMatrix.length-1]=bootstrapSameClusterMatrix[sameClusterMatrix.length-1][sameClusterMatrix.length-1]==replicateBelongingMatrix[sameClusterMatrix.length-1][sameClusterMatrix.length-1]?1:0;
		return sameClusterMatrix;
	}

	private void writeMatrixToCSV(int[][] sameClusterMatrix, String string) {
		// TODO Auto-generated method stub
		matrixCSVIO.writeIntegerMatrix(string, sameClusterMatrix);
	}

	private int[][] readMatrixFromCSV(String inputFileRoute) {
		// TODO Auto-generated method stub
		try {
			return matrixCSVIO.readIntegerMatrixFromCSV(inputFileRoute);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Unable to read "+inputFileRoute);
		}
		return null;
	}

	/**
	 * Generate the average agreement based on a set of bootstrap replicates and write the agreement matrices
	 * @param replicatesRoute Route where the bootstrap replicates are
	 * @param bootstrapSameClusterFile Route where the clustering to test is
	 * @param averageMatrixRoute Route where to write the average agreement matrix
	 */
	public void generateAverageAndWriteAgreementMatrices(String replicatesRoute, String bootstrapSameClusterFile, String averageMatrixRoute){
		this.generateAgreementMatrices(replicatesRoute, bootstrapSameClusterFile);
		writeAverageMatrix(averageAgreementMatrices(replicatesRoute), averageMatrixRoute);
	}
	

	private void writeAverageMatrix(double[][] averageAgreementMatrices,
			String averageMatrixRoute) {
		matrixCSVIO.writeDoubleMatrix(averageMatrixRoute, averageAgreementMatrices);
		
	}

	
	/**
	 * Calculate the normalized agreement matrix for a tested clustering and a set of bootstrap clusterings
	 * @param replicatesRoute Route where the bootstrap clustering matrices are
	 * @return the normalized agreement matrix
	 */
	public double[][] averageAgreementMatrices(String replicatesRoute){
		double[][] averageAgreementMatrix= null;
		List<File> subdirectories = collectSubdirectories(replicatesRoute);
		for(File subdirectory: subdirectories){
			averageAgreementMatrix = addMatrixDirectoryToAgreementMatrix(subdirectory);
			
		}
		normalizeAverageAgreementMatrix(subdirectories);
		return averageAgreementMatrix;
	}

	protected double[][] addMatrixDirectoryToAgreementMatrix(File subdirectory) {
		int[][] agreementMatrix= readMatrixFromCSV(subdirectory.getAbsolutePath()+AGREEMENT_MATRIX_DIRECTORY+AGREEMENT_MATRIX_FILENAME);
		if(averageAgreementMatrix==null){
			averageAgreementMatrix= new double[agreementMatrix.length][agreementMatrix.length];
			updateAgreementValue(agreementMatrix, false);
			
		} else{
			updateAgreementValue(agreementMatrix, true);
		}
		return averageAgreementMatrix;
	}

	protected void normalizeAverageAgreementMatrix(List<File> subdirectories) {
		for (int i = 0; i < averageAgreementMatrix.length; i++) {
			for (int j = 0; j < averageAgreementMatrix.length; j++) {
				averageAgreementMatrix[i][j]/=subdirectories.size();
			}
		}
	}
	
	private void updateAgreementValue(int[][] agreementMatrix, boolean increment){
		for (int i = 0; i < agreementMatrix.length; i++) {
			for (int j = 0; j < agreementMatrix.length; j++) {
				if(increment)
					averageAgreementMatrix[i][j]+=agreementMatrix[i][j]>0?1:0;
				else{
					averageAgreementMatrix[i][j]=agreementMatrix[i][j]>0?1:0;
				}
			}
		}
	}
	

}
