package com.fulfilment.application.monolith.warehouses.adapters.database;

import java.time.LocalDateTime;
import java.util.List;

import com.fulfilment.application.monolith.fullfilments.Fullfilment;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "warehouse")
@Cacheable
public class DbWarehouse {

@Id @GeneratedValue public Long id;

  public String businessUnitCode;

  public String location;

  public Integer capacity;

  public Integer stock;

  public LocalDateTime createdAt;

  public LocalDateTime archivedAt;
  
  @OneToMany(mappedBy = "dbWarehouse", cascade = CascadeType.ALL)
  private List<Fullfilment> fullfilments;
  
  public DbWarehouse() {}

  public Warehouse toWarehouse() {
    var warehouse = new Warehouse();
    warehouse.businessUnitCode = this.businessUnitCode;
    warehouse.location = this.location;
    warehouse.capacity = this.capacity;
    warehouse.stock = this.stock;
    warehouse.createdAt = this.createdAt;
    warehouse.archivedAt = this.archivedAt;
    return warehouse;
  }
}
