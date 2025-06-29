package tests;

import api.ApiConfig;
import api.UserApi;
import data.UserData;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class CreateUserTest {
    Faker faker = new Faker();
    UserData userData;
    String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = ApiConfig.BASE_URL;
    }

    @Step("Create user")
    public void createUser() {
        Response response = UserApi.createUser(userData);
        JsonPath jsonPath = response.jsonPath();
        accessToken = jsonPath.getString("accessToken");
        response = UserApi.getUser(accessToken);
        response.then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(userData.getEmail()))
                .body("user.name", equalTo(userData.getName()));
    }

    @Step("Create user with existing data")
    public void createUserAlreadyExisting() {
        Response response = UserApi.createUser(userData);
        response.then()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Step("Create User Without required field")
    public void createUserWithoutRequiredField(){
        Response response = UserApi.createUser(userData);
        response.then()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }


    @Test
    @Description("Проверка создания пользователя с валидными данными")
    public void createUserValid(){
        userData = new UserData(faker.internet().emailAddress(), faker.internet().password(), faker.name().name());
        createUser();
    }

    @Test
    @Description("Создание пользователя который уже есть в системе")
    public void creatingUserAlreadyExisting(){
        userData = new UserData(faker.internet().emailAddress(), faker.internet().password(), faker.name().name());
        createUser();
        createUserAlreadyExisting();

    }

    @Test
    @Description("Создание пользователя с пустым обязательным полем email")
    public void creatingUserWithEmptyEmailField() {
        userData = new UserData(null, faker.internet().password(), faker.name().name());
        createUserWithoutRequiredField();
    }

    @Test
    @Description("Создание пользователя с пустым обязательным полем password")
    public void creatingUserWithEmptyPasswordField() {
        userData = new UserData(faker.internet().emailAddress(), null, faker.name().name());
        createUserWithoutRequiredField();
    }

    @Test
    @Description("Создание пользователя с пустым обязательным полем name")
    public void creatingUserWithEmptyNameField() {
        userData = new UserData(faker.internet().emailAddress(), faker.internet().password(), null);
        createUserWithoutRequiredField();
    }

    @After
    public void deleteUser(){
        if (accessToken != null){
            UserApi.deleteUser(accessToken);}
    }




}
