package com.antonio.core.product.web.dto;

public class Product {
  private int productId;
  private String name;
  private int weight;

  public Product() {
    productId = 0;
    name = null;
    weight = 0;
  }

  public Product(int productId, String name, int weight) {
    this.productId = productId;
    this.name = name;
    this.weight = weight;
  }

  public int getProductId() {
    return productId;
  }

  public String getName() {
    return name;
  }

  public int getWeight() {
    return weight;
  }

  public void setProductId(int productId) {
    this.productId = productId;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }


  public Product getProduct(int productId) {
    return new Product(1, "Sample Product", 1);
  }
}
