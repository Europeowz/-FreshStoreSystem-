-- ============================================================
-- 生鲜门店管理系统 FreshStoreDB - 建库建表（不含触发器）
-- ============================================================

DROP DATABASE IF EXISTS FreshStoreDB;
CREATE DATABASE FreshStoreDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE FreshStoreDB;

CREATE TABLE Category (
    category_id   VARCHAR(10)  NOT NULL PRIMARY KEY,
    category_name VARCHAR(50)  NOT NULL
);

INSERT INTO Category VALUES ('C001','蔬菜'),('C002','水果'),('C003','肉禽'),('C004','水产'),('C005','乳制品');

CREATE TABLE Product (
    product_id   VARCHAR(20)    NOT NULL PRIMARY KEY,
    name         VARCHAR(100)   NOT NULL,
    category_id  VARCHAR(10)    NOT NULL,
    cost         DECIMAL(10,2)  NOT NULL DEFAULT 0.00,
    price        DECIMAL(10,2)  NOT NULL DEFAULT 0.00,
    unit         VARCHAR(10)    NOT NULL DEFAULT '斤',
    shelf_life   INT            NOT NULL DEFAULT 3,
    status       TINYINT        NOT NULL DEFAULT 1,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES Category(category_id)
);

CREATE TABLE Supplier (
    supplier_id   VARCHAR(20)  NOT NULL PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    contact       VARCHAR(50),
    phone         VARCHAR(20),
    address       VARCHAR(200),
    status        TINYINT      NOT NULL DEFAULT 1
);

INSERT INTO Supplier(supplier_id,name,contact,phone,address) VALUES
    ('S001','绿源蔬菜基地','张三','13800000001','天津市西青区'),
    ('S002','海鲜港物流','李四','13800000002','天津市滨海新区');

CREATE TABLE PurchaseOrder (
    order_id      VARCHAR(30)    NOT NULL PRIMARY KEY,
    supplier_id   VARCHAR(20)    NOT NULL,
    order_date    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount  DECIMAL(12,2)  NOT NULL DEFAULT 0.00,
    status        VARCHAR(10)    NOT NULL DEFAULT '待审核',
    CONSTRAINT fk_po_supplier FOREIGN KEY (supplier_id) REFERENCES Supplier(supplier_id)
);

CREATE TABLE PurchaseOrderItem (
    item_id     INT            NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_id    VARCHAR(30)    NOT NULL,
    product_id  VARCHAR(20)    NOT NULL,
    quantity    DECIMAL(10,2)  NOT NULL,
    unit_cost   DECIMAL(10,2)  NOT NULL,
    subtotal    DECIMAL(12,2)  GENERATED ALWAYS AS (quantity * unit_cost) STORED,
    CONSTRAINT fk_poi_order   FOREIGN KEY (order_id)   REFERENCES PurchaseOrder(order_id),
    CONSTRAINT fk_poi_product FOREIGN KEY (product_id) REFERENCES Product(product_id)
);

CREATE TABLE Inventory (
    product_id    VARCHAR(20)   NOT NULL PRIMARY KEY,
    stock_qty     DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    min_stock     DECIMAL(10,2) NOT NULL DEFAULT 5.00,
    last_updated  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_inv_product FOREIGN KEY (product_id) REFERENCES Product(product_id)
);

CREATE TABLE SalesRecord (
    sale_id      INT            NOT NULL AUTO_INCREMENT PRIMARY KEY,
    product_id   VARCHAR(20)    NOT NULL,
    sale_date    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    quantity     DECIMAL(10,2)  NOT NULL,
    unit_price   DECIMAL(10,2)  NOT NULL,
    subtotal     DECIMAL(12,2)  GENERATED ALWAYS AS (quantity * unit_price) STORED,
    customer     VARCHAR(100),
    remark       VARCHAR(200),
    CONSTRAINT fk_sale_product FOREIGN KEY (product_id) REFERENCES Product(product_id)
);

CREATE TABLE Users (
    user_id    INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(100) NOT NULL,
    role       VARCHAR(20)  NOT NULL DEFAULT 'operator',
    status     TINYINT      NOT NULL DEFAULT 1
);

INSERT INTO Users(username,password,role) VALUES
    ('admin', MD5('admin123'), 'admin'),
    ('operator', MD5('op123456'), 'operator');
