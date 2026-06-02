-- ============================================================
-- 生鲜门店管理系统 —— 数据库脚本（简化版）
-- 教材依据：数据库系统概论（第五版，王珊 萨师煊）第 1-3 章
-- 包含 5 张表：商品类别、用户信息、商品信息、库存信息、销售信息
-- 使用方法：在 MySQL 命令行执行  source /路径/init.sql
-- ============================================================

DROP DATABASE IF EXISTS FreshStoreDB;
CREATE DATABASE FreshStoreDB CHARACTER SET utf8mb4;
USE FreshStoreDB;


-- ============================================================
-- 一、创建表（第 3 章：数据定义）
--    使用了第 2 章的实体完整性（PRIMARY KEY）、
--    参照完整性（FOREIGN KEY）、用户定义完整性（NOT NULL / UNIQUE）
--    注意建表顺序：先建被引用的表，后建含外键的表
-- ============================================================

-- 1. 商品类别表（先建：被 Product 引用）
CREATE TABLE Category (
    category_id   CHAR(5)      PRIMARY KEY,
    category_name VARCHAR(20)  NOT NULL UNIQUE
);

-- 2. 用户信息表（先建：被 Inventory / Sale 引用）
CREATE TABLE Users (
    user_id   CHAR(8)      PRIMARY KEY,
    username  VARCHAR(20)  NOT NULL UNIQUE,
    password  VARCHAR(20)  NOT NULL,
    role      VARCHAR(20)  NOT NULL
);

-- 3. 商品信息表（后建：引用 Category）
CREATE TABLE Product (
    product_id    CHAR(10)      PRIMARY KEY,
    name          VARCHAR(50)  NOT NULL,
    category_id   CHAR(5)      NOT NULL,
    spec          VARCHAR(50),
    unit          VARCHAR(10)  NOT NULL,
    cost          DECIMAL(10,2) NOT NULL,
    price         DECIMAL(10,2) NOT NULL,
    shelf_life    INT,
    supplier_name VARCHAR(50),
    reg_date      DATE,
    FOREIGN KEY (category_id) REFERENCES Category(category_id)
);

-- 4. 库存信息表（后建：引用 Product 和 Users）
CREATE TABLE Inventory (
    product_id    CHAR(10)     PRIMARY KEY,
    stock         INT          NOT NULL,
    max_stock     INT,
    min_stock     INT,
    location      VARCHAR(50),
    last_in       DATE,
    user_id       CHAR(8),
    FOREIGN KEY (product_id) REFERENCES Product(product_id),
    FOREIGN KEY (user_id)    REFERENCES Users(user_id)
);

-- 5. 销售信息表（后建：引用 Product 和 Users）
CREATE TABLE Sale (
    sale_id       CHAR(10)     PRIMARY KEY,
    store_id      VARCHAR(20),
    product_id    CHAR(10)     NOT NULL,
    quantity      INT          NOT NULL,
    amount        DECIMAL(10,2) NOT NULL,
    sale_date     DATE,
    user_id       CHAR(8),
    FOREIGN KEY (product_id) REFERENCES Product(product_id),
    FOREIGN KEY (user_id)    REFERENCES Users(user_id)
);


-- ============================================================
-- 二、创建索引（第 3 章：索引）
--    为经常查询的外键列建立索引
-- ============================================================

CREATE INDEX idx_product_category ON Product(category_id);
CREATE INDEX idx_sale_product    ON Sale(product_id);
CREATE INDEX idx_sale_date       ON Sale(sale_date);


-- ============================================================
-- 三、插入示例数据（第 3 章：数据更新——INSERT）
-- ============================================================

-- 商品类别（5 条）
INSERT INTO Category VALUES ('C01', '蔬菜');
INSERT INTO Category VALUES ('C02', '水果');
INSERT INTO Category VALUES ('C03', '肉类');
INSERT INTO Category VALUES ('C04', '水产');
INSERT INTO Category VALUES ('C05', '乳制品');

-- 用户（3 条）
INSERT INTO Users VALUES ('U001', 'admin',   '123456', '管理员');
INSERT INTO Users VALUES ('U002', 'buyer1',  '123456', '采购员');
INSERT INTO Users VALUES ('U003', 'seller1', '123456', '销售员');

