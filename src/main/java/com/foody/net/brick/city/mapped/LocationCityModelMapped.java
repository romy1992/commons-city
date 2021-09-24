package com.foody.net.brick.city.mapped;

import com.foody.net.brick.city.utils.Util;
import com.foody.net.commons.city.model.HoursModel;
import com.foody.net.commons.city.model.LocalModel;
import com.foody.net.commons.city.model.OfferModel;
import com.foody.net.commons.city.model.google.AddressComponent;
import com.foody.net.commons.city.model.google.DistanceResponse;
import com.foody.net.commons.city.model.google.Geometry;
import com.foody.net.commons.city.model.google.GoogleMapsModel;
import com.foody.net.commons.city.model.google.OfferCityModel;
import com.foody.net.commons.city.model.google.PhotoModel;
import com.foody.net.commons.city.model.google.Result;
import org.mapstruct.AfterMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Mapper(componentModel = "spring")
public abstract class LocationCityModelMapped {

  private static final List<String> WEEK_DAY_TEXT_IT =
      Arrays.asList("Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica");
  private static final String PROV_DI = "Provincia di";
  private static final String PROV_DELL = "Provincia dell'";
  private static final String LOCALITY = "locality";
  private static final String PROVINCE = "administrative_area_level_2";

  @Autowired private Util util;

  @Mapping(target = "lat", source = "model.result.geometry.location.lat")
  @Mapping(target = "lng", source = "model.result.geometry.location.lng")
  @Mapping(
      target = "province",
      source = "model.result.address_components",
      qualifiedByName = "setProvince")
  @Mapping(target = "city", source = "model.result.address_components", qualifiedByName = "setName")
  @Mapping(target = "icon", source = "model.result.icon")
  @Mapping(target = "name", source = "model.result.name")
  @Mapping(
      target = "hours",
      source = "model.result.opening_hours.weekday_text",
      qualifiedByName = "hoursModel")
  @Mapping(target = "open_now", source = "model.result.opening_hours.open_now")
  @Mapping(target = "compound_code", source = "model.result.plus_code.compound_code")
  @Mapping(target = "place_id", source = "model.result.place_id")
  @Mapping(target = "types", source = "model.result.types")
  @Mapping(target = "user_ratings_total", source = "model.result.user_ratings_total")
  @Mapping(target = "rating", source = "model.result.rating")
  @Mapping(target = "vicinity", source = "model.result.vicinity")
  @Mapping(target = "formatted_address", source = "model.result.formatted_address")
  @Mapping(target = "formatted_phone_number", source = "model.result.formatted_phone_number")
  @Mapping(
      target = "international_phone_number",
      source = "model.result.international_phone_number")
  @Mapping(target = "website", source = "model.result.website")
  @Mapping(target = "url", source = "model.result.url")
  @Mapping(target = "photoReference", source = "result.photos", qualifiedByName = "photoReference")
  public abstract LocalModel mapped(GoogleMapsModel model);

  @Mapping(target = "province", source = "address_components", qualifiedByName = "setProvince")
  @Mapping(target = "city", source = "address_components", qualifiedByName = "setName")
  @Mapping(target = "hours", source = "opening_hours.weekday_text", qualifiedByName = "hoursModel")
  public abstract LocalModel mappedResult(Result resultModel);

  @Mapping(target = "nameLocal", source = "name")
  @Mapping(target = "distance", ignore = true)
  @Mapping(target = "openNow", source = "open_now")
  @Mapping(target = "rating", source = "rating")
  @Mapping(target = "verified", constant = "false")
  @Mapping(target = "coordinate", ignore = true)
  @Mapping(target = "city", source = "vicinity", qualifiedByName = "city")
  @Mapping(target = "typesGoogle", source = "types")
  @Mapping(target = "typesLocal", source = "typesLocal")
  @Mapping(target = "offers", source = "offers")
  @Mapping(target = "photoReference", source = "photos", qualifiedByName = "photoReference")
  public abstract DistanceResponse mapperDistanceResponse(LocalModel localModel);

