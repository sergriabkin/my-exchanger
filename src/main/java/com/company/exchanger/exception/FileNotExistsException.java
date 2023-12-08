package com.company.exchanger.exception;

public class FileNotExistsException extends RuntimeException {

    public static final String FILE_NOT_FOUND = "File not found: ";

    public FileNotExistsException(String name) {
        super( FILE_NOT_FOUND + name);
    }

    public FileNotExistsException(String name, Throwable cause) {
        super(FILE_NOT_FOUND + name, cause);
    }
}
