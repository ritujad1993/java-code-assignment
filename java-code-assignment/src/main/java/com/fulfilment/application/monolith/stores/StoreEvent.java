package com.fulfilment.application.monolith.stores;

public class StoreEvent {
	
	private Store store;
	private String actionType;

	public StoreEvent(Store store, String actionType) {
		this.store = store;
		this.actionType = actionType;
	}

	public Store getStore() {
		return store;
	}

	public String getActionType() {
		return actionType;
	}
}
