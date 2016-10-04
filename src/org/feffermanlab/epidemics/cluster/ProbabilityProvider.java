package org.feffermanlab.epidemics.cluster;

import java.util.Collection;
import java.util.stream.Collectors;

import cern.jet.random.engine.RandomEngine;

/**
 * A class to calculate probabilities based on a distribution
 * @author manu_
 *
 */
public abstract class ProbabilityProvider {

	public final RandomEngine engine;
	
	protected Collection<Double> dataset;
	
	public ProbabilityProvider(RandomEngine engine) {
		super();
		this.engine = engine;
		// TODO Auto-generated constructor stub
	}

	public void setDataset(Collection<Double> dataset) {
		this.dataset = dataset;
	}

	abstract double getFromDistribution(Double first, int dataValue);



	/**
	 * Calculate the probability of obtaining <i>dataValue</i> from a distribution
	 * @param dataValue data whose probability of occurrence is calculated
	 * @param values Statistical parameters of the mixture distribution
	 * @return the probability to obtaining <i>dataValue</i> from the distribution defined by the parameters in {@code values}
	 */
	public double calculateProbability(int dataValue, Collection<KData> values) {
		// TODO Auto-generated method stub
		double numerator=0.0f;
		for(Pair<Double, Integer> clusterPair: values.stream().flatMap(c -> c.averages.stream()).collect(Collectors.toSet())){
			double probability = getFromDistribution(clusterPair.getFirst(),dataValue);
			double mixtureModelElement=probability*clusterPair.getSecond();
			numerator+=mixtureModelElement;
		}
		return numerator;
	}

}
