package com.freshstore.service;

import com.freshstore.entity.Category;
import com.freshstore.exception.ValidationException;
import com.freshstore.repository.CategoryRepository;
import java.util.List;

public class CategoryService {
    private final CategoryRepository categoryRepo;

    public CategoryService(CategoryRepository categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    public List<Category> listAll() {
        try { return categoryRepo.listAll(); }
        catch (Exception e) { throw new RuntimeException("查询类别失败", e); }
    }

    public void add(Category cat) {
        validate(cat);
        try { categoryRepo.add(cat); }
        catch (Exception e) { throw new RuntimeException("添加类别失败", e); }
    }

    public void update(Category cat) {
        validate(cat);
        try { categoryRepo.update(cat); }
        catch (Exception e) { throw new RuntimeException("更新类别失败", e); }
    }

    public void delete(String id) {
        try { categoryRepo.delete(id); }
        catch (Exception e) { throw new RuntimeException("删除类别失败", e); }
    }

    private void validate(Category cat) {
        if (cat.getCategoryId() == null || cat.getCategoryId().isBlank())
            throw new ValidationException("类别编号不能为空");
        if (cat.getCategoryName() == null || cat.getCategoryName().isBlank())
            throw new ValidationException("类别名称不能为空");
    }
}
