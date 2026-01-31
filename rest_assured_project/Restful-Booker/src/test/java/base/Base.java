package base;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;

public class Base {

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
    }
}
