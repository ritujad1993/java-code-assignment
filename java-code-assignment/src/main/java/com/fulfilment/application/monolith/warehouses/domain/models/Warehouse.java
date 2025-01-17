package com.fulfilment.application.monolith.warehouses.domain.models;

import java.time.LocalDateTime;

public class Warehouse {

  // unique identifier
  public String businessUnitCode;

  public String location;

  public Integer capacity;

  public Integer stock;

  public LocalDateTime createdAt;

  public LocalDateTime archivedAt;

@Override
public String toString() {
	return "Warehouse [businessUnitCode=" + businessUnitCode + ", location=" + location + ", capacity=" + capacity
			+ ", stock=" + stock + ", createdAt=" + createdAt + ", archivedAt=" + archivedAt + "]";
}
}
