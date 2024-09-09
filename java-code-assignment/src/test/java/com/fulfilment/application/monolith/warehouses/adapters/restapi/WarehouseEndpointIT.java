package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;

import org.junit.jupiter.api.Test;

import com.warehouse.api.beans.Warehouse;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;

@QuarkusIntegrationTest
public class WarehouseEndpointIT {

 final String path = "warehouse";
  @Test
  public void testSimpleListWarehouses() {

    

    // List all, should have all 3 products the database has initially:
    given()
        .when()
        .get(path)
        .then()
        .statusCode(200)
        .body(containsString("MWH.001"), containsString("MWH.012"), containsString("MWH.023"));
    
    //Get by id
    given()
    .when()
    .get(path + "/1")
    .then()
    .statusCode(200)
    .body("businessUnitCode" , equalTo("MWH.001")).body("location", equalTo("ZWOLLE-001"))
    .body("capacity", equalTo(100)).body("stock", equalTo(10));
    
  //Get by id invalid id
    given()
    .when()
    .get(path + "/100")
    .then()
    .statusCode(404)
    .body("error" , equalTo("Warehouse with id of 100 does not exist."));
    
  }

	@Test
	public void testCreatingNewWarehouse() {
		Warehouse warehouse = new Warehouse();
		warehouse.setBusinessUnitCode("MWH.013");
		warehouse.setCapacity(30);
		warehouse.setLocation("AMSTERDAM-001");
		warehouse.setStock(20);
		
		given().contentType(ContentType.JSON).body(warehouse).when().post(path).then().statusCode(200)
		.body("businessUnitCode" , equalTo("MWH.013")).body("location", equalTo("AMSTERDAM-001"))
	    .body("capacity", equalTo(30)).body("stock", equalTo(20));
	}
	
	@Test
	public void testReplacingWarehouse() {
		Warehouse warehouse = new Warehouse();
		warehouse.setBusinessUnitCode("MWH.012");
		warehouse.setCapacity(35);
		warehouse.setLocation("AMSTERDAM-001");
		warehouse.setStock(5);
		
		given().contentType(ContentType.JSON).body(warehouse).when().post(path + "/MWH.012/replacement").then().statusCode(200)
		.body("businessUnitCode" , equalTo("MWH.012")).body("location", equalTo("AMSTERDAM-001"))
	    .body("capacity", equalTo(35)).body("stock", equalTo(5));
	}

  @Test
  public void testSimpleCheckingArchivingWarehouses() {


    // List all, should have all 3 products the database has initially:
     given()
         .when()
        .get(path)
         .then()
         .statusCode(200)
         .body(
             containsString("MWH.001"),
             containsString("MWH.012"),
             containsString("MWH.023"),
             containsString("ZWOLLE-001"),
             containsString("AMSTERDAM-001"),
             containsString("TILBURG-001"));

     // Archive the ZWOLLE-001:
    given().when().delete(path + "/1").then().statusCode(204);
    // given().when().get(path + "/1").then().statusCode(200).body(containsString("ZWOLLE-001"));

     // List all, ZWOLLE-001 should be missing now:
		
		  given() .when() .get(path) .then() .statusCode(200) .body(
		  not(containsString("ZWOLLE-001")), containsString("AMSTERDAM-001"),
		  containsString("TILBURG-001"));
		 
  }
}
