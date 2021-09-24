package com.foody.net.commons.city.model.google;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DistanceResponse {

  private String nameLocal;
  private String addresses;
  private BigDecimal distance;
  private boolean openNow;
  private double rating;
  private boolean verified;
  private String coordinate;
  private String city;
  private String province;
  private String provinceReference;
  private List<String> typesGoogle;
  private List<String> typesLocal;
  private List<OfferCityModel> offers;
  private String photoReference;
  private boolean selfPhoto;
  private String photoProfile;
  private String actualDay;

  public List<OfferCityModel> getOffers() {
    return offers == null ? new ArrayList<>() : offers;
  }
}
