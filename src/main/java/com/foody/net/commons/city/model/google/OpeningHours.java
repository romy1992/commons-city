package com.foody.net.commons.city.model.google;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OpeningHours {
  private boolean open_now;
  private List<Period> periods;
  private List<String> weekday_text;
}
