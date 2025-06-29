package tests;

import api.ApiConfig;
import api.LoginApi;
import api.UserApi;
import data.LoginData;
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

public class LoginTest {
    Faker faker = new Faker();
    UserData userData;
    LoginData loginData;
    String accessToken;


    @Before
    public void setUp() {
        RestAssured.baseURI = ApiConfig.BASE_URL;
        userData = new UserData(faker.internet().emailAddress(), faker.internet().password(), faker.name().name());
        createUser();

    }

    @Step("Create user")
    public void createUser() {
        Response response = UserApi.createUser(userData);
        response.then()
                .statusCode(HttpStatus.SC_OK);
        JsonPath jsonPath = response.jsonPath();
        accessToken = jsonPath.getString("accessToken");
    }

    @Step("Login with valid data")
    public void login() {
        Response response = LoginApi.login(loginData);
        response.then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(userData.getEmail()))
                .body("user.name", equalTo(userData.getName()));
    }

    @Step("Login without required field")
    public void loginWithoutRequiredField() {
        Response response = LoginApi.login(loginData);
        response.then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }


    @Test
    @Description("Логин с существующим логином и паролем")
    public void loginWithValidData(){
        loginData = new LoginData(userData.getEmail(), userData.getPassword());
        login();
    }

    @Test
    @Description("Логин с пустым полем email")
    public void loginWithEmptyEmail(){
        loginData = new LoginData(null, userData.getPassword());
        loginWithoutRequiredField();
    }

    @Test
    @Description("Логин с пустым полем email")
    public void loginWithEmptyPassword(){
        loginData = new LoginData(userData.getEmail(), null);
        loginWithoutRequiredField();
    }

    @After
    public void deleteUser(){
        if (accessToken != null){
            UserApi.deleteUser(accessToken);}
    }

}
