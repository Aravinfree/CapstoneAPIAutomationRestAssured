package APIProject.RestAssured;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import static org.hamcrest.Matchers.*;


public class CurrencyApiTest {

    @DataProvider(name = "currencyTestData")
    public Object[][] getCsvData() throws IOException {
        // Read data from CSV file
        String filePath = "D:\\Selenium\\WorkSpace\\CapstoneAPI_RestAssured\\currency,expected_status_code.csv";
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        
        lines.remove(0);
        // Prepare data for data provider
        Object[][] data = new Object[lines.size()][5];
        for (int i = 0; i < lines.size(); i++) {
            String[] values = lines.get(i).split(",");
            data[i][0] = values[0]; // currency
            data[i][1] = Integer.parseInt(values[1].trim()); // expected status code
            data[i][2] = values[2]; //expected name
            data[i][3] = values[3]; //expected symbol
            data[i][4] = values[4]; //expected region
        }
        return data;
    }

    @Test(dataProvider = "currencyTestData")
    public void testCurrencyApi(String currency, int expectedStatusCode, String name, String symbol, String region) {
        // Base URL for the API
        RestAssured.baseURI = "https://restcountries.com/v3.1";

        // Make the API request
        Response response = RestAssured.given()
                .pathParam("currency", currency)
                .when()
                .get("/currency/{currency}")
                .then()
                .extract().response();

        // Verify HTTP status code
        response.then().statusCode(expectedStatusCode);

        // If it's a positive case.
        if (expectedStatusCode == 200) {
            // Example: Verify that the response contains a specific field
   
            response.then()
                    .body("currencies.'"+currency+"'.name", hasItem(name))
                    .body("currencies.'"+currency+"'.symbol", hasItem(symbol))
                    .body("name", not(emptyOrNullString())) // Check if the 'name' field is not empty
                    .body("region", hasItem(region));
        }
        
        //If it's a negative case.
        if(expectedStatusCode == 404) {
        	response.then()
            .body("status", equalTo(expectedStatusCode))
            .body("message", equalTo(name));
        }
    }
}
