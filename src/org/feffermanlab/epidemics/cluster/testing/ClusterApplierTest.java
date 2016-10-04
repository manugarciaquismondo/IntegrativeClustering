package org.feffermanlab.epidemics.cluster.testing;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ClusterApplierTest {

	
	private ClusterTestConditionChecker conditionChecker;
	
	@Before
	public void setUp() throws Exception {
		conditionChecker = new ClusterTestConditionChecker();
	}

	@Test
	public void testAllNonCoincidentsAreZero() {
		conditionChecker.setSampleRoute("./sampledata");
		assertTrue(iterateThroughInstancesAndTestCoincidents(ClusterTestConditionChecker.ALL_NON_COINCIDENTS_ARE_0));
	}
	@Test
	public void testAllCoincidentsAreNotZero() {
		conditionChecker.setSampleRoute("./sampledata");
		assertTrue(iterateThroughInstancesAndTestCoincidents(ClusterTestConditionChecker.NO_COINCIDENT_IS_0));
	}
	@Test
	public void testAllProbabilitiesBetween0And1() {
		conditionChecker.setSampleRoute("./sampledata");
		assertTrue(iterateThroughInstancesAndTestCoincidents(ClusterTestConditionChecker.ALL_PROBABILITIES_ARE_BETWEEN_0_AND_1));
	}
	
	

	
	@Test
	public void testAllNonCoincidentsAreZeroOnProduction() {
		conditionChecker.setSampleRoute("./bootstrapping-csvs/0001");
		assertTrue(iterateThroughInstancesAndTestCoincidents(ClusterTestConditionChecker.ALL_NON_COINCIDENTS_ARE_0));
	}
	@Test
	public void testAllCoincidentsAreNotZeroOnProduction() {
		conditionChecker.setSampleRoute("./bootstrapping-csvs/0001");
		assertTrue(iterateThroughInstancesAndTestCoincidents(ClusterTestConditionChecker.NO_COINCIDENT_IS_0));
	}
	@Test
	public void testAllProbabilitiesBetween0And1OnProduction() {
		conditionChecker.setSampleRoute("./bootstrapping-csvs/0001");
		assertTrue(iterateThroughInstancesAndTestCoincidents(ClusterTestConditionChecker.ALL_PROBABILITIES_ARE_BETWEEN_0_AND_1));
	}

	private boolean iterateThroughInstancesAndTestCoincidents(int allProbabilitiesAreBetween0And1) {
		// TODO Auto-generated method stub
		return conditionChecker.iterateThroughInstancesAndTestCoincidents(allProbabilitiesAreBetween0And1);
	}

}
