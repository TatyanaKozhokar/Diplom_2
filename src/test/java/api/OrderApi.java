package api;

import data.OrderData;
import io.restassured.response.Response;


import static io.restassured.RestAssured.given;


public class OrderApi {
    public static Response createOrder(String accessToken, OrderData orderData) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(orderData)
                .when()
                .post(Endpoints.ORDER);
    }

    public static Response checkThatNewOrderIsExisting() {
        return given()
                .when()
                .get(Endpoints.ALL_ORDERS)
                .then()
                .extract()
                .response();
    }

    public static Response createOrderUnauthorized(OrderData orderData) {
        return given()
                .header("Content-type", "application/json")
                .body(orderData)
                .when()
                .post(Endpoints.ORDER);
    }
}
