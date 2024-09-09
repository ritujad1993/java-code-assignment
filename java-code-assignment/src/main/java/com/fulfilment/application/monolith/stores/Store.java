package com.fulfilment.application.monolith.stores;

import java.util.List;

import com.fulfilment.application.monolith.fullfilments.Fullfilment;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

@Entity
@Cacheable
public class Store extends PanacheEntity {

  @Column(length = 40, unique = true)
  public String name;

  public int quantityProductsInStock;
  
  @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
  private List<Fullfilment> fullfilments;

  public Store() {}

  public Store(String name) {
    this.name = name;
  }
}
