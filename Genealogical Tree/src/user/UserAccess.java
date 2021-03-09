package user;

import java.util.List;

import genTree.GenealogicTree;
import genTree.IGenTreeInput;
import genTree.Person;

public class UserAccess {

	private GenealogicTree famillyTree;
	private IGenTreeInput genTreeInput;
	
	public UserAccess(IGenTreeInput genTreeInput) {
		this.famillyTree = new GenealogicTree();
		this.genTreeInput = genTreeInput;
	}
	
	public UserAccess() {
		this(null);
	}
	
	//functionality--------------------------
	//get Persons
	public List<Person> getPersonsByFirstName(String firstName){
		List<Person> solution =  famillyTree.getPersonsByFirstName(firstName);
		if(solution.size() == 0) {
			System.out.println("There is not person in the Familly Tree with the firstName: " + firstName);
		}
		return solution;
	}
	
	public List<Person> getPersonsByLastName(String lastName){
		List<Person> solution =  famillyTree.getPersonsByLastName(lastName);
		if(solution.size() == 0) {
			System.out.println("There is not person in the Familly Tree with the lastName: " + lastName);
		}
		return solution;
	}
	
	public List<Person> getPersonsByFullName(String firstName, String lastName){
		List<Person> solution =  famillyTree.getPersonsByFullName(firstName, lastName);
		if(solution.size() == 0) {
			System.out.println("There is not person in the Familly Tree with the firstName: " + firstName + " and lastName: " + lastName);
		}
		return solution;
	}
	
	public List<Person> getPersonsByAge(int age){
		List<Person> solution =  famillyTree.getPersonsByAge(age);
		if(solution.size() == 0) {
			System.out.println("There is not person in the Familly Tree with the age: " + age);
		}
		return solution;
	}
	
	//check if 2 people from the famillyTree are related
	public boolean areRelated(Person firstPerson, Person secondPerson) {
		return famillyTree.isRelated(firstPerson, secondPerson);
	}
	
	//retrieves the farthest relatives of a person
	public List<Person> furthestAncester(Person p){
		return famillyTree.furthestAncester(p);
	}
	
	public List<Person> furthestDescendent(Person p){
		return famillyTree.furthestDescendent(p);
	}
	
	//populating the familly Tree
	public boolean createGenTree() {
		return famillyTree.populateGenTree(genTreeInput);
	}
	
	//updating the input source from the genTree
	public boolean uptdateInput() {
		if(genTreeInput == null) {
			return false;
		}
		
		return genTreeInput.updateValues(famillyTree);
	}
	
	//clear the famillyTree
	public void clear() {
		famillyTree.clear();
	}
	
	//saving the genTree as a Txt File different From the input source
	public boolean saveGenTree(String personFilePath, String connectionFileName) {
		return famillyTree.saveGenTreeTxt(personFilePath, connectionFileName);
	}
	
	//chagne the inputSource
	public boolean changeInputSource(IGenTreeInput newInput) {
		genTreeInput = newInput;
		return true;
	}
	
	
}
