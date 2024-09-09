package com.fulfilment.application.monolith.stores;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class StoreEndpointTest {
	final String path = "store";


	@Test
	public void testCrudProduct() {
		// List all, should have all 3 stores the database has initially:
		given().when().get(path).then().statusCode(200).body(containsString("TONSTAD"), containsString("KALLAX"),
				containsString("BESTÅ"));


		// find by id 1
		given().when().get(path + "/1").then().statusCode(200).body("name", equalTo("TONSTAD"));
		
		//find with invalid id
		given().when().get(path + "/100").then().statusCode(404).body("error", equalTo("Store with id of 100 does not exist."));

		
		Store store = new Store("New Store");
		store.id = 4l;
		// Create new product with id set will throw error
		given().contentType(ContentType.JSON).body(store).when().post(path).then().statusCode(422)
		.body("error", is("Id was invalidly set on request."));
		
		// Create new product success
		store.id = null;
		given().contentType(ContentType.JSON).body(store).when().post(path).then().statusCode(201)
				.body(containsString("New Store"));
		

		Store updatedStore = new Store();

		
		// Update and patch product name is null	
		given().contentType(ContentType.JSON).body(updatedStore).when().put(path + "/2").then().statusCode(422)
				.body("error", equalTo("Store Name was not set on request."));
		
		given().contentType(ContentType.JSON).body(updatedStore).when().patch(path + "/2").then().statusCode(422)
		.body("error", equalTo("Store Name was not set on request."));

		
		//update and patch product success
		updatedStore.name = "Updated";
		updatedStore.quantityProductsInStock = 100;
		given().contentType(ContentType.JSON).body(updatedStore).when().put(path + "/2").then().statusCode(200)
				.body("name", is("Updated")).body("quantityProductsInStock", is(100));
		
		given().contentType(ContentType.JSON).body(updatedStore).when().patch(path + "/2").then().statusCode(200)
		.body("name", is("Updated")).body("quantityProductsInStock", is(100));
		
		//update and patch  product invalid id
		given().contentType(ContentType.JSON).body(updatedStore).when().put(path + "/200").then().statusCode(404)
				.body("error", is("Store with id of 200 does not exist."));
		
		given().contentType(ContentType.JSON).body(updatedStore).when().patch(path + "/200").then().statusCode(404)
		.body("error", is("Store with id of 200 does not exist."));

		// Delete the TONSTAD:
		given().when().delete(path + "/1").then().statusCode(204);

		// List all, TONSTAD should be missing now:
		given().when().get(path).then().statusCode(200).body(not(containsString("TONSTAD")));
	}
}
