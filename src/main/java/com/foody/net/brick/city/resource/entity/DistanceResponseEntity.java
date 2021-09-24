package com.foody.net.brick.city.resource.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "DISTANCERESPONSE")
public class DistanceResponseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DISTANCERESPONSE_SEQUENCE")
  @GenericGenerator(name = "DISTANCERESPONSE_SEQUENCE", strategy = "uuid2")
  @Column(name = "ID_DISTANCE_RESPONSE", unique = true, columnDefinition = "BINARY(16)")
  @Basic(optional = false)
  private UUID idDistanceResponse;

  @Column(name = "NAMELOCAL")
  private String nameLocal;

  @Column(name = "ADDRESSES")
  private String addresses;

  @Column(name = "VERIFIED")
  private boolean verified;

  @Column(name = "COORDINATE")
  private String coordinate;

  @Column(name = "CITY")
  private String city;

  @Column(name = "PROVINCE")
  private String province;

  @ElementCollection private List<String> typesGoogle;

  @Column(name = "PHOTOREFERENCE")
  private String photoReference;

  @Column(name = "RATING")
  private double rating;

  @ElementCollection private List<String> typesLocal;
}
