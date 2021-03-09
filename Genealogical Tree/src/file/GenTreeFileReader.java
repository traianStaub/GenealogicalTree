package file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

import genTree.GenealogicTree;
import genTree.IGenTreeInput;
import genTree.Person;
import usefull.UtilityMethods;

public class GenTreeFileReader implements IGenTreeInput {

	private String valuesFilePath;
	private Person unknownPerson = new Person("Unknown", "Unknown", 0, "U", "U");
	
	private String personType = "personValues";
	private String connectionType = "connectionValues";
	
	public GenTreeFileReader(String filePath) {
		this.valuesFilePath = filePath;
	}
	
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

	@Override
	public boolean createPersons(GenealogicTree famillyTree) {
		Scanner sc = null;
		
		try {
			String valuePath = getFilePath(personType);
			sc = new Scanner(new FileReader(new File(valuePath)));
			
			famillyTree.addPerson(unknownPerson);
			
			while(sc.hasNext()) {
				String row = sc.nextLine();
				ArrayList<String> values = UtilityMethods.getValuesFromRow(row);
				
				String firstName = values.get(0);
				String lastName = values.get(1);
				int age = Integer.parseInt(values.get(2));
				String gender = values.get(3);
				String residency = values.get(4);
				
				Person newPerson = new Person(firstName, lastName, age,gender, residency);
				famillyTree.addPerson(newPerson);
				
				//for testing
				System.out.println(newPerson.getAllValues());
			}
			System.out.println("Succes to add the persons from the file");
			return true;
			
		} catch (TypeNotFoundException e){
			System.out.println(e.getMessage());
		} catch (FileNotFoundException e1) {
			System.out.println(e1.getMessage());
			System.out.println("Failed to add the persons from the file");
		} finally {
			if(sc != null) {
				sc.close();
			}
		}
		
		return false;
	}

	@Override
	public boolean createConnetions(GenealogicTree famillyTree) {
		Scanner sc = null;
		
		try {
			String valuePath = getFilePath(connectionType);
			sc = new Scanner(new FileReader(new File(valuePath)));
			
			while(sc.hasNext()) {
				String row = sc.nextLine();
				ArrayList<String> values = UtilityMethods.getValuesFromRow(row);
				
				//the person we are viewing
				int childIndex = Integer.parseInt(values.get(0));
				
				//the persons father_id
				int fatherIndex = Integer.parseInt(values.get(1));
				if(fatherIndex != 0) {
					famillyTree.connectFatherToChild(fatherIndex, childIndex);
				}
				
				//the persons mother_id
				int motherIndex = Integer.parseInt(values.get(2));
				if(motherIndex != 0) {
					famillyTree.connectMotherToChild(motherIndex, childIndex);
				}
				
				//for testing
				System.out.println("child: " + famillyTree.getPerson(childIndex) + ";father: " + famillyTree.getPerson(fatherIndex) + "; mother: " + famillyTree.getPerson(motherIndex));
			}
			
			System.out.println("Succes to add the connections from the file");
			return true;
			
		} catch (TypeNotFoundException e){
			System.out.println(e.getMessage());
		} catch (FileNotFoundException e1) {
			System.out.println(e1.getMessage());
			System.out.println("Failed to add the connections from the file");
		} finally {
			if(sc != null) {
				sc.close();
			}
		}
		
		return false;
	}
	
	//update a database from a GenTree
	@Override
	public boolean updateValues(GenealogicTree famillyTree) {
		String personInputFile = null;
		String connectionInputFile = null;
		
		try {
			personInputFile = getFilePath(personType);
		} catch(FileNotFoundException e) {
			System.out.println("personType issue in file");
			return false;
		}
		
		try {
			connectionInputFile = getFilePath(connectionType);
		} catch(FileNotFoundException e) {
			System.out.println("connectionType issue in file");
			return false;
		}
		
		if(personInputFile == null || connectionInputFile == null) {
			return false;
		}
		
		famillyTree.saveGenTreeTxt(personInputFile, connectionInputFile);
		return true;
	}
	
	//get the filePath from motherFile
	private String getFilePath(String type) throws FileNotFoundException{
		try (Scanner sc = new Scanner(new FileReader(new File(valuesFilePath)))){
			sc.nextLine();
			
			while(sc.hasNext()) {
				String row = sc.nextLine();
				ArrayList<String> values = UtilityMethods.getValuesFromRow(row);
				
				if(type.equals(values.get(0))) {
					return values.get(1);
				}
			}
			
			throw new TypeNotFoundException("ValueFileTypeNotFound");	
		} catch (FileNotFoundException e) {
			throw new TypeNotFoundException("ValueFileTypeNotFound");
		}

	}

}
