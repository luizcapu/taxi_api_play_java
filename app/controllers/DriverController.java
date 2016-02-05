package controllers;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import play.libs.Json;
import play.mvc.*;
import models.Driver;

public class DriverController extends Controller {
	
    private Result save(Integer id, JsonNode jsonBody, boolean updating) {
        Driver driver = Json.fromJson(jsonBody, Driver.class);
        driver.id = id;
        
        if (updating) {
            driver.update();        	
        } else {
            driver.insert();        	        	
        }
        return ok();
    }

	@BodyParser.Of(BodyParser.Json.class)
    public Result insert(Integer id) {
        return this.save(id, request().body().asJson(), false);
    }

	@BodyParser.Of(BodyParser.Json.class)
    public Result update(Integer id) {
        return this.save(id, request().body().asJson(), true);
    }
	
    public Result get(Long id) {
    	Driver driver = Driver.find.byId(id);
    	
    	if (driver == null) {
    		return status(900, "Driver not found");
    	} else {
    		return ok(Json.toJson(driver));
    	}    	    
    }

    public Result listAll() {
    	List<Driver> drivers = Driver.find.all();
    	return status(200, Json.toJson(drivers));
    }
    
}
