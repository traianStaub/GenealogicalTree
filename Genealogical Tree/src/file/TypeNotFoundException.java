package file;

public class TypeNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TypeNotFoundException() {}
	
	public TypeNotFoundException(String message) {
		super(message);
	}
}
