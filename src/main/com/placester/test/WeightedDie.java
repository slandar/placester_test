package com.placester.test;

import java.util.Arrays;
import java.util.Random;

public abstract class WeightedDie
{
	protected WeightedDieSide[] sides;
	protected Random random;

    public WeightedDie(float [] weights){
		if (weights == null) {
			throw new IllegalArgumentException("Weights cannot be null.");
		}
		
    	float sumOfWeights = 0f;
    	for (float w : weights) {
    		if (w < 0) {
    			throw new IllegalArgumentException("Weights cannot be negative.");
    		}
    		sumOfWeights += w;
    	}
    	if (sumOfWeights != 1f) {
    		throw new IllegalArgumentException("Weights should add to 1, not " + sumOfWeights);
    	}
    	

		// Create sides as probabilities
		sides = new WeightedDieSide[weights.length];
		float min = 0;
		for (int i = 0; i < weights.length; i++) {
			float max = min + weights[i];
			sides[i] = new WeightedDieSide(min, max);
			min = max;
		}
				
		// Create RNG seeded with current time. 
		// Use ThreadLocalRandom in multithreaded environment.
		random = new Random(System.currentTimeMillis()); 

    }
    
    public int throwDie() {
		Float f = random.nextFloat();
		int idx = Arrays.binarySearch(sides, f);
		if (idx < 0) {
			throw new Error("Random number out of bounds.");
		}
		return idx + 1;
    }

	public abstract float getChiSquareForLargeSample();

	/**
	 * An auxiliary class representing weighted die side. Weight here is stored as 
	 * probability range [min, max). Implements Comparable interface to tell if a 
	 * given probability falls within the [min, max) range. 
	 */
	private class WeightedDieSide implements Comparable<Float> {
		private float min;
		private float max;
		
		private WeightedDieSide(float min, float max) {
			this.min = min;
			this.max = max;
		}
		
		/**
		 * @return 0 if arg is within the [min, max) range, or 1 if 
		 * 	arg is less than min and -1 if arg greater or equal to max.
		 */
		@Override
		public int compareTo(Float arg) {
			if (arg < min) {
				return 1;
			}
			else if (arg >= max) {
				return -1;
			}
			return 0;
		}
	}
}
