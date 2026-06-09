-- 触发器：采购订单状态变为"已入库"时自动增加库存
USE FreshStoreDB;

DROP TRIGGER IF EXISTS trg_inventory_after_po_insert;
DROP TRIGGER IF EXISTS trg_inventory_after_sale_insert;

CREATE TRIGGER trg_inventory_after_po_insert
AFTER UPDATE ON PurchaseOrder
FOR EACH ROW
BEGIN
    IF NEW.status = '已入库' AND OLD.status != '已入库' THEN
        UPDATE Inventory i
        JOIN PurchaseOrderItem poi ON poi.product_id = i.product_id
        SET i.stock_qty = i.stock_qty + poi.quantity
        WHERE poi.order_id = NEW.order_id;
    END IF;
END;

CREATE TRIGGER trg_inventory_after_sale_insert
AFTER INSERT ON SalesRecord
FOR EACH ROW
BEGIN
    UPDATE Inventory
    SET stock_qty = stock_qty - NEW.quantity
    WHERE product_id = NEW.product_id;
END;
