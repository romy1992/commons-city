package com.foody.net.commons.city.model.google;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GoogleMapsModel {

  private List<Object> html_attributions;
  private String next_page_token;
  private List<Result> results;
  private Result result;
}
