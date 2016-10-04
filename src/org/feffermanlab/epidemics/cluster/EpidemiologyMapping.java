package org.feffermanlab.epidemics.cluster;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EpidemiologyMapping<Feature> {
	String instanceName, featureName;
	Map<String,Feature> featureMap;
	public EpidemiologyMapping(String instance, String feature) {
		super();
		if (instance == null) {
			throw new IllegalArgumentException("Argument instance of type "
					+ String.class
					+ " cannot be null when creating an object of type "
					+ getClass());
		}
		if (feature == null) {
			throw new IllegalArgumentException("Argument feature of type "
					+ String.class
					+ " cannot be null when creating an object of type "
					+ getClass());
		}
		this.instanceName = instance;
		this.featureName = feature;
		featureMap = new HashMap<String, Feature>();
	}
	String getFeature(){
		return featureName;
	}
	String getInstances(){
		return instanceName;
	}
	
	/**
	 * Add feature to the mapping
	 * @param featureName Name of the feature
	 * @param featureValue Value of the feature
	 * @return True if the feature did not exist, false otherwise
	 */
	public boolean addFeature(String featureName, Feature featureValue){
		if(featureMap.containsKey(featureName)){
			return false;
		}
		featureMap.put(featureName, featureValue);
		return true;
	}
	

	/** Get the value of a feature
	 * @param featureID the feature of which get the value
	 * @return The feature value
	 * @throws IllegalArgumentException if the feature does not exist
	 */
	public Feature getFeatureValue(String featureID) throws IllegalArgumentException{
		if(!featureMap.containsKey(featureID)){
			throw new IllegalArgumentException("Feature "+featureName+" does not contain a dimension named");
		}
		return featureMap.get(featureID);
	}
	
	public Set<String> getFeatureNames(){
		return featureMap.keySet();
	}

}
