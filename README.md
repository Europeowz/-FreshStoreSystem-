# 生鲜门店管理系统 FreshStoreSystem

Java 24 + SQLite + Swing 桌面应用，专为中小型生鲜门店设计的进销存管理系统。

## 功能模块

| 模块 | 功能 |
|------|------|
| 仪表盘 | 商品总数、库存概况、销售额统计、今日销售、低库存预警 |
| 商品管理 | 添加/搜索/筛选/修改/删除，支持按品类过滤 |
| 商品类别 | 分类 CRUD，表格排序 |
| 库存管理 | 库存查询/调整/预警标注（红色高亮低库存行） |
| 销售记录 | 销售登记、自动计算金额、库存不足拦截、自动扣减库存 |
| 用户管理 | 仅管理员可见，添加/修改/删除用户，角色分配 |
| 修改密码 | 当前登录用户修改自己的密码，验证原密码 |

## 快速启动

### 前置条件

- **JDK 24+**（需要 `--enable-preview` 和 `--enable-native-access`）
- Windows / Linux / macOS

### 启动方式

**方式一：双击启动（Windows）**

```
双击 target\start.bat
```

**方式二：命令行**

```bash
java --enable-preview --enable-native-access=ALL-UNNAMED -jar target/FreshStoreSystem.jar
```

首次运行自动在程序目录创建 `freshstore.db` 数据库文件并插入示例数据，无需手动配置。

### 默认账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | 管理员（全部功能） |
| buyer1 | 123456 | 采购员 |
| seller1 | 123456 | 销售员 |

## 使用说明

### 登录

启动后显示登录窗口，输入用户名和密码点击"登录"或按回车键。登录成功进入主界面，失败会提示错误信息。

### 仪表盘

登录后默认显示仪表盘页面，展示 6 个统计卡片（商品总数、库存品类、累计销售额、今日销售额、销售记录数、库存总量）以及低库存预警提示。点击"刷新"按钮重新加载数据。

### 商品管理

- **添加**：点击"添加商品"按钮，填写商品信息（名称、类别、规格、单位、成本、售价、保质期、供应商），商品编号自动生成
- **搜索**：在搜索框输入关键词，按回车或点击"搜索"按钮，支持按商品名称模糊搜索
- **筛选**：通过类别下拉框筛选特定类别的商品
- **修改**：选中表格行，点击"修改"按钮，在弹出的对话框中修改商品信息
- **删除**：选中表格行，点击"删除"按钮，确认后删除
- **排序**：点击表格列标题进行排序
- **导出**：点击"导出CSV"按钮，选择保存路径
- **刷新**：按 F5 键刷新数据

### 商品类别

管理商品的分类（如蔬菜、水果、肉类等）。支持添加、修改、删除、排序、CSV 导出，添加/修改时按回车键提交。

### 库存管理

- **添加/修改**：设置商品的当前库存、库存上限、库存下限、存放位置、最后入库日期
- **预警**：库存低于下限的行以红色高亮显示
- **搜索**：按商品名称过滤
- 支持排序、CSV 导出、F5 刷新

### 销售记录

- **添加销售**：选择商品、输入数量，系统自动根据商品售价计算销售金额；同时校验库存是否充足，不足时拦截并提示
- **自动扣减**：销售记录添加成功后，自动扣减对应商品的库存
- 销售日期默认为当天，可手动修改
- 支持按商品名称搜索、排序、CSV 导出、F5 刷新

### 用户管理（仅管理员）

管理员可以添加、修改、删除用户。用户角色支持三种：
- **管理员**：拥有全部权限
- **采购员**：可管理商品和库存
- **销售员**：可管理销售记录

编辑用户时密码输入框留空表示不修改密码。

### 修改密码

点击底部状态栏的"修改密码"按钮（蓝色），弹出对话框：
1. 输入原密码
2. 输入新密码（至少 4 位）
3. 确认新密码
4. 两次新密码需一致，且新密码不能与原密码相同

