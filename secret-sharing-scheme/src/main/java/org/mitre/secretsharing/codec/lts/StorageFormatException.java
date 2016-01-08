package org.mitre.secretsharing.codec.lts;

public class StorageFormatException extends StorageException {
	private static final long serialVersionUID = 0;
	
	public StorageFormatException() {
	}

	public StorageFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	public StorageFormatException(String message) {
		super(message);
	}

	public StorageFormatException(Throwable cause) {
		super(cause);
	}

}
