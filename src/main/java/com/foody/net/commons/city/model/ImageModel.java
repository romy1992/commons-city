package com.foody.net.commons.city.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ImageModel {

  private UUID idImage;
  private UUID idLocal;
  private String filepath;
  private String webviewPath;
  private String base64Data;
  private Boolean flgGallery;
}
