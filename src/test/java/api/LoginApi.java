package api;

import data.LoginData;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class LoginApi {
    public static Response login(LoginData loginData) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(loginData)
                .when()
                .post(Endpoints.LOGIN);
    }
}
