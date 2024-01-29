package Exceptions;

public class GameStateException extends RuntimeException {
    public GameStateException(String errorMessage) {
        super(errorMessage);
    }
}
