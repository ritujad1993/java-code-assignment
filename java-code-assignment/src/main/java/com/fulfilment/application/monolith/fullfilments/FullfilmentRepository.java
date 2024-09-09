package com.fulfilment.application.monolith.fullfilments;

import java.util.List;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FullfilmentRepository implements PanacheRepository<Fullfilment>  {
	
	public List<Fullfilment> findPerProductPerStore(Store store, Product product){
		return this
				.find("store = ?1 and product = ?2", store, product).list();
	}
	
	public List<Fullfilment> findPerStore(Store store){
		return this.find("store", store).list();
	}
	
	public List<Fullfilment> findPerWarehouses(DbWarehouse dbWarehouse){
		return this.find("dbWarehouse", dbWarehouse).list();
	}
}
