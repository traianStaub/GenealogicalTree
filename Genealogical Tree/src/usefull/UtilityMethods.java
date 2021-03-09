package usefull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class UtilityMethods {

	public static ArrayList<String> getValuesFromRow(String row) {

		StringBuilder sb = new StringBuilder();
		ArrayList<String> values = new ArrayList<>(2);
		
		//add the values from the file to the values arrayList;
		for(int i = 0, n = row.length(); i < n; i++) {
			char c = row.charAt(i);
			if(c == ' ') {
				continue;
			} else if(c == ','){
				values.add(sb.toString());
				sb.setLength(0);
			} else {
				sb.append(c);
			}
		}
		
		//adds the last values
		values.add(sb.toString());
		
		return values;
	}
	
	public static String getStringFromFile(String filePath) {
		try(Scanner input = new Scanner(new FileReader(new File(filePath)))){
			StringBuilder sb = new StringBuilder();
			
			while(input.hasNext()) {
				sb.append(input.nextLine());
			}

			return sb.toString();
		}catch(FileNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}
}
