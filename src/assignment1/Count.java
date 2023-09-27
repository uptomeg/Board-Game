package assignment1;

import java.util.ArrayList;
import assignment1.Aggregate;
import assignment1.Iterator;
import java.util.List;

public class Count implements Aggregate {
	private List<String> un = new ArrayList<>();
	private int count = 1;
	private String target;
	
	 public Count(List<String> un, String target) {
	        this.un = un;
	        this.target = target;
	    }
	 
	 
	 public Iterator<String> createIterator()
		{
		 ConcreteIterator v = new ConcreteIterator();
		 return v;
		}
	 
	 private class ConcreteIterator implements Iterator<String>
		{
			private int m_position;

			public boolean hasNext()
			{
				if (m_position < un.size()) {
					return true;}
				else
					return false;
			}
			
			public String next()
			{
				if (this.hasNext()) {
					String x = un.get(m_position);
					m_position += 1;
					return x;}
				else
					return null;
			}
		}

	 
	 public int count () {
	    	if (un != null && un.size() != 0) {
	    	Iterator<String> v = this.createIterator();
			Object a = v.next();
			while (a != null) {
				if(a == this.target) {// no matter who want to undo, we don't time this as a round.
				count += 1;}
			    a = v.next();
			}
	    	}
	    	return count;
}}
