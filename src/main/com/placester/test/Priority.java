package com.placester.test;

public class Priority<X>
{
    protected final X item;
    protected final int priority;
    
    public Priority(int priority, X item)
    {
        this.priority = priority;
        this.item = item;
    }
    
    public X item(){return item;}
    public int priority(){return  priority;}
    
    public boolean equals(Object o) {
    	if (o instanceof Priority) {
    		Priority<X> p = (Priority<X>)o;
        	return (priority == p.priority && item.equals(p.item));
    	}
    	return false;
    }
    
    public String toString() {
    	return "[" + priority + "," + item + "]";
    }
}
