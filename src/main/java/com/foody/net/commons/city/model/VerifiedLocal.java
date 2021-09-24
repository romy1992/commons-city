package com.foody.net.commons.city.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VerifiedLocal {

  private Boolean isVerified;
  private List<String> typesGoogle;
  private List<String> getTypesLocal;
  private List<OfferModel> offers;
  private String placeId;
  private boolean selfPhoto;
  private String photoProfile;

  public List<OfferModel> getOffers() {
    this.offers.sort(Comparator.comparing(OfferModel::getDatInsert));
    return offers;
  }
}