-- 商品（10 条）
INSERT INTO Product VALUES ('P001', '西红柿', 'C01', '1斤/袋',   '斤', 2.00,  3.50,  5,  '绿源蔬菜基地', '2024-01-10');
INSERT INTO Product VALUES ('P002', '黄瓜',   'C01', '1斤/袋',   '斤', 1.50,  2.80,  4,  '绿源蔬菜基地', '2024-01-11');
INSERT INTO Product VALUES ('P003', '苹果',   'C02', '2斤/袋',   '斤', 3.00,  5.50,  15, '鲜果供应基地', '2024-01-11');
INSERT INTO Product VALUES ('P004', '香蕉',   'C02', '1斤/把',   '斤', 2.20,  4.00,  7,  '鲜果供应基地', '2024-01-12');
INSERT INTO Product VALUES ('P005', '猪肉',   'C03', '500g/盒',  '盒', 10.00, 16.00, 3,  '牧原肉品集团', '2024-01-12');
INSERT INTO Product VALUES ('P006', '牛肉',   'C03', '500g/盒',  '盒', 20.00, 32.00, 3,  '牧原肉品集团', '2024-01-13');
INSERT INTO Product VALUES ('P007', '娃娃菜', 'C01', '2斤/袋',   '斤', 1.80,  3.20,  5,  '绿源蔬菜基地', '2024-01-14');
INSERT INTO Product VALUES ('P008', '三文鱼', 'C04', '200g/盒',  '盒', 25.00, 45.00, 2,  '海鲜直供中心', '2024-01-15');
INSERT INTO Product VALUES ('P009', '牛奶',   'C05', '1L/瓶',    '瓶', 5.00,  8.50,  7,  '乳品牧场',     '2024-01-15');
INSERT INTO Product VALUES ('P010', '鸡蛋',   'C03', '10枚/盒',  '盒', 6.00,  10.00, 15, '牧原肉品集团', '2024-01-16');

-- 库存（10 条，与商品一一对应）
INSERT INTO Inventory VALUES ('P001', 80,  100, 20, 'A区-01架', '2024-01-15', 'U002');
INSERT INTO Inventory VALUES ('P002', 60,  100, 20, 'A区-02架', '2024-01-15', 'U002');
INSERT INTO Inventory VALUES ('P003', 120, 150, 30, 'B区-01架', '2024-01-16', 'U002');
INSERT INTO Inventory VALUES ('P004', 90,  120, 25, 'B区-02架', '2024-01-16', 'U002');
INSERT INTO Inventory VALUES ('P005', 40,  60,  10, 'C区-01架', '2024-01-17', 'U002');
INSERT INTO Inventory VALUES ('P006', 35,  60,  10, 'C区-02架', '2024-01-17', 'U002');
INSERT INTO Inventory VALUES ('P007', 70,  100, 20, 'A区-03架', '2024-01-18', 'U002');
INSERT INTO Inventory VALUES ('P008', 25,  50,  8,  'D区-01架', '2024-01-18', 'U002');
INSERT INTO Inventory VALUES ('P009', 55,  80,  15, 'E区-01架', '2024-01-19', 'U002');
INSERT INTO Inventory VALUES ('P010', 100, 150, 30, 'C区-03架', '2024-01-19', 'U002');

-- 销售记录（8 条）
INSERT INTO Sale VALUES ('S001', '门店01', 'P001', 10, 35.00,  '2024-01-20', 'U003');
INSERT INTO Sale VALUES ('S002', '门店01', 'P002', 5,  14.00,  '2024-01-20', 'U003');
INSERT INTO Sale VALUES ('S003', '门店02', 'P003', 8,  44.00,  '2024-01-21', 'U003');
INSERT INTO Sale VALUES ('S004', '门店02', 'P005', 3,  48.00,  '2024-01-21', 'U003');
INSERT INTO Sale VALUES ('S005', '门店01', 'P001', 15, 52.50,  '2024-01-22', 'U003');
INSERT INTO Sale VALUES ('S006', '门店03', 'P004', 12, 48.00,  '2024-01-22', 'U003');
INSERT INTO Sale VALUES ('S007', '门店01', 'P009', 6,  51.00,  '2024-01-23', 'U003');
INSERT INTO Sale VALUES ('S008', '门店02', 'P010', 8,  80.00,  '2024-01-23', 'U003');


-- ============================================================
-- 四、创建视图（第 3 章：视图）
--    视图是虚拟表，用法与基本表相同
-- ============================================================

-- 视图 1：商品详情（含类别名称）
CREATE VIEW v_product_detail AS
SELECT p.product_id,
       p.name          AS 商品名称,
       c.category_name AS 类别,
       p.spec          AS 规格,
       p.unit          AS 单位,
       p.cost          AS 进价,
       p.price         AS 售价,
       p.supplier_name AS 供应商
FROM Product p
JOIN Category c ON p.category_id = c.category_id;

-- 视图 2：库存预警（库存量低于下限的商品）
CREATE VIEW v_stock_warning AS
SELECT i.product_id,
       p.name      AS 商品名称,
       i.stock     AS 当前库存,
       i.min_stock AS 库存下限,
       i.location  AS 位置
FROM Inventory i
JOIN Product p ON i.product_id = p.product_id
WHERE i.stock < i.min_stock;

