package br.com.compass.db.exception;

public class DbException extends RuntimeException {

    public DbException(String message, Exception exc) {
        super(message, exc);
    }
}
