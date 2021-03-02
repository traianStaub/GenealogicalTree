package db;

import genTree.GenealogicTree;
import genTree.Person;

public class WorkWithDB {

	public static void main(String[] args) {
		String db = "gentree";
		String username = "user";
		String password = "password";
		
		Person anton = new Person("anton" , "popescu", 2, "M", "AB");
		Person armand = new Person ("armand", "petrica", 10, "M", "CS");
		
		DBReader reader = new DBReader(db, username, password);
		GenealogicTree testing = new GenealogicTree();
		
		reader.getPersonValues(testing);
		System.out.println(testing.getGraphSize());
		reader.createConnetions(testing);
		System.out.println("===================================");
		reader.createConnetions(testing);
		
		System.out.println(testing.furthestAncester(anton));
		System.out.println(testing.furthestAncester(armand));
		//first i need to populate the nodes in the graph by adding them to the genealogical tree;
		
		reader.close();

	}

}
