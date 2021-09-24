package com.foody.net.commons.city.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CategoryModel {

  private UUID idCategory;
  private String nameCategory;
  private LocalDateTime dataInsert;
  private List<MenuModel> menu;
}
