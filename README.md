Exercises-Java
==========================

NOTE: DO NOT FORK THIS REPO ON GITHUB.

==========================

Testing materials for candidates

* Problem 1 - Weighted Die :

Implement a 6 sided die with weights on the sides, so that we don't have an even probability distribution, but it is weighted by a list of weights passed in at construction time.  After 10k iterations of throwing this die, the results should closely match the desired distribution, and this should be reproducible in the unit test in src/test/com/placester/test/WeightedDiceTest.  The class to implement is com.placester.test.SixSidedWeightedDie.

* Problem 2 - Priority Queue :

The task is as follows: implement the class com.placester.test.ThreadSafePriorityQueue as you see fit (modulo not using any collections classes from java.util or java.util.collections, and get the unit test in src/test/com/placester/test/PriorityQueueTest to pass). This class must allow dynamic resizing as elements are added. What the strategy is to do this is entirely up to you modulo the previously stated constraint.

NOTE: we are aware that there is a PriorityQueue in java.util. Please do not use any containers from java.util in your solution, as this is a test of data structure knowledge, rather than a test of java library knowledge
