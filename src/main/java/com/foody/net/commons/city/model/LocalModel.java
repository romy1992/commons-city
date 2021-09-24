package com.foody.net.commons.city.model;

import com.foody.net.commons.city.model.google.PhotoModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class LocalModel {

  private UUID idLocal;
  private UUID idUser;
  private double lat;
  private double lng;
  private String icon;
  private String name;
  private boolean open_now;
  private String photoReference;
  private List<PhotoModel> photos;
  private String compound_code;
  private String place_id;
  private List<String> types;
  private long user_ratings_total;
  private double rating;
  private String vicinity;
  private String city;
  private String province;
  private String formatted_address;
  private String formatted_phone_number;
  private String international_phone_number;
  private String website;
  private String url;
  private boolean registered;
  private boolean selfPhoto;
  private String photoProfile;

  private List<String> typesLocal;
  private String descriptionLocal;
  private List<SocialModel> socials;
  private List<CertificateModel> certificates;
  private List<OfferModel> offers;
  private List<ImageModel> gallery;
  private List<CategoryModel> categories;
  private List<HoursModel> hours;
  private HolidayModel holiday;

  public LocalModel(UUID idUser) {
    this.idUser = idUser;
  }
}
