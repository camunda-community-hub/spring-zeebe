package io.camunda.common.auth;

import java.util.Arrays;
import java.util.stream.Collectors;

/** Enum for different C8 Products */
public enum Product {
  ZEEBE(true),
  OPERATE(true),
  TASKLIST(true),
  CONSOLE(false),
  OPTIMIZE(true),
  WEB_MODELER(false),
  IDENTITY(true);

  private final boolean covered;

  Product(boolean covered) {
    this.covered = covered;
  }

  public static Product[] coveredProducts() {
    return Arrays.stream(Product.values())
        .filter(Product::covered)
        .collect(Collectors.toList())
        .toArray(new Product[0]);
  }

  public boolean covered() {
    return covered;
  }
}
