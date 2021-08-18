package com.foody.net.commons.city.model.google;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Result {
  private String business_status;
  private Geometry geometry;
  private String icon;
  private String name;
  private OpeningHours opening_hours;
  private List<PhotoModel> photos;
  private String place_id;
  private PlusCode plus_code;
  private long price_level;
  private double rating;
  private String reference;
  private String scope;
  private List<String> types;
  private long user_ratings_total;
  private String vicinity;

  private List<AddressComponent> address_components;
  private String adr_address;
  private String formatted_address;
  private String formatted_phone_number;
  private String international_phone_number;
  private String website;
  private String url;
  private long utc_offset;
}