### 底部状态栏

- 显示当前登录用户和角色
- "修改密码"按钮（蓝色）
- "退出登录"按钮（红色）：退回登录界面

## 技术架构

### 整体架构

```
┌─────────────────────────────────────────────────┐
│                    UI 层 (Swing)                  │
│  LoginFrame  MainFrame  DashboardPanel           │
│  ProductPanel  CategoryPanel  InventoryPanel     │
│  SalePanel  UserPanel                            │
├─────────────────────────────────────────────────┤
│                  服务层 (Service)                  │
│  UserService  ProductService  CategoryService    │
│  InventoryService  SaleService                   │
│  ServiceFactory（单例工厂）                         │
├─────────────────────────────────────────────────┤
│                仓储层 (Repository)                 │
│  UserRepository  ProductRepository               │
│  CategoryRepository  InventoryRepository         │
│  SaleRepository                                  │
│  ↕ Jdbc* 实现类（纯 JDBC + PreparedStatement）     │
├─────────────────────────────────────────────────┤
│              工具层 (Util)                         │
│  DBUtil（SQLite 连接管理）                          │
│  HashUtil（bcrypt 密码哈希）                       │
│  DatabaseInitializer（自动建表+初始数据）             │
├─────────────────────────────────────────────────┤
│              数据存储                               │
│  SQLite 数据库（freshstore.db）                     │
│  bcrypt 加密密码                                   │
└─────────────────────────────────────────────────┘
```

采用经典三层架构：UI → Service → Repository → SQLite。各层之间通过接口解耦，Service 层通过构造器注入依赖，Repository 层可替换实现（如切换数据库或用于测试的 Mock）。

### 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 24 | 开发语言 |
| Swing | — | 桌面 GUI 框架 |
| SQLite | 3.x（sqlite-jdbc 3.49.1.0）| 嵌入式数据库 |
| bcrypt | 0.4（jbcrypt）| 密码哈希，12 轮加盐 |
| SLF4J + Logback | 2.0.16 / 1.5.16 | 日志框架 |
| JUnit 5 | 5.11.4 | 单元测试 + 集成测试 |
| AssertJ | 3.27.3 | 流式断言 |
| Mockito | 5.15.2 | Mock 框架 |

### 项目结构

