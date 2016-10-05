package org.feffermanlab.epidemics.cluster;


public class ClusteringInputPoint {
	public static void main(String[] args) throws Exception {
		String inputDirectory=args[0];
		ClusteringDirectoryRunner directoryRunner = new ClusteringDirectoryRunner();
		String clusteringDirectory = "features", agreementMatrix="agreementMatrix.csv";
		boolean integrateClusterings=false, isIncidenceMatrix=false;
		int estimateBestK=0;
		String comparisonRoute = args[1];
		if(args.length>2){
			integrateClusterings = Boolean.parseBoolean(args[2]);
			if(args.length>3){
				estimateBestK = Integer.max(Integer.parseInt(args[3]),0);
				if(args.length>4){
					isIncidenceMatrix = Boolean.parseBoolean(args[4]);
				}
			}
		}
		directoryRunner.setUpDirectoryClustering(clusteringDirectory, agreementMatrix);
		directoryRunner.setIntegrateClusteringsAndEstimateBestK(integrateClusterings, estimateBestK);
		Pair<Double, Integer> clusteringApplication =directoryRunner.applyClusteringOnDirectory(inputDirectory, comparisonRoute, isIncidenceMatrix);
		System.out.println("OutputLine: "+clusteringApplication.getFirst()+","+clusteringApplication.getSecond());
	}
	

}
