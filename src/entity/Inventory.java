package entity;

/** 库存信息实体 */
public class Inventory {
    private String productId;
    private String productName;   // 联查字段
    private int    stock;
    private int    maxStock;
    private int    minStock;
    private String location;
    private String lastIn;
    private String userId;

    public Inventory() {}

    public String getProductId()     { return productId; }
    public void   setProductId(String v)   { this.productId = v; }
    public String getProductName()   { return productName; }
    public void   setProductName(String v) { this.productName = v; }
    public int    getStock()         { return stock; }
    public void   setStock(int v)          { this.stock = v; }
    public int    getMaxStock()      { return maxStock; }
    public void   setMaxStock(int v)       { this.maxStock = v; }
    public int    getMinStock()      { return minStock; }
    public void   setMinStock(int v)       { this.minStock = v; }
    public String getLocation()      { return location; }
    public void   setLocation(String v)    { this.location = v; }
    public String getLastIn()        { return lastIn; }
    public void   setLastIn(String v)      { this.lastIn = v; }
    public String getUserId()        { return userId; }
    public void   setUserId(String v)      { this.userId = v; }
}
