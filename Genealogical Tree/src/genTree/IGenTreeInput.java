package genTree;

public interface IGenTreeInput {
	
	boolean populateGenTree(GenealogicTree famillyTree);
	
	boolean createPersons(GenealogicTree famillyTree);
	
	boolean createConnetions(GenealogicTree famillyTree);
	
	boolean updateValues(GenealogicTree famillyTree);
	
	//TODO
	//creates a new inputSource from scratch
	//boolean createInputSource(GenealogicTree famillyTree, String inputSourceName);
}
