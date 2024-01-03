package apiTests;

import specifications.*;
import io.qameta.allure.Description;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;

public class ReqresTest {

    private final static String URL = "https://reqres.in/";

    @Test
    @Description("Check that avatar link has id of user")
    public void checkAvatarAndIdTest() {
        Specifications.installSpecification
                (Specifications.requestSpecification(URL),Specifications.responseSpecificationUnique(200));
        List<UserData> users = given()
                .when()
                .get("specifications/users?page=2")
                .then().log().headers()
                .extract().body().jsonPath().getList("data",UserData.class);

        users.forEach(x-> Assertions.assertThat(x.getAvatar()).contains(x.getId().toString()));
    }

    @Test
    public void getAllUsersTest() {
        Specifications.installSpecification
                (Specifications.requestSpecification(URL), Specifications.responseSpecificationUnique(200));
        List<UserData> allUsers= given()
                .when()
                .get("specifications/users?page=2")
                .then().log().all()
                        .extract().body().jsonPath().getList("data", UserData.class);

        Assertions.assertThat(allUsers.size()).isEqualTo(6);
    }

    @Test
    public void userNotFoundTest() {
        Specifications.installSpecification
                (Specifications.requestSpecification(URL), Specifications.responseSpecificationUnique(404));
                given()
                        .when()
                        .get("specifications/users/23")
                        .then().log().all();
    }

    @Test
    @Description("Registration")
    public void successRegistrationTest() {
        Specifications.installSpecification
                (Specifications.requestSpecification(URL),Specifications.responseSpecificationUnique(200));
        Integer id = 4;
        Register user = new Register("eve.holt@reqres.in", "pistol");
        SuccessRegistration successRegistration = given()
                .body(user)
                .when()
                .post("specifications/register")
                .then().log().all()
                .extract().as(SuccessRegistration.class);

        Assertions.assertThat(successRegistration.getToken()).isNotNull();
        Assertions.assertThat(successRegistration.getId()).isEqualTo(id);
    }

    @Test
    public void unSuccessRegistrationTest() {
        Specifications.installSpecification
                (Specifications.requestSpecification(URL), Specifications.responseSpecificationUnique(400));
        Register user = new Register("error", "");
        UnSuccessRegistration unSuccessRegistration = given()
                .body(user)
                .when()
                .post("specifications/register")
                .then().log().all()
                .extract().as(UnSuccessRegistration.class);

        Assertions.assertThat(unSuccessRegistration.getError()).isEqualTo("Missing password");
    }

    @Test
    @Description("Sort users by year")
    public void sortedYearsTest() {
        Specifications.installSpecification
                (Specifications.requestSpecification(URL),Specifications.responseSpecificationUnique(200));
        List<String> years = given()
                .when()
                .get("specifications/unknown")
                .then().log().all()
                .extract().body().jsonPath().getList("years", String.class);
        Assertions.assertThat(years).isSorted();

        //OR
        List<ColorData> data = given()
                .when()
                .get("specifications/unknown")
                .then().log().all()
                .extract().body().jsonPath().getList("data", ColorData.class);

        List<ColorData> sortedYears = new ArrayList<>(data);
        sortedYears.sort(Comparator.comparing(ColorData::getYear));
        Assertions.assertThat(data).containsExactlyElementsOf(sortedYears);
    }

    @Test
    public void deleteUserTest() {
        Specifications.installSpecification
                (Specifications.requestSpecification(URL),Specifications.responseSpecificationUnique(204));
        given()
                .when()
                .delete("specifications/users/2")
                .then().log().all()
                .body(is(emptyString()));
    }

    @Test
    @Description("Check time of update")
    public void timeTest() {
        Specifications.installSpecification
                (Specifications.requestSpecification(URL),Specifications.responseSpecificationUnique(200));
        UserTimeData user = new UserTimeData("morpheus", "zion resident");
        UserTimeResponse response = given()
                .body(user)
                .when()
                .put("specifications/users/2")
                .then().log().all()
                .extract().as(UserTimeResponse.class);

        String regex = "(.{5})$";
        String currentTime = Clock.systemUTC().instant().toString();
        currentTime = currentTime.replaceAll(regex,"");

        Assertions.assertThat(currentTime).contains(response.getUpdatedAt().replaceAll(regex,""));
    }

    @Test
    public void createUserTest() {
        Specifications.installSpecification
                (Specifications.requestSpecification(URL),Specifications.responseSpecificationUnique(201));
        UserTimeData user = new UserTimeData("morpheus", "leader");
            given()
                .body(user)
                .when()
                .post("specifications/users")
                .then().log().all();
    }

    @Test
    public void successfulLoginTest() {
        Specifications.installSpecification
                (Specifications.requestSpecification(URL),Specifications.responseSpecificationUnique(200));
        Register user = new Register("eve.holt@reqres.in","cityslicka");
        SuccessfulLogin login = given()
                .body(user)
                .when()
                .post("specifications/login")
                .then().log().all()
                .extract().as(SuccessfulLogin.class);

       Assertions.assertThat(login.getToken()).isNotNull();
    }
}
