package sme.account.exception;

public class GenericException extends RuntimeException {

    public GenericException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
    }

}
