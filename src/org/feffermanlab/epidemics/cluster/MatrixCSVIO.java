package org.feffermanlab.epidemics.cluster;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class MatrixCSVIO {
	/**
	 * Write matrix of double numbers on CSV format
	 * @param outputRoute route where to write the matrix
	 * @param matrix matrix to write
	 */
	public void writeDoubleMatrix(String outputRoute, double[][] matrix) {
		try {
			CSVWriter writer = new CSVWriter(new FileWriter(outputRoute));
			for (int instanceIndex = 0; instanceIndex < matrix.length; instanceIndex++) {
				writer.writeNext(convertToStringArray(matrix[instanceIndex]));
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private String[] convertToStringArray(double[] ds) {
		String[] stringArray= new String[ds.length];
		for(int instanceIndex=0; instanceIndex<ds.length; instanceIndex++)
			stringArray[instanceIndex]=ds[instanceIndex]+"";
		return stringArray;
	}
	/**
	 * Write matrix of integer numbers on CSV format
	 * @param outputRoute route where to write the matrix
	 * @param matrix matrix to write
	 */
	public void writeIntegerMatrix(String outputRoute, int[][] matrix) {
		try {
			CSVWriter writer = new CSVWriter(new FileWriter(outputRoute));
			for (int instanceIndex = 0; instanceIndex < matrix.length; instanceIndex++) {
				writer.writeNext(convertToStringArray(matrix[instanceIndex]));
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private String[] convertToStringArray(int[] ds) {
		String[] stringArray= new String[ds.length];
		for(int instanceIndex=0; instanceIndex<ds.length; instanceIndex++)
			stringArray[instanceIndex]=ds[instanceIndex]+"";
		return stringArray;
	}
	
	
	
	/** Read matrix of floating numbers on CSV format
	 * @param inputRoute where from read the matrix
	 * @return the read matrix
	 * @throws IOException if any problem occurred accessing the matrix file
	 */
	public float[][] readFloatMatrixFromCSV(String inputRoute) throws IOException {
		// TODO Auto-generated method stub
		try {
			CSVReader reader = new CSVReader(new FileReader(inputRoute));
			float[][] readMatrix=transformToFloatMatrix(reader.readAll());
			reader.close();
			return readMatrix;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			throw new IOException("File not found exception: "+e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new IOException("Generic IO exception: "+e.getMessage());
		}
		
	}
	
	/** Read matrix of double numbers on CSV format
	 * @param inputRoute where from read the matrix
	 * @return the read matrix
	 * @throws IOException if any problem occurred accessing the matrix file
	 */
	public double[][] readDoubleMatrixFromCSV(String inputRoute) throws IOException {
		// TODO Auto-generated method stub
		try {
			CSVReader reader = new CSVReader(new FileReader(inputRoute));
			double[][] readMatrix=transformToDoubleMatrix(reader.readAll());
			reader.close();
			return readMatrix;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			throw new IOException("File not found exception: "+e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new IOException("Generic IO exception: "+e.getMessage());
		}
		
	}
	
	private double[][] transformToDoubleMatrix(List<String[]> readAll) {
		// TODO Auto-generated method stub
		double[][] transformedMatrix = new double[readAll.get(0).length][readAll.get(0).length];
		int i=0;
		for(String[] readLine: readAll){
			for (int j = 0; j < readLine.length; j++) {
				try{					
					transformedMatrix[i][j]=Float.parseFloat(readLine[j]);
				}
				catch(NumberFormatException e){
					System.err.println(readLine[j]+" is not a float");
				}
				
			}
			i++;
		}
		return transformedMatrix;
	}

	private float[][] transformToFloatMatrix(List<String[]> readAll) {
		// TODO Auto-generated method stub
		float[][] transformedMatrix = new float[readAll.get(0).length][readAll.get(0).length];
		int i=0;
		for(String[] readLine: readAll){
			for (int j = 0; j < readLine.length; j++) {
				try{					
					transformedMatrix[i][j]=Float.parseFloat(readLine[j]);
				}
				catch(NumberFormatException e){
					System.err.println(readLine[j]+" is not a float");
				}
				
			}
			i++;
		}
		return transformedMatrix;
	}
	
	/** Read matrix of integer numbers on CSV format
	 * @param inputRoute where from read the matrix
	 * @return the read matrix
	 * @throws IOException if any problem occurred accessing the matrix file
	 */
	public int[][] readIntegerMatrixFromCSV(String inputRoute) throws IOException {
		// TODO Auto-generated method stub
		try {
			CSVReader reader = new CSVReader(new FileReader(inputRoute));
			int[][] readMatrix=transformToIntegerMatrix(reader.readAll());
			reader.close();
			return readMatrix;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			throw new IOException("File not found exception: "+e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new IOException("Generic IO exception: "+e.getMessage());
		}
		
	}
	
	
	private int[][] transformToIntegerMatrix(List<String[]> readAll) {
		// TODO Auto-generated method stub
		int[][] transformedMatrix = new int[readAll.size()][readAll.get(0).length];
		int i=0;
		for(String[] readLine: readAll){
			for (int j = 0; j < readLine.length; j++) {
				transformedMatrix[i][j]=Integer.parseInt(readLine[j]);
			}
			i++;
		}
		return transformedMatrix;
	}

	public void writeClusters(List<Cluster<Integer>> intersectedClusters, String clusteringRoute) throws IOException {
		int cluster_index=0;
		CSVWriter writer = new CSVWriter(new FileWriter(clusteringRoute));
		writer.writeNext(new String[]{"cluster", "element"});
		for(Cluster<Integer> cluster: intersectedClusters){
			for(Integer clusteredElement: cluster.getInstances()){
				writer.writeNext(new String[]{cluster_index+"", clusteredElement+""});
			}
			cluster_index++;
		}
		writer.close();



	}

	/**
	 * Get the original order of the clustered instances
	 * @param inputRoute File route of the clustered instances
	 * @return A list containing the original order of the clustered instances
	 */
	public List<Integer> getInstanceOrder(String inputRoute) {
		// TODO Auto-generated method stub
		try{
			CSVReader reader = new CSVReader(new FileReader(inputRoute));
			List<Integer> instanceOrder=new LinkedList<Integer>();
			int[][] instanceOrderAsMatrix=transformToIntegerMatrix(reader.readAll());
			for (int i = 0; i < instanceOrderAsMatrix.length; i++) {
				instanceOrder.add(instanceOrderAsMatrix[i][1]);
			}
			reader.close();
			return instanceOrder;
		} catch(IOException e){
			System.out.println("Errors occurred while reading instance ordering file "+inputRoute);
			e.printStackTrace();
		}
		return null;
	}
}
