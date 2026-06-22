package com.emras.product.constant;

public final class CacheKeys {
    private CacheKeys() {}
    public static String productDetail(Long productId) {
        return "product:detail:" + productId;
    }
    public static String productBySlug(String slug) {
        return "product:slug:" + slug;
    }
    public static String productList(String filterHash) {
        return "product:list:" + filterHash;
    }
    public static String categoryList() {
        return "category:list:all";
    }
    public static String categoryDetail(Long categoryId) {
        return "category:detail:" + categoryId;
    }
}