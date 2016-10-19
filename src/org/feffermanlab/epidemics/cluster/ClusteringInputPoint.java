package org.feffermanlab.epidemics.cluster;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClusteringInputPoint {
	public static void main(String[] args) throws Exception {
		String inputDirectory=args[0];
		ClusteringDirectoryRunner directoryRunner = new ClusteringDirectoryRunner();
		String clusteringDirectory = "features", agreementMatrix="agreementMatrix.csv";
		boolean integrateClusterings=false, isIncidenceMatrix=false, compareClustering=false;
		int estimateBestK=0;
		
		//Check if the clustering is compared
		List<String> comparedFiles = Arrays.stream(args).filter((String elem) -> elem.matches("-c=[^\\s]+")).collect(Collectors.toList());
		String comparisonFilename = "";
		
		//If the clustering is compared, set the file for the ground-truth clustering
		if(!comparedFiles.isEmpty()){
			compareClustering=true;
			String comparedComposite = comparedFiles.get(0);
			comparisonFilename=comparedComposite.substring(3);
			System.out.println("Comparing the final clustering to the ground-truth clustering in "+comparisonFilename);
		}
		
		//Check if the final clusterings are integrated
		List<String> integrateFileFlags = Arrays.stream(args).filter((String elem) -> elem.matches("-r=[\\d]+")).collect(Collectors.toList());
		if(!integrateFileFlags.isEmpty()){
			//If so, get the number of expected final clusterings
			integrateClusterings=true;
			String comparedComposite = integrateFileFlags.get(0);
			try {
				estimateBestK=Integer.parseInt(comparedComposite.substring(3));
			} catch (NumberFormatException e) {
				System.err.println(comparedComposite.substring(3)+" is not an integer");
				return;
			}
			System.out.println("Final number of clusters set to "+estimateBestK);
			
		}
		isIncidenceMatrix = Arrays.stream(args).anyMatch((String elem) -> elem.matches("-i"));

		directoryRunner.setUpDirectoryClustering(clusteringDirectory, agreementMatrix);
		directoryRunner.setIntegrateClusteringsAndEstimateBestK(integrateClusterings, estimateBestK);
		Pair<Double, Integer> clusteringApplication =directoryRunner.applyClusteringOnDirectory(inputDirectory, compareClustering, comparisonFilename, isIncidenceMatrix);
		System.out.println("OutputLine: "+clusteringApplication.getFirst()+","+clusteringApplication.getSecond());
	}
	

}
