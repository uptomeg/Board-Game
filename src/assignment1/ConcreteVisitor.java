package assignment1;

import java.util.Collection;
import java.util.Iterator;

public class ConcreteVisitor implements Visitor{
	private int a = 0;
	public void visitCollection(Collection<String> collection) {
	    Iterator<String> iterator = collection.iterator();
	    while (iterator.hasNext()) {
	            String o = iterator.next();
	            StringElement app = new StringElement(o);
	            app.accept(this);
	            }
	            
                }
	   
	 public void visitString(StringElement stringE) {
         if(stringE.equal("A"));
        	 a += 1;
	 } 
	 
	 public int getIt(){
		 
         return a;
	 } 

}