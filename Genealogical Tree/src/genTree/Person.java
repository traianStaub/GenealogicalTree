package genTree;

public class Person {

	private String firstName;
	private String lastName;
	private int age;
	private String gender;
	private String residence;
	
	public Person(String firstName, String lastName, int age, String gender, String residence) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.age = age;
		this.gender = gender;
		this.residence = residence;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getResidence() {
		return residence;
	}

	public void setResidence(String residence) {
		this.residence = residence;
	}

	//equals only checks if they have the same name
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Person)) {
			return false;
		}
		
		Person person = (Person)obj;
		
		if(!firstName.equals(person.firstName))
			return false;

		if(!lastName.equals(person.lastName))
			return false;
		
		return true;
	}
	
	public String getName() {
		return firstName + " " + lastName;
	}
	
	public String getAllValues() {
		return firstName + " " + lastName + " " + age + " " + gender + " " + residence;
	}
	
	@Override
	public String toString() {
		return firstName + " " + lastName;
	}
	
	//String that is the apropriate format to write to a txt file
	public String toStringForFile() {
		return firstName + ", " + lastName + ", " + age + ", " + gender + ", " + residence;
	}
	
	@Override
	public int hashCode() {
		StringBuilder sb = new StringBuilder();
		sb.append(firstName);
		sb.append(lastName);
		sb.append(age);
		sb.append(gender);
		sb.append(residence);
		return sb.toString().hashCode();
	}
	

}
