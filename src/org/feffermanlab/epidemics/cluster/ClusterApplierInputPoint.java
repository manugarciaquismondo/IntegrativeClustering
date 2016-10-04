package org.feffermanlab.epidemics.cluster;

public class ClusterApplierInputPoint {
	public static void main(String[] args) {
		ClusterApplier applier= new ClusterApplier();
		try {
			applier.applyClusteringAndProcessExceptions(args[0]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("Errors occurred while processing directory "+args[0]);
			e.printStackTrace();
		}
	}
}
