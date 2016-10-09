# IntegrativeClustering
An algorithm for integrative clustering across different types of data.

Integrative Clustering is an algorithm for data integration of partial clusterings. The algorithm works as follows:

1. The data is first clustered according to each type of descriptor. 
2. These partitions are intersected. This generates a new partition.
3. The centroid of each cluster of this partition is calculated. 
4. These new centroids are clustered.
5. The centroids are replaced by the data from each cluster.

To run Integrative Clustering, type the command:

>java -jar IntegrativeClustering.jar _directory_ _comparisonFilename_ _integrateClusters_ _numberOfClusters_ _isIncidenceMatrix_

where:

* _directory_ is the directory containing the files for each feature separarely. These files must be in [Weka](http://www.cs.waikato.ac.nz/ml/weka/ "Weka") (_.arff_) format. These files must be in a directory __features__ inside _directory_, i.e., in _directory_/__features__.
* _comparisonFilename_ is the reference file where the clustering for comparison is. It must be in the directory _directory_, i.e., the route of the file containing the reference clustering will be _directory_/_comparisonFilename_.
* _integrateClusters_ is a boolean (true or false) that indicates if clusters are integrated. If false, steps from 3 to 5 will not be executed.
* _numberOfClusters_ is an integer that indicates the number of clusters. If 0, it is estimated using the [gap statistic](http://doi.wiley.com/10.1111/1467-9868.00293 "Gap statistic").
* _isIncidenceMatrix_ is a boolean (true or false) that indicates if the file _comparisonFilename_ is an incidence matrix. If it is set to false, then this file is a CSV file without a header consisting of a set of pairs __element__,__cluster__.

An example of a call is:

>java -jar IntegrativeClustering.jar /home/user/data comparison.csv true 3 true

This indicates that the comparison of the clustering resulting from integrating the features defined in the Weka file in the directory __/home/user/data/features__ are compared against the clustering defined in __comparison.csv__, that is an incidence matrix defining the reference clustering for the generated clustering. The clusters are integrated, and the number of clusters is set to 3.
