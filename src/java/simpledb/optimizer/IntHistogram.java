package simpledb.optimizer;

import simpledb.execution.Predicate;

/** A class to represent a fixed-width histogram over a single integer-based field.
 */
public class IntHistogram {
    private int min;
    private int max;
    private int width;
    private int[] buckets;
    private int cou;
    /**
     * Create a new IntHistogram.
     * 
     * This IntHistogram should maintain a histogram of integer values that it receives.
     * It should split the histogram into "buckets" buckets.
     * 
     * The values that are being histogrammed will be provided one-at-a-time through the "addValue()" function.
     * 
     * Your implementation should use space and have execution time that are both
     * constant with respect to the number of values being histogrammed.  For example, you shouldn't 
     * simply store every value that you see in a sorted list.
     * 
     * @param buckets The number of buckets to split the input value into.
     * @param min The minimum integer value that will ever be passed to this class for histogramming
     * @param max The maximum integer value that will ever be passed to this class for histogramming
     */
    public IntHistogram(int buckets, int min, int max) {
    	// some code goes here
        this.max=max;
        this.min=min;
        this.buckets=new int[buckets];
        double t=Math.ceil((1.+max-min)/buckets);
        this.width=(int)t;

    }

    /**
     * Add a value to the set of values that you are keeping a histogram of.
     * @param v Value to add to the histogram
     */
    public void addValue(int v) {
    	// some code goes here
        int t=(v-min+1)/width;
        int t1=(v-min+1)%width;
        if(t1==0)
            buckets[t-1]++;
        else
            buckets[t]++;
        cou++;

    }

    /**
     * Estimate the selectivity of a particular predicate and operand on this table.
     * 
     * For example, if "op" is "GREATER_THAN" and "v" is 5, 
     * return your estimate of the fraction of elements that are greater than 5.
     * 
     * @param op Operator
     * @param v Value
     * @return Predicted selectivity of this particular operator and value
     */
    public double estimateSelectivity(Predicate.Op op, int v) {

    	// some code goes here
        if(op.equals(Predicate.Op.LESS_THAN))
        {
            if(v<=min)
                return 0;
            if(v>=max)
                return 1;

            int index=getIndex(v);
            double count=0;
            for(int i=0;i<index;i++)
            {
                count+=buckets[i];
            }
            count+=buckets[index]*(v-index*width-min)/width;
            return count/cou;
        }else if(op.equals(Predicate.Op.LESS_THAN_OR_EQ))
            return estimateSelectivity(Predicate.Op.LESS_THAN,v+1);
        else if(op.equals(Predicate.Op.EQUALS))
            return estimateSelectivity(Predicate.Op.LESS_THAN,v+1)-estimateSelectivity(Predicate.Op.LESS_THAN,v);
        else if(op.equals(Predicate.Op.GREATER_THAN))
            return 1-estimateSelectivity(Predicate.Op.LESS_THAN,v+1);
        else if(op.equals(Predicate.Op.GREATER_THAN_OR_EQ))
            return 1-estimateSelectivity(Predicate.Op.LESS_THAN,v);
        else if(op.equals(Predicate.Op.NOT_EQUALS))
            return 1-estimateSelectivity(Predicate.Op.EQUALS,v);
        return 0;
    }
    public int getIndex(int v)
    {
        int t=(v-min+1)/width;
        int t1=(v-min+1)%width;
        if(t1==0)
            return t-1;
        else
            return t;
    }
    /**
     * @return
     *     the average selectivity of this histogram.
     *     
     *     This is not an indispensable method to implement the basic
     *     join optimization. It may be needed if you want to
     *     implement a more efficient optimization
     * */
    public double avgSelectivity()
    {
        double sum=0;
        for(int i=0;i<buckets.length;i++)
        {
            sum+=buckets[i];
        }
        // some code goes here
        return sum/=buckets.length;
    }
    
    /**
     * @return A string describing this histogram, for debugging purposes
     */
    public String toString() {
        // some code goes here
        return "min: "+min+"max: "+max+" width: "+width+"buckets: "+buckets.toString()+"cou: "+cou;
    }
}
