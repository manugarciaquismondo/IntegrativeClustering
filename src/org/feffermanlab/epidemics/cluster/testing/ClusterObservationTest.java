package org.feffermanlab.epidemics.cluster.testing;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ClusterObservationTest{

	private ClusterTestConditionChecker conditionChecker;
	
	
	@Before
	public void setUp() throws Exception {
		conditionChecker = new ClusterTestConditionChecker();
	}
	
	@Test
	public void testAllNonCoincidentsAreZero() {
		conditionChecker.setSampleRoute("./observations");
		assertTrue(iterateThroughInstancesAndTestCoincidents(ClusterTestConditionChecker.ALL_NON_COINCIDENTS_ARE_0));
	}

	private boolean iterateThroughInstancesAndTestCoincidents(int allNonCoincidentsAre0) {
		// TODO Auto-generated method stub
		return conditionChecker.iterateThroughInstancesAndTestCoincidents(allNonCoincidentsAre0);
	}

}
