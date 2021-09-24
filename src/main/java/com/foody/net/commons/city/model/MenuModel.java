package com.foody.net.commons.city.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class MenuModel {

  private UUID idMenu;
  private String nameDish;
  private String ingredients;
  private ImageModel image;
}
