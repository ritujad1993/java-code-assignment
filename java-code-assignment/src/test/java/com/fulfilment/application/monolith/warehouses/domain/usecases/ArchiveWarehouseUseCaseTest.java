package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ArchiveWarehouseUseCaseTest {
	@Mock
    WarehouseStore warehouseStore;

    @InjectMocks
    ArchiveWarehouseUseCase archiveWarehouseUseCase;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testArchive() {
        Warehouse warehouse = new Warehouse();
        LocalDateTime beforeArchiving = LocalDateTime.now().minusDays(1);
        warehouse.archivedAt = beforeArchiving;

        archiveWarehouseUseCase.archive(warehouse);

        verify(warehouseStore).update(argThat(updatedWarehouse -> 
            updatedWarehouse.archivedAt != null &&
            updatedWarehouse.archivedAt.isAfter(beforeArchiving)
        ));
    }
}
