package com.fulfilment.application.monolith.fullfilments;

import java.util.List;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;

@Path("fullfilments")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class FullfilmentResource {

	@Inject
	FullfilmentRepository fullfilmentRepository;

	@Inject 
	WarehouseRepository warehouseRepository;
	
	@GET
	public List<Fullfilment> getAllFullfilments() {
		return fullfilmentRepository.listAll();
	}

	@POST
	@Transactional
	public Fullfilment createFullfilment(@NotNull FullfilmentRequest fullfilmentRequest) {
		Store store = fullfilmentRequest.store;
	    Product product = fullfilmentRequest.product;
	    Warehouse warehouse = fullfilmentRequest.warehouse;
	
		// Each `Product` can be fulfDbWarehouseilled by a maximum of 2 different `Warehouses` per
		// `Store`
		List<Fullfilment> warehhousesPerProductPerStore = fullfilmentRepository.findPerProductPerStore(store, product);
		long countProductPerStore = warehhousesPerProductPerStore.stream().map(a -> a.dbWarehouse).distinct().count();
				
		if (countProductPerStore >= 2) {
			throw new WebApplicationException("Product can only be fulfilled by 2 different warehouses per store.",
					422);
		}

		// Each `Store` can be fulfilled by a maximum of 3 different `Warehouses`
		List<Fullfilment> warehousesForStore = fullfilmentRepository.findPerStore(store);
		long warehouseCount = warehousesForStore.stream().map(a -> a.dbWarehouse).distinct().count();
		if (warehouseCount >= 3) {
			throw new WebApplicationException("Store can only be fulfilled by 3 different warehouses.", 422);
		}

		// Each `Warehouse` can store maximally 5 types of `Products`
		 List<DbWarehouse> dbWarehouseList = warehouseRepository.find("businessUnitCode", warehouse.businessUnitCode).list();
		 DbWarehouse dbWarehouse = dbWarehouseList.stream().filter(dbw -> dbw.archivedAt == null).findFirst().orElse(null);
		 if (dbWarehouse == null) {
			    throw new WebApplicationException("Warehouse not found with ID: " + warehouse.businessUnitCode, 422);
			}
		 List<Fullfilment> productsPerWarehouse = fullfilmentRepository.findPerWarehouses(dbWarehouse);
		long countProductsInWarehouse = productsPerWarehouse.stream().map(a -> a.product).distinct().count();
		if (countProductsInWarehouse >= 5) {
			throw new WebApplicationException("Warehouse can only store 5 different products.", 422);
		}

		// Create new fulfillment if constraints are met
		Fullfilment fullfilment = new Fullfilment();
		fullfilment.store = store;
		fullfilment.product = product;
		fullfilment.dbWarehouse = dbWarehouse;
		
		fullfilmentRepository.persist(fullfilment);

		
		return fullfilment;
	}
}