```
FreshStoreSystem/
├── README.md
├── pom.xml                              # Maven 构建配置
├── freshstore.db                        # SQLite 数据库（自动生成）
├── sql/                                 # 参考 SQL 脚本（MySQL 版）
├── target/
│   ├── FreshStoreSystem.jar             # 可执行胖 JAR（14MB）
│   ├── start.bat                        # Windows 启动脚本
│   ├── classes/                         # 编译产物
│   └── work/                            # JAR 打包临时目录
└── src/
    ├── main/java/com/freshstore/
    │   ├── Main.java                    # 入口：初始化数据库 → 启动登录窗口
    │   ├── entity/
    │   │   ├── User.java                # 用户实体
    │   │   ├── Product.java             # 商品实体
    │   │   ├── Category.java            # 类别实体
    │   │   ├── Inventory.java           # 库存实体
    │   │   └── Sale.java                # 销售实体
    │   ├── repository/
    │   │   ├── UserRepository.java      # 用户仓储接口
    │   │   ├── ProductRepository.java   # 商品仓储接口
    │   │   ├── CategoryRepository.java  # 类别仓储接口
    │   │   ├── InventoryRepository.java # 库存仓储接口
    │   │   ├── SaleRepository.java      # 销售仓储接口
    │   │   ├── JdbcUserRepository.java  # JDBC 实现
    │   │   ├── JdbcProductRepository.java
    │   │   ├── JdbcCategoryRepository.java
    │   │   ├── JdbcInventoryRepository.java
    │   │   └── JdbcSaleRepository.java
    │   ├── service/
    │   │   ├── UserService.java         # 登录认证、密码修改、CRUD
    │   │   ├── ProductService.java      # 商品业务逻辑
    │   │   ├── CategoryService.java     # 类别业务逻辑
    │   │   ├── InventoryService.java    # 库存业务逻辑
    │   │   ├── SaleService.java         # 销售业务逻辑（含库存校验）
    │   │   └── ServiceFactory.java      # 服务单例工厂
    │   ├── ui/
    │   │   ├── LoginFrame.java          # 登录窗口
    │   │   ├── MainFrame.java           # 主窗口（标签页 + 状态栏）
    │   │   ├── DashboardPanel.java      # 仪表盘（统计卡片 + 预警）
    │   │   ├── ProductPanel.java        # 商品管理面板
    │   │   ├── CategoryPanel.java       # 类别管理面板
    │   │   ├── InventoryPanel.java      # 库存管理面板
    │   │   ├── SalePanel.java           # 销售记录面板
    │   │   └── UserPanel.java           # 用户管理面板
    │   ├── util/
    │   │   ├── DBUtil.java              # SQLite 连接管理
    │   │   ├── HashUtil.java            # bcrypt 加解密工具
    │   │   └── DatabaseInitializer.java # 自动建表 + 种子数据
    │   └── exception/
    │       ├── BusinessException.java   # 业务异常基类
    │       ├── AuthenticationException.java # 认证异常
    │       └── ValidationException.java # 校验异常
    └── test/java/com/freshstore/
        ├── util/
        │   └── HashUtilTest.java        # bcrypt 单元测试（8 个）
        ├── service/
        │   ├── UserServiceTest.java     # 用户服务测试（17 个）
        │   ├── ProductServiceTest.java  # 商品服务测试（13 个）
        │   ├── CategoryServiceTest.java # 类别服务测试（6 个）
        │   ├── InventoryServiceTest.java # 库存服务测试（7 个）
        │   └── SaleServiceTest.java     # 销售服务测试（8 个）
        └── repository/
            ├── JdbcUserRepositoryTest.java  # 用户仓储集成测试（8 个）
            └── JdbcProductRepositoryTest.java # 商品仓储集成测试（8 个）
```

### 数据库设计

```
Category ──< Product ──< Inventory
    │            │
    │            └──< Sale
    │
    └──< (无直接关联)
Users（独立，登录认证 + 角色权限）
```

**5 张核心表：**

| 表名 | 主键 | 关键字段 |
|------|------|----------|
| Category | category_id | category_name (UNIQUE) |
| Product | product_id | name, category_id (FK), spec, unit, cost, price, shelf_life, supplier_name |
| Inventory | product_id (FK) | stock, max_stock, min_stock, location, last_in |
| Sale | sale_id | store_id, product_id (FK), quantity, amount, sale_date |
| Users | user_id | username (UNIQUE), password (bcrypt hash), role |

全部使用 SQLite 外键约束（`PRAGMA foreign_keys = ON`），保证数据完整性。

### 安全设计

- **bcrypt 密码哈希**：12 轮加盐，每用户独立盐值，抵御彩虹表攻击
- **SQL 注入防护**：全部数据库操作使用 `PreparedStatement` 参数化查询
- **密码不可见**：用户管理面板不展示密码哈希值；列表接口自动清空密码字段
- **认证校验**：登录时 bcrypt 验证；修改密码需先验证原密码
- **异常安全**：`HashUtil.verify()` 对所有异常静默返回 `false`，不泄露哈希格式信息

### 设计模式

| 模式 | 应用位置 | 说明 |
|------|----------|------|
| 仓储模式 (Repository) | repository 包 | 接口定义数据访问契约，JDBC 实现类处理 SQLite |
| 服务层模式 (Service Layer) | service 包 | 封装业务逻辑、校验规则、异常转换 |
| 单例工厂 (Singleton Factory) | ServiceFactory | 懒加载单例，统一管理依赖注入 |
| 构造器注入 (Constructor DI) | 全部 Service | 依赖通过构造器传入，便于测试 Mock |
| 模板方法 (Template Method) | UI 面板 | 各 Panel 遵循统一的工具栏+表格+操作按钮布局 |

