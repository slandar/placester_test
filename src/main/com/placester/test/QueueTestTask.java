package com.placester.test;

public class QueueTestTask
{
    public final int ordinality;
    
    public QueueTestTask(int ordinality)
    {
        this.ordinality = ordinality;
    }
    
    public String toString() {
    	return "" + ordinality;
    }
    
    public boolean equals(Object o) {
    	return (o instanceof QueueTestTask) &&
    		ordinality == ((QueueTestTask)o).ordinality;
    }
}
