package org.feffermanlab.epidemics.cluster;

/**
 * Largest cluster selection criterion. It process clusters intersecting it with a local buffer.
 * @author manu_
 * @param <E> Class of the intersected instances
 *
 */
public class LargestIntersectionSelectionCriterion<E> implements
		ClusterSelectionCriterion<E> {

	
	protected Cluster<E> intersectedCluster, largestIntersectedCluster;
	protected int intersectionSize;
	public LargestIntersectionSelectionCriterion(
			Cluster<E> intersectedCluster) {
		super();
		this.intersectedCluster = intersectedCluster;
	}
	@Override
	public boolean selectCluster(Cluster<E> largestCluster,
			Cluster<E> iteratedCluster) {
		// TODO Auto-generated method stub
		largestIntersectedCluster=iteratedCluster;
		Cluster<E> intersection=(Cluster<E>) iteratedCluster.clone();
		intersection.retainInstanceSet(intersectedCluster.getInstances());
		if(largestCluster==null||intersection.size()>intersectionSize){
			intersectionSize=intersection.size();
			largestIntersectedCluster=iteratedCluster;
			return true;
		}
		return false;
	}
	@Override
	public Cluster<E> getSelectedCluster() {
		// TODO Auto-generated method stub
		return largestIntersectedCluster;
	}
	@Override
	public Cluster<E> processCluster(Cluster<E> largestCluster) {
		// TODO Auto-generated method stub
		return intersectedCluster.intersectCluster(largestCluster);
	}

}
