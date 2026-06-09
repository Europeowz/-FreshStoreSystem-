package com.freshstore.entity;

/** 销售信息实体 */
public class Sale {
    private String saleId;
    private String storeId;
    private String productId;
    private String productName;   // JOIN 联查字段
    private int quantity;
    private double amount;
    private String saleDate;
    private String userId;

    public Sale() {}

    // --- getters / setters ---

    public String getSaleId()      { return saleId; }
    public void   setSaleId(String v)     { this.saleId = v; }
    public String getStoreId()     { return storeId; }
    public void   setStoreId(String v)    { this.storeId = v; }
    public String getProductId()   { return productId; }
    public void   setProductId(String v)  { this.productId = v; }
    public String getProductName() { return productName; }
    public void   setProductName(String v){ this.productName = v; }
    public int    getQuantity()    { return quantity; }
    public void   setQuantity(int v)      { this.quantity = v; }
    public double getAmount()      { return amount; }
    public void   setAmount(double v)     { this.amount = v; }
    public String getSaleDate()    { return saleDate; }
    public void   setSaleDate(String v)   { this.saleDate = v; }
    public String getUserId()      { return userId; }
    public void   setUserId(String v)     { this.userId = v; }
}
