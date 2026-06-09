package com.freshstore.service;

import com.freshstore.entity.Product;
import com.freshstore.exception.ValidationException;
import com.freshstore.repository.ProductRepository;
import java.util.List;

public class ProductService {
    private final ProductRepository productRepo;

    public ProductService(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    public List<Product> listAll() {
        try { return productRepo.listAll(); }
        catch (Exception e) { throw new RuntimeException("查询商品失败", e); }
    }

    public List<Product> searchByName(String keyword) {
        try { return productRepo.searchByName(keyword); }
        catch (Exception e) { throw new RuntimeException("搜索商品失败", e); }
    }

    public List<Product> listByCategory(String categoryId) {
        try { return productRepo.listByCategory(categoryId); }
        catch (Exception e) { throw new RuntimeException("按类别查询失败", e); }
    }

    public void add(Product p) {
        validate(p);
        try { productRepo.add(p); }
        catch (Exception e) { throw new RuntimeException("添加商品失败", e); }
    }

    public void update(Product p) {
        validate(p);
        try { productRepo.update(p); }
        catch (Exception e) { throw new RuntimeException("更新商品失败", e); }
    }

    public Product findById(String id) {
        try { return productRepo.findById(id); }
        catch (Exception e) { throw new RuntimeException("查询商品失败", e); }
    }

    public void delete(String id) {
        try { productRepo.delete(id); }
        catch (Exception e) { throw new RuntimeException("删除商品失败", e); }
    }

    private void validate(Product p) {
        if (p.getProductId() == null || p.getProductId().isBlank())
            throw new ValidationException("商品编号不能为空");
        if (p.getName() == null || p.getName().isBlank())
            throw new ValidationException("商品名称不能为空");
        if (p.getUnit() == null || p.getUnit().isBlank())
            throw new ValidationException("单位不能为空");
        if (p.getCost() < 0) throw new ValidationException("进价不能为负数");
        if (p.getPrice() < 0) throw new ValidationException("售价不能为负数");
    }
}
