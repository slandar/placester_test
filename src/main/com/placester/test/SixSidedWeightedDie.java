package com.placester.test;

import java.util.Arrays;

/*
 * Implement a 6 sided die with weights on the sides, so that we don't have an even probability distribution, but it is 
 * weighted by a list of weights passed in at construction time
 * 
 * After 10k iterations of throwing this die, the results should closely match the desired distribution, and this should
 * be reproducible in the unit test in
 * 
 * src/test/com/placester/test/WeightedDiceTest
 */
public class SixSidedWeightedDie extends WeightedDie
{
    //NOTE: since these are weights on a probability distribution, these should sum to one, and the incoming array
    // should be of length 6. You should throw if either of these preconditions is false
    public SixSidedWeightedDie(float[] weights)
    {
        super(weights);
        if (weights.length != 6) {
        	throw new IllegalArgumentException("The number of weights " + weights.length + " != 6.");
        }
    }

	public float getChiSquareForLargeSample() {
		// with p=0.05, and 5 degrees of freedom, assuming n->oo, x2 should be < 1.14
		return 1.14f;
	}

}
