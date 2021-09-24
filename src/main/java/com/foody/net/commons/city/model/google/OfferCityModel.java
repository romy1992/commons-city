package com.foody.net.commons.city.model.google;

import com.foody.net.commons.city.model.ImageModel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class OfferCityModel {

  private UUID idOffer;
  private UUID idLocal;
  private String nameLocal;
  private String addresses;
  private BigDecimal distance;
  private boolean openNow;
  private String title;
  private String description;
  private ImageModel image;
  private LocalDateTime datInsert;
}
