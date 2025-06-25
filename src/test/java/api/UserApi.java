package api;

import data.UserData;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserApi {
    public static Response createUser(UserData userData) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(userData)
                .when()
                .post(Endpoints.REGISTER);
    }

    public static Response getUser(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .get(Endpoints.GET_USER);
    }


}
