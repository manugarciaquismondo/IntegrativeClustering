package org.feffermanlab.epidemics.cluster;


public class ClusteringInputPoint {
	public static void main(String[] args) throws Exception {
		String inputDirectory=args[0];
		ClusteringDirectoryRunner directoryRunner = new ClusteringDirectoryRunner();
		String clusteringDirectory = "features";
		boolean integrateClusterings=false;
		int estimateBestK=0;
		if(args.length>1){
			integrateClusterings = Boolean.parseBoolean(args[1]);
			if(args.length>2){
				estimateBestK = Integer.max(Integer.parseInt(args[2]),0);
			}
		}
		directoryRunner.setUpDirectoryClustering(clusteringDirectory);
		directoryRunner.setIntegrateClusteringsAndEstimateBestK(integrateClusterings, estimateBestK);
		Pair<Double, Integer> clusteringApplication =directoryRunner.applyClusteringOnDirectory(inputDirectory);
		System.out.println("OutputLine: "+clusteringApplication.getFirst()+","+clusteringApplication.getSecond());
	}
	

}
