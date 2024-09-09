package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import java.util.List;

import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.WebApplicationException;

@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {

  @Inject private WarehouseRepository warehouseRepository;
  
  @Inject private CreateWarehouseOperation createWarehouseOperation;
  
  @Inject private ArchiveWarehouseOperation archiveWarehouseOperation;
  
  @Inject private ReplaceWarehouseOperation replaceWarehouseOperation;

  @Override
  public List<Warehouse> listAllWarehousesUnits() {
    return warehouseRepository.getAll().stream().map(this::toWarehouseResponse).toList();
  }

  @Override
  @Transactional
  public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {
	  
	  createWarehouseOperation.create(fromWarehouseRequest(data));
	  return data;
    
  }

  @Override
  public Warehouse getAWarehouseUnitByID(String id) {
	  
	  DbWarehouse dbWarehouse = warehouseRepository.findById(Long.parseLong(id));
	  if(dbWarehouse != null && dbWarehouse.archivedAt == null) {
		 return  toWarehouseResponse(dbWarehouse.toWarehouse());
	  }else {
		  throw new WebApplicationException("Warehouse with id of " + id + " does not exist.", 404);
	  }

//    throw new UnsupportedOperationException("Unimplemented method 'getAWarehouseUnitByID'");
  }

  @Override
  @Transactional
  public void archiveAWarehouseUnitByID(String id) {
	  DbWarehouse dbWarehouse = warehouseRepository.findById(Long.parseLong(id));
	  if(dbWarehouse != null && dbWarehouse.archivedAt == null) {
		  archiveWarehouseOperation.archive(dbWarehouse.toWarehouse());
		  }else {
			  throw new WebApplicationException("Warehouse unit not found", 404);
		  }
  }

  @Override
  @Transactional
  public Warehouse replaceTheCurrentActiveWarehouse(
      String businessUnitCode, @NotNull Warehouse data) {
    
	  replaceWarehouseOperation.replace(businessUnitCode, fromWarehouseRequest(data));
	  return data;
  }

  private Warehouse toWarehouseResponse(
      com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse) {
    var response = new Warehouse();
    response.setBusinessUnitCode(warehouse.businessUnitCode);
    response.setLocation(warehouse.location);
    response.setCapacity(warehouse.capacity);
    response.setStock(warehouse.stock);

    return response;
  }
  
  private com.fulfilment.application.monolith.warehouses.domain.models.Warehouse fromWarehouseRequest(Warehouse warehouse){
	  var request = new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
	  request.businessUnitCode = warehouse.getBusinessUnitCode();
	  request.location = warehouse.getLocation();
	  request.capacity = warehouse.getCapacity();
	  request.stock = warehouse.getStock();

	  return request;
  }
}
