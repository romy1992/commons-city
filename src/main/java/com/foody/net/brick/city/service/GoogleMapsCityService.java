package com.foody.net.brick.city.service;

import com.foody.net.brick.city.exception.GoogleMapsException;
import com.foody.net.commons.city.model.LocalModel;
import com.foody.net.commons.city.model.google.AutocompleteLocalModel;
import com.foody.net.commons.city.model.google.DistanceResponse;
import com.foody.net.commons.city.model.google.GoogleMapsModel;

import java.util.List;
import java.util.Map;

public interface GoogleMapsCityService {

  GoogleMapsModel getGoogleMapsPositionForEachCity(
      String coordinate, String pageToken, String keyword, String radius, String rankby);

  GoogleMapsModel getAllDetailsForEachCity(String placeId) throws GoogleMapsException;

  byte[] getAllPhotosForEachLocation(String photoReference);

  Map<String, List<DistanceResponse>> getDistance(
      String personalCoordinates, String keyword, String radius) throws GoogleMapsException;

  List<LocalModel> autocompleteLocalForSigIn(String value, AutocompleteLocalModel local)
      throws GoogleMapsException;

  Map<String, List<DistanceResponse>> searchByValue(
      String coordinate, String value, boolean open, List<String> categories)
      throws GoogleMapsException;

  Map<String, List<DistanceResponse>> searchLocalByGeneral(String personalCoordinate);
}
