package com.foody.net.commons.city.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CertificateModel {

  private UUID idCertificate;
  private String nameCertificate; // DOMAIN??
  private Boolean certificated;
}
