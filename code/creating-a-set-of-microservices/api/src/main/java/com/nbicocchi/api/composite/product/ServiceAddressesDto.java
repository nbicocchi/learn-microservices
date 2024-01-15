package com.nbicocchi.api.composite.product;

public class ServiceAddressesDto {
  private final String cmp;
  private final String pro;
  private final String rev;
  private final String rec;

  public ServiceAddressesDto() {
    cmp = null;
    pro = null;
    rev = null;
    rec = null;
  }

  public ServiceAddressesDto(
    String compositeAddress,
    String productAddress,
    String reviewAddress,
    String recommendationAddress) {

    this.cmp = compositeAddress;
    this.pro = productAddress;
    this.rev = reviewAddress;
    this.rec = recommendationAddress;
  }

  public String getCmp() {
    return cmp;
  }

  public String getPro() {
    return pro;
  }

  public String getRev() {
    return rev;
  }

  public String getRec() {
    return rec;
  }
}
