package base;
import io.qameta.allure.testng.AllureTestNg;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;

@Listeners({AllureTestNg.class})
public class Base {

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
    }
}
