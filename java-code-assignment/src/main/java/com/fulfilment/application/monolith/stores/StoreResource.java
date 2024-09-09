package com.fulfilment.application.monolith.stores;

import java.util.List;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@Path("store")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class StoreResource {

	@Inject
	LegacyStoreManagerGateway legacyStoreManagerGateway;

	@Inject
	Event<StoreEvent> storeEvent;

	@GET
	public List<Store> get() {
		return Store.listAll(Sort.by("name"));
	}

	@GET
	@Path("{id}")
	public Store getSingle(Long id) {
		Store entity = Store.findById(id);
		if (entity == null) {
			throw new WebApplicationException("Store with id of " + id + " does not exist.", 404);
		}
		return entity;
	}

	@POST
	@Transactional
	public Response create(Store store) {
		if (store.id != null) {
			throw new WebApplicationException("Id was invalidly set on request.", 422);
		}

		store.persist();

		storeEvent.fire(new StoreEvent(store, "Created"));

		return Response.ok(store).status(201).build();
	}

	@PUT
	@Path("{id}")
	@Transactional
	public Store update(Long id, Store updatedStore) {
		if (updatedStore.name == null) {
			throw new WebApplicationException("Store Name was not set on request.", 422);
		}

		Store entity = Store.findById(id);

		if (entity == null) {
			throw new WebApplicationException("Store with id of " + id + " does not exist.", 404);
		}

		entity.name = updatedStore.name;
		entity.quantityProductsInStock = updatedStore.quantityProductsInStock;

		storeEvent.fire(new StoreEvent(entity, "Updated"));

		return entity;
	}

	@PATCH
	@Path("{id}")
	@Transactional
	public Store patch(Long id, Store updatedStore) {
		if (updatedStore.name == null) {
			throw new WebApplicationException("Store Name was not set on request.", 422);
		}

		Store entity = Store.findById(id);

		if (entity == null) {
			throw new WebApplicationException("Store with id of " + id + " does not exist.", 404);
		}

		if (entity.name != null) {
			entity.name = updatedStore.name;
		}

		if (entity.quantityProductsInStock != 0) {
			entity.quantityProductsInStock = updatedStore.quantityProductsInStock;
		}

		storeEvent.fire(new StoreEvent(entity, "Updated"));
		
		return entity;
	}

	@DELETE
	@Path("{id}")
	@Transactional
	public Response delete(Long id) {
		Store entity = Store.findById(id);
		if (entity == null) {
			throw new WebApplicationException("Store with id of " + id + " does not exist.", 404);
		}
		entity.delete();
		return Response.status(204).build();
	}

	void onStoreChange(@Observes(during = TransactionPhase.AFTER_SUCCESS) StoreEvent storeEvent) {
		// This method is called only after the transaction has been successfully
		// committed
		if (storeEvent.getActionType().equalsIgnoreCase("Created")) {
			legacyStoreManagerGateway.createStoreOnLegacySystem(storeEvent.getStore());
		} else if (storeEvent.getActionType().equalsIgnoreCase("Updated")) {
			legacyStoreManagerGateway.updateStoreOnLegacySystem(storeEvent.getStore());
		}
	}

	
}
