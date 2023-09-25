package api;

public class UserTimeResponse extends UserTimeData{
    public UserTimeResponse(){}

    private String updatedAt;

    public UserTimeResponse(String name, String job, String updateAt) {
        super(name, job);
        this.updatedAt = updateAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
