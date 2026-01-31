package tests;

import base.Base;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import static org.hamcrest.core.IsNull.notNullValue;

public class BookingE2E extends Base {
    int bookingId;
    String token;
    @BeforeClass
    public void generateToken(){
        token =
            given()
                    .contentType("application/json")
                    .body("""
                        {
                          "username": "admin",
                          "password": "password123"
                        }
                    """)
            .when()
                 .post("/auth")
            .then()
                    .statusCode(200)
                    .extract()
                    .path("token");
    }
    //Verify creating booking with valid data
    @Test
    public void createBooking(){

        String randomFirstName = "User"+ (int)Math.floor(Math.random() * 1000);
        String randomLastName = "Test" + (int)Math.floor(Math.random() * 1000);

        int price = (int)Math.floor(Math.random() *1000);

        Response response = given()
                .contentType("application/json")
                .body("""
                        {
                        "firstname" : "%s",
                            "lastname" : "%s",
                            "totalprice" : %d,
                            "depositpaid" : true,
                            "bookingdates" : {
                                "checkin" : "2018-01-01",
                                "checkout" : "2019-01-01"
                            },
                            "additionalneeds" : "Breakfast"
                        }
                        """.formatted(randomFirstName, randomLastName, price))
                .when()
                .post("/booking")
                .then()
                .log().all()
                .statusCode(200)
                .body("bookingid", notNullValue())
                .body("booking.firstname", equalTo(randomFirstName))
                .body("booking.lastname", equalTo(randomLastName))
                .body("booking.totalprice",  equalTo(price))
                .extract().response();

        bookingId = response.path("bookingid");
    }
    //Verify updating an existing booking with valid token
    @Test(dependsOnMethods = "createBooking")
    public void updateBooking() {
        given()
            .contentType("application/json")
            .cookie("token", token)
            .body("""
            {
              "firstname": "Updated",
              "lastname": "User",
              "totalprice" : 99,
              "depositpaid" : true,
              "bookingdates" : {
                    "checkin" : "2018-01-01",
                    "checkout" : "2019-01-01"
              },
              "additionalneeds" : "Breakfast"
            }
            """)
        .when()
            .put("/booking/" + bookingId)
        .then()
            .log().all()
            .statusCode(200)
            .body("firstname", equalTo("Updated"))
            .body("lastname", equalTo("User"));
    }

    //Verify updating Dates only
    @Test(dependsOnMethods = "createBooking")
    public void updateDatesOnly(){
        given()
                .contentType("application/json")
                .cookie("token", token)
                .body("""
                {
                  "bookingdates" : {
                        "checkin" : "2026-01-01",
                        "checkout" : "2026-05-04"
                  }
                }
                """)
        .when()
                .patch("/booking/" + bookingId)
        .then()
                .log().all()
                .statusCode(200)
                .body("bookingdates.checkin", equalTo("2026-01-01"))
                .body("bookingdates.checkout", equalTo("2026-05-04"));

    }
    //Verify deleting booking with token
    @Test(dependsOnMethods = "createBooking", priority = 1)
    public void deleteBooking() {
        given()
                .contentType("application/json")
                .cookie("token", token)
        .when()
                .delete("/booking/" + bookingId)
        .then()
                .log().all()
                .statusCode(201);
    }
}
