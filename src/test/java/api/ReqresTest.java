package api;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class ReqresTest {

    private final static String URL = "https://reqres.in/";

    @Test
    public void checkAvatarAndIdTest() {
        Specifications.installSpecification
                (Specifications.requestSpecification(URL),Specifications.responseSpecificationOK200());
        // ключевое слово given(). С него начинаются все запросы rest assured
        //when и then.
        List<UserData> users = given()
                .when() //когда
                .get("api/users?page=2")//куда обращаемся
                .then().log().all()
                .extract().body().jsonPath().getList("data",UserData.class); //извлекли data в класс
        users.forEach(x-> Assert.assertTrue(x.getAvatar().contains(x.getId().toString()))); //перебрать список и вызвать какой-то метод по очередности. X - счетчик экземпляров класса( какой-то элемент это)

        List <String> avatars = users.stream().map(UserData::getAvatar).collect(Collectors.toList());
        List <String> ids = users.stream().map(x->x.getId().toString()).collect(Collectors.toList()); //вызываем лямбду чтобы преобразовать int в id
        for(int i = 0; i<avatars.size(); i++) {
            Assert.assertTrue(avatars.get(i).contains(ids.get(i)));
        }
    }

    @Test
    public void getAllUsersTest() {
        Specifications.installSpecification
                (Specifications.requestSpecification(URL), Specifications.responseSpecificationUnique(200));
        given()
                .when()
                .get("api/users?page=2")
                .then().log().all();
    }

    @Test
    public void userNotFoundTest() {
        Specifications.installSpecification
                (Specifications.requestSpecification(URL), Specifications.responseSpecificationUnique(404));
                given()
                        .when()
                        .get("api/users/23")
                        .then().log().all();
    }

    @Test
    public void successRegistrationTest() {
        Specifications.installSpecification
                (Specifications.requestSpecification(URL),Specifications.responseSpecificationOK200());
        Integer id = 4;
        String token = "QpwL5tke4Pnpja7X4";
        Register user = new Register("eve.holt@reqres.in", "pistol");
        SuccessRegistration successRegistration = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().as(SuccessRegistration.class);
        Assert.assertNotNull(successRegistration.getId());
        Assert.assertNotNull(successRegistration.getToken());

        Assert.assertEquals(id, successRegistration.getId());
        Assert.assertEquals(token, successRegistration.getToken());
    }

    @Test
    public void unSuccessRegistrationTest() {
        Specifications.installSpecification
                (Specifications.requestSpecification(URL), Specifications.responseSpecificationError400());
        Register user = new Register("error", "");
        UnSuccessRegistration unSuccessRegistration = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().as(UnSuccessRegistration.class);
        Assert.assertEquals("Missing password", unSuccessRegistration.getError());
    }

    @Test
    public void sortedYearsTest() {
        Specifications.installSpecification
                (Specifications.requestSpecification(URL),Specifications.responseSpecificationOK200());
        List<ColorData> colors = given()
                .when()
                .get("api/unknown")
                .then().log().all()
                .extract().body().jsonPath().getList("data", ColorData.class);
        List<Integer> years = colors.stream().map(ColorData::getYear).collect(Collectors.toList());
        List<Integer> sortedYears = years.stream().sorted().collect(Collectors.toList());

        Assert.assertEquals(sortedYears, years);
    }

    @Test
    public void deleteUserTest() {
        Specifications.installSpecification
                (Specifications.requestSpecification(URL),Specifications.responseSpecificationUnique(204));
        given()
                .when()
                .delete("api/users/2")
                .then().log().all();
    }

    @Test
    public void timeTest() {
        Specifications.installSpecification
                (Specifications.requestSpecification(URL),Specifications.responseSpecificationUnique(200));
        UserTimeData user = new UserTimeData("morpheus", "zion resident");
        UserTimeResponse response = given()
                .when()
                .put("api/users/2")
                .then().log().all()
                .extract().as(UserTimeResponse.class);
        String regex = "(.{5})$";
        String currentTime = Clock.systemUTC().instant().toString().replaceAll(regex,"");
        Assert.assertEquals(currentTime, response.getUpdatedAt().replaceAll(regex,""));
    }

    @Test
    public void createUserTest() {
        Specifications.installSpecification
                (Specifications.requestSpecification(URL),Specifications.responseSpecificationUnique(201));
        UserTimeData user = new UserTimeData("morpheus", "leader");
            given()
                .body(user)
                .when()
                .post("api/users")
                .then().log().all();
    }

    @Test
    public void updateUserTest() {
        Specifications.installSpecification
                (Specifications.requestSpecification(URL),Specifications.responseSpecificationUnique(200));
        UserTimeData user = new UserTimeData("morpheus", "zion resident");
        UserTimeResponse response = given()
                .body(user)
                .when()
                .put("api/users/2")
                .then().log().all()
                .extract().as(UserTimeResponse.class);
    }

    @Test
    public void successfulLoginYest() {
        Specifications.installSpecification
                (Specifications.requestSpecification(URL),Specifications.responseSpecificationUnique(200));
        Register user = new Register("eve.holt@reqres.in","cityslicka");
        SuccessfulLogin login = given()
                .body(user)
                .when()
                .post("api/login")
                .then().log().all()
                .extract().as(SuccessfulLogin.class);
        Assert.assertNotNull(login.getToken());
    }
}
