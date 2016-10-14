#!bin/bash
# This is a sample PBS script. 
# Specify which shell to use
#$ -S /bin/bash
#   
#
#   Request 20 hours of walltime
#
#$ -l h_vmem=3G
#$ -l h_rt=20:00:00
#
# Maintain environment variables
##$ -V
#
#   The following is the body of the script. By default,
#   PBS scripts execute in your home directory, not the
#   directory from which they were submitted. The following
#   line places you in the directory from which the job
#   was submitted.
#
#
#   Send mail when job begins
##$ -m b
#   Send mail when job ends
##$ -m e
#   Send mail when job aborts
##$ -m a

DATASET=$1
CLUSTERS=$2
MEMORY=2G
echo "Working with dataset "${DATASET}" and "${CLUSTERS}" clusters in directory "${HOME}
java -Xms${MEMORY} -Xmx${MEMORY} -jar ${HOME}/machine-learning/software/IntegrativeClustering.jar ${HOME}/machine-learning/data/arff/${DATASET} ${DATASET}.csv true ${CLUSTERS} false &> ${HOME}/machine-learning/results/${DATASET}.txt 
