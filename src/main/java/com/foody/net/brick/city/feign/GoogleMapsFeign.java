package com.foody.net.brick.city.feign;

import com.foody.net.commons.city.model.google.AutocompleteLocalModel;
import com.foody.net.commons.city.model.google.DistanceFromApiGoogleModel;
import com.foody.net.commons.city.model.google.GoogleMapsModel;
import com.foody.net.commons.city.model.google.ResponseMapsLatLng;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(url = "https://maps.googleapis.com/maps/api/", value = "GOOGLE")
public interface GoogleMapsFeign {

  @GetMapping("place/findplacefromtext/json")
  ResponseEntity<ResponseMapsLatLng> getAllCityWithLatAndLng(
      @RequestParam(name = "input") String input,
      @RequestParam(name = "inputtype") String inputType,
      @RequestParam(name = "fields") String fields,
      @RequestParam(name = "language") String language,
      @RequestParam(name = "key") String key);

  @GetMapping("place/nearbysearch/json")
  ResponseEntity<GoogleMapsModel> getAllCity(
      @RequestParam(name = "location") String location,
      @RequestParam(name = "radius", required = false) String radius,
      @RequestParam(name = "keyword", required = false) String keyword,
      @RequestParam(name = "key") String key,
      @RequestParam(name = "rankby", required = false) String rankby,
      @RequestParam(name = "pagetoken", required = false) String pageToken,
      @RequestParam(name = "type", required = false) String type,
      @RequestParam(name = "language") String language);

  @GetMapping("place/details/json")
  ResponseEntity<GoogleMapsModel> getAllDetails(
      @RequestParam(name = "place_id") String placeId,
      @RequestParam(name = "fields") String fields,
      @RequestParam(name = "key") String key,
      @RequestParam(name = "language") String language);

  @GetMapping("place/photo")
  ResponseEntity<String> getAllPhotos(
      @RequestParam(name = "maxwidth") String maxwidth,
      @RequestParam(name = "photoreference") String photoreference,
      @RequestParam(name = "language") String language,
      @RequestParam(name = "key") String key);

  @GetMapping("place/queryautocomplete/json")
  ResponseEntity<AutocompleteLocalModel> autoComplete(
      @RequestParam(name = "input") String input,
      @RequestParam(name = "language") String language,
      @RequestParam(name = "key") String key);

  // Max 25 destination con piano gratuito...illimitati con piano a pagamento
  @GetMapping("distancematrix/json")
  ResponseEntity<DistanceFromApiGoogleModel> distanceFromGoogle(
      @RequestParam(name = "origins") String origins,
      @RequestParam(name = "destinations") String destinations,
      @RequestParam(name = "language") String language,
      @RequestParam(name = "key") String key);

  // Geolocalizza e ti restiuisce la città in base all coordinate
  @GetMapping("geocode/json")
  ResponseEntity<GoogleMapsModel> geocode(
      @RequestParam(name = "latlng") String latlng,
      @RequestParam(name = "language") String language,
      @RequestParam(name = "key") String key);
}
