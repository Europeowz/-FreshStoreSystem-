# 生鲜门店管理系统 FreshStoreSystem

> Java + SQLite + Swing 全栈桌面应用。首次运行自动建库建表，开箱即用。

---

## 功能模块

### 商品管理
- 添加商品（名称、类别、规格、单位、进价、售价、保质期、供应商）
- 模糊搜索商品名称
- 修改商品信息
- 删除商品

### 类别管理
- 五大预置类别：蔬菜、水果、肉类、水产、乳制品
- 支持新增 / 修改 / 删除类别

### 库存管理
- 查看全部库存（商品名称、当前库存、最大/最小库存、库位、最后入库时间）
- 手动入库（增加库存数量）
- 手动出库（减少库存数量）
- 库存预警：低于最小库存值时界面红色高亮标注

### 销售记录
- 登记销售（选择商品 → 填写数量 → 自动计算金额）
- 按商品名称搜索销售记录
- 销售列表：商品名、数量、金额、销售日期、经手人

### 用户管理（仅管理员可见）
- 用户列表（编号、用户名、角色）
- 新增用户（设定用户名、密码、角色）
- 修改用户信息 / 重置密码
- 删除用户

### 登录与权限
- 用户名 + 密码登录
- 三种角色：管理员 / 采购员 / 销售员
- 用户管理面板仅管理员有权访问
- 底部状态栏显示当前登录用户及角色

---

## 技术架构

```
┌─────────────────────────────────────────┐
│                 UI 层 (Swing)            │
│  LoginFrame  MainFrame  5× Panel        │
├─────────────────────────────────────────┤
│               DAO 层 (JDBC)              │
│  UserDAO  ProductDAO  CategoryDAO       │
│  InventoryDAO  SaleDAO                  │
├─────────────────────────────────────────┤
│              Entity 层 (POJO)            │
│  User  Product  Category                │
│  Inventory  Sale                        │
├─────────────────────────────────────────┤
│              Util 层                     │
│  DBUtil  DatabaseInit  HashUtil         │
├─────────────────────────────────────────┤
│              SQLite                      │
│         freshstore.db (自动生成)          │
└─────────────────────────────────────────┘
```

### 技术要点

| 维度 | 实现 |
|------|------|
| 语言 | Java（JDK 8+） |
| 数据库 | SQLite，JDBC 直连，零配置 |
| GUI | Swing（JFrame / JTabbedPane / JTable / JOptionPane） |
| 密码安全 | SHA-256 + 随机 16 字节盐值 + 10000 次迭代哈希 |
| SQL 注入防护 | 全 PreparedStatement 参数化查询 |
| 外键约束 | `PRAGMA foreign_keys = ON` 每次连接自动启用 |
| 数据隔离 | 数据库文件随项目存放，无需安装 MySQL |
| AI 预警 | `InventoryPanel` 中库存低于 `min_stock` 时行背景变红色 |

---

## 项目结构

```
simple_gui/
├── README.md
├── run.bat                      # Windows 一键运行脚本（自动检测/下载 JDK）
├── freshstore.db                # 数据库文件（首次运行自动生成）
├── lib/
│   └── sqlite-jdbc-3.49.1.0.jar
├── out/                         # 编译输出目录
└── src/
    ├── Main.java                # 入口：初始化数据库 → 启动登录窗口
    ├── util/
    │   ├── DBUtil.java          # SQLite 数据库连接（单例驱动加载，外键自动启用）
    │   ├── DatabaseInit.java    # 首次运行自动建 5 张表 + 插入 10 种商品 + 3 个初始用户
    │   └── HashUtil.java        # SHA-256 密码哈希（盐值 + 迭代 + 常量时间比对）
    ├── entity/
    │   ├── User.java            # 用户实体（编号、用户名、密码、角色）
    │   ├── Product.java         # 商品实体（编号、名称、类别、规格、单位、进价、售价…）
    │   ├── Category.java        # 类别实体
    │   ├── Inventory.java       # 库存实体（商品、库存量、最大/最小库存、库位…）
    │   └── Sale.java            # 销售实体（商品、数量、金额、日期、经手人）
    ├── dao/
    │   ├── UserDAO.java         # 用户 CRUD + 哈希登录验证
    │   ├── ProductDAO.java      # 商品 CRUD + 模糊搜索
    │   ├── CategoryDAO.java     # 类别 CRUD
    │   ├── InventoryDAO.java    # 库存查询 + 入库/出库
    │   └── SaleDAO.java         # 销售记录 CRUD + 按商品查询
    └── ui/
        ├── LoginFrame.java      # 登录窗口（中文界面、JPasswordField 密文输入）
        ├── MainFrame.java       # 主窗口（JTabbedPane 标签页 + 底部状态栏 + 退出按钮）
        ├── ProductPanel.java    # 商品管理面板（搜索栏 + 类别下拉筛选 + 表格 + 新增/修改弹窗）
        ├── CategoryPanel.java   # 类别管理面板
        ├── InventoryPanel.java  # 库存管理面板（预警高亮 + 入库/出库弹窗）
        ├── SalePanel.java       # 销售记录面板（搜索 + 新增销售弹窗）
        └── UserPanel.java       # 用户管理面板（仅管理员可见，密码框输入）
```

