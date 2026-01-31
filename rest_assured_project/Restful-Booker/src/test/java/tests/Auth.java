package tests;

import base.Base;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsNull.notNullValue;

public class Auth extends Base {

    //Verify creating an auth token with valid credentials
    @Test
    public void createTokenTest() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                                "username": "admin",
                                "password": "password123"
                        }
                        """
                )
        .when()
                .post("/auth")
        .then()
                .log().all()
                .statusCode(200)
                .body("token", notNullValue());
    }
}
