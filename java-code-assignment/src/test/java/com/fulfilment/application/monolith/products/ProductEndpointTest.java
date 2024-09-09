package com.fulfilment.application.monolith.products;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class ProductEndpointTest {

	final String path = "product";

	@Test
	public void testCrudProduct() {

		// List all, should have all 3 products the database has initially:
		given().when().get(path).then().statusCode(200).body(containsString("TONSTAD"), containsString("KALLAX"),
				containsString("BESTÅ"));

		// find by id 1
		given().when().get(path + "/1").then().statusCode(200).body("name", equalTo("TONSTAD"));

		// Create new product
		Product product2 = new Product("New Product");
		// Perform the request and validate the response
		// Check that the status code is 201 (Created)
		given().contentType(ContentType.JSON).body(product2).when().post(path).then().statusCode(201)
				.body(containsString("New Product"));

		Product updatedProduct = new Product();
		
		
		// Update product name is null	
		given().contentType(ContentType.JSON).body(updatedProduct).when().put(path + "/2").then().statusCode(422)
				.body("error", equalTo("Product Name was not set on request."));
		
		//update product success
		updatedProduct.name = "Updated";
		updatedProduct.stock = 100;
		given().contentType(ContentType.JSON).body(updatedProduct).when().put(path + "/2").then().statusCode(200)
				.body("name", is("Updated")).body("stock", is(100));
		
		//update product invalid id
		given().contentType(ContentType.JSON).body(updatedProduct).when().put(path + "/200").then().statusCode(404)
				.body("error", is("Product with id of 200 does not exist."));

		// Delete the TONSTAD:
		given().when().delete(path + "/1").then().statusCode(204);

		// List all, TONSTAD should be missing now:
		given().when().get(path).then().statusCode(200).body(not(containsString("TONSTAD")));
	}

}
