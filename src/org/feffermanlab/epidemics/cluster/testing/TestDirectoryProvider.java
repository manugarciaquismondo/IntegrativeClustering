package org.feffermanlab.epidemics.cluster.testing;

public class TestDirectoryProvider {
	
	public String getTestDirectory(){
		return "./sampledata";
	}
	
	public String getProductionDirectory(){
		return "./bootstrapping-csvs";
	}
	
	public String getResultsDirectory(){
		return "./results";
	}
	
	public String getExamplesDirectory(){
		return "./examples";
	}
	
	public String getReplicatesDirectory(){
		return "./bootstrapping-prob/replicates";
	}
}
