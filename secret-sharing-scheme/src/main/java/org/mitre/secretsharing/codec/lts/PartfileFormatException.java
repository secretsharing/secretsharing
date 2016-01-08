package org.mitre.secretsharing.codec.lts;

public class PartfileFormatException extends PartfileException {
	private static final long serialVersionUID = 0;
	
	public PartfileFormatException() {
	}

	public PartfileFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	public PartfileFormatException(String message) {
		super(message);
	}

	public PartfileFormatException(Throwable cause) {
		super(cause);
	}

}
