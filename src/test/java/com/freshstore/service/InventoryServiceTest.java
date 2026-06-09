package com.freshstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.freshstore.entity.Inventory;
import com.freshstore.exception.ValidationException;
import com.freshstore.repository.InventoryRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepo;

    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryService(inventoryRepo);
    }

    @Test
    @DisplayName("listAll returns all inventory")
    void listAll_returnsAll() throws Exception {
        when(inventoryRepo.listAll()).thenReturn(List.of(createInventory("P001")));
        assertThat(inventoryService.listAll()).hasSize(1);
    }

    @Test
    @DisplayName("add valid inventory succeeds")
    void add_valid_succeeds() throws Exception {
        Inventory i = createInventory("P001");
        inventoryService.add(i);
        verify(inventoryRepo).add(i);
    }

    @Test
    @DisplayName("add with blank productId throws ValidationException")
    void add_blankProductId_throws() {
        Inventory i = createInventory("");
        assertThatThrownBy(() -> inventoryService.add(i))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("编号");
        verifyNoInteractions(inventoryRepo);
    }

    @Test
    @DisplayName("add with negative stock throws ValidationException")
    void add_negativeStock_throws() {
        Inventory i = createInventory("P001");
        i.setStock(-1);
        assertThatThrownBy(() -> inventoryService.add(i))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("库存数量");
        verifyNoInteractions(inventoryRepo);
    }

    @Test
    @DisplayName("add with negative maxStock throws ValidationException")
    void add_negativeMaxStock_throws() {
        Inventory i = createInventory("P001");
        i.setMaxStock(-1);
        assertThatThrownBy(() -> inventoryService.add(i))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("上限");
        verifyNoInteractions(inventoryRepo);
    }

    @Test
    @DisplayName("update delegates to repository")
    void update_delegates() throws Exception {
        Inventory i = createInventory("P001");
        inventoryService.update(i);
        verify(inventoryRepo).update(i);
    }

    @Test
    @DisplayName("delete delegates to repository")
    void delete_delegates() throws Exception {
        inventoryService.delete("P001");
        verify(inventoryRepo).delete("P001");
    }

    private static Inventory createInventory(String productId) {
        Inventory i = new Inventory();
        i.setProductId(productId);
        i.setStock(50);
        i.setMaxStock(100);
        i.setMinStock(20);
        i.setLocation("A区-01架");
        i.setLastIn("2024-01-01");
        return i;
    }
}
