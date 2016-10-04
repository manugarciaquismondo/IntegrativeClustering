package org.feffermanlab.epidemics.cluster;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jsat.DataSet;
import jsat.SimpleDataSet;
import jsat.classifiers.DataPoint;
import jsat.clustering.GapStatistic;
import jsat.clustering.kmeans.HamerlyKMeans;
import jsat.linear.DenseVector;
import jsat.linear.Vec;

/**
 * A class to estimate the best K via the Gap Statistic using the JSAT package
 * @author manu_
 *
 */
public class JSATKEstimator extends KEstimator {
	GapStatistic gapStatistic;



	@Override
	public int estimateBestK(String directoryRoute) throws UnsupportedEncodingException, Exception {
		// TODO Auto-generated method stub
		GapStatistic statistic = new GapStatistic(new HamerlyKMeans(), true);
		statistic.setSamples(NUM_B);
		SimpleDataSet dataSet=convertToDataset(directoryRoute);
		statistic.cluster(dataSet, MIN_K, MAX_K);
		double[] gapStatistics=statistic.getGap();
		double[] elogKStdDev=statistic.getElogWkStndDev();
		return bestK(gapStatistics, elogKStdDev);
		
	}



	private int bestK(double[] gapStatistics, double[] elogKStdDev) {
		// TODO Auto-generated method stub
		int bestK=gapStatistics.length+1;
		for (int i = 0; i < elogKStdDev.length-1; i++) {
			if(gapStatistics[i]>=gapStatistics[i+1]-elogKStdDev[i+1]){
				bestK=i;
				break;
			}
				
			
		}
		return bestK+MIN_K;
	}



	private SimpleDataSet convertToDataset(String directoryRoute) {
		// TODO Auto-generated method stub
		Map<Integer, double[]> instancesMapping=getInstancesMapping(directoryRoute);
		List<double[]> centroids=clusterIntersector.getClusters().stream().map(elem -> formater.calculateCentroid(elem, instancesMapping)).collect(Collectors.toList());
		SimpleDataSet dataset = new SimpleDataSet(centroids.stream().map(elem -> new DataPoint(transformToVec(elem))).collect(Collectors.toList()));
		return dataset;
	}



	private Vec transformToVec(double[] elem) {
		// TODO Auto-generated method stub
		return new DenseVector(elem);
	}
	
	
	
}
