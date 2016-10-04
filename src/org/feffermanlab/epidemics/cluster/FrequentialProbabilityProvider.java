package org.feffermanlab.epidemics.cluster;

import cern.jet.random.engine.RandomEngine;

/**
 * A calculator of the probability of an event to occur based on a frequential approach of the Poisson distribution
 * @author manu_
 *
 */
public class FrequentialProbabilityProvider extends PoissonCDFProbabilityProvider {


	public FrequentialProbabilityProvider(RandomEngine engine) {
		super(engine);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double getFromDistribution(Double first, int dataValue) {
		// TODO Auto-generated method stub
		int datasetSize=dataset.size();
		long numberOfInterestingValues = dataset.stream().filter(v->(v>=dataValue-interval)&&(v<=dataValue+interval)).count();
		return(((double)numberOfInterestingValues)/((double)datasetSize));
	}

}
