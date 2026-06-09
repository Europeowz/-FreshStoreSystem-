# 生鲜门店管理系统 FreshStoreSystem

Java 24 + SQLite + Swing 桌面应用，专为中小型生鲜门店设计的进销存管理系统。

[![License](https://img.shields.io/badge/license-MIT-green)](./LICENSE)
[![Java](https://img.shields.io/badge/Java-24-orange)](https://jdk.java.net/24/)
[![Release](https://img.shields.io/badge/release-v2.0-blue)](https://github.com/Europeowz/-FreshStoreSystem-/releases)

## 快速开始

### 下载即用（推荐）

从 [Releases](https://github.com/Europeowz/-FreshStoreSystem-/releases) 下载 `FreshStoreSystem.jar` 和 `start.bat`，放到同一目录，双击 `start.bat` 启动。

或命令行：

```bash
java --enable-preview --enable-native-access=ALL-UNNAMED -jar FreshStoreSystem.jar
```

> 需要 JDK 24+。首次运行自动创建 `freshstore.db` 并填充示例数据。

### 默认账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| `admin` | `123456` | 管理员（全部功能） |
| `buyer1` | `123456` | 采购员 |
| `seller1` | `123456` | 销售员 |

### 从源码构建

```bash
git clone https://github.com/Europeowz/-FreshStoreSystem-.git
cd FreshStoreSystem
mvn package
java --enable-preview --enable-native-access=ALL-UNNAMED -jar target/FreshStoreSystem.jar
```

> 需要 JDK 24+、Maven 3.6+。Maven 自动下载 SQLite、bcrypt、SLF4J 等依赖。

---

## 功能模块

| 模块 | 功能 |
|------|------|
| 仪表盘 | 6 个统计卡片（商品总数、库存品类、累计销售额、今日销售额、销售记录数、库存总量）+ 低库存预警 |
| 商品管理 | 添加/搜索/筛选/修改/删除，按品类过滤，支持排序、CSV 导出 |
| 商品类别 | 分类 CRUD，表格排序，CSV 导出 |
| 库存管理 | 库存查询/调整/预警（低于下限红色高亮），排序、CSV 导出 |
| 销售记录 | 销售登记、自动计算金额、库存不足拦截、自动扣减库存 |
| 用户管理 | 仅管理员可见，添加/修改/删除用户，角色分配 |
| 修改密码 | 当前用户修改自己的密码，需验证原密码 |

---

## 使用指南

### 登录

启动后输入用户名密码，点击"登录"或按 Enter。失败时会提示具体原因。

### 仪表盘

登录后默认展示。6 个统计卡片实时反映经营概况，低库存商品列表红色高亮提醒。点击"刷新"重新加载。

### 商品管理

- **添加**：点击"添加商品" → 填写名称、类别、规格、单位、成本、售价、保质期、供应商 → 商品编号自动生成
- **搜索**：输入关键词按 Enter，支持商品名称模糊搜索
- **筛选**：下拉框按类别过滤
- **修改**：选中行 → 点击"修改" → 弹窗编辑
- **删除**：选中行 → 点击"删除" → 确认后删除
- **导出**：点击"导出CSV"，选择保存路径
- **F5**：刷新数据

### 销售记录

选择商品 → 输入数量 → 系统自动计算金额（售价 × 数量）。若库存不足会拦截并提示所需数量。提交成功后自动扣减对应商品库存。

### 用户管理（仅管理员）

三种角色：
- **管理员**：全部权限
- **采购员**：商品 + 库存管理
- **销售员**：销售记录管理

编辑用户时密码留空表示不修改密码。

### 修改密码

点击底部"修改密码"按钮 → 输入原密码 → 新密码（≥4位）→ 确认新密码 → 提交。

---

## 技术架构

```
┌─────────────────────────────────────────────┐
│              UI 层 (Swing)                    │
│  LoginFrame  MainFrame  DashboardPanel       │
│  ProductPanel  CategoryPanel  InventoryPanel │
│  SalePanel  UserPanel                        │
├─────────────────────────────────────────────┤
│             服务层 (Service)                   │
│  UserService  ProductService                 │
│  CategoryService  InventoryService           │
│  SaleService  ServiceFactory（单例工厂）        │
├─────────────────────────────────────────────┤
│            仓储层 (Repository)                 │
│  接口 → Jdbc* 实现（PreparedStatement 参数化）   │
├─────────────────────────────────────────────┤
│             工具层 (Util)                      │
│  DBUtil（SQLite 连接）  HashUtil（bcrypt）       │
│  DatabaseInitializer（自动建表 + 种子数据）       │
├─────────────────────────────────────────────┤
│             数据存储                            │
│  SQLite（freshstore.db）+ bcrypt 密码哈希        │
└─────────────────────────────────────────────┘
```

### 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 24 | 开发语言 |
| Swing | — | 桌面 GUI |
| SQLite | 3.x（sqlite-jdbc 3.49.1.0）| 嵌入式数据库 |
| bcrypt | jbcrypt 0.4 | 密码哈希（12 轮加盐） |
| SLF4J + Logback | 2.0.16 | 日志 |
| JUnit 5 + AssertJ + Mockito | 5.11 / 3.27 / 5.15 | 测试框架 |

### 数据库设计

```
Users ───────────────────────────── 独立，登录认证 + 角色权限

Category ──< Product ──< Inventory     商品分类 → 商品 → 库存
                │
                └──< Sale              商品 → 销售记录
```

| 表 | 主键 | 关键字段 |
|----|------|----------|
| `Users` | user_id | username (UNIQUE), password (bcrypt), role |
| `Category` | category_id | category_name (UNIQUE) |
| `Product` | product_id | name, category_id (FK), spec, unit, cost, price, shelf_life |
| `Inventory` | product_id (FK) | stock, max_stock, min_stock, location |
| `Sale` | sale_id | product_id (FK), quantity, amount, sale_date |

全部外键约束通过 `PRAGMA foreign_keys = ON` 保证数据完整性。

### 安全设计

| 措施 | 实现 |
|------|------|
| 密码哈希 | bcrypt，12 轮加盐，每用户独立盐值 |
| SQL 注入防护 | 100% PreparedStatement 参数化查询 |
| 密码不可见 | 用户列表不展示密码哈希，getAll 自动清空密码字段 |
| 认证校验 | 登录 bcrypt 验证；修改密码需验证原密码 |
| 异常安全 | HashUtil.verify() 静默返回 false，不泄露哈希格式 |

### 测试

**75 个测试全部通过：**

| 层级 | 文件 | 数量 | 方式 |
|------|------|------|------|
| 单元 | HashUtilTest | 8 | 纯函数 |
| 单元 | UserServiceTest | 17 | Mockito |
| 单元 | ProductServiceTest | 13 | Mockito |
| 单元 | CategoryServiceTest | 6 | Mockito |
| 单元 | InventoryServiceTest | 7 | Mockito |
| 单元 | SaleServiceTest | 8 | Mockito |
| 集成 | JdbcUserRepositoryTest | 8 | 真实 SQLite |
| 集成 | JdbcProductRepositoryTest | 8 | 真实 SQLite |

```bash
# 运行全部测试
mvn test
```

---

## 项目结构

```
FreshStoreSystem/
├── LICENSE
├── README.md
├── pom.xml
├── freshstore.db                    # 自动生成（运行时）
└── src/
    ├── main/java/com/freshstore/
    │   ├── Main.java                # 入口
    │   ├── entity/                  # 实体类 ×5
    │   ├── repository/              # 仓储接口 + JDBC 实现 ×10
    │   ├── service/                 # 业务服务 + 工厂 ×6
    │   ├── ui/                      # Swing UI ×8
    │   ├── util/                    # DBUtil, HashUtil, DatabaseInitializer
    │   └── exception/               # 业务异常 ×3
    └── test/java/com/freshstore/
        ├── util/    HashUtilTest.java
        ├── service/ 5 个 Service 测试
        └── repository/ 2 个集成测试
```

---

## 常见问题

**Q: 双击 JAR 无法启动？**

需要 JDK 24+。使用 `start.bat` 或命令行启动。

**Q: 数据库在哪里？**

`freshstore.db` 在 JAR 所在目录自动生成。删除后重启即重置所有数据。

**Q: 端口 / 防火墙？**

不需要。SQLite 是嵌入式数据库，无需额外服务进程。
