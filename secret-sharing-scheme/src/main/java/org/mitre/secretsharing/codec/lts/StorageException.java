package org.mitre.secretsharing.codec.lts;

public class StorageException extends Exception {
	private static final long serialVersionUID = 0;
	
	public StorageException() {
		super();
	}

	public StorageException(String message, Throwable cause) {
		super(message, cause);
	}

	public StorageException(String message) {
		super(message);
	}

	public StorageException(Throwable cause) {
		super(cause);
	}

}
