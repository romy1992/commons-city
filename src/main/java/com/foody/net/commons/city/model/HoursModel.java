package com.foody.net.commons.city.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class HoursModel {

  private UUID idDay;
  private String day;
  private Boolean continuedSchedule;
  private String openAM;
  private String closeAM;
  private String openPM;
  private String closePM;
  private Boolean open;
}
