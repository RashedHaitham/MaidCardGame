package Exceptions;

public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
