package testing;

import db.DataBase;
import file.GenTreeFileReader;
import genTree.GenealogicTree;

public class Main {

	public static void main(String[] args) {
		//Db specs
		String db = "testing";
		String username = "newuser";
		String password = "password";
		
		//fileReader specs
		String fileReaderValuesFile = "inputFiles/FileReader_input_files.txt";
		
		//create Db and file Reader
		DataBase reader = new DataBase(db, username, password);
		reader.clearDB();
		reader.populateDB("inputFiles/DB_table_values.txt");
		
		GenTreeFileReader fileReader = new GenTreeFileReader(fileReaderValuesFile);
		
		GenealogicTree testing = new GenealogicTree();
		
		testing.populateGenTree(reader);
		System.out.println(testing.updateValues(fileReader));
		
		
		reader.close();
	}

}
