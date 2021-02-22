
public class Testing {

	public static void main(String[] args) {
		
		
		GenealogicTree tree = new GenealogicTree();
		populateTree(tree);
		Person andrei = new Person("Andrei");
		System.out.println(tree.furthestAncester(new Person("Daria")));
		System.out.println(tree.furthestDescendent(andrei));



	}
	
	public static void populateTree(GenealogicTree tree) {
		Person andrei = new Person("Andrei");
		Person Andreea = new Person("Andreea");
		Person Alex = new Person("Alex");
		Person AndreiJr = new Person("AndreiJr");
		Person Vali = new Person("Vali");
		Person Gabi = new Person("Gabi");
		Person Edgar = new Person("Edgar");
		Person GabiJR = new Person("GabiJR");
		Person Tudor = new Person("Tudor");
		Person Mihai = new Person("Mihai");
		Person Mihaiela = new Person("Mihaiela");
		Person Ana = new Person("Ana");
		Person Anton = new Person("Anton");
		Person Florica = new Person("Florica");
		Person Angela = new Person("Angela");
		Person Dragos = new Person("Dragos");
		Person Radu = new Person("Radu");
		Person Daria = new Person("Daria");
		Person Camelia = new Person("Camelia");
		Person Sorana = new Person("Sorana");
		Person Magda = new Person("Magda");
		Person Luiza = new Person("Luiza");
		Person Maria = new Person("Maria");
		Person Tavi = new Person("Tavi");
		Person Lucian = new Person("Lucian");
		Person Denis = new Person("Denis");
		Person Miron = new Person("Miron");
		
		tree.addPerson(Alex, andrei, Andreea);
		tree.addPerson(AndreiJr, andrei, Andreea);
		tree.addPerson(Vali, andrei, Andreea);
		
		tree.addPerson(Mihai, Vali, Gabi);
		tree.addPerson(GabiJR, Vali, Gabi);
		tree.addPerson(Tudor, Edgar, Gabi);
		tree.addPerson(Ana, Mihai, Mihaiela);
		tree.addPerson(Anton, Mihai, Mihaiela);

		tree.addPerson(Tavi, Anton, Maria);
		tree.addPerson(Lucian, Anton, Maria);
		tree.addPerson(Denis, Anton, Maria);
		tree.addPerson(Miron, Anton, Maria);
		

		tree.addPerson(Sorana, Tavi, Camelia);
		tree.addPerson(Luiza, Miron, Magda);
		

		tree.addPerson(Radu, Dragos, Sorana);
		tree.addPerson(Daria, Dragos, Sorana);
		
		
		tree.addPerson(Angela, Dragos, Florica);
		
		
	}

}
