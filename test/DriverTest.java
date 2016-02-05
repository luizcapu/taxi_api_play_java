import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.JsonNodeDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.Driver;

import org.junit.*;

import play.mvc.*;
import play.test.*;
import play.data.DynamicForm;
import play.data.validation.ValidationError;
import play.data.validation.Constraints.RequiredValidator;
import play.i18n.Lang;
import play.libs.F;
import play.libs.Json;
import play.libs.F.*;
import play.twirl.api.Content;
import static play.test.Helpers.*;
import static org.junit.Assert.*;
import play.mvc.Http.*;


/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class DriverTest {
	
	private static final String ADDRESS = "http://localhost";
	private static final Integer PORT = 3333;
	private static final String TEST_DB_NAME = "application/json";
	private static final String APP_JSON_TYPE = "application/json";
	private static final String EMPTY_STR = "";
	private static final String PAYLOAD = "[{\"id\":1,\"latitude\":-20.123,\"longitude\":-22.432,\"available\":true},"
			+ "{\"id\":2,\"latitude\":-30.123,\"longitude\":-32.432,\"available\":false},"
			+ "{\"id\":3,\"latitude\":-40.123,\"longitude\":-42.432,\"available\":true},"
			+ "{\"id\":4,\"latitude\":-50.123,\"longitude\":-52.432,\"available\":true}]";
	
	private static String getTestURL() {
		return DriverTest.ADDRESS + ":" + DriverTest.PORT;
	}

	private static String getTestURL(String route) {
		return DriverTest.getTestURL() + "/" + route;
	}
	
	private static void compareDrivers(Driver d1, Driver d2) {
		assertEquals(d1.id, d2.id);
		assertEquals(d1.latitude, d2.latitude);
		assertEquals(d1.longitude, d2.longitude);
		assertEquals(d1.available, d2.available);
	}
	
	private static JsonNode resultBodyAsJson(Result result) {
		return Json.parse(contentAsString(result));
	}
	
    @Test
    public void testEndPointsFlow() {
    	
        running(testServer(DriverTest.PORT, fakeApplication(inMemoryDatabase(DriverTest.TEST_DB_NAME))), new Runnable(){ 
        	public void run(){             	
        		RequestBuilder rb;
        		Result result;
        		
        		JsonNode jsonPayload = Json.parse(DriverTest.PAYLOAD);
        		if (jsonPayload.isArray()) {
        			for (JsonNode item : jsonPayload) {
                		Driver driver = new Driver();
                		driver.id = item.get("id").asInt();
                		driver.latitude = item.get("latitude").asDouble();
                		driver.longitude = item.get("longitude").asDouble();
                		driver.available = item.get("available").asBoolean();
                		
                		System.out.println("Testing driver " + driver.id);
                		
                		//-- BEGIN test insert 
                		rb = new RequestBuilder()
	                		.method(PUT)
	                    	.bodyJson(Json.toJson(driver))
	                    	.uri(DriverTest.getTestURL("driver/"+driver.id+"/status"));
                    	result = route(rb);
                    	
                    	assertEquals(result.status(), 200);        		
                    	assertEquals(contentAsString(result), DriverTest.EMPTY_STR);
                		//-- END test insert
                    	
                		// BEGIN compare after insert
                		rb = new RequestBuilder();
                    	rb.method(GET);
                    	rb.uri(DriverTest.getTestURL("driver/"+driver.id+"/status"));
                    	result = route(rb);
                    	            	
                    	assertEquals(result.status(), 200);
                    	DriverTest.compareDrivers(driver, Json.fromJson(DriverTest.resultBodyAsJson(result), Driver.class));
                		// END compare after insert

                		//-- BEGIN test update 
                    	driver.latitude += 10;
                    	driver.longitude += 5;
                    	driver.available = !driver.available;
                		rb = new RequestBuilder();
                    	rb.method(POST);
                    	rb.bodyJson(Json.toJson(driver));
                    	rb.uri(DriverTest.getTestURL("driver/"+driver.id+"/status"));
                    	result = route(rb);
                    	
                    	assertEquals(result.status(), 200);        		
                    	assertEquals(contentAsString(result), DriverTest.EMPTY_STR);
                		//-- END test update
                    	
                		// BEGIN compare after update
                		rb = new RequestBuilder();
                    	rb.method(GET);
                    	rb.uri(DriverTest.getTestURL("driver/"+driver.id+"/status"));
                    	result = route(rb);
                    	            	
                    	assertEquals(result.status(), 200);
                    	DriverTest.compareDrivers(driver, Json.fromJson(DriverTest.resultBodyAsJson(result), Driver.class));
                		// END compare after update                    	
        			}
        		}            	
            	
        		// BEGIN fetch drivers
        		System.out.println("Fetching all drivers");
        		rb = new RequestBuilder()
	            	.method(GET)
	            	.uri(DriverTest.getTestURL("drivers"));
            	result = route(rb);
            	
            	assertEquals(result.status(), 200);            	            	
            	assertEquals(result.contentType(), DriverTest.APP_JSON_TYPE);
            	assertEquals(DriverTest.resultBodyAsJson(result).size(), jsonPayload.size());
        		// END fetch drivers
        	}

        });
    }


}
