# IntegrativeClustering
An algorithm for integrative clustering across different types of data.

Integrative Clustering is an algorithm for data integration of partial clusterings. The algorithm works as follows:
1. The data is first clustered according to each type of descriptor. 
2. These partitions are intersected. This generates a new partition.
3. The centroid of each cluster of this partition is calculated. 
4. These new centroids are clustered.
5. The centroids are replaced by the data from each cluster.

To run Integrative Clustering, type the command:

>java -jar IntegrativeClustering.jar <directory> <integrateClusters> <clusters>

where:

* <directory> is the directory containing the files for each feature separarely. These files must be in [Weka](http://www.cs.waikato.ac.nz/ml/weka/ "Weka") format.
* <integrateClusters> is a boolean (true or false) that indicates if clusters are integrated. If false, steps from 3 to 5 will not be executed.
* <estimateBestKs> is an integer that indicates the number of clusters. If 0, it is estimated using the (gap statistic)[http://doi.wiley.com/10.1111/1467-9868.00293 "gap statistic"].

An example of a call is:

>java -jar IntegrativeClustering.jar /home/user/data true 3
