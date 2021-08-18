package com.foody.net.commons.city.model.google;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResultModelMaps {
  private List<Object> html_attributions;
  private List<GoogleMapsModel> googleMapsModels;
  private List<Result> result;
}