  @Mapping(target = "nameLocal", source = "name")
  @Mapping(target = "addresses", ignore = true)
  @Mapping(target = "distance", ignore = true)
  @Mapping(target = "openNow", source = "opening_hours.open_now")
  @Mapping(target = "rating", source = "rating")
  @Mapping(target = "verified", constant = "false")
  @Mapping(target = "coordinate", source = "geometry", qualifiedByName = "coordinate")
  @Mapping(target = "city", source = "vicinity", qualifiedByName = "city")
  @Mapping(
      target = "province",
      source = "plus_code.compound_code",
      qualifiedByName = "provinceByResult")
  @Mapping(target = "typesGoogle", source = "types")
//  @Mapping(target = "typesLocal", qualifiedByName = "emptyList")
//  @Mapping(target = "offers", qualifiedByName = "emptyList")
  @Mapping(target = "photoReference", source = "photos", qualifiedByName = "photoReference")
  public abstract DistanceResponse mapperDistanceResponse(Result result);

  @Mapping(target = "nameLocal", source = "distanceResponse.nameLocal")
  @Mapping(target = "addresses", source = "distanceResponse.addresses")
  @Mapping(target = "distance", source = "distanceResponse.distance")
  @Mapping(target = "openNow", source = "distanceResponse.openNow")
  @Mapping(target = "title", source = "offerModel.title")
  @Mapping(target = "description", source = "offerModel.description")
  @Mapping(target = "image", source = "offerModel.image")
  @Mapping(target = "datInsert", source = "offerModel.datInsert")
  public abstract OfferCityModel mapperOfferList(
      DistanceResponse distanceResponse, OfferCityModel offerModel);

  @IterableMapping(elementTargetType = OfferCityModel.class)
  public abstract List<OfferCityModel> mapperOfferList(List<OfferModel> modelList);

  @Named("hoursModel")
  public List<HoursModel> compositionHours(List<String> week) {
    List<HoursModel> hoursModels = new ArrayList<>();
    AtomicInteger pos = new AtomicInteger(0);
    String valueDefault = "00:00";
    if (week != null)
      week.forEach(
          dayFor -> {
            HoursModel hoursModel = new HoursModel();
            String day = WEEK_DAY_TEXT_IT.get(pos.get()).trim();
            hoursModel.setDay(day.toUpperCase());
            hoursModel.setOpenAM(valueDefault);
            hoursModel.setCloseAM(valueDefault);
            hoursModel.setOpenPM(valueDefault);
            hoursModel.setClosePM(valueDefault);
            hoursModel.setOpen(true);
            hoursModel.setContinuedSchedule(true);
            pos.set(pos.get() + 1);
            hoursModels.add(hoursModel);
          });

    return hoursModels;
  }

  @Named("setProvince")
  public String setProvince(List<AddressComponent> addressComponents) {
    return addressComponents != null
        ? addressComponents.stream()
            .filter(Objects::nonNull)
            .filter(ad -> ad.getTypes().contains(PROVINCE))
            .filter(ad -> ad.getLong_name() != null)
            .findAny()
            .orElse(new AddressComponent())
            .getShort_name()
        : null;
  }

  @Named("setName")
  public String setNameCity(List<AddressComponent> addressComponents) {
    return addressComponents != null
        ? addressComponents.stream()
            .filter(Objects::nonNull)
            .filter(ad -> ad.getTypes().contains(LOCALITY))
            .findAny()
            .orElse(new AddressComponent())
            .getLong_name()
        : null;
  }

  @AfterMapping
  public void setAfter(@MappingTarget DistanceResponse distanceResponse, LocalModel local) {
    distanceResponse.setAddresses(
        local.getFormatted_address() != null ? local.getFormatted_address() : local.getVicinity());
    distanceResponse.setProvince(
        local.getFormatted_address() != null
            ? util.splitProvince(local.getFormatted_address())
            : util.splitAddress(local.getVicinity()));
  }

  @Named("city")
  public String setCity(String vicinity) {
    return util.splitAddress(vicinity);
  }

  @Named("provinceByResult")
  public String setProvinceByResult(String province) {
    return util.splitProvince(province);
  }

  @Named("photoReference")
  public String setPhoto(List<PhotoModel> photos) {
    return Optional.ofNullable(photos)
        .flatMap(ph -> ph.stream().findFirst())
        .orElse(new PhotoModel())
        .getPhoto_reference();
  }

  @Named("coordinate")
  public String setCoordinate(Geometry geometry) {
    return geometry.getLocation().getLat() + "," + geometry.getLocation().getLng();
  }

  @Named("emptyList")
  public List<Object> emptyList() {
    return Collections.emptyList();
  }
}
