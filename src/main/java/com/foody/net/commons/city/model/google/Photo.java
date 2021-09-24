package com.foody.net.commons.city.model.google;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Photo {
  private long height;
  private List<String> html_attributions;
  private String photo_reference;
  private long width;
}
