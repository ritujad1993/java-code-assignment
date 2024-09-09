package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.WebApplicationException;

@QuarkusTest
public class CreateWarehouseUseCaseTest {
	@Mock
	WarehouseStore warehouseStore;

	@Mock
	LocationResolver locationResolver;

	@InjectMocks
	CreateWarehouseUseCase createWarehouseUseCase;

	private Warehouse warehouse;
	private Location location;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		warehouse = new Warehouse();
		warehouse.businessUnitCode = "MWH.013";
		warehouse.location = "AMSTERDAM-001";
		warehouse.capacity = 30;
		warehouse.stock = 20;

		location = new Location("AMSTERDAM-001", 5, 100);
	}

	@Test
	void testCreateWarehouseSuccessfully() {

		when(warehouseStore.findByBusinessUnitCode("MWH.013")).thenReturn(null);
		when(locationResolver.resolveByIdentifier("AMSTERDAM-001")).thenReturn(location);
		when(warehouseStore.findByLocation("AMSTERDAM-001")).thenReturn(Collections.emptyList());

		createWarehouseUseCase.create(warehouse);

		verify(warehouseStore).create(warehouse);
	}

	@Test
	void testCreateWarehouseWithExistingBusinessUnitCode() {

		when(warehouseStore.findByBusinessUnitCode("MWH.013")).thenReturn(new Warehouse());

		assertThrows(WebApplicationException.class, () -> createWarehouseUseCase.create(warehouse));
	}

	@Test
	void testCreateWarehouseWithInvalidLocation() {
		warehouse.location = "INVALID.LOC";

		when(locationResolver.resolveByIdentifier("INVALID.LOC")).thenReturn(null);

		assertThrows(WebApplicationException.class, () -> createWarehouseUseCase.create(warehouse));
	}

	@Test
	void testCreateWarehouseExceedsMaxWarehousesAtLocation() { // Arrange

		location.maxNumberOfWarehouses = 1; // //
		when(locationResolver.resolveByIdentifier("AMSTERDAM-001")).thenReturn(location);
		when(warehouseStore.findByLocation("AMSTERDAM-001")).thenReturn(Collections.singletonList(new Warehouse()));

		assertThrows(WebApplicationException.class, () -> createWarehouseUseCase.create(warehouse));
	}

	@Test
	void testCreateWarehouseExceedsMaxCapacityAtLocation() { // Arrange
		warehouse.capacity = 70;

		when(locationResolver.resolveByIdentifier("AMSTERDAM-001")).thenReturn(location);
		when(warehouseStore.findByLocation("AMSTERDAM-001")).thenReturn(Collections.singletonList(new Warehouse() {
			{
				capacity = 50;
			}
		}));

		assertThrows(WebApplicationException.class, () -> createWarehouseUseCase.create(warehouse));
	}

	@Test
	void testCreateWarehouseStockExceedsCapacity() {
		Warehouse warehouse = new Warehouse();
		warehouse.capacity = 50;
		warehouse.stock = 60;

		assertThrows(WebApplicationException.class, () -> createWarehouseUseCase.create(warehouse));
	}
}
