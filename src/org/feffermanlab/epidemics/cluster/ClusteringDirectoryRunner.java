package org.feffermanlab.epidemics.cluster;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.feffermanlab.epidemics.cluster.testing.MatrixAuxiliaryClass;

public class ClusteringDirectoryRunner {

	private ClusterIntersector clusterIntersector;
	private ClusterApplier clusterApplier;
	private ClusterAgreementGenerator clusterAgreementGenerator;
	private MatrixAuxiliaryClass matrixAverager;
	private MatrixCSVIO matrixIO;
	private ClusterIntegrator clusterIntegrator;
	private boolean integrateClusters;
	private int estimateBestK;
	private List<Integer> bestKValues;
	private String featureDirectoryName;
	
	/**
	 * Initialize variables to cluster the directory
	 * @param featureDirectoryName Directory for the feature clusterings
	 */
	public void setUpDirectoryClustering(String featureDirectoryName, String agreementFileRoute){
		clusterIntersector = new ClusterIntersector();
		clusterApplier= new ClusterApplier();
		clusterAgreementGenerator= new ClusterAgreementGenerator();
		clusterAgreementGenerator.setAgreementMatrixFilename(agreementFileRoute);
		matrixAverager= new MatrixAuxiliaryClass();
		matrixIO = new MatrixCSVIO();
		integrateClusters=false;
		bestKValues = new LinkedList<Integer>();
		this.featureDirectoryName=featureDirectoryName;
	}
	
	/**
	 * Apply the clustering pipeline in a directory
	 * @param baseDirectory The directory route where to apply clustering 
	 * @param comparisonFilename The filename of the clustering for comparison
	 * @param bootstrapIsIncidenceMatrix True if the bootstrap matrix is an incidence matrix
	 * @return A pair containing the degree of agreement and number of clusters in the clustering
	 * @throws Exception If errors occur during I/O or clustering
	 */
	public Pair<Double, Integer> applyClusteringOnDirectory(String baseDirectory, String comparisonFilename, boolean bootstrapIsIncidenceMatrix) throws Exception {
		String predirectory="";
		applyClusteringAndWriteIncidenceMatrices(predirectory+baseDirectory+"/"+featureDirectoryName);
		int numberOfClusters = checkIntegrationAndWriteClustering(baseDirectory, "clustering");			
		double agreement=getAgreementBetweenClusterings(predirectory+baseDirectory+"/"+featureDirectoryName, predirectory+baseDirectory+"/"+comparisonFilename, bootstrapIsIncidenceMatrix);
		System.out.println("The degree of agreement is "+agreement+" with "+numberOfClusters+" clusters");
		return new Pair<Double, Integer>(agreement, numberOfClusters);
	}
	
	private void applyClusteringAndWriteIncidenceMatrices(String geneDirectoryRoute) throws Exception {
		int numberOfIntegratedClusters=0;
//		final int PRESET_INTEGRATED_CLUSTERS=7;
		//Apply the clustering algorithm on each feature separately and intersect the resulting clusterings.
		clusterApplier.applyClusteringAndProcessExceptions(geneDirectoryRoute, "probabilitymatrix.csv");
		clusterIntersector.intersectClusters(clusterApplier.getBestClusterings());
		if(integrateClusters){
			// If estimate the best K is set to true, estimate the best K for clustering. Otherwise, use the preset value
			if(estimateBestK<=0){
				KEstimator estimator = new LocaleKEstimator();
				//KEstimator estimator = new JSATKEstimator();
				numberOfIntegratedClusters=estimator.estimateBestK(geneDirectoryRoute);
				bestKValues.add(numberOfIntegratedClusters);
			} else{
				numberOfIntegratedClusters=estimateBestK;
			}
			// Integrate clusters and build and write the incidence matrix
			this.clusterIntegrator=new ClusterIntegrator(clusterApplier.getInstancesMapping(), clusterIntersector.getClusters(), clusterApplier.getAllAttributeNames());
			this.clusterIntegrator.integrateClusters(numberOfIntegratedClusters);
			this.clusterIntegrator.buildIncidenceMatrix();
			this.clusterIntegrator.writeIncidenceMatrix(geneDirectoryRoute+"/belongingmatrices", "incidencematrix.csv");
		} else{
			//If clusters are not integrated, write incidence matrix directly
			clusterIntersector.writeIncidenceMatrix(geneDirectoryRoute+"/belongingmatrices", "incidencematrix.csv");
		}
		
		
	}
	
