package org.feffermanlab.epidemics.cluster;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import org.apache.commons.lang3.ArrayUtils;

import com.opencsv.CSVReader;

public class ClusterReader {
	/**
	 * Read a cluster from a CSV file
	 * @param inputRoute The route where the clustering is defined
	 * @return The read clustering
	 * @throws IOException If errors occurred while reading the CSV file
	 */
	public ClusteringInfo readCluster(String inputRoute) throws IOException{
		String[] splittedRoute=  inputRoute.split("\\|/");
		String fileName=splittedRoute[splittedRoute.length-1];
		fileName=fileName.substring(0, fileName.lastIndexOf("."));
		List<Integer> clusterLabels=readClusterLabelsAndProcessExceptions(inputRoute);
		ClusteringInfo returnedCluster=new ClusteringInfo(clusterLabels.size(), ArrayUtils.toPrimitive(clusterLabels.toArray(new Integer[0])), 1, fileName);
		return returnedCluster;
	}
	
	private List<Integer> readClusterLabelsAndProcessExceptions(String inputRoute) throws IOException{
		try {
			CSVReader reader = new CSVReader(new FileReader(inputRoute));
			List<Integer> clusterLabels=reader.readAll().stream().mapToInt((String[] elem)->Integer.parseInt(elem[0])).collect(ArrayList::new, ArrayList::add,
                    ArrayList::addAll);
			reader.close();
			return clusterLabels;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			throw new IOException("File not found exception: "+e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new IOException("Generic IO exception: "+e.getMessage());
		}
	}
}
