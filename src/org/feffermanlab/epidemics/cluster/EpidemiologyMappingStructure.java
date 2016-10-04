package org.feffermanlab.epidemics.cluster;

import java.util.HashMap;
import java.util.Map;

public class EpidemiologyMappingStructure {
	public Map<String,EpidemiologyMapping<Float>> geneMapping, environmentalMapping, ecologyMapping;
	public  Map<String,EpidemiologyMapping<Integer>> epidemicMapping;
	public EpidemiologyMappingStructure(String[] instanceNames) {
		super();
		geneMapping = new HashMap<String,EpidemiologyMapping<Float>>();
		environmentalMapping = new HashMap<String,EpidemiologyMapping<Float>>();
		ecologyMapping = new HashMap<String,EpidemiologyMapping<Float>>();
		epidemicMapping = new HashMap<String,EpidemiologyMapping<Integer>>();
		for(String instanceName: instanceNames){
			geneMapping.put(instanceName, new EpidemiologyMapping<Float>(instanceName, "Gene"));
			environmentalMapping.put(instanceName, new EpidemiologyMapping<Float>(instanceName, "Environment"));
			ecologyMapping.put(instanceName, new EpidemiologyMapping<Float>(instanceName, "Ecology"));
			epidemicMapping.put(instanceName, new EpidemiologyMapping<Integer>(instanceName, "Epidemic"));
		}
	}
}