	/**
	 * Check if the integration option is enabled and write the clustering
	 * @param baseDirectory The directory of the output file where to write the clustering
	 * @param clusteringFileName The name of the output file where to write clustering
	 * @return The number of clusters
	 * @throws IOException If errors occur during I/O
	 */
	public int checkIntegrationAndWriteClustering(String baseDirectory, String clusteringFileName) throws IOException {
		String clusteringFileRoute=baseDirectory+"/"+featureDirectoryName+"/agreementmatrices/"+clusteringFileName+".csv";
		ClusteringFramework clusteringFramework=clusterIntersector;
		if(integrateClusters){
			clusteringFramework=clusterIntegrator;
		}
		clusteringFramework.writeClustering(clusteringFileRoute);
		return clusteringFramework.getNumberOfClusters();
	}

	
	/**
	 * Calculate the degree of agreement between clusterings
	 * @param geneDirectoryRoute Route of the generated clustering
	 * @param comparisonFileRoute Route of the baseline clustering
	 * @param bootstrapIsIncidence True if the bootstrap clustering is an incidence matrix
	 * @return The percentage of agreement between clusterings
	 * @throws Exception If errors occur during I/O or clustering
	 */
	public double getAgreementBetweenClusterings(String geneDirectoryRoute, String comparisonFileRoute, boolean bootstrapIsIncidence) throws Exception {
		String agreementFilename = "agreementMatrix.csv";
		applyClusteringAndWriteIncidenceMatrices(geneDirectoryRoute);
		clusterAgreementGenerator.generateAgreementMatrix(geneDirectoryRoute, comparisonFileRoute, "incidencematrix.csv", agreementFilename, bootstrapIsIncidence);
		double binarizedMatrix[][] = matrixAverager.binarizeMatrixAndCalculateFailureRates(matrixIO.readDoubleMatrixFromCSV(geneDirectoryRoute+"/agreementmatrices/"+agreementFilename));
		double agreementBetweenClusterings=matrixAverager.averageMatrix(binarizedMatrix);
		return agreementBetweenClusterings;
	}
	
	/**
	 * Integrate feature-specific clusterings and write the incidence matrix
	 * @param directoryRoute Directory where the feature-specific clusterings is
	 */
	public void integrateClustersAndWriteIncidenceData(String directoryRoute){
		clusterApplier.applyClusteringAndProcessExceptions("./"+directoryRoute);
		clusterIntersector.intersectClusters(clusterApplier.getBestClusterings());
		clusterIntersector.writeIncidenceMatrix("./"+directoryRoute+"/belongingmatrices");
	}
	
	/**
	 * Get the clusters
	 * @return A list containing the clusters
	 */
	public List<Cluster<Integer>> getClusters(){
		return clusterIntersector.getClusters();
	}
	
	/**
	 * Intersect clusters from the given argument
	 * @param clusterings Clustering to intersect
	 */
	public void intersectClusters(Map<String, ClusteringInfo> clusterings){
		clusterIntersector.intersectClusters(clusterings);
	}
	
	/**
	 * Write the clustering in an output file
	 * @param clusterings File where to write the clustering
	 */
	public void writeClusters(String outputRoute) throws IOException {
		matrixIO.writeClusters(getClusters(), outputRoute);
		
	}
	
	/**
	 * Get the false metrics from the clustering
	 * @return A pair containing the proportion of elements falsely classified in the same and different clusters, respectively
	 */
	public Pair<Double,Double> getFalseSameClusterMetrics() {
		return new Pair<Double,Double>(matrixAverager.getFalseSameCluster(),  matrixAverager.getFalseDifferentCluster());
	}

	/**
	 * Extract assignments from one single feature and assignments from algorihtm application
	 * @param kMeansRoute The route where the file to extract the assignment is
	 * @param kMeansFile The file where the file to extract the assignment is
	 * @return A bidimensional matrix. The first row is the assignments from the algorithm and the second row is the assignments from a single feature
	 * @throws Exception If errors occur during I/O or clustering
	 */
	public int[][] extractAssignments(String kMeansRoute, String kMeansFile) throws Exception {
		// Apply K-Means and intersect clusters
		clusterApplier.applyKmeans(3, kMeansRoute+"/"+featureDirectoryName+"/"+kMeansFile);
		int oneFeatureDirectAssignments[]=clusterApplier.getTemporalAssignments();
		clusterApplier.applyClusteringAndProcessExceptions(kMeansRoute+"/"+featureDirectoryName, "probabilitymatrix.csv");
		clusterIntersector.intersectClusters(clusterApplier.getBestClusterings());
		
		//Extract assignments
		int algorithmAssignments[]=clusterIntersector.getClusteringsAsArray();
		int assignments[][]=new int[2][algorithmAssignments.length];
		assignments[0]=algorithmAssignments;
		assignments[1]=oneFeatureDirectAssignments;
		return assignments;
	}
	
	/**
	 * Write matrix of double numbers on CSV format
	 * @param outputRoute route where to write the matrix
	 * @param matrix matrix to write
	 */
	public void writeDoubleMatrix(String outputRoute, double[][] matrix) {
		matrixIO.writeDoubleMatrix(outputRoute, matrix);
	}

	/**
	 * Set clustering integration and estimation of best K value
	 * @param integrateClusterings True if clusterings are integrated across features
	 * @param estimateBestK K value to set. If lower or equal to 0, then it is estimated
	 */
	public void setIntegrateClusteringsAndEstimateBestK(boolean integrateClusterings, int estimateBestK){
		this.integrateClusters=integrateClusterings;
		this.estimateBestK=estimateBestK;
	}


}
