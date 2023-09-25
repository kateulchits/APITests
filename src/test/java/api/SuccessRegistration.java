package api;

public class SuccessRegistration {
    public SuccessRegistration() {}

    private Integer id;
    private String token;

    public SuccessRegistration(Integer id, String token) {
        this.id = id;
        this.token = token;
    }

    public Integer getId() {
        return id;
    }

    public String getToken() {
        return token;
    }
}
