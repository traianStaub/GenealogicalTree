package db;

import genTree.GenealogicTree;
import genTree.Person;

public class WorkWithDB {

	public static void main(String[] args) {
		String db = "genTree";
		String username = "";
		String password = "";
		
		
		
		
		DataBase reader = new DataBase(db, username, password);
		GenealogicTree testing = new GenealogicTree();
		
		testing.fillGraph(reader);
		
		testing.saveGenTreeTxt("saveFiles/saveGenTree.txt", "saveFiles/saveConnection.txt");
		
		
		/*
		//Person anton = reader.getPerson(14);
		Person armand = reader.getPerson(11);
		Person strabuniAndrei = reader.getPerson(10);
		Person tataCezar = reader.getPerson(15);
		Person cezar = reader.getPerson(6);
		Person tataAndreea = reader.getPerson(19);
		Person andreea = reader.getPerson(2);
		Person claudiu = reader.getPerson(8);
		Person gabriel = reader.getPerson(3);
		Person mihai = reader.getPerson(4);

		reader.getPersonValues(testing);
		reader.createConnetions(testing);
		System.out.println("===========================");
		
		
		System.out.println(testing.getChildren(6));
		System.out.println(testing.getPerson(0));
		System.out.println(reader.getSize());
		
		*/
		/*
		System.out.println(anton.getName() + " furthers ancesters: " + testing.furthestAncester(anton));
		System.out.println(armand.getName() + " furthers ancesters: " + testing.furthestAncester(armand));
		System.out.println(strabuniAndrei.getName() + " furthers descendents: " + testing.furthestDescendent(strabuniAndrei));
		System.out.println(tataCezar.getName() + " furthers descendents: " + testing.furthestDescendent(tataCezar));
		System.out.println("-----------------");

		System.out.println(strabuniAndrei.getName() + " ancestors of " + cezar.getName() + " : " + testing.searchDescendent(cezar, strabuniAndrei));
		System.out.println(cezar.getName() + " descended of " + strabuniAndrei.getName() + " : " + testing.searchAncester(strabuniAndrei, cezar));

		System.out.println(cezar.getName() + " descended of " + strabuniAndrei.getName() + " : " + testing.searchAncesterBFS(strabuniAndrei, cezar));

		System.out.println(strabuniAndrei.getName() + " ancestors of " + anton.getName() + " : " + testing.searchDescendent(anton, strabuniAndrei));
		System.out.println(anton.getName() + " descended of " + strabuniAndrei.getName() + " : " + testing.searchAncester(tataAndreea, anton));

		System.out.println(anton.getName() + " descended of " + strabuniAndrei.getName() + " : " + testing.searchAncesterBFS(tataAndreea, anton));

		
		System.out.println(testing.isRelated(gabriel, mihai));
		*/
		reader.close();

	}

}
