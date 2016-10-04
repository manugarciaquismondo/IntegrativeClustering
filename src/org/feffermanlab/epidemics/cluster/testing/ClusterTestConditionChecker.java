package org.feffermanlab.epidemics.cluster.testing;

import javax.annotation.Nonnull;

import org.feffermanlab.epidemics.cluster.ClusterApplier;

public class ClusterTestConditionChecker {
	@Nonnull
	private String sampleRoute="";
	
	public static final int ALL_NON_COINCIDENTS_ARE_0=0, NO_COINCIDENT_IS_0=1, ALL_PROBABILITIES_ARE_BETWEEN_0_AND_1=2;
	
	public String getSampleRoute() {
		return sampleRoute;
	}
	public void setSampleRoute(String sampleRoute) {
		this.sampleRoute = sampleRoute;
	}
	
	
	/**
	 * Iterate through a file with clustering instances and tests a condition
	 * @param testConditionInput A flag indicating the condition to check:
	 * ALL_NON_COINCIDENTS_ARE_0: The cell for all couple of instances which are not in the same cluster in any clustering is 0
	 * NO_COINCIDENT_IS_0: The cell for all couple of instances which are in the same cluster in at least one clustering is not 0
	 * ALL_PROBABILITIES_ARE_BETWEEN_0_AND_1: All probabilities are between 0 and 1
	 * @return true if the condition is satisfied for all pairs of instances, false otherwise
	 */
	public boolean iterateThroughInstancesAndTestCoincidents(int testConditionInput) {
		ClusterApplier clusterApplier= new ClusterApplier();
		clusterApplier.applyClusteringAndProcessExceptions(sampleRoute);
		boolean zeroConcidenceTest=true;
		for (int instanceIterator1 = 0; instanceIterator1 < clusterApplier.getNumberOfInstances(); instanceIterator1++) {
			for (int instanceIterator2 = 0; instanceIterator2 < clusterApplier.getNumberOfInstances(); instanceIterator2++) {
				zeroConcidenceTest=zeroConcidenceTest&&testCondition(testConditionInput, clusterApplier, instanceIterator1, instanceIterator2);
				
			}
			
		}
		return zeroConcidenceTest;
	}
	private boolean testCondition(int testAllNonCoincidentsAreZero, ClusterApplier clusterApplier, int instance1, int instance2){
		boolean testResult=false;
		switch(testAllNonCoincidentsAreZero){
			case ALL_NON_COINCIDENTS_ARE_0:
				testResult= !((!clusterApplier.containsAnyCommonFeature(instance1, instance2))&&(clusterApplier.getProbabilityOfAppearance(instance1, instance2)>0.0f));
			case NO_COINCIDENT_IS_0:
				testResult= !((clusterApplier.containsAnyCommonFeature(instance1, instance2))&&(clusterApplier.getProbabilityOfAppearance(instance1, instance2)<=0.0f));
			case ALL_PROBABILITIES_ARE_BETWEEN_0_AND_1:
				testResult=(clusterApplier.getProbabilityOfAppearance(instance1, instance2)>=0.0f&&clusterApplier.getProbabilityOfAppearance(instance1, instance2)<=1.0f);
		}
		return testResult;
	}
}
