package com.foody.net.brick.city.utils;

public enum KeyWeek {
  MONDAY("LUNEDÌ"),
  TUESDAY("MARTEDÌ"),
  WEDNESDAY("MERCOLEDÌ"),
  THURSDAY("GIOVEDÌ"),
  FRIDAY("VENERDÌ"),
  SATURDAY("SABATO"),
  SUNDAY("DOMENICA");

  public final String codeWeek;

  KeyWeek(String codeWeek) {
    this.codeWeek = codeWeek;
  }
}
