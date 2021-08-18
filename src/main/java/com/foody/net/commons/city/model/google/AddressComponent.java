package com.foody.net.commons.city.model.google;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddressComponent {
  private String long_name;
  private String short_name;
  private List<String> types;
}
