package com.freshstore.repository;

import com.freshstore.entity.Category;
import java.util.List;

/** 商品类别数据访问接口 */
public interface CategoryRepository {
    List<Category> listAll() throws Exception;
    void add(Category cat) throws Exception;
    void update(Category cat) throws Exception;
    void delete(String id) throws Exception;
}
