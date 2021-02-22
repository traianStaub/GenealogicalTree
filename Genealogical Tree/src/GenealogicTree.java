import java.util.ArrayList;
import java.util.Deque;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class GenealogicTree {

	//in case there is null in the parents associated list spaces for presentation it places unknownPerson
	private Person unknownPerson = new Person("Unknown");
	
	//stores the (key) arrayList index = (value) Person associations
	private Hashtable<Integer, Person> indexToPerson;
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
		this.indexToPerson = new Hashtable<>();
		this.personToIndex = new Hashtable<>();
		this.graph = new ArrayList<>();
	}
	
	//addPersons to the graph----------------------
	public boolean addPerson(Person person, Person father, Person mother) {
		//checks if there is a person associated with the next index
		if(indexToPerson.get(graph.size()) != null) {
			System.out.println("there is a value asociated with the key: " + graph.size());
			return false;
		} else {
			if(getIndex(person) != -1) {
				System.out.println("The person is in the graph already" + person.getName());
				return false;
			}
			
			//adds the person and index to the 2 hashMaps
			indexToPerson.put(graph.size(), person);
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
	}
	
	public boolean addPerson(Person person) {
		return addPerson(person, null, null);	
	}
	
	//retrieve from the maps--------------
	private Person getPerson(Integer index) {
		if(index == null)
			return null;
		return indexToPerson.get(Integer.valueOf(index));
	}
	
	private int getIndex(Person person) {	
		Integer index =  personToIndex.get(person);
		if(index == null) {
			return -1;
		} else {
			return index;
		}
	}
	
	//get parents----------------
	public ArrayList<Person> getParents(Person person){
		int index = getIndex(person);
		
		//check if the person is in the graph
		if(index == -1) {
			System.out.println("the person: " + person.getName() + " is not in the graph");
			return new ArrayList<Person>();
		}
		
		ArrayList<Person> parents = new ArrayList<>(2);
		
		//get the connections of the person
		List<Integer> connections = graph.get(index);
		ListIterator<Integer> it = connections.listIterator();
		
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
	
	//addParent--------
	//add Father
	public boolean addFather(Person child, Person father) {
		return addParent(child, father, fatherIndex);
	}
	
	//add Mother
	public boolean addMother(Person child, Person mother) {
		return addParent(child, mother, motherIndex);
	}
	
	//adds both Parents
	public boolean addParents(Person child, Person father, Person mother) {
		int childIndex = getIndex(child);
		
		//checks if the child provided is in the graph if not it will return false
		if(childIndex == -1) {
			System.out.println("Child: " + child.getName() + " is not in the graph when trying to add the parents: " + father.getName() + ", " + mother.getName());
			return false;
		}
		
		//tries to add the father as a parent for the child
		if(!addParent(childIndex, father, fatherIndex)) {
			System.out.println("Coud not add the parent: " + father.getName() + " to the child: " + child.getName());
		}
		
		//tries to add the mother as a parent for the child
		if(!addParent(childIndex, mother, motherIndex)) {
			System.out.println("Coud not add the parent: " + mother.getName() + " to the child: " + child.getName());
		}
		
		//if only one of the parents can be added it will add that parent only
		return true;
	}
	
	//adds a parent needs to be specified on what position to add the parent - mother or father
	private boolean addParent(Person child, Person parent, int parentPositionIndex) {
		int childIndex = getIndex(child);
		
		//checks if the child provided is in the graph if not it will return false
		if(childIndex == -1) {
			System.out.println("Child: " + child.getName() + " is not in the graph when trying to add the parent: " + parent.getName());
			return false;
		}
		
		//checks if the parent is null or the unknownPerson
		if(parent != null && parent.equals(unknownPerson)) {
			System.out.println("The Parent that was provided is either null or is unknownParent");
			return false;
		}
		
		//finds the parent if he is there or creates a new person if it's not in the list
		int newParentIndex = getIndex(parent);
		if(newParentIndex == -1) {
			addPerson(parent);
			newParentIndex = getIndex(parent);
		} else {
			//check if the parent and the child are already related
			//TO IMPLEMENT check if related
		}
		
		//add the parent to the child connection list
		graph.get(childIndex).set(parentPositionIndex, newParentIndex);
		
		//add the child to the parent connection list
		graph.get(newParentIndex).add(childIndex);
		
		return true;
		
	}
	
	//needs to check if the childIndex is correct before use
	private boolean addParent(int childIndex, Person parent, int parentPositionIndex) {
		//checks if the parent is null or the unknownPerson
		if(parent != null && parent.equals(unknownPerson)) {
			System.out.println("The Parent that was provided is either null or is unknownParent");
			return false;
		}
		
		//finds the parent if he is there or creates a new person if it's not in the list
		int newParentIndex = getIndex(parent);
		if(newParentIndex == -1) {
			addPerson(parent);
			newParentIndex = getIndex(parent);
		} else {
			//check if the parent and the child are already related
			//TO IMPLEMENT check if related
		}
		
		//add the parent to the child connection list
		graph.get(childIndex).set(parentPositionIndex, newParentIndex);
		
		//add the child to the parent connection list
		graph.get(newParentIndex).add(childIndex);
		
		return true;
		
	}
	
	//get children-----------------
	//by person
	public List<Person> getChildren(Person person){
		int index = getIndex(person);
		
		if(index == -1) {
			System.out.println("the person: " + person.getName() + " is not in the graph");
			return new ArrayList<Person>();
		}
		
		List<Person> children = new ArrayList<Person>();
		ListIterator<Integer> it = graph.get(index).listIterator(childrenIndex);
		while(it.hasNext()) {
			children.add(getPerson(it.next()));
		}
		return children;
	}

	//addChildren----------------
	//add a child to one parent
	private boolean addChild(Person parent, Person child, int parentIndex) {
		int newParentIndex = getIndex(parent);
			
		if(newParentIndex == -1) {
			System.out.println("Parent: " + parent.getName() + " is not in the graph");
			return false;
		}
		
		//checks if the child is in the graph and if not it ads it to the graph
		int newChildIndex = getIndex(child);
		if(newChildIndex == -1) {
			addPerson(child);
			newChildIndex = getIndex(child);
		} else {
			//check if the parent and the child are already related
			//TO IMPLEMENT check if related
		}
		
		//ads the index of the child to the linked list of the parent
		graph.get(newParentIndex).add(newChildIndex);
		
		//ads the index of the parent to the linked list of the parent
		graph.get(newChildIndex).set(parentIndex, newParentIndex);
		return true;
	}
	
	public boolean addChildFather(Person father, Person child) {
		return addChild(father, child, fatherIndex);
	}
	
	public boolean addChildMother(Person mother, Person child) {
		return addChild(mother, child, motherIndex);
	}
	
	//add a child to both parent
	public boolean addChild(Person father, Person mother, Person child) {
		int newFatherIndex = getIndex(father);
		int newMotherIndex = getIndex(mother);
		
		//checks if the parents are in the graph
		if(newFatherIndex == -1) {
			System.out.println("Parent: " + father.getName() + " is not in the graph");
			return false;
		}
		
		if(newMotherIndex == -1) {
			System.out.println("Parent: " + mother.getName() + " is not in the graph");
			return false;
		}
		
		
		//checks if the child is in the graph and if not it ads it to the graph
		int newChildIndex = getIndex(child);
		if(newChildIndex == -1) {
			addPerson(child);
			newChildIndex = getIndex(child);
		} else {
			//check if the parents and the child are already related
			//TO IMPLEMENT check if related
		}
		
		
		//adds the index of the child to the connections of the parents
		graph.get(newFatherIndex).add(newChildIndex);
		graph.get(newMotherIndex).add(newChildIndex);
		
		//ads the index of the parents to the linked list of the parent
		graph.get(newChildIndex).set(fatherIndex, newFatherIndex);
		graph.get(newChildIndex).set(motherIndex, newMotherIndex);
		
		return true;
	}

	//add children from a list
	private boolean addChildren(Person parent, List<Person> children, int parentIndex) {
		int newParentIndex = getIndex(parent);
		
		//checks if the new parent exists
		if(newParentIndex == -1) {
			System.out.println("the parent: " + parent.getName() + " is not in the graph");
			return  false;
		}
		
		//iterates trough the children list and adds them one by one
		for(Person newChild : children) {
			//checks if each child
			
			int newChildIndex = getIndex(newChild);
			
			if(newChildIndex == -1) {
				addPerson(newChild);
				newChildIndex = getIndex(newChild);
			} else {
				//check if the parents and the child are already related
				//TO IMPLEMENT check if related
			}
			
			//ads the index of the child to the linked list of the parent
			graph.get(newParentIndex).add(newChildIndex);
			
			//ads the index of the parent to the linked list of the parent
			graph.get(newChildIndex).set(parentIndex, newParentIndex);
		}
		
		return true;
	}
	
	public boolean addChildrenFather(Person father, List<Person> children) {
		return addChildren(father, children, fatherIndex);
	}
	
	public boolean addChildrenMother(Person mother, List<Person> children) {
		return addChildren(mother, children, motherIndex);
	}
	
	public boolean addChildren(Person father, Person mother, List<Person> children) {
		int newFatherIndex = getIndex(father);
		int newMotherIndex = getIndex(mother);
		
		//checks if the mother and father exist
		if(newFatherIndex == -1) {
			System.out.println("Father: " + father.getName() + " is not in the graph");
		}
		
		if(newMotherIndex == -1) {
			System.out.println("Mother: " + mother.getName() + " is not in the graph");
		}
		
		//iterates trough the children List
		for(Person newChild : children) {
			int newChildIndex = getIndex(newChild);
			
			if(newChildIndex == -1) {
				addPerson(newChild);
				newChildIndex = getIndex(newChild);
			} else {
				//check if the parents and the child are already related
				//TO IMPLEMENT check if related
			}
			
			//ads the index of the child to the linked list of the parents
			graph.get(newFatherIndex).add(newChildIndex);
			graph.get(newMotherIndex).add(newChildIndex);
			
			//ads the index of the parent to the linked list of the parent
			graph.get(newChildIndex).set(fatherIndex, newFatherIndex);	
			graph.get(newChildIndex).set(motherIndex, newMotherIndex);	
		}
		
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
	public boolean searchAncester(Person start, Person ancestor) {
		Integer startIndex = getIndex(start);
		
		if(start == null) {
			return false;
		} else if(start.equals(ancestor)) {
			return true;
		} else {
			ListIterator<Integer> it = graph.get(startIndex).listIterator(fatherIndex);
			Integer currentParent = it.next();
			
			//searches on the fathers side if it finds it return true
			if(searchAncester(getPerson(currentParent), ancestor))
				return true;
			
			//searches on the mothers side if it finds it return true
			currentParent = it.next();
			if(searchAncester(getPerson(currentParent), ancestor)) {
				return true;
			}
			
			return false;
		}
	}
	
	public boolean searchDescendent(Person start, Person descendent) {
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
				
				if(searchDescendent(getPerson(childIndex), descendent)) {
					return true;
				}
			}
			
			return false;
		}
	}
	
	//working on
	//no loops can form because we are going only in one direction
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
