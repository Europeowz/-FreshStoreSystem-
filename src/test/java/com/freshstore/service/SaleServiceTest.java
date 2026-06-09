package com.freshstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.freshstore.entity.Inventory;
import com.freshstore.entity.Product;
import com.freshstore.entity.Sale;
import com.freshstore.exception.ValidationException;
import com.freshstore.repository.SaleRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock
    private SaleRepository saleRepo;
    @Mock
    private ProductService productService;
    @Mock
    private InventoryService inventoryService;

    private SaleService saleService;

    @BeforeEach
    void setUp() {
        saleService = new SaleService(saleRepo, productService, inventoryService);
    }

    @Test
    @DisplayName("listAll returns all sales")
    void listAll_returnsAll() throws Exception {
        when(saleRepo.listAll()).thenReturn(List.of(createSale("S001")));
        assertThat(saleService.listAll()).hasSize(1);
    }

    @Test
    @DisplayName("searchByProduct delegates to repository")
    void searchByProduct_delegates() throws Exception {
        when(saleRepo.searchByProduct("番茄")).thenReturn(List.of(createSale("S001")));
        assertThat(saleService.searchByProduct("番茄")).hasSize(1);
        verify(saleRepo).searchByProduct("番茄");
    }

    @Test
    @DisplayName("add valid sale succeeds")
    void add_valid_succeeds() throws Exception {
        Sale s = createSale("S001");
        Inventory inv = new Inventory();
        inv.setProductId("P001"); inv.setStock(50);
        inv.setMaxStock(100); inv.setMinStock(10);
        Product p = new Product();
        p.setProductId("P001"); p.setPrice(3.5);
        when(inventoryService.findById("P001")).thenReturn(inv);
        when(productService.findById("P001")).thenReturn(p);
        saleService.add(s);
        verify(saleRepo).add(s);
    }

    @Test
    @DisplayName("add with blank saleId throws ValidationException")
    void add_blankId_throws() {
        Sale s = createSale("");
        assertThatThrownBy(() -> saleService.add(s))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("单号");
        verifyNoInteractions(saleRepo);
    }

    @Test
    @DisplayName("add with blank productId throws ValidationException")
    void add_blankProductId_throws() {
        Sale s = createSale("S001");
        s.setProductId("");
        assertThatThrownBy(() -> saleService.add(s))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("商品编号");
        verifyNoInteractions(saleRepo);
    }

    @Test
    @DisplayName("add with zero quantity throws ValidationException")
    void add_zeroQuantity_throws() {
        Sale s = createSale("S001");
        s.setQuantity(0);
        assertThatThrownBy(() -> saleService.add(s))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("数量");
        verifyNoInteractions(saleRepo);
    }

    @Test
    @DisplayName("add with negative amount throws ValidationException")
    void add_negativeAmount_throws() {
        Sale s = createSale("S001");
        s.setAmount(-1);
        assertThatThrownBy(() -> saleService.add(s))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("金额");
        verifyNoInteractions(saleRepo);
    }

    @Test
    @DisplayName("delete delegates to repository")
    void delete_delegates() throws Exception {
        saleService.delete("S001");
        verify(saleRepo).delete("S001");
    }

    private static Sale createSale(String id) {
        Sale s = new Sale();
        s.setSaleId(id);
        s.setStoreId("门店01");
        s.setProductId("P001");
        s.setQuantity(10);
        s.setAmount(35.0);
        s.setSaleDate("2024-01-20");
        s.setUserId("U003");
        return s;
    }
}
