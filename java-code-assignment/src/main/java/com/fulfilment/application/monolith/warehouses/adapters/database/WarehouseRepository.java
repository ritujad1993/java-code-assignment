package com.fulfilment.application.monolith.warehouses.adapters.database;

import java.util.List;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

	@Override
	public List<Warehouse> getAll() {
		return this.listAll().stream().filter(dbw -> dbw.archivedAt == null).map(DbWarehouse::toWarehouse).toList();
	}

	@Override
	public void create(Warehouse warehouse) {
		DbWarehouse dbWarehouse = new DbWarehouse();
		dbWarehouse.businessUnitCode = warehouse.businessUnitCode;
		dbWarehouse.location = warehouse.location;
		dbWarehouse.capacity = warehouse.capacity;
		dbWarehouse.stock = warehouse.stock;
		dbWarehouse.createdAt = warehouse.createdAt;
		dbWarehouse.archivedAt = warehouse.archivedAt;
		this.persist(dbWarehouse);
	}

	@Override
	public void update(Warehouse warehouse) {
		List<DbWarehouse> dbWarehouses = this.find("businessUnitCode", warehouse.businessUnitCode).list();
		DbWarehouse dbWarehouse = dbWarehouses.stream().filter(dbw -> dbw.archivedAt == null).findFirst().orElse(null);
		if (dbWarehouse != null) {
			dbWarehouse.location = warehouse.location;
			dbWarehouse.capacity = warehouse.capacity;
			dbWarehouse.stock = warehouse.stock;
			dbWarehouse.createdAt = warehouse.createdAt;
			dbWarehouse.archivedAt = warehouse.archivedAt;
			this.persist(dbWarehouse);
		} else {
			throw new WebApplicationException("Warehouse does not exist.", 404);
		}

	}

	@Override
	public void remove(Warehouse warehouse) {
		DbWarehouse dbWarehouse = this.find("businessUnitCode", warehouse.businessUnitCode).firstResult();

		if (dbWarehouse != null) {
			this.delete(dbWarehouse);
		} else {
			throw new WebApplicationException("Warehouse does not exist.", 404);
		}
	}

	@Override
	public Warehouse findByBusinessUnitCode(String buCode) {
		List<DbWarehouse> dbWarehouses = this.find("businessUnitCode", buCode).list();
		DbWarehouse dbWarehouse = dbWarehouses.stream().filter(dbw -> dbw.archivedAt == null).findFirst().orElse(null);
		
		return dbWarehouse != null ? dbWarehouse.toWarehouse() : null;

	}

	@Override
	public List<Warehouse> findByLocation(String location) {
		List<DbWarehouse> dbWarehouses = this.find("location", location).list();
		return dbWarehouses.stream().filter(dbw -> dbw.archivedAt == null).map(DbWarehouse::toWarehouse).toList();

	}
}
