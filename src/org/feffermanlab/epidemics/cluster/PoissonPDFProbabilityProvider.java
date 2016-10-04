package org.feffermanlab.epidemics.cluster;

import cern.jet.random.Poisson;
import cern.jet.random.engine.RandomEngine;

/**
 * A class to obtain the Probability Distribution Function for a data to occur in a distribution
 * @author manu_
 *
 */
public class PoissonPDFProbabilityProvider extends ProbabilityProvider {



	public PoissonPDFProbabilityProvider(RandomEngine engine) {
		super(engine);
		// TODO Auto-generated constructor stub
	}

	@Override
	double getFromDistribution(Double mean, int dataValue) {
		// TODO Auto-generated method stub
		double value= new Poisson(mean, engine).cdf(dataValue);
		return value;
	}


}
