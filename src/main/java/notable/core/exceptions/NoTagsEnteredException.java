package notable.core.exceptions;

public class NoTagsEnteredException extends IllegalArgumentException {

	
	private String message;

	public NoTagsEnteredException() {

	}

	public NoTagsEnteredException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
