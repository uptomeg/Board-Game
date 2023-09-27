package assignment1;

import java.util.Collection;
public interface Visitor
{
public void visitCollection(Collection<String> collection);

public void visitString(StringElement s); 

}