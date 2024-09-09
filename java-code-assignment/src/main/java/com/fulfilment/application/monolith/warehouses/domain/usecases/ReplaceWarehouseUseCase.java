package com.fulfilment.application.monolith.warehouses.domain.usecases;

import java.time.LocalDateTime;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final CreateWarehouseOperation createWarehouseOperation;

  public ReplaceWarehouseUseCase(WarehouseStore warehouseStore, CreateWarehouseOperation createWarehouseOperation) {
    this.warehouseStore = warehouseStore;
	this.createWarehouseOperation = createWarehouseOperation;
  }

  @Override
  public void replace( String businessUnitCode, Warehouse newWarehouse) {
   
	  Warehouse existingWarehouse =  warehouseStore.findByBusinessUnitCode(businessUnitCode);
	  if(existingWarehouse == null) {
		  throw new WebApplicationException("Existing warehouse not found", 404);
	  }
	  
	 // Ensure the new warehouse's capacity can accommodate the stock from the warehouse being replaced.
	  if(newWarehouse.capacity < existingWarehouse.stock ) {
		  throw new WebApplicationException("New warehouse cannot accommodate the stock of the old warehouse",422);
	  }
	 // Confirm that the stock of the new warehouse matches the stock of the previous warehouse. 
	  if (newWarehouse.stock != existingWarehouse.stock) {
          throw new WebApplicationException("The stock of the new warehouse must match the stock of the old warehouse", 422);
      }
	  
	  //archive old warehouse
	  existingWarehouse.archivedAt = LocalDateTime.now();
	  warehouseStore.update(existingWarehouse);
	  
	  //create new warehouse
	  createWarehouseOperation.create(newWarehouse);
  }
}
