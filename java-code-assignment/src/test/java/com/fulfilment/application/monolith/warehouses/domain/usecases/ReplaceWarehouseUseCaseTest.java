package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.WebApplicationException;

@QuarkusTest
public class ReplaceWarehouseUseCaseTest {
	@Mock
	WarehouseStore warehouseStore;

	@Mock
	CreateWarehouseOperation createWarehouseOperation;
	
	@InjectMocks
	ReplaceWarehouseUseCase replaceWarehouseUseCase;
	
	private Warehouse warehouse;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		
		warehouse = new Warehouse();
		warehouse.businessUnitCode = "MWH.013";
		warehouse.location = "AMSTERDAM-001";
		warehouse.capacity = 50;
		warehouse.stock = 20;
	}
	
	@Test
	void testReplaceWarehouseSuccessfully() {
		Warehouse oldWarehouse =new Warehouse();
		oldWarehouse.businessUnitCode = "MWH.013";
		oldWarehouse.location = "AMSTERDAM-001";
		oldWarehouse.capacity = 30;
		oldWarehouse.stock = 20;
		
		when(warehouseStore.findByBusinessUnitCode("MWH.013")).thenReturn(oldWarehouse);
		
		replaceWarehouseUseCase.replace("MWH.013", warehouse);

        // Assert
        verify(warehouseStore).update(oldWarehouse);
        verify(createWarehouseOperation).create(warehouse);
	}
	
	@Test
	void testReplaceWarehouseNotFound() {
		
		when(warehouseStore.findByBusinessUnitCode("MWH.013")).thenReturn(null);
		
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> replaceWarehouseUseCase.replace("MWH.013", warehouse));
		assertEquals("Existing warehouse not found", exception.getMessage());
       
	}
	
	@Test
	void testReplaceWarehouseInsufficientCapacity() {
		Warehouse oldWarehouse =new Warehouse();
		oldWarehouse.businessUnitCode = "MWH.013";
		oldWarehouse.location = "AMSTERDAM-001";
		oldWarehouse.capacity = 70;
		oldWarehouse.stock = 70;
		
		when(warehouseStore.findByBusinessUnitCode("MWH.013")).thenReturn(oldWarehouse);
		
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> replaceWarehouseUseCase.replace("MWH.013", warehouse));
		assertEquals("New warehouse cannot accommodate the stock of the old warehouse", exception.getMessage());
	}
	
	@Test
	void testReplaceWarehouseStockMismatch() {
		Warehouse oldWarehouse =new Warehouse();
		oldWarehouse.businessUnitCode = "MWH.013";
		oldWarehouse.location = "AMSTERDAM-001";
		oldWarehouse.capacity = 30;
		oldWarehouse.stock = 30;
		
		when(warehouseStore.findByBusinessUnitCode("MWH.013")).thenReturn(oldWarehouse);
		
		WebApplicationException exception = assertThrows(WebApplicationException.class, () -> replaceWarehouseUseCase.replace("MWH.013", warehouse));
		assertEquals("The stock of the new warehouse must match the stock of the old warehouse", exception.getMessage());
	}
	
}
