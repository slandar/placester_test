package com.placester.test;

import org.junit.Assert;
import org.junit.Test;

public class WeightedDiceTest
{
    @Test
    public void test()
    {
        float errVal = .01f;
        float [] weights = new float[]{.05f, .10f, .15f, .2f, .2f, .3f};
        SixSidedWeightedDie theDie = new SixSidedWeightedDie(weights);
        int [] counts = new int []{0,0,0,0,0,0};
        int numThrows = 100000;
        for(int i = 0; i < numThrows; i++)
        {
            int val = theDie.throwDie();
            counts[val-1]++;
        }
        
        for(int i = 0; i < 6; i++)
        {
            float actual = (float)counts[i]/numThrows;
            float diff = Math.abs(actual - weights[i]);
            Assert.assertTrue(diff <= errVal);
        }
    }
    	
	@Test
	public void testChiSquare() {
		// assign sample weights
		float[] weights = new float[] {1f/6, 1f/6, 1f/6, 1f/6, 1f/6, 1f/6};
				
		// run test, calculate observed frequencies
		int numberOfTests = 100000;
		int[] samples = new int[6];
		SixSidedWeightedDie die = new SixSidedWeightedDie(weights);
		for (int i = 0; i < numberOfTests; i++) {
			samples[die.throwDie()-1]++;
		}
		
		float[] observed = new float[samples.length];
		for (int i = 0; i < observed.length; i++) {
			observed[i] = (float)samples[i]/numberOfTests;
		}
		
		// calculate chi-square as SUM of (observed[i]-expected[i])^2/expected[i]
		float chiSquare = 0;
		for (int i = 0; i < observed.length; i++) {
			chiSquare += (observed[i] - weights[i]) * (observed[i] - weights[i]) 
					/ weights[i];
			System.out.println((i+1) + "s expected:" + weights[i] + " observed:" + observed[i]);
		}
		
		System.out.println(chiSquare);	
		Assert.assertTrue(chiSquare < die.getChiSquareForLargeSample());
	}
	
	@Test
	public void testSampleSizeValid() {
		int numOfTests = 100000;
		int samples[] = generateDistributionFunction(numOfTests, 
				new float[] {.05f, .2f, .15f, .3f, .2f, .1f});		
		int totalCount = 0;
		for (int i = 0; i < samples.length; i++) {
			totalCount += samples[i];
		}
		Assert.assertEquals(numOfTests, totalCount);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testArgumentNotNull() {
		new SixSidedWeightedDie(null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testArgumentRightSize() {
		new SixSidedWeightedDie(new float[5]);
	}

	private int[] generateDistributionFunction(int sampleSize, float[] weights) {
		int counts[] = new int[6];
		SixSidedWeightedDie die = new SixSidedWeightedDie(weights);
		for (int i = 0; i < sampleSize; i++) {
			counts[die.throwDie()-1]++;
		}
		return counts;
	}
}
