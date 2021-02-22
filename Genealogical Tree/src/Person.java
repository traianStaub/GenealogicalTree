
public class Person {

	private String name;
	
	public Person(String name) {
		this.name = name;
	}

	//getters and setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Person)) {
			return false;
		}
		
		Person person = (Person)obj;
		
		if(name.equals(person.name))
			return true;
		else 
			return false;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	

}
