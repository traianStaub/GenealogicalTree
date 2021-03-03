package genTree;
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
	
	public GenealogicTree() {
		this.indexToPerson = new ArrayList<>();
		this.personToIndex = new Hashtable<>();
		this.graph = new ArrayList<>();
		
		//on the 0 position adds the unknown person
		LinkedList<Integer> unknownPersonList = new LinkedList<>();
		unknownPersonList.add(null);
		unknownPersonList.add(null);
		graph.add(unknownPersonList);
		indexToPerson.add(unknownPerson);
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

	//create connection
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
		graph.get(parent).add(child);
				
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
	
	//searching the tree
	//Depth-first search method - recursive
	public boolean searchDescendent(Person start, Person ancestor) {
		
		if(start == null) {
			return false;
		} else if(start.equals(ancestor)) {
			return true;
		} else {
			
			Integer startIndex = getIndex(start);
			
			ListIterator<Integer> it = graph.get(startIndex).listIterator(fatherIndex);
			Integer currentParent = it.next();
				
			//searches on the fathers side if it finds it return true
			if(searchDescendent(getPerson(currentParent), ancestor))
				return true;
			
			//searches on the mothers side if it finds it return true
			currentParent = it.next();
			if(searchDescendent(getPerson(currentParent), ancestor)) {
				return true;
			}
				
			
			return false;
		}
	}
	
	public boolean searchAncester(Person start, Person descendent) {
		Integer startIndex = getIndex(start);
		
		if(start == null) {
			return false;
		} else if(start.equals(descendent)) {
			return true;
		} else {
			ListIterator<Integer> it = graph.get(startIndex).listIterator(childrenIndex);
			Integer childIndex = null;
			
			while(it.hasNext()) {
				childIndex = it.next();
				
				if(searchAncester(getPerson(childIndex), descendent)) {
					return true;
				}
			}
			
			return false;
		}
	}
	
	//working on
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
	
	//getters
	public int getGraphSize() {
		return graph.size();
	}
}
