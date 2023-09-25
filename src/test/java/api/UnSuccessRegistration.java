package api;

public class UnSuccessRegistration {
    public UnSuccessRegistration() {}

    private String error;

    public UnSuccessRegistration(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
