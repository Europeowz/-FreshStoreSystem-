package com.freshstore.repository;

import com.freshstore.entity.Product;
import java.util.List;

/** 商品数据访问接口 */
public interface ProductRepository {
    List<Product> listAll() throws Exception;
    List<Product> searchByName(String keyword) throws Exception;
    List<Product> listByCategory(String categoryId) throws Exception;
    void add(Product p) throws Exception;
    void update(Product p) throws Exception;
    void delete(String id) throws Exception;
    Product findById(String id) throws Exception;
}
