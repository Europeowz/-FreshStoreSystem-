package com.freshstore.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.freshstore.entity.Product;
import com.freshstore.util.DBUtil;
import java.io.File;
import org.junit.jupiter.api.*;

@DisplayName("JdbcProductRepository integration tests")
class JdbcProductRepositoryTest {

    private JdbcProductRepository repo;
    private File tmpDb;

    @BeforeEach
    void setUp() throws Exception {
        tmpDb = File.createTempFile("test_prd_", ".db");
        tmpDb.deleteOnExit();
        DBUtil.setUrl("jdbc:sqlite:" + tmpDb.getAbsolutePath().replace('\\', '/'));

        try (var c = DBUtil.getConnection(); var s = c.createStatement()) {
            s.execute("CREATE TABLE Category(category_id TEXT PRIMARY KEY, category_name TEXT NOT NULL UNIQUE)");
            s.execute("CREATE TABLE Product(product_id TEXT PRIMARY KEY, name TEXT NOT NULL, category_id TEXT NOT NULL, spec TEXT, unit TEXT NOT NULL, cost REAL NOT NULL, price REAL NOT NULL, shelf_life INTEGER, supplier_name TEXT, reg_date TEXT)");
            s.execute("INSERT INTO Category VALUES('C01','蔬菜')");
            s.execute("INSERT INTO Category VALUES('C02','水果')");
            s.execute("INSERT INTO Product VALUES('P001','西红柿','C01','1斤/袋','斤',2.0,3.5,5,'绿源蔬菜基地','2024-01-10')");
            s.execute("INSERT INTO Product VALUES('P002','苹果','C02','2斤/袋','斤',3.0,5.5,15,'鲜果供应基地','2024-01-11')");
            s.execute("INSERT INTO Product VALUES('P003','黄瓜','C01','1斤/袋','斤',1.5,2.8,4,'绿源蔬菜基地','2024-01-11')");
        }
        repo = new JdbcProductRepository();
    }

    @AfterEach
    void tearDown() {
        DBUtil.resetUrl();
    }

    @Test
    @DisplayName("listAll returns all products with category names")
    void listAll_returnsWithCategoryNames() throws Exception {
        var products = repo.listAll();
        assertThat(products).hasSize(3);
        assertThat(products.get(0).getCategoryName()).isEqualTo("蔬菜");
    }

    @Test
    @DisplayName("findById returns product when exists")
    void findById_existing_returnsProduct() throws Exception {
        Product p = repo.findById("P001");
        assertThat(p).isNotNull();
        assertThat(p.getName()).isEqualTo("西红柿");
        assertThat(p.getPrice()).isEqualTo(3.5);
        assertThat(p.getCategoryName()).isEqualTo("蔬菜");
    }

    @Test
    @DisplayName("findById returns null when not exists")
    void findById_missing_returnsNull() throws Exception {
        assertThat(repo.findById("P999")).isNull();
    }

    @Test
    @DisplayName("searchByName finds matching products")
    void searchByName_matches() throws Exception {
        var list = repo.searchByName("苹果");
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getProductId()).isEqualTo("P002");
    }

    @Test
    @DisplayName("listByCategory filters correctly")
    void listByCategory_filters() throws Exception {
        var list = repo.listByCategory("C01");
        assertThat(list).hasSize(2);
        assertThat(list).extracting(Product::getProductId).contains("P001", "P003");
    }

    @Test
    @DisplayName("add then find returns product")
    void add_then_findById() throws Exception {
        var p = new Product();
        p.setProductId("P004"); p.setName("香蕉"); p.setCategoryId("C02");
        p.setSpec("1斤/把"); p.setUnit("斤"); p.setCost(2.2);
        p.setPrice(4.0); p.setShelfLife(7);
        p.setSupplierName("鲜果基地"); p.setRegDate("2024-01-12");
        repo.add(p);
        Product found = repo.findById("P004");
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("香蕉");
    }

    @Test
    @DisplayName("update changes product fields")
    void update_changesFields() throws Exception {
        Product p = repo.findById("P001");
        p.setPrice(4.0);
        p.setShelfLife(10);
        repo.update(p);
        Product updated = repo.findById("P001");
        assertThat(updated.getPrice()).isEqualTo(4.0);
        assertThat(updated.getShelfLife()).isEqualTo(10);
    }

    @Test
    @DisplayName("delete removes product")
    void delete_removes() throws Exception {
        repo.delete("P002");
        assertThat(repo.findById("P002")).isNull();
        assertThat(repo.listAll()).hasSize(2);
    }
}
