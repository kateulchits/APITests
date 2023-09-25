package api;

public class SuccessfulLogin {
    public SuccessfulLogin() {}

    public String token;

    public SuccessfulLogin(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
