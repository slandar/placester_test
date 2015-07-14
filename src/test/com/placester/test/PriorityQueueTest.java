package com.placester.test;


import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.junit.Test;

@SuppressWarnings("deprecation")
public class PriorityQueueTest
{
	@Test
    public void test() throws InterruptedException
    {
        final ThreadSafePriorityQueue<QueueTestTask> q = new ThreadSafePriorityQueue<>();
        final Random rand = new Random();
        ExecutorService threadPool = Executors.newFixedThreadPool(20);

        int max = 1000;
        for(int i = 0; i < max; i++)
        {
            final int idx = i;
            threadPool.submit(new Runnable(){
                @Override
                public void run()
                {
                    q.add(new Priority<QueueTestTask>(rand.nextInt(100), new QueueTestTask(idx)));
                    try
                    {
                        //make sure we actually hit multiple threads;
                        Thread.sleep(50);
                    } catch (InterruptedException e)
                    {
                        // TODO Auto-generated catch block
                        throw new RuntimeException(e);
                    }
                }});
        }
        
        threadPool.awaitTermination(10000, TimeUnit.MILLISECONDS);
        
        int startPriority = 0;
        //Test that threadsafe insertion worked
        while(!q.isEmpty())
        {
            Priority<QueueTestTask> item = q.poll();
            Assert.assertTrue(item.priority >= startPriority);
        }
        
        Priority<QueueTestTask> item1 = new Priority<QueueTestTask>(666, new QueueTestTask(0));
        Priority<QueueTestTask> item2 = new Priority<QueueTestTask>(0, new QueueTestTask(0));
        Priority<QueueTestTask> item3 = new Priority<QueueTestTask>(42, new QueueTestTask(0));

        
        q.add(item1);
        q.add(item2);
        q.add(item3);

        Assert.assertTrue(q.contains(item1));
        Priority<QueueTestTask> item = q.poll();
        Assert.assertEquals(item, item2);
        item = q.poll();
        Assert.assertEquals(item, item3);
        item = q.poll();
        Assert.assertEquals(item, item1);

        
        q.add(item1);
        Assert.assertFalse(q.isEmpty());
        item = q.peek();
        Assert.assertEquals(item, item1);
        Assert.assertFalse(q.isEmpty());

        item = q.poll();
        Assert.assertEquals(item, item1);
        Assert.assertTrue(q.isEmpty());
    }

	/**
	 * Test adding elements by a single thread.
	 */
	@Test
	public void testSingleThreadedAdd() {
		ThreadSafePriorityQueue<QueueTestTask> queue = new ThreadSafePriorityQueue<>();
		for (int i = 0; i < 100; i++) {
			int r = new Random().nextInt(5);
			queue.add(new Priority<QueueTestTask>(r, new QueueTestTask(0)));
			System.out.print(r + ",");
		}
		System.out.println("testSingleThreadedAdd, Q size=" + queue.size());
		prettyPrint(queue);

		// make sure the queue size equals the number of objects placed
		Assert.assertEquals(queue.size(), 100);
		// make sure the queue is sorted
		Assert.assertTrue(isArraySorted(queue.toArray()));
	}

	/**
	 * Test adding elements by multiple threads.
	 */
	@Test
	public void testMultiThreadedAdd() {
		final ThreadSafePriorityQueue<QueueTestTask> queue = new ThreadSafePriorityQueue<>();

		// create a thread pool for placing objects on the queue
		ExecutorService putter = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 100; i++) {
			putter.execute(new Runnable() {
				@Override
				public void run() {
					int r = new Random().nextInt(5);
					queue.add(new Priority<QueueTestTask>(r, new QueueTestTask(0)));
				}
			});
		}
		// let tasks finish
		try {
			putter.awaitTermination(5, TimeUnit.SECONDS);
		}
		catch (InterruptedException ie) {}
		putter.shutdown();
				
		System.out.println("testMultiThreadedAdd, Q size=" + queue.size());
		prettyPrint(queue);

