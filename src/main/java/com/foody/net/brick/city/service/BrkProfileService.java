package com.foody.net.brick.city.service;

import com.foody.net.commons.city.model.LocalModel;
import com.foody.net.commons.city.model.google.Location;

import java.util.Map;

public interface BrkProfileService {

  Map<String, Location> getAllLocation();

  LocalModel searchLocal(String nameLocal, String address);
}