### UI 设计

- **配色**：暖白底色（#FAF7F0）+ 绿色主色（#2E7D32）+ 橙色强调（#E65100）
- **字体**：标题使用"微软雅黑"，正文使用 SansSerif
- **表格**：行高 28-30px，选中行绿色高亮，库存低预警行红色标记
- **一致性**：`MainFrame.makeButton()` 和 `MainFrame.makeTitledPanel()` 提供统一组件样式
- **交互**：所有操作有确认对话框，Enter 键提交表单，F5 刷新数据

### 测试

**75 个测试全部通过，覆盖两层：**

| 层级 | 文件 | 测试数 | 方式 |
|------|------|--------|------|
| 单元测试 | HashUtilTest | 8 | 纯函数测试 |
| 单元测试 | UserServiceTest | 17 | Mockito Mock Repository |
| 单元测试 | ProductServiceTest | 13 | Mockito Mock Repository |
| 单元测试 | CategoryServiceTest | 6 | Mockito Mock Repository |
| 单元测试 | InventoryServiceTest | 7 | Mockito Mock Repository |
| 单元测试 | SaleServiceTest | 8 | Mockito Mock 多个依赖 |
| 集成测试 | JdbcUserRepositoryTest | 8 | 真实 SQLite（临时文件） |
| 集成测试 | JdbcProductRepositoryTest | 8 | 真实 SQLite（临时文件） |

集成测试使用 `DBUtil.setUrl()` 切换到临时 SQLite 文件，每个测试方法独立的数据库实例，确保测试隔离。

### 关键业务规则

- **销售关联库存**：添加销售时自动校验库存充足性，不足则拒绝并提示所需数量
- **自动计算金额**：销售金额 = 商品售价 × 数量，自动填充（可手动覆盖）
- **库存数值校验**：库存/上限/下限均不能为负数，分别给出明确提示
- **密码复杂度**：新密码至少 4 位，不能与原密码相同
- **角色权限**：管理员可访问用户管理面板，采购员/销售员不可见

## 构建说明

### 编译

```bash
# 编译主代码
JR=/path/to/jars
javac --enable-preview -source 24 -d target/classes \
  -cp "$JR/jbcrypt.jar:$JR/sqlite-jdbc.jar" @main-sources.txt

# 编译测试代码
javac --enable-preview -source 24 -d target/test-classes \
  -cp "target/classes:$JR/jbcrypt.jar:...:$JR/byte-buddy-agent.jar" @test-sources.txt
```

### 运行测试

```bash
java --enable-preview --enable-native-access=ALL-UNNAMED \
  -cp "target/classes;target/test-classes;...all-jars..." \
  org.junit.platform.console.ConsoleLauncher \
  --select-package com.freshstore
```

### 打包胖 JAR

```bash
mkdir target/work && cd target/work
cp ../classes/com . && jar xf $JR/jbcrypt.jar && jar xf $JR/sqlite-jdbc.jar
echo "Main-Class: com.freshstore.Main" > MANIFEST.MF
jar cmf MANIFEST.MF ../FreshStoreSystem.jar .
```

> Maven 未安装在当前环境，以上通过手动 javac + jar 完成构建。

## 常见问题

**Q: 双击 JAR 无法启动？**

使用 `target\start.bat` 启动，或命令行加 `--enable-native-access=ALL-UNNAMED` 参数。JDK 24 默认禁止 JNI 本地访问，SQLite 驱动需要此参数。

**Q: 数据库文件在哪里？**

`freshstore.db` 在程序运行目录自动生成。删除此文件后重新启动，将自动重建并插入初始数据。

**Q: 如何重置数据？**

删除 `freshstore.db` 文件，重新启动程序即可。
