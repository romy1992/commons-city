package com.foody.net.brick.city.service;

import com.foody.net.brick.city.feign.BrkProfileFeign;
import com.foody.net.commons.city.model.LocalModel;
import com.foody.net.commons.city.model.google.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BrkProfileServiceImpl implements BrkProfileService {

  @Autowired private BrkProfileFeign brkProfileFeign;

  @Override
  public Map<String, Location> getAllLocation() {
    return brkProfileFeign.getAllCoordinates();
  }

  @Override
  public LocalModel searchLocal(String nameLocal, String address) {
    return brkProfileFeign.getLocalByNameAndAddress(nameLocal, address);
  }
}
