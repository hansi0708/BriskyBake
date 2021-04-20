package com.hv.briskybake.Model;

public class Order {
    private String ProductId;
    private String ProductNmae;
    private String Quality;
    private String Price;
    private String Discount;

    public Order() {
    }

    public Order(String productId, String productNmae, String quality, String price, String discount) {
        ProductId = productId;
        ProductNmae = productNmae;
        Quality = quality;
        Price = price;
        Discount = discount;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getProductNmae() {
        return ProductNmae;
    }

    public void setProductNmae(String productNmae) {
        ProductNmae = productNmae;
    }

    public String getQuality() {
        return Quality;
    }

    public void setQuality(String quality) {
        Quality = quality;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }
}
