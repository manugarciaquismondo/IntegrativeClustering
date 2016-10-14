qstat -u $(whoami) | grep Eqw | cut -f 1 -d ' ' | qdel
