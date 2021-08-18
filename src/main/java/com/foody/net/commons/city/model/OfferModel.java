package com.foody.net.commons.city.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class OfferModel {

  private UUID idOffer;
  private UUID idLocal;
  private String title;
  private String description;
  private ImageModel image;
  private LocalDateTime datInsert;
}
