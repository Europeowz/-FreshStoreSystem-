package entity;

/** 商品信息实体 */
public class Product {
    private String productId;
    private String name;
    private String categoryId;
    private String categoryName;   // 联查字段
    private String spec;
    private String unit;
    private double cost;
    private double price;
    private int    shelfLife;
    private String supplierName;
    private String regDate;

    public Product() {}

    public String getProductId()     { return productId; }
    public void   setProductId(String v)   { this.productId = v; }
    public String getName()          { return name; }
    public void   setName(String v)        { this.name = v; }
    public String getCategoryId()    { return categoryId; }
    public void   setCategoryId(String v)  { this.categoryId = v; }
    public String getCategoryName()  { return categoryName; }
    public void   setCategoryName(String v){ this.categoryName = v; }
    public String getSpec()          { return spec; }
    public void   setSpec(String v)        { this.spec = v; }
    public String getUnit()          { return unit; }
    public void   setUnit(String v)        { this.unit = v; }
    public double getCost()          { return cost; }
    public void   setCost(double v)        { this.cost = v; }
    public double getPrice()         { return price; }
    public void   setPrice(double v)       { this.price = v; }
    public int    getShelfLife()     { return shelfLife; }
    public void   setShelfLife(int v)      { this.shelfLife = v; }
    public String getSupplierName()  { return supplierName; }
    public void   setSupplierName(String v){ this.supplierName = v; }
    public String getRegDate()       { return regDate; }
    public void   setRegDate(String v)     { this.regDate = v; }
}