-- 视图 3：销售汇总（每种商品的总销量和总金额）
CREATE VIEW v_sales_summary AS
SELECT p.name        AS 商品名称,
       SUM(s.quantity) AS 总销量,
       SUM(s.amount)   AS 总金额
FROM Sale s
JOIN Product p ON s.product_id = p.product_id
GROUP BY p.name;


-- ============================================================
-- 五、查询练习——搜索筛选（第 3 章：数据查询）
--    以下每条 SELECT 均可独立运行
-- ============================================================

-- ----- 单表查询 -----

-- Q1：查询全部商品
SELECT * FROM Product;

-- Q2：查询指定列（教材 3.3.1 节：选择表中的若干列）
SELECT product_id, name, price, unit FROM Product;

-- Q3：等于筛选（=）
SELECT * FROM Product WHERE category_id = 'C01';

-- Q4：模糊查询（LIKE）——含"菜"字的商品
SELECT * FROM Product WHERE name LIKE '%菜%';

-- Q5：范围查询（BETWEEN）——售价在 5 到 20 元之间
SELECT name, price FROM Product WHERE price BETWEEN 5 AND 20;

-- Q6：比较运算——库存低于 50
SELECT product_id, stock FROM Inventory WHERE stock < 50;

-- Q7：IN 操作——蔬菜、水果、肉类
SELECT * FROM Product WHERE category_id IN ('C01', 'C02', 'C03');

-- Q8：排序（ORDER BY DESC）——按售价从高到低
SELECT name, price FROM Product ORDER BY price DESC;

-- Q9：按保质期从短到长（ASC）
SELECT name, shelf_life FROM Product ORDER BY shelf_life ASC;

-- Q10：去重（DISTINCT）——有哪些供应商
SELECT DISTINCT supplier_name FROM Product;

-- ----- 聚合函数 -----

-- Q11：COUNT——商品总数
SELECT COUNT(*) AS 商品总数 FROM Product;

-- Q12：AVG / MAX / MIN——售价统计
SELECT AVG(price) AS 平均售价,
       MAX(price) AS 最高售价,
       MIN(price) AS 最低售价
FROM Product;

-- Q13：SUM——销售总金额
SELECT SUM(amount) AS 销售总额 FROM Sale;

-- ----- 分组（GROUP BY + HAVING） -----

-- Q14：各类别商品数量
SELECT c.category_name AS 类别, COUNT(*) AS 商品数
FROM Product p
JOIN Category c ON p.category_id = c.category_id
GROUP BY c.category_name;

-- Q15：平均售价超过 10 元的类别
SELECT c.category_name AS 类别, AVG(p.price) AS 平均售价
FROM Product p
JOIN Category c ON p.category_id = c.category_id
GROUP BY c.category_name
HAVING AVG(p.price) > 10;

-- ----- 连接查询（JOIN） -----

-- Q16：等值连接——每件商品的类别名称
SELECT p.name AS 商品, c.category_name AS 类别
FROM Product p
JOIN Category c ON p.category_id = c.category_id;

-- Q17：多表连接——每笔销售的详细信息
SELECT s.sale_id AS 销售单号, p.name AS 商品,
       s.quantity AS 数量, s.amount AS 金额,
       s.sale_date AS 日期, s.store_id AS 门店
FROM Sale s
JOIN Product p ON s.product_id = p.product_id;

-- Q18：连接 + 筛选——蔬菜类的销售记录
SELECT p.name, s.quantity, s.amount, s.sale_date
FROM Sale s
JOIN Product p ON s.product_id = p.product_id
WHERE p.category_id = 'C01';

-- ----- 子查询 -----

-- Q19：售价高于平均售价的商品
SELECT name, price
FROM Product
WHERE price > (SELECT AVG(price) FROM Product);

-- Q20：库存不足的商品（子查询 + IN）
SELECT name
FROM Product
WHERE product_id IN (
    SELECT product_id FROM Inventory WHERE stock < min_stock
);

-- ----- 使用视图（视图用法与表相同） -----

-- Q21：查看商品详情视图
SELECT * FROM v_product_detail;

-- Q22：查看需要补货的商品
SELECT * FROM v_stock_warning;

-- Q23：查看销售汇总
SELECT * FROM v_sales_summary;


-- ============================================================
-- 六、数据更新示例（第 3 章：数据更新——UPDATE / DELETE）
--    去掉行首 -- 即可执行
-- ============================================================

-- 修改售价
-- UPDATE Product SET price = 4.00 WHERE product_id = 'P001';

-- 入库：增加库存（UPDATE + 算术表达式）
-- UPDATE Inventory SET stock = stock + 20 WHERE product_id = 'P001';

-- 删除一条销售记录
-- DELETE FROM Sale WHERE sale_id = 'S008';