---

## 数据库设计

### 数据表

| 表名 | 主键 | 主要字段 | 外键 |
|------|------|----------|------|
| `Category` | `category_id` | category_name | — |
| `Product` | `product_id` | name, category_id, spec, unit, cost, price, shelf_life, supplier_name, reg_date | → Category |
| `Inventory` | `product_id` | stock, max_stock, min_stock, location, last_in, user_id | → Product, → Users |
| `Sale` | `sale_id` | store_id, product_id, quantity, amount, sale_date, user_id | → Product, → Users |
| `Users` | `user_id` | username, password（SHA-256 哈希）, role | — |

### 表关系

```
Category ──< Product ──< Inventory
               │
               └──< Sale
Users ──< Inventory
Users ──< Sale
```

### 初始数据

首次运行时自动插入：

- **5 个类别**：蔬菜、水果、肉类、水产、乳制品
- **3 个用户**：admin（管理员）、buyer1（采购员）、seller1（销售员），默认密码均为 `123456`
- **10 种商品**：西红柿、黄瓜、苹果、香蕉、猪肉、牛肉、娃娃菜、三文鱼、牛奶、鸡蛋
- **10 条库存记录**：含库位、最大/最小库存
- **8 条销售记录**：覆盖 3 个门店

---

## 快速启动

### Windows 用户

```bash
# 双击运行
run.bat
```

脚本会自动：
1. 检查 `lib/sqlite-jdbc-3.49.1.0.jar` 是否存在
2. 检测系统 JDK（本地 `.\jdk\` → 系统 PATH → JAVA_HOME）
3. 若均未找到，自动下载 JDK 11 到 `.\jdk\`（约 180MB，仅首次）
4. 编译源码
5. 启动应用

### macOS / Linux 用户

```bash
# 1. 确保已安装 JDK 8+
java -version

# 2. 编译
javac -cp "lib/sqlite-jdbc-3.49.1.0.jar" -encoding UTF-8 -d out \
  src/util/*.java src/entity/*.java src/dao/*.java src/ui/*.java src/Main.java

# 3. 运行
java -cp "out:lib/sqlite-jdbc-3.49.1.0.jar" Main
```

### 默认账号

| 用户名 | 密码 | 角色 | 权限 |
|--------|------|------|------|
| admin | 123456 | 管理员 | 全部功能（含用户管理） |
| buyer1 | 123456 | 采购员 | 商品 / 类别 / 库存 / 销售 |
| seller1 | 123456 | 销售员 | 商品 / 类别 / 库存 / 销售 |

---

## 安全设计

- **密码哈希**：SHA-256 + 16 字节随机盐值 + 10000 次迭代，存储格式 `Base64(salt)$Base64(hash)`
- **常量时间比对**：使用 `MessageDigest.isEqual()` 防止时序攻击
- **密文输入**：登录与用户管理均使用 `JPasswordField`，不显示输入字符
- **不展示密码**：用户管理表格不显示密码列，编辑用户时需重新输入密码
- **SQL 注入防护**：全 PreparedStatement，绝不拼接用户输入到 SQL
- **登录不可见**：界面不再显示默认密码提示

---

## 设计亮点

- **零配置启动**：首次运行自动建库建表、插入示例数据，无需手动执行 SQL 或安装 MySQL
- **自动 JDK 下载**：`run.bat` 在未检测到 JDK 时自动从 Adoptium 下载 JDK 11
- **AI 库存预警**：库存低于最小阈值时表格行背景变红
- **角色权限**：用户管理面板仅 `管理员` 角色可见
- **稳定数据库**：SQLite 文件存储，不依赖外部服务，拷走即用
- **中文界面**：全中文 UI，微软雅黑字体

---

## 开发说明

本项目采用 AI 协同开发模式完成。我负责数据库表结构设计（5 张表及关系）、核心业务流程定义、GUI 界面布局设计、AI 生成代码的调试与集成测试。

---

## 许可

MIT
