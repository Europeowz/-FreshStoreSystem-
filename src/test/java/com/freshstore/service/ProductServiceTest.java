package com.freshstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.freshstore.entity.Product;
import com.freshstore.exception.ValidationException;
import com.freshstore.repository.ProductRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepo;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepo);
    }

    @Test
    @DisplayName("listAll returns all products")
    void listAll_returnsProducts() throws Exception {
        when(productRepo.listAll()).thenReturn(List.of(createProduct("P001")));
        assertThat(productService.listAll()).hasSize(1);
        verify(productRepo).listAll();
    }

    @Test
    @DisplayName("searchByName delegates to repository")
    void searchByName_delegates() throws Exception {
        when(productRepo.searchByName("tomato")).thenReturn(List.of(createProduct("P001")));
        assertThat(productService.searchByName("tomato")).hasSize(1);
        verify(productRepo).searchByName("tomato");
    }

    @Test
    @DisplayName("listByCategory delegates to repository")
    void listByCategory_delegates() throws Exception {
        when(productRepo.listByCategory("C01")).thenReturn(List.of(createProduct("P001")));
        assertThat(productService.listByCategory("C01")).hasSize(1);
        verify(productRepo).listByCategory("C01");
    }

    @Test
    @DisplayName("add valid product succeeds")
    void add_valid_succeeds() throws Exception {
        Product p = createProduct("P001");
        productService.add(p);
        verify(productRepo).add(p);
    }

    @Test
    @DisplayName("add with blank id throws ValidationException")
    void add_blankId_throws() {
        Product p = createProduct("");
        assertThatThrownBy(() -> productService.add(p))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("编号");
        verifyNoInteractions(productRepo);
    }

    @Test
    @DisplayName("add with blank name throws ValidationException")
    void add_blankName_throws() {
        Product p = createProduct("P001");
        p.setName("");
        assertThatThrownBy(() -> productService.add(p))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("名称");
        verifyNoInteractions(productRepo);
    }

    @Test
    @DisplayName("add with blank unit throws ValidationException")
    void add_blankUnit_throws() {
        Product p = createProduct("P001");
        p.setUnit("");
        assertThatThrownBy(() -> productService.add(p))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("单位");
        verifyNoInteractions(productRepo);
    }

    @Test
    @DisplayName("add with negative cost throws ValidationException")
    void add_negativeCost_throws() {
        Product p = createProduct("P001");
        p.setCost(-1);
        assertThatThrownBy(() -> productService.add(p))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("进价");
        verifyNoInteractions(productRepo);
    }

    @Test
    @DisplayName("add with negative price throws ValidationException")
    void add_negativePrice_throws() {
        Product p = createProduct("P001");
        p.setPrice(-1);
        assertThatThrownBy(() -> productService.add(p))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("售价");
        verifyNoInteractions(productRepo);
    }

    @Test
    @DisplayName("update delegates to repository")
    void update_delegates() throws Exception {
        Product p = createProduct("P001");
        productService.update(p);
        verify(productRepo).update(p);
    }

    @Test
    @DisplayName("update with blank id throws ValidationException")
    void update_blankId_throws() {
        Product p = createProduct("");
        assertThatThrownBy(() -> productService.update(p))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("delete delegates to repository")
    void delete_delegates() throws Exception {
        productService.delete("P001");
        verify(productRepo).delete("P001");
    }

    @Test
    @DisplayName("repository exception is wrapped in RuntimeException")
    void repositoryException_wrapped() throws Exception {
        when(productRepo.listAll()).thenThrow(new RuntimeException("DB error"));
        assertThatThrownBy(() -> productService.listAll())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("查询商品失败");
    }

    private static Product createProduct(String id) {
        Product p = new Product();
        p.setProductId(id);
        p.setName("测试商品");
        p.setCategoryId("C01");
        p.setSpec("1斤/袋");
        p.setUnit("斤");
        p.setCost(2.0);
        p.setPrice(3.5);
        p.setShelfLife(5);
        p.setSupplierName("测试供应商");
        p.setRegDate("2024-01-01");
        return p;
    }
}
