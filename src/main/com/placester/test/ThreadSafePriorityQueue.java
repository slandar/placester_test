package com.placester.test;
// NOTE: we are aware that there is a PriorityQueue in
// java.util. Please do not use this. 
// If you are doing this test at home, please do not use any containers from
// java.util in your solution, as this is a test of data
// structure knowledge, rather than a test of java library knowledge.
// If you are doing it in the office, please ask the person testing you if you are going to
// use any built in collections other than arrays.

/*
 * The task is as follows: implement this class as you see fit, and get the unit test in
 * src/test/com/placester/test/PriorityQueueTest to pass. This class
 * must allow dynamic resizing as elements are added. What the
 * strategy is to do this is entirely up to you modulo the previously
 * stated constraints.
 * 
 * Feel free to use anything from Java.util.Arrays (e.g., you don't need to implement
 * your own sort if you don't want to).
 */
public class ThreadSafePriorityQueue<X> implements SimpleQueue<Priority<X>>
{
	private static final int INITIAL_CAPACITY = 10;
	private Object[] queue;
	private int queueLength;

	
    public ThreadSafePriorityQueue()
    {
        initialize();
    }
    
    
    public synchronized void initialize()
    {
		queue = new Object[INITIAL_CAPACITY];
		queueLength = 0;
    }
    
    
    @Override
    public synchronized int size()
    {
        return queueLength;
    }

    @Override
    public synchronized boolean isEmpty()
    {
		return (queueLength == 0);
    }

    @Override
    public synchronized void clear()
    {
		queue = new Object[queue.length];
		queueLength = 0;
    }

    @Override
    public boolean add(Priority<X> e)
    {
		if (e == null) {
			throw new IllegalArgumentException("Element cannot be null.");
		}

		synchronized (this) {
			int index = findPosition(e);	
			// if index >= 0, we found an element with the same priority in the queue
			// if index < 0, its value is -(index), where index is the possible location
			index = Math.abs(index);
			return insertAt(e, index);
		}
    }

    @Override
    public synchronized Priority<X> poll()
    {
		Object o = queue[0];
		deleteAt(0);
		return (Priority<X>)o;
    }

    @Override
    public synchronized Priority<X> peek()
    {
		return (Priority<X>)queue[0];
    }

    @Override
    public boolean contains(Priority<X> x)
    {		
		if (x == null) {
			throw new IllegalArgumentException("Element cannot be null.");
		}

 	   /* 
 	    * Could be a bit optimized to only search through the elements with the priority same as x.
 	    */
		synchronized (this) {
	 		for (int i = 0; i < queueLength; i++) {
	 			if (queue[i].equals(x)) {
	 				return true;
	 			}
	 		}
		}
 		return false;
    }
    
	private int findPosition(Priority<X> e) {
		return findPosition(e, 0, queueLength);
	}

	@Override
	public boolean remove(Priority<X> e) {
		if (e == null) {
			throw new IllegalArgumentException("Element cannot be null.");
		}
		synchronized (this) {
			for (int i = 0; i < queueLength; i++) {
				if (queue[i].equals(e)) {
					return deleteAt(i);
				}
			}
		}
		return false;
	}

	@Override
	public synchronized Priority<X>[] toArray() {
		// Shallow copy
		Priority<X>[] newArray = new Priority[queueLength];
		System.arraycopy(queue, 0, newArray, 0, queueLength);
		return (Priority<X>[])newArray;
	}

	/**
	 * Find position of the element in the queue, implements binary search.
	 * @param e Element whose position needs to be found.
	 * @param  start Starting position.
	 * @param end Ending position.
	 * @return Element position if the element was found. Otherwise, it will return a negative
	 *   -(pos), where pos is the proposed position at which element could be inserted.
	 */
	private int findPosition(Priority<X> e, int start, int end) {
		// This checks if the search has been exhausted and element not found. 
		// Return value is the negative start position where the element could fit
		// should we need to insert it.
        if (start > end - 1) {
            return -(start);
        }

        // Split in half, test if median element is what we are looking for,
        // otherwise rerun recursively on the appropriate half of the array.
        int median = (start + end - 1) / 2;
        if (e.priority() > ((Priority<X>)queue[median]).priority()) {
        	return findPosition(e, median + 1, end);
        }
        else if (e.priority() < ((Priority<X>)queue[median]).priority()) {
        	return findPosition(e, start, median);
        }
        return median;
	}
		
	/**
	 * Insert element o at the specified position pos. The method will resize 
	 * internal array, if the new element doesn't fit the size of the queue.
	 * @param e Element to insert.
	 * @param pos Position to insert at.
	 * @return True if the element was inserted.
	 */
	private boolean insertAt(Priority<X> e, int pos) {
		if (queueLength >= queue.length) {
			// double the size of the array if its full, copy elements
			Object[] newQueue = new Object[queue.length * 2];
			System.arraycopy(queue, 0, newQueue, 0, pos);
			newQueue[pos] = e;
			System.arraycopy(queue, pos, newQueue, pos + 1, queue.length - pos);	
			queue = newQueue;
		}
		else {
			System.arraycopy(queue, pos, queue, pos + 1, queue.length - pos - 1);	
			queue[pos] = e;
		}
		queueLength++;
		return true;
	}
	
	/**
	 * Delete element at the specified position.
	 * @param pos Position to delete at.
	 * @return True if the element was deleted.
	 */
	private boolean deleteAt(int pos) {
		if (queueLength == 0) {
			return false;
		}
		System.arraycopy(queue, pos + 1, queue, pos, queueLength - pos - 1);
		queueLength--;
		queue[queueLength] = null; // optional
		return true;
	}
}
