package com.fulfilment.application.monolith.fullfilments;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Fullfilment {
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	@JoinColumn(name = "store_id", nullable = false)
	public Store store;

	@ManyToOne
	@JoinColumn(name = "product_id", nullable = false)
	public Product product;

	@ManyToOne
	@JoinColumn(name = "warehouse_id", nullable = false)
	public DbWarehouse dbWarehouse;
	
	
}
