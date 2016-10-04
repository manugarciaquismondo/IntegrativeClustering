package org.feffermanlab.epidemics.cluster;

/**
 * A Pair in which the order of the elements does not matter, that is, &lt;a,b&gt;==&lt;b,a&gt;
 * @author manu_
 *
 * @param <E> First data type
 * @param <T> Second data type
 */
public class SymmetricPair<E, T> extends Pair<E, T> {

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		final int prime = 31;
		int result = 1;
		int minHashCode=((getFirst() == null) ? 0 : getFirst().hashCode());
		int maxHashCode=((getSecond() == null) ? 0 : getSecond().hashCode());
		if(minHashCode>maxHashCode){
			int bufferCode=maxHashCode;
			maxHashCode=minHashCode;
			minHashCode=bufferCode;
		}
		result = prime * result + minHashCode;
		result = prime * result + maxHashCode;
		return result;
	}

	public SymmetricPair(E first, T second) {
		super(first, second);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(super.equals(obj)){
			return true;
		}
		Pair<E,T> comparedPair=(Pair<E,T>)obj;
		if(areTheSameClass(comparedPair)&&areTheSameClass(this)){
			return comparedPair.getFirst().equals(getSecond())&&
				comparedPair.getSecond().equals(getFirst());
		}
		return false;
	}

	/**
	 * Check if both elements in the pair are instances of the same class
	 * @param symmetricPair The pair to be tested
	 * @return True if the elements of {@code symmetricPair} are of the same pair, false otherwise.
	 */
	private boolean areTheSameClass(Pair<E, T> symmetricPair) {
		// TODO Auto-generated method stub
		if(symmetricPair.getFirst()==null&&symmetricPair.getSecond()==null) return true;
		if(symmetricPair.getFirst()==null || symmetricPair.getSecond()==null) return false;
		return symmetricPair.getFirst().getClass().equals(symmetricPair.getSecond().getClass());
	}

}
