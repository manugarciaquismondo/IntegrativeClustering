package org.feffermanlab.epidemics.cluster;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

public class ClusterCombinatoricsLauncher {
	
	/**
	 * Run clustering on all combinations of features and write the clustering matrices
	 * @param inputDirectory the directory route where the data for all the features is
	 * @throws IOException If errors occurred while reading the original order of the clustered instances
	 */
	public void writeClusteringForFeatureCombinations(String inputDirectory){
		ClusterApplier clusterApplier = new ClusterApplier();
		ClusterIntersector clusterChecker = new ClusterIntersector();
		   ICombinatoricsVector<File> featurelVector = Factory.createVector(
				   clusterApplier.getListOfFiles(inputDirectory ,"arff") );
		   for (int featureIndex = 1; featureIndex <= featurelVector.getSize(); featureIndex++) {
			   Generator<File> gen = Factory.createSimpleCombinationGenerator(featurelVector,featureIndex);
			   for (ICombinatoricsVector<File> combination : gen) {
				     runFeatureClustering(inputDirectory, clusterApplier,
							clusterChecker, combination.getVector());
				   }
		}
				   // Create a simple combination generator to generate 3-combinations of the initial vector
				 
	}

	protected void runFeatureClustering(String inputDirectory,
			ClusterApplier clusterApplier, ClusterIntersector clusterChecker,
			List<File> list){
		clusterApplier.applyClusteringAndProcessExceptions(transformToFileArray(list), inputDirectory);
		 clusterChecker.intersectClusters(clusterApplier.getBestClusterings());
		 clusterChecker.writeIncidenceMatrix(inputDirectory+"/belongingmatrices");
	}
	
	protected void runFeatureClustering(String inputDirectory,
			ClusterApplier clusterApplier, ClusterIntersector clusterChecker){
		clusterApplier.applyClusteringAndProcessExceptions(inputDirectory);
		clusterChecker.intersectClusters(clusterApplier.getBestClusterings());

		clusterChecker.writeIncidenceMatrix(inputDirectory+"/belongingmatrices");
	}

	protected File[] transformToFileArray(
			List<File> list) {
		File[] fileArray = new File[list.size()];
		Iterator<File> fileIterator = list.iterator();
		for (int fileIndex = 0; fileIndex < fileArray.length; fileIndex++) {
			fileArray[fileIndex]=fileIterator.next();
			
		}
		return fileArray;
	}
	
	/**
	 * Run clustering for a single feature
	 * @param inputDirectoryRoute The directory where the feature data is
	 */
	public void runSubClusterings(String inputDirectoryRoute){
		ClusterApplier clusterApplier = new ClusterApplier();
		ClusterIntersector clusterChecker = new ClusterIntersector();
		File inputDirectory = new File(inputDirectoryRoute);
		Arrays.asList(inputDirectory.listFiles()).stream().filter(f -> f.isDirectory()).forEach(f->runFeatureClustering(f.getAbsolutePath(), clusterApplier, clusterChecker));

	}

}
