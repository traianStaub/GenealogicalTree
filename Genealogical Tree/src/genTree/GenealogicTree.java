package genTree;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class GenealogicTree {

	//in case there is null in the parents associated list spaces for presentation it places unknownPerson
	private Person unknownPerson = new Person("Unknown", "Unknown", 0, "U", "");
	
	//stores the (key) arrayList index = (value) Person associations
	private ArrayList<Person> indexToPerson;
	private Hashtable<Person, Integer> personToIndex;
	
	//stores the connection the person have with one another;
	//position 0 - the actual person
	//position 1 - father
	//position 2 - mother
	//position 3 and up - children
	private ArrayList<LinkedList<Integer>> graph;
	private int fatherIndex = 0;
	private int motherIndex = 1;
	private int childrenIndex = 2;
	
	//constructor
	public GenealogicTree() {
		this.indexToPerson = new ArrayList<>();
		this.personToIndex = new Hashtable<>();
		this.graph = new ArrayList<>();
		
		//on the 0 position adds the unknown person
		LinkedList<Integer> unknownPersonList = new LinkedList<>();
		unknownPersonList.add(null);
		unknownPersonList.add(null);
	}
	
	//addPersons to the graph----------------------
	public boolean addPerson(Person person, Person father, Person mother) {
		//checks if there is a person associated with the next index

		if(getIndex(person) != -1) {
			System.out.println("The person is in the graph already" + person.getName());
			return false;
		}
		
		//adds the person and index to the 2 hashMaps
		indexToPerson.add(person);
		personToIndex.put(person, graph.size());
		
		//save the index of the current person
		int currentPerson = graph.size();
		
		//add the person to the graph but do not add the connections yet
		graph.add(new LinkedList<Integer>());
		
		//create a reference to the connection list of the person
		LinkedList<Integer> conections = graph.get(currentPerson);
		
		if(father == null) {
			conections.add(null);
		} else {
			//creates the father if it is not in the graph and map with 2 null parents
			if(getIndex(father) == -1) {
				addPerson(father);
			}
			
			//adds the child to the father connections;
			graph.get(getIndex(father)).add(getIndex(person));
			
			//add the father to the connections list
			conections.add(getIndex(father));
		}
		
		if(mother == null) {
			conections.add(null);
		} else {
			//creates the father if it is not in the graph and map with 2 null parents
			if(getIndex(mother) == -1) {
				addPerson(mother);
			}
			
			//adds the child to the mother connections;
			graph.get(getIndex(mother)).add(getIndex(person));
			
			//add the mother to the connection list
			conections.add(getIndex(mother));
		}
			
		return true;
			
	}
	
	public boolean addPerson(Person person) {
		return addPerson(person, null, null);	
	}
	
	//populate graph from input
	public boolean populateGenTree(IGenTreeInput input) {
		if(input == null) {
			addPerson(unknownPerson);
			return false;
		}
		
		return input.populateGenTree(this);
	}
	
	//retrieve from the maps--------------
	public Person getPerson(Integer index) {
		if(index == null) {
			//System.out.println("the index provided was null");
			return null;
		}
		
		if(index < 0 || index >= graph.size()) {
			System.out.println("the index provided: " + index + ". was negative or larger then the graph size");
			return unknownPerson;
		}
		
		return indexToPerson.get(Integer.valueOf(index));
	}
	
	public int getIndex(Person person) {	
		Integer index =  personToIndex.get(person);
		
		if(index == null) {
			return -1;
		} else {
			return index;
		}
	}
	
	//getPersons by: -----------------------------------
	//by names
	public List<Person> getPersonsByLastName(String lastName){
		ICriteriumMethod critMethod = (p, criterium) -> (criterium.equals(p.getLastName()));
		
		//get personByCriterium willCheck if the lastName is null
		return getPersonByCriterium(lastName, critMethod);
	}
	
	public List<Person> getPersonsByFirstName (String firstName){
		ICriteriumMethod critMethod = (p, criterium) -> (criterium.equals(p.getFirstName()));
		
		//get personByCriterium willCheck if the lastName is null
		return getPersonByCriterium(firstName, critMethod);
	}
	
	public List<Person> getPersonsByFullName(String firstName, String lastName){
		String fullName = firstName + lastName;
		ICriteriumMethod critMethod = (p, criterium) -> {
				String pFullName = p.getFirstName() + p.getLastName();
				return criterium.equals(pFullName);
			};
		
		//get personByCriterium willCheck if the lastName is null
		return getPersonByCriterium(fullName, critMethod);				
	}
	
	//by age
	public List<Person> getPersonsByAge(int age){
		String ageString = String.valueOf(age);
		ICriteriumMethod critMethod = (p, criterium) -> (criterium.equals(String.valueOf(p.getAge())));
		
		//get personByCriterium willCheck if the lastName is null
		return getPersonByCriterium(ageString, critMethod);
	}
	
	//it can be made to return a list of persons by any combination of fields of the Person Class
	
	//interface usedBy getPersonByCriterium
	private interface ICriteriumMethod {
		boolean comparing(Person p, String criterium);
	}
		
	//main method that is used by the other methods
	private List<Person> getPersonByCriterium(String criterium, ICriteriumMethod criteriumMethod){
		List<Person> matches = new ArrayList<>();
		
		if(criterium == null) {
			return matches;
		}
		
		for(Person p : indexToPerson) {
			if(criteriumMethod.comparing(p, criterium)) {
				matches.add(p);
			}
		}
		
		return matches;
	}
	
	//get parents----------------
	//by person
	public ArrayList<Person> getParents(Person person){
		int personIndex = getIndex(person);
		
		//check if the person is in the graph
		if(personIndex == -1) {
			System.out.println("the person: " + person.getName() + " is not in the graph");
			return new ArrayList<Person>();
		}
		
		return getParents(personIndex);
		
	}
	
	//from a index
	public ArrayList<Person> getParents(int personIndex){
		if(personIndex < 0 || personIndex >= graph.size()) {
			return new ArrayList<>();
		}
		
		ArrayList<Person> parents = new ArrayList<>(2);
		
		//get the connections of the person
		List<Integer> connections = graph.get(personIndex);
		ListIterator<Integer> it = connections.listIterator(fatherIndex);
		
		Integer currentIndex = it.next();
		
		//adds the father
		if(currentIndex != null) {
			parents.add(getPerson(currentIndex));
		}else {
			parents.add(unknownPerson);
		}

		//adds the mother
		currentIndex = it.next();
		if(currentIndex != null) {
			parents.add(getPerson(currentIndex));
		}else {
			parents.add(unknownPerson);
		}
		
		return parents;
	}
	
	//get children-----------------
	//by person
	public List<Person> getChildren(Person person){
		int personIndex = getIndex(person);
		
		if(personIndex == -1) {
			System.out.println("the person: " + person.getName() + " is not in the graph");
			return new ArrayList<Person>();
		}
		
		return getChildren(personIndex);
	}
	
	//by index
	public List<Person> getChildren(int personIndex){
		if(personIndex < 0 || personIndex >= graph.size()) {
			return new ArrayList<>();
		}
		
		List<Person> children = new ArrayList<Person>();
		ListIterator<Integer> it = graph.get(personIndex).listIterator(childrenIndex);
		while(it.hasNext()) {
			children.add(getPerson(it.next()));
		}
		return children;
	}

	//create connections---------------
	//create connection with Person object
	public boolean connectFatherToChild(Person father, Person child) {
		return createConnection(father, child, fatherIndex);
	}
	
	public boolean connectMotherToChild(Person mother, Person child) {
		return createConnection(mother, child, motherIndex);
	}
	
	private boolean createConnection(Person parent, Person child, int parentPositionIndex) {
		int childIndex = getIndex(child);
		int parentIndex = getIndex(parent);
		
		//checks if the child provided is in the graph if not it will return false
		if(childIndex == -1) {
			System.out.println("Child: " + child.getName() + " is not in the graph when trying to add the parent: " + parent.getName());
			return false;
		}
		
		//checks if the parent provided is in the graph if not it will return false
		if(parentIndex == -1) {
			System.out.println("Parent: " + parent.getName() + " is not in the graph when trying to add the parent: " + parent.getName());
			return false;
		}
		
		//add the parent to the child connection list
		graph.get(childIndex).set(parentPositionIndex, parentIndex);
		
		//add the child to the parent connection list
		graph.get(parentIndex).add(childIndex);
		
		return true;
	}
	
	//create connections but with index not with person objects
	public boolean connectFatherToChild(int father, int child) {
		return createConnection(father, child, fatherIndex);
	}
	
	public boolean connectMotherToChild(int mother, int child) {
		return createConnection(mother, child, motherIndex);
	}
	
	public boolean createConnection(int parent, int child, int parentPositionIndex) {
		//checks if the index is valid and in the graph
		if(parent >= graph.size() || parent < 0) {
			System.out.println("parent index " + parent + " is not in the graph");
			return false;
		}
		
		if(child >= graph.size() || child < 0) {
			System.out.println("child index " + child + " is not in the graph");
			return false;
		}
		
		//add the parent to the child connection list
		graph.get(child).set(parentPositionIndex, parent);
				
		//add the child to the parent connection list
		//checks if the child is not already in the list first
		List<Integer> parentList = graph.get(parent);
		if(!parentList.contains(child))
			parentList.add(child);
				
		return true;
	}

	//get Siblings-----------------
	public List<Person> getSiblings(Person person){
		//change so that the siblings list is in order so you can search in a ordered list with binary search
		//the classic search is too long
		int personIndex = getIndex(person);
		
		if(personIndex == -1) {
			System.out.println("the person: " + person.getName() + " is not in the graph");
			return  new LinkedList<Person>();
		}
		
		return getSiblings(personIndex);
		
	}
	
	public List<Person> getSiblings(int personIndex){
		//checks if the index is valid and in the graph
		if(personIndex >= graph.size() || personIndex < 0) {
			return new ArrayList<>();
		}

		List<Integer> siblings = new ArrayList<>();
		
		//get the parents of the person
		ListIterator<Integer> personConnectionsIterator = graph.get(personIndex).listIterator(fatherIndex);
		
		//get the father 
		Integer parentIndex = personConnectionsIterator.next();
		if(parentIndex != null) {
			ListIterator<Integer> fatherChildrenIterator = graph.get(parentIndex).listIterator(childrenIndex);
			
			while(fatherChildrenIterator.hasNext()) {
				int child = fatherChildrenIterator.next();
				if(!siblings.contains(child) && child != personIndex) {
					siblings.add(child);
				}
			}
		}
		
		//get the mother 
		parentIndex = personConnectionsIterator.next();
		if(parentIndex != null) {
			ListIterator<Integer> fatherChildrenIterator = graph.get(parentIndex).listIterator(childrenIndex);
			
			while(fatherChildrenIterator.hasNext()) {
				int child = fatherChildrenIterator.next();
				if(!siblings.contains(child) && child != personIndex){
					siblings.add(child);
				}
			}
		}
		
		List<Person> siblingsPersons = new ArrayList<Person>(siblings.size());
		for(Integer index : siblings) {
			siblingsPersons.add(getPerson(index));
		}
		return siblingsPersons;
	}

	//check Related-----------------
	//check if 2 people are related
	public boolean isRelated(Person start, Person end) {
		
		//check if the 2 person are in the graph
		int startIndex = getIndex(start);
		if(startIndex == -1) {
			System.out.println("The first Person: " + start.getName() + " is not in the graph");
			return false;
		}
		
		int endIndex = getIndex(end);
		if(endIndex == -1) {
			System.out.println("The first Person: " + end.getName() + " is not in the graph");
			return false;
		}
		
		int[] visited = new int[graph.size()];
		
		return checkAncesters(startIndex, endIndex, visited);
	}
	
	//checks the descendens of a person for the isRelated method
	private boolean checkDescendents(int start, int toCheck, int[] visited) {
		
		//if the node has already been checked do not bother
		if(visited[start] == 1) {
			return false;
		}
		
		Deque<Integer> toVisit = new LinkedList<>();
		toVisit.add(start);
		
		while(!toVisit.isEmpty()) {
			int visiting = toVisit.poll();
			
			if(visiting == toCheck) {
				return true;
			}
			
			visited[visiting] = 1;
			
			ListIterator<Integer> it = graph.get(visiting).listIterator(childrenIndex);
			while(it.hasNext()) {
				Integer currentPerson = it.next();
				if(currentPerson != null && visited[currentPerson] != 1) {
					System.out.println(getPerson(currentPerson).getName());
					toVisit.add(currentPerson);
				}
				
			}
			
			
		}
		
		return false;
	}
	
	//checks the ancesters and the descendens of a person for the isRelated method
	private boolean checkAncesters(int start, int toCheck, int[] visited) {
		
		if(visited[start] == 1) {
			return false;
		}
		
		Deque<Integer> toVisit = new LinkedList<>();
		toVisit.add(start);
		
		while(!toVisit.isEmpty()) {
			int visiting = toVisit.pop();
			
			if(visiting == toCheck) {
				return true;
			}
			
			visited[visiting] = 1;
			
			ListIterator<Integer> it = graph.get(visiting).listIterator(fatherIndex);
			//check father and his ancestors and related descendants
			Integer currentPerson = it.next();
			if(currentPerson != null && visited[currentPerson] != 1) {
				System.out.println(getPerson(currentPerson).getName());
				if(checkAncesters(currentPerson, toCheck, visited)) {
					return true;
				}
			}
			
			//check mother and hers ancestors and related descendants
			currentPerson = it.next();
			if(currentPerson != null && visited[currentPerson] != 1) {
				System.out.println(getPerson(currentPerson).getName());
				if(checkAncesters(currentPerson, toCheck, visited)) {
					return true;
				}
			}
			
			//check children
			while(it.hasNext()) {
				currentPerson = it.next();
				if(checkDescendents(currentPerson, toCheck, visited)) {
					return true;
				}
			}
			
			return false;
		}
		
		return false;
	}
	
	//searching the tree------------------
	//Depth-first search method - recursive
	public boolean searchAncester(Person ascendent, Person descendent) {
		
		if(descendent == null) {
			return false;
		} else if(descendent.equals(ascendent)) {
			return true;
		} else {
			
			Integer startIndex = getIndex(descendent);
			
			ListIterator<Integer> it = graph.get(startIndex).listIterator(fatherIndex);
			Integer currentParent = it.next();
			
			//searches on the fathers side if it finds it return true
			if(searchAncester(ascendent, getPerson(currentParent)))
				return true;

			//searches on the mothers side if it finds it return true
			currentParent = it.next();
			if(searchAncester(ascendent, getPerson(currentParent))) {
				return true;
			}
				
			
			return false;
		}
	}

	public boolean searchDescendent(Person descendent, Person ascendent) {
		Integer startIndex = getIndex(ascendent);
		
		if(ascendent == null) {
			return false;
		} else if(ascendent.equals(descendent)) {
			return true;
		} else {
			ListIterator<Integer> it = graph.get(startIndex).listIterator(childrenIndex);
			Integer childIndex = null;
			
			while(it.hasNext()) {
				childIndex = it.next();
				
				if(searchDescendent(descendent, getPerson(childIndex))) {
					return true;
				}
			}
			
			return false;
		}
	}
	
	public boolean searchAncesterBFS(Person ancester, Person descendent) {	
		int descendentIndex = getIndex(descendent);
		int ancesterIndex = getIndex(ancester);
		
		//check if the 2 people are in the graph
		if(descendentIndex == -1) {
			System.out.println("The first Person: " + descendent.getName() + " is not in the graph");
			return false;
		}
		
		if(ancesterIndex == -1) {
			System.out.println("The second Person: " + ancester.getName() + " is not in the graph");
			return false;
		}
		
		int[] visited = new int[graph.size()];
		
		Deque<Integer> toVisit = new LinkedList<>();
		toVisit.add(descendentIndex);
		
		while(!toVisit.isEmpty()) {
			//current node to visit-
			int visiting = toVisit.poll();
			
			//we have found the person we were searching for
			if(visiting == ancesterIndex) {
				return true;
			}
			
			LinkedList<Integer> parents = graph.get(visiting);
			ListIterator<Integer> it = parents.listIterator(fatherIndex);
			
			//father index
			Integer currentParent = it.next();
			if(currentParent != null && visited[currentParent] != 1) {
				toVisit.add(currentParent);
			}

			//motherIndex
			currentParent = it.next();
			if(currentParent != null && visited[currentParent] != 1) {
				toVisit.add(currentParent);
			}

			//mark this node as visited
			visited[visiting] = 1;
		}
		
		return false;
	}

	//longest connection-----------------
	//returns the furthest descendant or ancestor
	public List<Person> furthestAncester(Person start) {
		int[] graphArray = new int[graph.size()];
		int startIndex = getIndex(start);
		int[] visited = new int[graph.size()];
		
		//the stack for traversing the graph
		Deque<Integer> toVisit = new LinkedList<Integer>();
		toVisit.push(startIndex);
		
		//searches the stack for future nodes it can go to
		while(!toVisit.isEmpty()) {
			Integer visiting = toVisit.pop();
			
			//circular loops checking
			if(visited[visiting] == 1) {
				continue;
			}else {
				visited[visiting] = 1;
			}
			
			//increases the persons score because it was there
			graphArray[visiting]++;
				
			//the connections of the current node
			ListIterator<Integer>  edges = graph.get(visiting).listIterator(fatherIndex);
				
			//get the father connection
			Integer currentParent = edges.next();
			if(currentParent != null) {
				//sets the parent array score the same as this node
				graphArray[currentParent] = graphArray[visiting];
				toVisit.push(currentParent);
			}
				
			//get the mother connection
			currentParent = edges.next();
			if(currentParent != null) {
				//sets the parent array score the same as this node
				graphArray[currentParent] = graphArray[visiting];
				toVisit.push(currentParent);
			}
		}
		
		
		List<Person> furthestAncester = new ArrayList<>();
		int furthest = 0;
		for(int i = 0; i < graphArray.length; i++) {
			if(furthest < graphArray[i]) {
				furthestAncester.clear();
				furthest = graphArray[i];
				furthestAncester.add(getPerson(i));
			} else if(furthest == graphArray[i]){
				furthestAncester.add(getPerson(i));
			}
		}
		
		return furthestAncester;
	}

	public List<Person> furthestDescendent(Person start) {
		int[] graphArray = new int[graph.size()];
		int[] visited = new int[graphArray.length];
		int startIndex = getIndex(start);
		
		Deque<Integer> toVisit = new LinkedList<>();
		toVisit.push(startIndex);
		
		while(!toVisit.isEmpty()) {
			Integer visiting = toVisit.pop();
			
			//circular loops checking
			if(visited[visiting] == 1) {
				continue;
			}else {
				visited[visiting] = 1;
			}
			
			//increases the persons score because it was there
			graphArray[visiting]++;
				
			//the connections of the current node
			ListIterator<Integer>  edges = graph.get(visiting).listIterator(childrenIndex);
			
			while(edges.hasNext()) {
				//get the connection for each of the children
				Integer currentChild = edges.next();
				
				if(currentChild == null)
					continue;
				
				//sets the child array score the same as this node
				graphArray[currentChild] = graphArray[visiting];
				toVisit.push(currentChild);
				
			}	
		}
		
		List<Person> furthestDescendent = new ArrayList<>();
		int furthest = 0;
		for(int i = 0; i < graphArray.length; i++) {
			if(furthest < graphArray[i]) {
				furthestDescendent.clear();
				furthest = graphArray[i];
				furthestDescendent.add(getPerson(i));
			} else if(furthest == graphArray[i]){
				furthestDescendent.add(getPerson(i));
			}
			
		}
		
		return furthestDescendent;
	}
	
	//save-----------------
	//save the GenealogicTree to a txtFile
	public boolean saveGenTreeTxt(String personFilePath, String connectionFileName) {
		
		try (FileWriter personOutpt = new FileWriter(new  File(personFilePath))){
			;
			for(int i = 1, n = indexToPerson.size(); i < n; i++) {
				personOutpt.write(indexToPerson.get(i).toStringForFile() + "\n");
			}
			personOutpt.close();
			
			System.out.println("Succes when writting the person save file");
		} catch(IOException e) {
			System.out.println("FAIL when writting the person save file");
			e.printStackTrace();
		}
		
		try (FileWriter connectionOutpt = new FileWriter(new  File(connectionFileName));) {
			
			String valuesBreak = ", ";
			for(int i = 1, n = graph.size(); i < n; i++) {
				ListIterator<Integer> it = graph.get(i).listIterator(fatherIndex);
				StringBuilder sb = new StringBuilder();
				sb.append(i + valuesBreak);
				Integer fatherIndex = it.next();
				if(fatherIndex == null) {
					sb.append(0 + valuesBreak);
				} else {
					sb.append(fatherIndex + valuesBreak);
				}

				Integer motherIndex = it.next();
				if(motherIndex == null) {
					sb.append(0);
				} else {
					sb.append(motherIndex);
				}
				
				connectionOutpt.write(sb.toString() + "\n");
			}
			
			connectionOutpt.close();
			System.out.println("Succes when writting the connection save file");
		}catch(IOException e){
			System.out.println("FAIL when writting the connection save file");
			//e.printStackTrace();
		}
		
		return false;
	}
	
	//update----
	//update input values
	public boolean updateValues(IGenTreeInput input) {
		if(input.updateValues(this) == false) {
			return false;
		}
		
		System.out.println("Succes updating the values");
		return true;
	}
	
	//clear all values
	public void clear() {
		graph.clear();
		indexToPerson.clear();
		personToIndex.clear();
	}
	
	//getters
	public int getGraphSize() {
		return graph.size();
	}

	
}
