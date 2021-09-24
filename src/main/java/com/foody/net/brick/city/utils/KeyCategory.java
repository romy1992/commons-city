package com.foody.net.brick.city.utils;

public enum KeyCategory {
  RESTAURANT("RISTORANTE"),
  FOOD("NELLE VICINANZE"),
  BAKERY("FORNO"),
  FORNO("FORNO"),
  PESCE("PESCE"),
  CARNE("CARNE");

  public final String codeField;

  KeyCategory(String codeField) {
    this.codeField = codeField;
  }
}
