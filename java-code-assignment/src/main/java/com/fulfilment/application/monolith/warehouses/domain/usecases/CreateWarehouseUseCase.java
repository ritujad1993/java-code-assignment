package com.fulfilment.application.monolith.warehouses.domain.usecases;

import java.time.LocalDateTime;
import java.util.List;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

	private final WarehouseStore warehouseStore;
	private final LocationResolver locationResolver;

	public CreateWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
		this.warehouseStore = warehouseStore;
		this.locationResolver = locationResolver;
	}

	@Override
	public void create(Warehouse warehouse) {
		// business code should not exist
		if (warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode) != null) {
			throw new WebApplicationException(
					"Business Unit Code " + warehouse.businessUnitCode + " already exist. Please enter another value", 422);
		}
		
		// location must be valid must be of existing location
		Location location = locationResolver.resolveByIdentifier(warehouse.location);
		if (location == null) {
			throw new WebApplicationException("Location is invalid and is not among the existing location.", 422);
		}
		
		List<Warehouse> existingWarehouses = warehouseStore.findByLocation(warehouse.location);
		
		// Check if a new warehouse can be created at the specified location or if the
		// maximum number of warehouses has already been reached.	
		if(existingWarehouses.size() >= location.maxNumberOfWarehouses) {
			throw new WebApplicationException("Exceeds the maximum number of warehouses that can be created at this location.", 422);
		}
		
		// Validate the warehouse capacity, ensuring it does not exceed the maximum
		// capacity associated with the location and that it can handle the stock
		// informed.
		int existingCapacity = existingWarehouses.stream().mapToInt(whouse -> whouse.capacity).sum();
		if(existingCapacity + warehouse.capacity > location.maxCapacity ) {
			throw new WebApplicationException("Exceeds the maximum capacity that can be stored at this location.", 422);
		}
		
		if (warehouse.stock > warehouse.capacity) {
            throw new WebApplicationException("Warehouse stock exceeds its capacity", 422);
        }
		// if all went well, create the warehouse
		warehouse.createdAt = LocalDateTime.now();
		
		warehouseStore.create(warehouse);
	}
}
