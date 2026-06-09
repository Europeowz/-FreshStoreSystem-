package com.freshstore.repository;

import com.freshstore.entity.Sale;
import java.util.List;

/** 销售数据访问接口 */
public interface SaleRepository {
    List<Sale> listAll() throws Exception;
    List<Sale> searchByProduct(String keyword) throws Exception;
    void add(Sale s) throws Exception;
    void delete(String saleId) throws Exception;
}
