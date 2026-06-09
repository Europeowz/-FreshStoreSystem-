package com.freshstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.freshstore.entity.Category;
import com.freshstore.exception.ValidationException;
import com.freshstore.repository.CategoryRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepo;

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryService(categoryRepo);
    }

    @Test
    @DisplayName("listAll returns all categories")
    void listAll_returnsCategories() throws Exception {
        when(categoryRepo.listAll()).thenReturn(List.of(new Category("C01", "蔬菜")));
        assertThat(categoryService.listAll()).hasSize(1);
    }

    @Test
    @DisplayName("add valid category succeeds")
    void add_valid_succeeds() throws Exception {
        categoryService.add(new Category("C01", "蔬菜"));
        verify(categoryRepo).add(any(Category.class));
    }

    @Test
    @DisplayName("add with blank id throws ValidationException")
    void add_blankId_throws() {
        assertThatThrownBy(() -> categoryService.add(new Category("", "蔬菜")))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("编号");
        verifyNoInteractions(categoryRepo);
    }

    @Test
    @DisplayName("add with blank name throws ValidationException")
    void add_blankName_throws() {
        assertThatThrownBy(() -> categoryService.add(new Category("C01", "")))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("名称");
        verifyNoInteractions(categoryRepo);
    }

    @Test
    @DisplayName("update delegates to repository")
    void update_delegates() throws Exception {
        categoryService.update(new Category("C01", "水果"));
        verify(categoryRepo).update(any(Category.class));
    }

    @Test
    @DisplayName("delete delegates to repository")
    void delete_delegates() throws Exception {
        categoryService.delete("C01");
        verify(categoryRepo).delete("C01");
    }
}
