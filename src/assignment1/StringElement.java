package assignment1;

public class StringElement implements Visitable
{
private String value;

 public void accept(Visitor visitor) {
     visitor.visitString(this);
}
 

 public StringElement(String s) {
     this.value = s;
}

 public String getValue(){
 return value;
}

 public boolean equal(String h) {
	 return value == h;
 }
}