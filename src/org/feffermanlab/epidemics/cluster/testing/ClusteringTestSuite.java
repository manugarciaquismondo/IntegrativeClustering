package org.feffermanlab.epidemics.cluster.testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ClusterApplierTest.class, ClusterCheckerTester.class,
		ClusterCombinatoricsLauncherTester.class, ClusterObservationTest.class, TestClusteringIntegrationFromObservations.class })
public class ClusteringTestSuite {

}
