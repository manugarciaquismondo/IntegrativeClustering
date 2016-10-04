package org.feffermanlab.epidemics.cluster;

/**
 * Non-modifying selection criterion
 * @author manu_
 * @param <E> Class of the intersected instances
 *
 */
public class LargestClusterSelectionCriterion<E> implements
		ClusterSelectionCriterion<E> {
	
	Cluster<E> iteratedCluster;
	@Override
	public boolean selectCluster(Cluster<E> largestCluster,
			Cluster<E> iteratedCluster) {
		this.iteratedCluster = iteratedCluster;
		return largestCluster==null||iteratedCluster.size()>largestCluster.size();
	}
	@Override
	public Cluster<E> getSelectedCluster() {
		// TODO Auto-generated method stub
		return iteratedCluster;
	}
	@Override
	public Cluster<E> processCluster(Cluster<E> largestCluster) {
		// TODO Auto-generated method stub
		return largestCluster;
	}
}
