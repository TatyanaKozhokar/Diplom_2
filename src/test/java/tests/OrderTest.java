package tests;

import api.ApiConfig;
import api.OrderApi;
import api.UserApi;
import data.OrderData;
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
import static org.hamcrest.CoreMatchers.hasItem;


public class OrderTest {
    Faker faker = new Faker();
    UserData userData;
    String accessToken;
    int orderNumber;

    @Before
    public void setUp() {
        RestAssured.baseURI = ApiConfig.BASE_URL;
    }

    @Step("Создание пользователя")
    public void createUser(){
        Response response = UserApi.createUser(userData);
        JsonPath jsonPath = response.jsonPath();
        accessToken = jsonPath.getString("accessToken");
    }

    @Step("Создание заказа")
    public void createAnOrder() {
        String[] ingredients = {"61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f"};
        OrderData orderData = new OrderData(ingredients);
        Response response = OrderApi.createOrder(accessToken, orderData);
        response.then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true));
        JsonPath jsonPath = response.jsonPath();
        orderNumber = jsonPath.getInt("order.number");
    }

    @Step("Проверка что заказ создался")
    public void checkAnOrder(){
        Response response = OrderApi.checkThatNewOrderIsExisting();
        response.then()
                .statusCode(HttpStatus.SC_OK)
                .body("orders.number", hasItem(orderNumber));
    }

    @Step("Создание заказа неавторизованным")
    public void createAnOrderUnauth() {
        String[] ingredients = {"61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f"};
        OrderData orderData = new OrderData(ingredients);
        Response response = OrderApi.createOrderUnauthorized(orderData);
        response.then()
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Step("Создание заказа с несуществующим хэшем")
    public void createAnOrderInvalidData() {
        String[] ingredients = {"000000000000000000000000"};
        OrderData orderData = new OrderData(ingredients);
        Response response = OrderApi.createOrder(accessToken, orderData);
        response.then()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @Step("Создание заказа без ингредиентов")
    public void createAnOrderEmptyData() {
        OrderData orderData = new OrderData(null);
        Response response = OrderApi.createOrder(accessToken, orderData);
        response.then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }



    @Test
    @Description("Создание заказа авторизованным пользователем с валидными данными")
    public void createAnOrderWithValidData(){
        userData = new UserData(faker.internet().emailAddress(), faker.internet().password(), faker.name().name());
        createUser();
        createAnOrder();
        checkAnOrder();
    }

    @Test
    @Description("Создание заказа неавторизованным пользователем")
    public void createAnOrderUnauthorized(){
        createAnOrderUnauth();
    }

    @Test
    @Description("Создание заказа авторизованным пользователем с невалидными данными")
    public void createAnOrderWithInvalidData(){
        userData = new UserData(faker.internet().emailAddress(), faker.internet().password(), faker.name().name());
        createUser();
        createAnOrderInvalidData();
    }

    @Test
    @Description("Создание заказа авторизованным пользователем без ингредиентов")
    public void createAnOrderWithEmptyData(){
        userData = new UserData(faker.internet().emailAddress(), faker.internet().password(), faker.name().name());
        createUser();
        createAnOrderEmptyData();
    }

    @After
    public void deleteUser(){
        if (accessToken != null){
            UserApi.deleteUser(accessToken);}
    }


}
