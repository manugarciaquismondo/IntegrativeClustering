package org.feffermanlab.epidemics.cluster;

import cern.jet.random.Poisson;
import cern.jet.random.engine.RandomEngine;

/**
 * A class to obtain the Cumulative Distribution Function for a data to occur in a distribution
 * @author manu_
 *
 */
public class PoissonCDFProbabilityProvider extends ProbabilityProvider {

	
	protected int interval;
	
	public PoissonCDFProbabilityProvider(RandomEngine engine) {
		super(engine);
		// TODO Auto-generated constructor stub
	}

	@Override
	double getFromDistribution(Double first, int dataValue) {
		// TODO Auto-generated method stub
		Poisson poisson=new Poisson(first, engine);
		double distributionValue= poisson.cdf(dataValue+interval)-poisson.cdf(dataValue-interval);
		return distributionValue;
	}

	/**
	 * Get the CDF interval
	 * @return the CDF interval
	 */
	public int getInterval() {
		return interval;
	}

	/**
	 * Set the CDF interval
	 * @param interval the CDF interval
	 */
	public void setInterval(int interval) {
		this.interval = interval;
	}



}
