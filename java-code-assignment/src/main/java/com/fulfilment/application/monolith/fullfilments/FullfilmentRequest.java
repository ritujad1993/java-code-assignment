package com.fulfilment.application.monolith.fullfilments;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;


public class FullfilmentRequest {
	public Store store;
	
	public Product product;
	
	public Warehouse warehouse;
}
