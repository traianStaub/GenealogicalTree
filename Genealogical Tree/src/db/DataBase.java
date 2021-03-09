package db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

import genTree.GenealogicTree;
import genTree.IGenTreeInput;
import genTree.Person;
import usefull.UtilityMethods;

public class DataBase implements IGenTreeInput{
	
	private Connection con;
	private Statement statement;
	private Person unknownPerson = new Person("Unknown", "Unknown", 0, "U", "U");
	private String inputFileParent = null;
	
	private int nextPersonID = 0;
	
	//constructor
	//the DataBase class needs to have the respective databaseCreated
	public DataBase(String databaseName, String username, String password) {
		String url = "jdbc:mysql://127.0.0.1:3306/" + databaseName;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			con = DriverManager.getConnection(url, username, password);
			statement = con.createStatement();
			
		}catch(SQLException e) {
			System.out.print("the connection or the statement could not be initialized");
			e.printStackTrace();
			
			if(con != null) {
				System.out.println("creatign the statement was succesful but the statement threw a exception. trying to close the connection that was oppend");
				try {
					con.close();
				} catch(SQLException e2) {
					System.out.println("closing the connection threw a eception");
					e2.printStackTrace();
				}
			}	
		}
	}
	
	//fill a DB from files
	public boolean populateDB(String inputFilesPathFile) {
		inputFileParent = inputFilesPathFile;
		String personTable = "person";
		
		try (Scanner sc = new Scanner(new FileReader(new File(inputFilesPathFile)))){
			
			sc.nextLine();
			
			while(sc.hasNext()) {
				
				String row = sc.nextLine();
				ArrayList<String> values = UtilityMethods.getValuesFromRow(row);
				
				String table = values.get(0);
				String idCollumnName = values.get(1);
				String valuesFilePath = values.get(2);
				String createTableFilePath = values.get(3);
				
				//try to create the tables if they are not already created
				//if the table is already created it will just skypp this code
				try {
					createTable(createTableFilePath);
					System.out.println("created the Table " + table);
				} catch(SQLException e) {
					e.getMessage();
					System.out.println("The table already exists");
				}
				
				ResultSet result = statement.executeQuery("Select max(" + idCollumnName + ")  From " + table + ";");
				result.next();
				//if the db has no values it add the first person unknonwnPerson
				//and the fills from the file
				
				if(table.equals(personTable)) {
					if(result.getString(1) == null) {
						addPersonToDB(unknownPerson);
						fillPersonFromFile(valuesFilePath);
					} else {
						//if the db has values it changes the nextPersonID variable to store the next id
						nextPersonID = result.getInt(1) + 1;
					}
				} else {
					if(result.getString(1) == null) {
						fillTableFromFile(valuesFilePath, table);
					}
				}
				
				result.close();
			}
			
			return true;		
		} catch(SQLException e) {
			e.printStackTrace();
			return false;
		} catch(FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	//create a table for the DB
	private void createTable(String createTableFilePath) throws SQLException{
		try {
			String query = UtilityMethods.getStringFromFile(createTableFilePath);
			statement.executeUpdate(query);
		} catch(SQLException e){
			throw new SQLException();
		}
		
	}
	
	//will populate the Db with the files from this folder
	public boolean populateDB() {
		return populateDB("inputFiles/DB_table_values.txt");
	}
	//fill a database from a file we are assuming the file written in a appropriate manner
	private void fillTableFromFile(String filePath, String table) {
		
		try (Scanner inputFile = new Scanner(new FileReader (new File(filePath)));) {
				
			//iterates trough the file
			while(inputFile.hasNext()) {
				//the row of values to be added
				String row = inputFile.nextLine();
				
				ArrayList<String> values = UtilityMethods.getValuesFromRow(row);
				
				//create the query to add the values
				StringBuilder query = new StringBuilder();
				query.append("INSERT INTO " + table + " VALUES(");
				for(int i = 0, n = values.size(); i < n; i++) {
					query.append(values.get(i));
					
					if(i == n - 1) {
						query.append(");");
					} else {
						query.append(", ");
					}
				}
				
				System.out.println(query);
				statement.executeUpdate(query.toString());
				//the query stringBuilder and the values array will reset themselves because they are created in the loop
			}
			
			System.out.println("Succes when importint fromfile " + filePath + " to table: " + table);
			
		} catch(IOException e) {
			System.out.println("FAIL when importint fromfile " + filePath + " to table: " + table + " ISSUE with the file");
			//e.printStackTrace();
		} catch(SQLException e) {
			System.out.println("FAIL when importint fromfile " + filePath + " to table: " + table + " ISSUE with the DataBase");
			//e.printStackTrace();
		}
	}
	
	//fill the person table from the file it is a different method because we insert the person_id
	private void fillPersonFromFile(String filePath) {
		try(Scanner inputFile = new Scanner(new FileReader(new File(filePath)))) {
			
			while(inputFile.hasNext()) {
				//the row of values
				String row = inputFile.nextLine();
				
				ArrayList<String> values = UtilityMethods.getValuesFromRow(row);
				
				
				String first_name = values.get(0);
				String last_name = values.get(1);
				int age = Integer.parseInt(values.get(2));
				String gender = values.get(3);
				String residence = values.get(4);
				Person newPerson = new Person(first_name, last_name, age, gender, residence);
				
				addPersonToDB(newPerson);
			}
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	//adds a person to the DB - the person_id is nextPersonID
	public boolean addPersonToDB(Person newPerson) {
		try {
			String prepared = "INSERT INTO person VALUES( ?, ?, ?, ?, ?, ?)";
			PreparedStatement prepState = con.prepareStatement(prepared);
			prepState.setInt(1, nextPersonID);
			prepState.setString(2, newPerson.getFirstName());
			prepState.setString(3, newPerson.getLastName());
			prepState.setInt(4, newPerson.getAge());
			
			ResultSet gender = statement.executeQuery("SELECT gender_id FROM gender WHERE gender = '" + newPerson.getGender() + "';");
			gender.next();
			int genderId = gender.getInt(1);
			gender.close();
			
			//add the gender to the DB
			prepState.setInt(5, genderId);
			
			ResultSet county = statement.executeQuery("SELECT county_id FROM county WHERE county = '" + newPerson.getResidence() + "';");
			county.next();
			int countyId = county.getInt(1);
			county.close();
			
			//add the county id to the DB
			prepState.setInt(6, countyId);
			
			prepState.execute();
			prepState.close();
			
			nextPersonID++;
			
			//testing
			System.out.println("INSERT INTO person: " + newPerson.getAllValues());
			return true;
		}catch(SQLException e) {
			System.out.println("Could not ad the person " + newPerson.getAllValues());
			e.printStackTrace();
			return false;
		}
	}
	
	//methods to populate the GenTree----------
	//get data from DB to Graph
	@Override
	public boolean populateGenTree(GenealogicTree famillyTree) {
		if(createPersons(famillyTree) == false) {
			return false;
		}
		
		if(createConnetions(famillyTree) == false) {
			return false;
		}
		
		return true;
	}
	
	//read from a database and add the to the graph
	@Override
	public boolean createPersons(GenealogicTree famillyTree){
		String query = "SELECT person.person_id, person.first_name, person.last_name, person.age, gender.gender, county.county "
				+ "FROM person "
				+ "JOIN gender on person.gender = gender.gender_id "
				+ "JOIN county on person.residence = county.county_id "
				+ "WHERE person.person_id >= " + famillyTree.getGraphSize() + " "
				+ "ORDER BY person.person_id;";
		
		try {
			ResultSet result = statement.executeQuery(query);
			
			while(result.next()){
				//check if the person id is not the next person
				int person_id = result.getInt(1);

				if(person_id > famillyTree.getGraphSize()) {
					System.out.println("index missing " + famillyTree.getGraphSize());
					famillyTree.clear();
					return false;
				}
				
				//get the values of the person
				String first_name = result.getString(2);
				String last_name = result.getString(3);
				int age = result.getInt(4);
				String gender = result.getString(5);
				String residence = result.getString(6);
				
				//adds the person in the family tree
				famillyTree.addPerson(new Person(first_name, last_name, age, gender, residence));
				
				//for testing
				System.out.println(person_id + ": " + first_name + " " + last_name + " in DB || " + famillyTree.getPerson(person_id).getName() + " in graph");
			}
			
			result.close();
			System.out.println("Succes when adding the persons from the DB tot the graph");
			return true;
			
		} catch(SQLException e) {
			System.out.println("FAIL when adding the persons from the DB tot the graph");
			//e.printStackTrace();
			return false;
		}
	}
	
	//create the connections that are in he database
	@Override
	public boolean createConnetions(GenealogicTree famillyTree) {
		String query = "Select person_id, father_id, mother_id "
				+ "FROM connections";
		
		try{
			ResultSet result = statement.executeQuery(query);
			
			while(result.next()) {
				
				//the person we are viewing
				int childIndex = result.getInt(1);
				
				//the persons father_id
				int fatherIndex = result.getInt(2);
				if(fatherIndex != 0) {
					famillyTree.connectFatherToChild(fatherIndex, childIndex);
				}
				
				//the persons mother_id
				int motherIndex = result.getInt(3);
				if(motherIndex != 0) {
					famillyTree.connectMotherToChild(motherIndex, childIndex);
				}
				
				//for testing
				System.out.println("child: " + famillyTree.getPerson(childIndex) + ";father: " + famillyTree.getPerson(fatherIndex) + "; mother: " + famillyTree.getPerson(motherIndex));
			}
			System.out.println("Succes when creating the connections from the DB");
			return true;
		} catch(SQLException e) {
			System.out.println("FAIL when creating the connections from the DB");
			//e.printStackTrace();
			return false;
		}

	}
	
	//TO-DO
	//update a database from a GenTree
	@Override
	public boolean updateValues(GenealogicTree famillyTree) {
		String personInputFile = getInputFile("person");
		String connectionInputFile = getInputFile("connections");
		
		//if there was an error when findin them retun null
		if(personInputFile == null) {
			return false;
		}
		
		if(connectionInputFile == null) {
			return false;
		}
		
		//if there was not inputParentFile create those files
		if(personInputFile.equals("NOFILE")) {
			personInputFile = "inputFiles/person_values.txt";
		}
		
		if(connectionInputFile.equals("NOFILE")) {
			connectionInputFile = "inputFiles/person_values.txt";
		}
		
		//overide the values with the values from the GenTree
		famillyTree.saveGenTreeTxt(personInputFile, connectionInputFile);
		
		//clear the values from the DB
		clearTable("person");
		clearTable("connections");
		
		//update the DB with these new values
		fillTableFromFile(personInputFile, "person");
		fillTableFromFile(connectionInputFile, "connections");
		
		return false;
	}
	
	//getters----------
	//get number of people in the DB
	public int getSize() {
		return nextPersonID;
	}
	
	//get Person from DB
	public Person getPerson(int index) {
		
		String query = "SELECT person.first_name, person.last_name, person.age, gender.gender, county.county "
				+ "FROM person "
				+ "JOIN gender on person.gender = gender.gender_id "
				+ "JOIN county on person.residence = county.county_id "
				+ "WHERE person.person_id = " + index + ";";
		
		String maxIdQuery = "SELECT MAX(person_id) FROM person;";
		
		try {
			//get the maximum id from the DB
			ResultSet maxIdResult = statement.executeQuery(maxIdQuery);
			maxIdResult.next();
			int maxId = maxIdResult.getInt(1);
			maxIdResult.close();
			
			if(index > maxId) {
				System.out.println("the id provided is bigger then the max id in the db " + maxId);
				return unknownPerson;
			}
			
			
			ResultSet result = statement.executeQuery(query);
			result.next();
			
			//get the values of the person
			String first_name = result.getString(1);
			String last_name = result.getString(2);
			int age = result.getInt(3);
			String gender = result.getString(4);
			String residence = result.getString(5);
			
			result.close();
			
			return new Person(first_name, last_name, age, gender, residence);
			
		} catch(SQLException e) {
			e.printStackTrace();
			return unknownPerson;
		}
	}

	//get input files from valuesFile
	private String getInputFile(String table) {
		
		if(inputFileParent == null) {
			return "NOFILE";
		}
		
		try(Scanner sc = new Scanner(new FileReader (new File(inputFileParent)))){
			//skyp over the instruction line
			sc.nextLine();
			
			//iterates trough the file
			while(sc.hasNext()) {
				
				//the row of values to be added
				String row = sc.nextLine();
				
				ArrayList<String> values = UtilityMethods.getValuesFromRow(row);
				if(table.equals(values.get(0))) {
					return values.get(2);
				}
			}
			return null;
			
		} catch(FileNotFoundException e) {
			System.out.println("FAIL when trying to open and read the " + inputFileParent);
			return null;
		}
	}
	
	//clear-------------
	//clear DB
	public void clearDB() {
		try {
			String deleteConnection = "DELETE FROM connections";
			String deletePerson = "DELETE FROM person";
			String deleteCounty = "DELETE FROM county";
			String deleteGender = "DELETE FROM gender";
			
			statement.execute(deleteConnection);
			statement.execute(deletePerson);
			statement.execute(deleteCounty);
			statement.execute(deleteGender);
		} catch(SQLException e) {
			
		}
	}
	
	//clear just one table
	public void clearTable(String table) {
		try {
			String deleteTable = "DELETE FROM " + table;
			statement.execute(deleteTable);
			
		} catch (SQLException e) {
			
		}
	}
	
	//closes the connection
	public void close() {
		try {
			statement.close();
			con.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
}
