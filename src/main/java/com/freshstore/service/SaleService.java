package com.freshstore.service;

import com.freshstore.entity.Inventory;
import com.freshstore.entity.Product;
import com.freshstore.entity.Sale;
import com.freshstore.exception.ValidationException;
import com.freshstore.repository.SaleRepository;
import java.util.List;

public class SaleService {
    private final SaleRepository saleRepo;
    private final ProductService productService;
    private final InventoryService inventoryService;

    public SaleService(SaleRepository saleRepo, ProductService productService,
                       InventoryService inventoryService) {
        this.saleRepo = saleRepo;
        this.productService = productService;
        this.inventoryService = inventoryService;
    }

    public List<Sale> listAll() {
        try { return saleRepo.listAll(); }
        catch (Exception e) { throw new RuntimeException("查询销售记录失败", e); }
    }

    public List<Sale> searchByProduct(String keyword) {
        try { return saleRepo.searchByProduct(keyword); }
        catch (Exception e) { throw new RuntimeException("搜索销售记录失败", e); }
    }

    public void add(Sale s) {
        validate(s);
        try { saleRepo.add(s); }
        catch (Exception e) { throw new RuntimeException("添加销售记录失败", e); }
    }

    public void delete(String saleId) {
        try { saleRepo.delete(saleId); }
        catch (Exception e) { throw new RuntimeException("删除销售记录失败", e); }
    }

    private void validate(Sale s) {
        if (s.getSaleId() == null || s.getSaleId().isBlank())
            throw new ValidationException("销售单号不能为空");
        if (s.getProductId() == null || s.getProductId().isBlank())
            throw new ValidationException("商品编号不能为空");
        if (s.getQuantity() <= 0) throw new ValidationException("数量必须大于0");
        if (s.getAmount() < 0) throw new ValidationException("金额不能为负数");

        Inventory inv = inventoryService.findById(s.getProductId());
        if (inv == null)
            throw new ValidationException("商品 [" + s.getProductId() + "] 库存不存在，请先添加库存");
        if (inv.getStock() < s.getQuantity())
            throw new ValidationException("库存不足: 需要 " + s.getQuantity()
                    + "，库存仅 " + inv.getStock());

        Product product = productService.findById(s.getProductId());
        if (product != null && s.getAmount() == 0) {
            s.setAmount(Math.round(product.getPrice() * s.getQuantity() * 100.0) / 100.0);
        }
    }
}
