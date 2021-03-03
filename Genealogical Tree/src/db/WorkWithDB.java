package db;

import genTree.GenealogicTree;
import genTree.Person;

public class WorkWithDB {

	public static void main(String[] args) {
		String db = "gentree";
		String username = "user";
		String password = "password";
		
		
		
		
		DBReader reader = new DBReader(db, username, password);
		GenealogicTree testing = new GenealogicTree();
		
		Person anton = reader.getPerson(14);
		Person armand = reader.getPerson(11);
		Person strabuniAndrei = reader.getPerson(10);
		Person tataCezar = reader.getPerson(15);
		Person cezar = reader.getPerson(6);
		
		reader.getPersonValues(testing);
		reader.createConnetions(testing);
		
		System.out.println(anton.getName() + " furthers ancesters: " + testing.furthestAncester(anton));
		System.out.println(armand.getName() + " furthers ancesters: " + testing.furthestAncester(armand));
		System.out.println(strabuniAndrei.getName() + " furthers descendents: " + testing.furthestDescendent(strabuniAndrei));
		System.out.println(tataCezar.getName() + " furthers descendents: " + testing.furthestDescendent(tataCezar));
		System.out.println("-----------------");
		System.out.println(strabuniAndrei.getName() + " ascenster of " + cezar.getName() + " : " + testing.searchDescendent(cezar, strabuniAndrei));
		System.out.println(cezar.getName() + " descended of " + strabuniAndrei.getName() + " : " + testing.searchAncester(strabuniAndrei, cezar));
		System.out.println(strabuniAndrei.getName() + " ascenster of " + anton.getName() + " : " + testing.searchDescendent(anton, strabuniAndrei));
		System.out.println(anton.getName() + " descended of " + strabuniAndrei.getName() + " : " + testing.searchAncester(strabuniAndrei, anton));
		
	
		reader.close();

	}

}
