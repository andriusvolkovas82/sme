package sme.account.exception;

public enum ErrorMessage {

    ACCOUNT_NOT_FOUND("Account not found"),
    ACCOUNT_IS_CLOSED("Account is closed"),
    INSUFFICIENT_FUNDS("Account doesn't have enough funds"),
    INVALID_CURRENCY("Invalid currency provided"),
    INVALID_AMOUNT("Invalid amount provided");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public GenericException getException() {
        return new GenericException(this);
    }

    public void throwException() {
        throw new GenericException(this);
    }

}
