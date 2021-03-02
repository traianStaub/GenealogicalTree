package db;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import genTree.GenealogicTree;
import genTree.Person;

public class DBReader {
	
	private Connection con;
	private Statement statement;
	
	public DBReader(String databaseName, String username, String password) {
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
	
	//fill a database from a file we are assuming the file written in a appropriate manner
	public void fillDB(String filePath, String table) {
		try {
			Scanner inputFile = new Scanner(new FileReader (new File(filePath)));
			
			
			//iterates trough the file
			while(inputFile.hasNext()) {
				List<String> values = new ArrayList<>();
				
				//the row of values to be added
				String row = inputFile.nextLine();
				
				//it builds from chars the value for the cell
				StringBuilder cellValue = new StringBuilder();
				
				//create a string arrayList of the values to be added
				for(int i = 0 ; i < row.length(); i++) {
					char c = row.charAt(i);
					
					if(c == ' ') {
						//adds the value as string to the values array and resets the stringBuilder
						values.add(cellValue.toString());
						cellValue.setLength(0);
					} else {
						cellValue.append(c);
					}
				}
				
				//one final add when it reaches the end of the row
				values.add(cellValue.toString());
				
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
			
		} catch(IOException e) {
			e.printStackTrace();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	//read from a database and add the to the graph
	public boolean getPersonValues(GenealogicTree famillyTree){
		String query = "SELECT person.person_id, person.first_name, person.last_name, person.age, gender.gender, county.county "
				+ "FROM person "
				+ "JOIN gender on person.gender = gender.gender_id "
				+ "JOIN county on person.residence = county.county_id "
				+ "ORDER BY person.person_id";
		
		try {
			ResultSet result = statement.executeQuery(query);
			
			while(result.next()){
				//check if the person id is not the next person
				int person_id = result.getInt(1);
				
				if(person_id > famillyTree.getGraphSize()) {
					System.out.println("index missing " + person_id);
					return false;
				} else if(person_id <famillyTree.getGraphSize()) {
					System.out.println("index is in the graph " + person_id);
					continue;
				}
				
				//get the values of the person
				String first_name = result.getString(2);
				String last_name = result.getString(3);
				int age = result.getInt(4);
				String gender = result.getString(5);
				String residence = result.getString(6);
				
				//adds the person in the family tree
				famillyTree.addPerson(new Person(first_name, last_name, age, gender, residence));
				System.out.println(person_id + ": " + first_name + " " + last_name + " in DB || " + famillyTree.getPerson(person_id).getName() + " in graph");
			}
			
			result.close();
		} catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	//create the connections that are in he database
	public boolean createConnetions(GenealogicTree famillyTree) {
		String query = "Select person_id, father_id, mother_id "
				+ "FROM person";
		
		try{
			
			ResultSet result = statement.executeQuery(query);
			
			while(result.next()) {
				
				//the person we are viewing
				int childIndex = result.getInt(1);
				//System.out.println(famillyTree.getPerson(childIndex) + "-----------");
				
				//the persons father_id
				int fatherIndex = result.getInt(2);
				if(fatherIndex != 0) {
					famillyTree.connectFatherToChild(fatherIndex, childIndex);
				}
				//System.out.println(famillyTree.getPerson(fatherIndex));
				
				//the persons mother_id
				int motherIndex = result.getInt(3);
				if(motherIndex != 0) {
					famillyTree.connectMotherToChild(motherIndex, childIndex);
				}
				//System.out.println(famillyTree.getPerson(motherIndex));
				//System.out.println("parents" + famillyTree.getParents(famillyTree.getPerson(childIndex)));
				//System.out.println("children " + famillyTree.getChildren(famillyTree.getPerson(childIndex)));
			}
		} catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
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
