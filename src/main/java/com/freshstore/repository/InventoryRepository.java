package com.freshstore.repository;

import com.freshstore.entity.Inventory;
import java.util.List;

/** 库存数据访问接口 */
public interface InventoryRepository {
    List<Inventory> listAll() throws Exception;
    void add(Inventory inv) throws Exception;
    void update(Inventory inv) throws Exception;
    void delete(String productId) throws Exception;
    Inventory findById(String productId) throws Exception;
}
