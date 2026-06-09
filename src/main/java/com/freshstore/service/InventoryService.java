package com.freshstore.service;

import com.freshstore.entity.Inventory;
import com.freshstore.exception.ValidationException;
import com.freshstore.repository.InventoryRepository;
import java.util.List;

public class InventoryService {
    private final InventoryRepository inventoryRepo;

    public InventoryService(InventoryRepository inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public List<Inventory> listAll() {
        try { return inventoryRepo.listAll(); }
        catch (Exception e) { throw new RuntimeException("查询库存失败", e); }
    }

    public void add(Inventory inv) {
        validate(inv);
        try { inventoryRepo.add(inv); }
        catch (Exception e) { throw new RuntimeException("添加库存失败", e); }
    }

    public void update(Inventory inv) {
        validate(inv);
        try { inventoryRepo.update(inv); }
        catch (Exception e) { throw new RuntimeException("更新库存失败", e); }
    }

    public Inventory findById(String productId) {
        try { return inventoryRepo.findById(productId); }
        catch (Exception e) { throw new RuntimeException("查询库存失败", e); }
    }

    public void delete(String productId) {
        try { inventoryRepo.delete(productId); }
        catch (Exception e) { throw new RuntimeException("删除库存失败", e); }
    }

    private void validate(Inventory inv) {
        if (inv.getProductId() == null || inv.getProductId().isBlank())
            throw new ValidationException("商品编号不能为空");
        if (inv.getStock() < 0) throw new ValidationException("库存数量不能为负数");
        if (inv.getMaxStock() < 0) throw new ValidationException("库存上限不能为负数");
        if (inv.getMinStock() < 0) throw new ValidationException("库存下限不能为负数");
    }
}
