package Exceptions;

public class DeckEmptyException extends RuntimeException {
    public DeckEmptyException(String errorMessage) {
        super(errorMessage);
    }
}


