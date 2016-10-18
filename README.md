# IntegrativeClustering
An algorithm for integrative clustering across different types of data.

Integrative Clustering is an algorithm for data integration of partial clusterings. The algorithm works as follows:

1. The data is first clustered according to each type of descriptor. 
2. These partitions are intersected. This generates a new partition.
3. The centroid of each cluster of this partition is calculated. 
4. These new centroids are clustered.
5. The centroids are replaced by the data from each cluster.

To run Integrative Clustering, type the command:

>java -jar IntegrativeClustering.jar _directory_

where:

* _directory_ is the directory containing the files for each feature separarely. These files must be in [Weka](http://www.cs.waikato.ac.nz/ml/weka/ "Weka") (_.arff_) format. These files must be in a directory __features__ inside _directory_, i.e., in _directory_/__features__.

Optional parameters:

* -c=_fileName_ compares the resulting clustering with the ground-truth clustering from _fileName_. If this parameter is not set, then the comparison score is assumed to be 1 (perfect match).
* -i indicates that the file containing the ground-truth clustering is an incidence matrix. It has only effect if -c=_fileName_ is set. If -i is not present, the file in _fileName_ contains a clustering as a comma-separated file (CSV) without header. Each row in this file is a pair _instance_,_cluster_, indicating that the instance _instance_ belongs to the cluster _cluster_.
* -r=_clusters_ integrates the clusterings accross features. The final number of clusters is set to _clusters_. If _clusters_ is set to 0, then this number is estimated using the [gap statistic](http://doi.wiley.com/10.1111/1467-9868.00293 "Gap statistic").


An example of a call is:

>java -jar IntegrativeClustering.jar /home/user/data -c=comparison.csv -r=3 -i

This indicates that the comparison of the clustering resulting from integrating the features defined in the Weka file in the directory __/home/user/data/features__ are compared against the clustering defined in __comparison.csv__, that is an incidence matrix defining the reference clustering for the generated clustering. The clusters are integrated, and the number of clusters is set to 3.
