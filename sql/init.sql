-- ============================================================
-- 生鲜门店管理系统 v2.0 数据库初始化脚本
-- 4 个核心实体：商品、商品类别、库存、配送/销售
-- 用户表独立
-- ============================================================

DROP DATABASE IF EXISTS FreshStoreDB;
CREATE DATABASE FreshStoreDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE FreshStoreDB;

-- ----------------------------------------------------------
-- 1. 商品类别
-- ----------------------------------------------------------
CREATE TABLE Category (
    category_id   VARCHAR(10)  NOT NULL PRIMARY KEY COMMENT '类别编号',
    category_name VARCHAR(50)  NOT NULL COMMENT '类别名称',
    status        TINYINT     NOT NULL DEFAULT 1 COMMENT '1=正常 0=停用'
) COMMENT='商品类别';

INSERT INTO Category VALUES
    ('C001','蔬菜',1),
    ('C002','水果',1),
    ('C003','肉禽',1),
    ('C004','水产',1),
    ('C005','乳制品',1);

-- ----------------------------------------------------------
-- 2. 供应商表（保留结构，仅用于联查商品时的名称展示）
-- ----------------------------------------------------------
CREATE TABLE Supplier (
    supplier_id   VARCHAR(20)   NOT NULL PRIMARY KEY COMMENT '供应商编号',
    name          VARCHAR(100)  NOT NULL COMMENT '供应商名称',
    contact       VARCHAR(50)   COMMENT '联系人',
    phone         VARCHAR(20)   COMMENT '联系电话',
    address       VARCHAR(200)  COMMENT '地址',
    status        TINYINT       NOT NULL DEFAULT 1 COMMENT '1=合作中 0=停用'
) COMMENT='供应商';

INSERT INTO Supplier(supplier_id,name,contact,phone,address) VALUES
    ('S001','绿源蔬菜基地','张三','13800000001','天津市西青区'),
    ('S002','海鲜港物流','李四','13800000002','天津市滨海新区');

-- ----------------------------------------------------------
-- 3. 商品表（含供应商外键）
-- ----------------------------------------------------------
CREATE TABLE Product (
    product_id   VARCHAR(20)    NOT NULL PRIMARY KEY COMMENT '商品编号',
    name         VARCHAR(100)   NOT NULL COMMENT '商品名称',
    category_id  VARCHAR(10)    NOT NULL COMMENT '类别编号',
    supplier_id  VARCHAR(20)     COMMENT '供应商编号（选填）',
    cost         DECIMAL(10,2)  NOT NULL DEFAULT 0.00 COMMENT '进价（元）',
    price        DECIMAL(10,2)  NOT NULL DEFAULT 0.00 COMMENT '售价（元）',
    unit         VARCHAR(10)    NOT NULL DEFAULT '斤'  COMMENT '计量单位',
    shelf_life   INT            NOT NULL DEFAULT 3     COMMENT '保质期（天）',
    status       TINYINT        NOT NULL DEFAULT 1     COMMENT '1=在售 0=下架',
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES Category(category_id),
    CONSTRAINT fk_product_supplier FOREIGN KEY (supplier_id) REFERENCES Supplier(supplier_id)
) COMMENT='商品信息';

-- ----------------------------------------------------------
-- 4. 库存表
-- ----------------------------------------------------------
CREATE TABLE Inventory (
    product_id    VARCHAR(20)   NOT NULL PRIMARY KEY COMMENT '商品编号',
    stock_qty     DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '当前库存量',
    min_stock     DECIMAL(10,2) NOT NULL DEFAULT 5.00  COMMENT '最低库存预警值',
    last_updated  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_inv_product FOREIGN KEY (product_id) REFERENCES Product(product_id)
) COMMENT='库存';

-- ----------------------------------------------------------
-- 5. 配送/销售记录
-- ----------------------------------------------------------
CREATE TABLE SalesRecord (
    sale_id      INT            NOT NULL AUTO_INCREMENT PRIMARY KEY,
    product_id   VARCHAR(20)    NOT NULL COMMENT '商品编号',
    sale_date    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '销售/配送时间',
    quantity     DECIMAL(10,2)  NOT NULL COMMENT '销售数量',
    unit_price   DECIMAL(10,2)  NOT NULL COMMENT '实际售价',
    subtotal     DECIMAL(12,2)  GENERATED ALWAYS AS (quantity * unit_price) STORED COMMENT '小计',
    customer     VARCHAR(100)    COMMENT '客户/门店名称',
    remark       VARCHAR(200)   COMMENT '备注',
    CONSTRAINT fk_sale_product FOREIGN KEY (product_id) REFERENCES Product(product_id)
) COMMENT='配送/销售记录';

-- ----------------------------------------------------------
-- 6. 用户表（登录用）
-- ----------------------------------------------------------
CREATE TABLE Users (
    user_id    INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    password   VARCHAR(100) NOT NULL COMMENT 'MD5加密密码',
    role       VARCHAR(20)  NOT NULL DEFAULT 'operator' COMMENT 'admin/operator',
    status     TINYINT      NOT NULL DEFAULT 1 COMMENT '1=启用 0=禁用'
) COMMENT='系统用户';

-- 默认管理员账号：admin / admin123
INSERT INTO Users(username,password,role) VALUES
    ('admin', MD5('admin123'), 'admin'),
    ('operator', MD5('op123456'), 'operator');

-- ----------------------------------------------------------
-- 触发器：销售出库时自动扣减库存
-- ----------------------------------------------------------
DELIMITER $$

CREATE TRIGGER trg_inventory_after_sale_insert
AFTER INSERT ON SalesRecord
FOR EACH ROW
BEGIN
    UPDATE Inventory
    SET stock_qty = stock_qty - NEW.quantity
    WHERE product_id = NEW.product_id;
END$$

DELIMITER ;
