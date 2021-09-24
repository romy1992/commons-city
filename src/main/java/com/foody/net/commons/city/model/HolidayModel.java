package com.foody.net.commons.city.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class HolidayModel {
  private UUID idHoliday;
  private LocalDateTime initDate;
  private LocalDateTime finishDate;
}
