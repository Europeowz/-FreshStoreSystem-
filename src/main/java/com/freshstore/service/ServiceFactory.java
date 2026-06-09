package com.freshstore.service;

import com.freshstore.repository.*;

public final class ServiceFactory {
    private static final ProductRepository PRODUCT_REPO = new JdbcProductRepository();
    private static final CategoryRepository CATEGORY_REPO = new JdbcCategoryRepository();
    private static final InventoryRepository INVENTORY_REPO = new JdbcInventoryRepository();
    private static final SaleRepository SALE_REPO = new JdbcSaleRepository();
    private static final UserRepository USER_REPO = new JdbcUserRepository();

    private static ProductService productService;
    private static CategoryService categoryService;
    private static InventoryService inventoryService;
    private static SaleService saleService;
    private static UserService userService;

    private ServiceFactory() {}

    public static synchronized ProductService getProductService() {
        if (productService == null) productService = new ProductService(PRODUCT_REPO);
        return productService;
    }

    public static synchronized CategoryService getCategoryService() {
        if (categoryService == null) categoryService = new CategoryService(CATEGORY_REPO);
        return categoryService;
    }

    public static synchronized InventoryService getInventoryService() {
        if (inventoryService == null) inventoryService = new InventoryService(INVENTORY_REPO);
        return inventoryService;
    }

    public static synchronized SaleService getSaleService() {
        if (saleService == null) saleService = new SaleService(SALE_REPO,
                getProductService(), getInventoryService());
        return saleService;
    }

    public static synchronized UserService getUserService() {
        if (userService == null) userService = new UserService(USER_REPO);
        return userService;
    }
}
