package com.foody.net.commons.city.model.google;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Prediction {

  private String description;
  private List<String> types;
}
