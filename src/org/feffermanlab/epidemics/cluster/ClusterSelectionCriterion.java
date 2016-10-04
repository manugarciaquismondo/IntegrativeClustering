package org.feffermanlab.epidemics.cluster;

/**
 * A general criterion to find the best cluster intersection
 * @author manu_
 *
 * @param <E> Class of the intersected instances
 */
public interface ClusterSelectionCriterion<E> {
	public boolean selectCluster(Cluster<E> largestCluster,
			Cluster<E> iteratedCluster);

	/**
	 * Return the selected cluster
	 * @return the selected cluster
	 */
	public Cluster<E> getSelectedCluster();

	/**
	 * Return Process cluster according to the criterion
	 * @param clusterToProcess cluster to be processed
	 * @return <i>largestCluster</i> processed
	 */
	public Cluster<E> processCluster(Cluster<E> clusterToProcess);
}