		// make sure the queue size equals the number of objects placed
		Assert.assertEquals(queue.size(), 100);
		// make sure the queue is sorted
		Assert.assertTrue(isArraySorted(queue.toArray()));
	}

	/**
	 * Test adding/removing elements concurrently.
	 */
	@Test
	public void testMultiThreadedAddRemove() {
		final ThreadSafePriorityQueue<QueueTestTask> queue = new ThreadSafePriorityQueue<>();
	
		// create a pool of 10 threads for placing objects on the queue
		ExecutorService putter = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 100; i++) {
			putter.execute(new Runnable() {
				@Override
				public void run() {
					int r = new Random().nextInt(5);
					queue.add(new Priority<QueueTestTask>(r, new QueueTestTask(0)));
				}
			});
		}
		
		// create a pool of 7 threads for removing objects from the queue
		ExecutorService getter = Executors.newFixedThreadPool(7);
		for (int i = 0; i < 300; i++) {
			putter.execute(new Runnable() {
				@Override
				public void run() {
					queue.poll();
				}
			});
		}

		try {
			putter.awaitTermination(5, TimeUnit.SECONDS);
			getter.awaitTermination(5, TimeUnit.SECONDS);

		}
		catch (InterruptedException ie) {}
		
		System.out.println("testMultiThreadedAddRemove, Q size=" + queue.size());
		prettyPrint(queue);

		putter.shutdown();
		getter.shutdown();
	}
	
	/**
	 * Run a sequence of add/remove/contains/poll/peek on a single thread, 
	 * look for inconsistencies in the queue state.
	 */
	@Test
	public void testAddRemovePeek() {
		ThreadSafePriorityQueue<QueueTestTask> queue = new ThreadSafePriorityQueue<>();
		
		Assert.assertTrue(queue.isEmpty());
		Assert.assertNull(queue.peek());
		Assert.assertNull(queue.poll());
		Assert.assertFalse(queue.contains(new Priority<QueueTestTask>(0, new QueueTestTask(0))));
		Assert.assertFalse(queue.remove(new Priority<QueueTestTask>(0, new QueueTestTask(0))));
		Assert.assertTrue(queue.add(new Priority<QueueTestTask>(1, new QueueTestTask(0))));
		Assert.assertEquals(queue.size(), 1);
		Assert.assertTrue(queue.add(new Priority<QueueTestTask>(2, new QueueTestTask(0))));
		Assert.assertEquals(queue.size(), 2);
		Assert.assertTrue(queue.add(new Priority<QueueTestTask>(1, new QueueTestTask(0))));
		Assert.assertEquals(queue.size(), 3);
		Assert.assertTrue(queue.add(new Priority<QueueTestTask>(5, new QueueTestTask(0))));
		Assert.assertTrue(queue.add(new Priority<QueueTestTask>(2, new QueueTestTask(0))));
		Assert.assertTrue(queue.add(new Priority<QueueTestTask>(2, new QueueTestTask(0))));
		Assert.assertTrue(queue.contains(new Priority<QueueTestTask>(2, new QueueTestTask(0))));
		Assert.assertTrue(queue.remove(new Priority<QueueTestTask>(2, new QueueTestTask(0))));
		Assert.assertTrue(queue.remove(new Priority<QueueTestTask>(2, new QueueTestTask(0))));
		Assert.assertTrue(queue.remove(new Priority<QueueTestTask>(2, new QueueTestTask(0))));
		Assert.assertFalse(queue.remove(new Priority<QueueTestTask>(2, new QueueTestTask(0))));		
		Assert.assertEquals(queue.peek(), new Priority<QueueTestTask>(1, new QueueTestTask(0)));
		Assert.assertEquals(queue.poll(), new Priority<QueueTestTask>(1, new QueueTestTask(0)));
		Assert.assertEquals(queue.peek(), new Priority<QueueTestTask>(1, new QueueTestTask(0)));
		Assert.assertEquals(queue.poll(), new Priority<QueueTestTask>(1, new QueueTestTask(0)));
		Assert.assertEquals(queue.peek(), new Priority<QueueTestTask>(5, new QueueTestTask(0)));
		Assert.assertEquals(queue.poll(), new Priority<QueueTestTask>(5, new QueueTestTask(0)));
		Assert.assertNull(queue.peek());
		Assert.assertNull(queue.poll());
	}
	
	@Test
	public void test2() {
		ThreadSafePriorityQueue<QueueTestTask> queue = new ThreadSafePriorityQueue<>();
		queue.add(new Priority<QueueTestTask>(4, new QueueTestTask(0)));
		queue.add(new Priority<QueueTestTask>(1, new QueueTestTask(0)));
		queue.add(new Priority<QueueTestTask>(3, new QueueTestTask(0)));
		queue.add(new Priority<QueueTestTask>(0, new QueueTestTask(0)));
		queue.add(new Priority<QueueTestTask>(1, new QueueTestTask(0)));
		queue.add(new Priority<QueueTestTask>(1, new QueueTestTask(0)));
		queue.add(new Priority<QueueTestTask>(1, new QueueTestTask(0)));
		queue.add(new Priority<QueueTestTask>(2, new QueueTestTask(0)));
		queue.add(new Priority<QueueTestTask>(0, new QueueTestTask(0)));
		queue.add(new Priority<QueueTestTask>(0, new QueueTestTask(0)));
		Assert.assertEquals(queue.size(), 10);
		Assert.assertTrue(isArraySorted(queue.toArray()));

	}
	
	private static boolean isArraySorted(Priority<QueueTestTask>[] elements) {
		for (int i = 0; i < elements.length - 1; i++) {
			if (elements[i].priority() > elements[i+1].priority()) { 
				return false; 
			}
		}
		return true;
	}
	
	private static void prettyPrint(SimpleQueue q) {
		for (Object o : q.toArray()) 
			System.out.print(o + ","); 
		System.out.println();
	}

}
