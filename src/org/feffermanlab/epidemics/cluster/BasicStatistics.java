package org.feffermanlab.epidemics.cluster;

import java.util.Arrays;

/**
 * A class for basic statistical operations
 * @author manu_
 *
 */
public class BasicStatistics 
{
    double[] data;
    int size;   

    public BasicStatistics(double[] data) 
    {
        this.data = data;
        size = data.length;
    }   

    /**
     * Get the data mean 
     * @return the mean of the data stored in the object
     */
    public double getMean()
    {
        double sum = 0.0;
        for(double a : data)
            sum += a;
        return sum/size;
    }

    /**
     * Get the data variance 
     * @return the variance of the data stored in the object
     */
    public double getVariance()
    {
        double mean = getMean();
        double temp = 0;
        for(double a :data)
            temp += (mean-a)*(mean-a);
        return temp/size;
    }

    /**
     * Get the data standard deviation 
     * @return the standard deviation of the data stored in the object
     */
    public double getStdDev()
    {
        return Math.sqrt(getVariance());
    }

    /**
     * Get the data median
     * @return the median of the data stored in the object
     */
    public double median() 
    {
       Arrays.sort(data);

       if (data.length % 2 == 0) 
       {
          return (data[(data.length / 2) - 1] + data[data.length / 2]) / 2.0;
       } 
       else 
       {
          return data[data.length / 2];
       }
    }
}
