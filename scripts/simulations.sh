cd ~/machine-learning/data/arff
INFODIR=~/machine-learning/data/messages/
for directory in $(ls -d *)
do
	referencePartition=${directory}/${directory}.csv
	clusters=$(cat ${referencePartition} | cut -f 2 -d , | sort | uniq | wc -l)
	DATASET=${directory}
	CLUSTERS=${clusters}
#	java -jar ~/machine-learning/software/IntegrativeClustering.jar ~/machine-learning/data/arff/${DATASET} ${DATASET}.csv true ${CLUSTERS} false

	qsub -e ${INFODIR}errors/error_${DATASET}.txt -o ${INFODIR}output/output_${DATASET}.txt ~/machine-learning/scripts/submit.sh ${DATASET} ${CLUSTERS}
#	echo ${DATASET} ${CLUSTERS}
done