package com.foody.net.commons.city.model.google;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DistanceFromApiGoogleModel {

  private List<String> destination_addresses;
  private List<String> origin_addresses;
  private List<Row> rows;
  private String status;
}
