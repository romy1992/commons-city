package com.foody.net.brick.city.service;

import com.commons.shared.utils.ProvinceUtils;
import com.commons.shared.utils.model.ProvinceRegion;
import com.foody.net.brick.city.exception.GoogleMapsException;
import com.foody.net.brick.city.feign.BrkProfileFeign;
import com.foody.net.brick.city.feign.GoogleMapsFeign;
import com.foody.net.brick.city.mapped.LocationCityModelMapped;
import com.foody.net.brick.city.resource.crudservice.DistanceResponseResource;
import com.foody.net.brick.city.utils.KeyWeek;
import com.foody.net.brick.city.utils.Util;
import com.foody.net.commons.city.model.LocalModel;
import com.foody.net.commons.city.model.VerifiedLocal;
import com.foody.net.commons.city.model.google.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class GoogleMapsCityServiceImpl implements GoogleMapsCityService {

  private final Map<String, ProvinceRegion> provinceOfRegion;
  @Autowired private GoogleMapsFeign googleMapsFeign;
  @Autowired private LocationCityModelMapped mapped;
  @Autowired private BrkProfileFeign roleFeign;
  @Autowired private Util util;
  @Autowired private DistanceResponseResource responseResource;

  @Value("${google.maps.keyword}")
  private String keyword;

  @Value("${google.maps.key}")
  private String key;

  @Value("${google.maps.inputtype}")
  private String inputtype;

  @Value("${google.maps.fields.for.api.getAllCityWithLatAndLng}")
  private String fieldsForCoordinates;

  @Value("${google.maps.fields.for.api.getDetils}")
  private String fieldsForDetails;

  @Value("${google.maps.language.it}")
  private String language;

  @Value("#{'${google.maps.types}'.split(',')}")
  private List<String> types;

  @Value("${google.maps.types.locality}")
  private String localityType;

  @Value("${google.maps.types.administrative_area_level_2}")
  private String administrative_area_level_2;

  @Value("${google.maps.rankby}")
  private String rankby;

  @Value("${google.maps.radius}")
  private String radius;

  public GoogleMapsCityServiceImpl() {
    provinceOfRegion = ProvinceUtils.getInstance().getProvinceAndRegion();
  }

  @Override
  public GoogleMapsModel getGoogleMapsPositionForEachCity(
      String coordinate, String pageToken, String keyword, String radius, String rankby) {
    return googleMapsFeign
        .getAllCity(coordinate, radius, keyword, key, rankby, pageToken, null, language)
        .getBody();
  }

  @Override
  public byte[] getAllPhotosForEachLocation(String photoReference) {
    if (photoReference != null) {
      ResponseEntity<String> responseEntity =
          googleMapsFeign.getAllPhotos("400", photoReference, language, key);
      if (responseEntity != null && responseEntity.getBody() != null)
        return responseEntity.getBody().getBytes();
    }
    return null;
  }

  @Override
  public GoogleMapsModel getAllDetailsForEachCity(String placeId) {
    return googleMapsFeign.getAllDetails(placeId, fieldsForDetails, key, language).getBody();
  }

  @Override
  public List<LocalModel> autocompleteLocalForSigIn(String nameOrCity, AutocompleteLocalModel local)
      throws GoogleMapsException {
    local = local == null ? getAutocompleteLocalModel(nameOrCity) : local;
    List<ResponseMapsLatLng> responseMapsLatLngList = getResponseMapsLatLngs(local);
    // Dalle coordinate controllo se è un gluten free con una distanza di 10 metri
    if (!responseMapsLatLngList.isEmpty()) {
      // Se è un Gluten free restituisco la lista dei locali/e
      return getLocationCityModels(getResults(responseMapsLatLngList));
    }
    return Collections.emptyList();
  }

  @Override
  public Map<String, List<DistanceResponse>> getDistance(
      String personalCoordinates, String keyword, String radius) {
    /*
    Api nearbysearch per farmi dare dal mio punto
     ad un raggio di 50 km tutti i locali per prendere le loro coordinate
    */
    String ran = this.rankby;

    if (keyword == null) keyword = this.keyword;

    // For api distanceFrom
    if (radius != null && radius.equals("search")) {
      radius = this.radius;
      ran = null;
    }

    // Recupera tutti i dettagli dei locali passati in input
    List<DistanceResponse> distanceResponses =
        getDistanceResponses(personalCoordinates, keyword, radius, ran);

    // Accumula le coordinate e ti restituisce le distanze
    DistanceFromApiGoogleModel distance =
        getDistanceFromApiGoogleModel(personalCoordinates, distanceResponses);

    // Controlla se è verificato in db e le mette in ordine
    List<DistanceResponse> responses = mappedResponseDistance(distanceResponses, distance);

    return util.filterByCategory(responses, true);
  }

  @Override
  public Map<String, List<DistanceResponse>> searchByValue(
      String coordinate, String nameOrCity, boolean open, List<String> categories)
      throws GoogleMapsException {

    List<LocalModel> localForName = new ArrayList<>();
    AtomicReference<List<DistanceResponse>> fromNameToDistance =
        new AtomicReference<>(new ArrayList<>());

    localForName =
        checkForNameOrCityOrCoordinates(coordinate, nameOrCity, localForName, fromNameToDistance);

    // Trasforma localForName in DistanceResponse
    List<DistanceResponse> finalFromNameToDistance = fromNameToDistance.get();
    localForName.forEach(
        local -> finalFromNameToDistance.add(mapped.mapperDistanceResponse(local)));

    // api Coordinate
    fromNameToDistance
        .get()
        .forEach(
            a -> {
              // Chiama API per le farsi dare le coordinate data una via se è diversa da null
              // altrimenti
              // per nome locale
              ResponseMapsLatLng objects =
                  googleMapsFeign
                      .getAllCityWithLatAndLng(
                          a.getAddresses() != null ? a.getAddresses() : a.getNameLocal(),
                          inputtype,
                          "geometry",
                          language,
                          key)
                      .getBody();
              String coord = null;
              if (objects != null && objects.getCandidates() != null) {
                Candidates candidates = objects.getCandidates().stream().findFirst().orElse(null);
                coord =
                    candidates != null
                        ? candidates.getGeometry().getLocation().getLat()
                            + ","
                            + candidates.getGeometry().getLocation().getLng()
                        : null;
              }
              // e le setta
              a.setCoordinate(coord);
            });

    // Accumula le coordinate e ti restituisce le distanze
    DistanceFromApiGoogleModel distance =
        getDistanceFromApiGoogleModel(coordinate, fromNameToDistance.get());
    // Controlla se è verificato in db e le mette in ordine
    List<DistanceResponse> isVerified = mappedResponseDistance(fromNameToDistance.get(), distance);

    return util.filterByCategory(
        util.searchByCategoriesOrOpenLocal(open, categories, isVerified), false);
  }

  @Override
  public Map<String, List<DistanceResponse>> searchLocalByGeneral(String personalCoordinates) {

    List<DistanceResponse> responses = new ArrayList<>();
    // Cerco la provincia in base alle mie coordinate
    String province = getProvinceByCoordinate(personalCoordinates);
    // Traduco la provincia
    ProvinceRegion checkProvince = provinceOfRegion.get(province);
    if (checkProvince != null) {
      DistanceResponse distanceResponse = new DistanceResponse();
      distanceResponse.setProvince(province);
      distanceResponse.setProvinceReference(checkProvince.getProvince());
      // Cerca i locali salvati in precedenza
      responses = responseResource.search(distanceResponse);
      if (responses.isEmpty()) return new HashMap<>();
      /*
      Prima di filtrare per categorie:
      1 - controllo se ci sono locali registrati nella nostra piattaforma
      2 - mappo la nuova distanza
      3 - se è aperto/chiuso
      4 - se ha offerte in caso di locale registrato */

      // 1 - controllo se ci sono nuovi locali registrati nella nostra piattaforma
      List<DistanceResponse> getAllLocal =
          getAllFromResourceByCities(Collections.singletonList(distanceResponse.getProvince()));

      // Lista di nomi di locali da aggiungere nella lista finale per evitare ripetizioni di locali
      Set<String> nameLocal =
          responses.stream().map(DistanceResponse::getNameLocal).collect(Collectors.toSet());
      List<DistanceResponse> finalResponses = responses;
      getAllLocal.forEach(
          loc -> {
            if (!nameLocal.contains(loc.getNameLocal())) finalResponses.add(loc);
          });

      // 2 - mappo la nuova distanza
      DistanceFromApiGoogleModel model =
          getDistanceFromApiGoogleModel(personalCoordinates, responses);
      /* Prendo il nome del locale e lo cerco nelle "destination_addresses"
      dandomi la posizione */
      responses.forEach(
          res -> {
            int position = model.getDestination_addresses().indexOf(res.getAddresses());
            if (position >= 0)
              model.getRows().stream()
                  .findFirst()
                  .ifPresent(
                      a ->
                          res.setDistance(
                              new BigDecimal(
                                  util.setTexKmM(
                                      a.getElements().get(position).getDistance().getText()))));
          });

      // TODO
      // 3 - se è aperto/chiuso

      // 4 - se ha offerte in caso di locale registrato
      Set<String> address =
          responses.stream().map(DistanceResponse::getAddresses).collect(Collectors.toSet());
      if (!address.isEmpty()) {
        // Controlla a DB se c'è corrispondenza con la via del locale
        setDistanceResponseByProfileVerified(
            roleFeign.isVerified(new ArrayList<>(address)), responses);
      }
    }
    // Filtro le categorie
    return responses.isEmpty() ? new HashMap<>() : util.filterByCategory(responses, false);
  }

  private String getProvinceByCoordinate(String personalCoordinate) {
    Result result =
        Objects.requireNonNull(googleMapsFeign.geocode(personalCoordinate, language, key).getBody())
            .getResults()
            .stream()
            .findFirst()
            .orElse(null);
    if (result != null) {
      AddressComponent addressComponent =
          result.getAddress_components().stream()
              .filter(a -> a.getTypes().contains(administrative_area_level_2))
              .findFirst()
              .orElse(null);
      return addressComponent != null ? util.splitProvince(addressComponent.getLong_name()) : "";
    }
    return "";
  }

  private List<LocalModel> checkForNameOrCityOrCoordinates(
      String coordinate,
      String nameOrCity,
      List<LocalModel> localForName,
      AtomicReference<List<DistanceResponse>> fromNameToDistance)
      throws GoogleMapsException {
    // Se nameOrCity non ha valore , cerca per le coordinate personali
    if (nameOrCity == null || nameOrCity.isEmpty()) {
      fromNameToDistance.set(splitWithCoordinates(coordinate, localForName));
    } else {
      // Se nameOrCity ha un valore, controlla prima se è una città o un nome locale
      AutocompleteLocalModel autocompleteLocalModel = getAutocompleteLocalModel(nameOrCity);
      // Se il risultato non ha come tipologia LOCALITY allora vuol dire che è un nome locale
      if (autocompleteLocalModel.getPredictions().stream()
          .filter(a -> a.getTypes() != null)
          .noneMatch(a -> a.getTypes().contains(localityType))) {
        localForName =
            autocompleteLocalForSigIn(nameOrCity, autocompleteLocalModel).stream()
                .filter(local -> local.getFormatted_address().endsWith("Italia"))
                .filter(local -> !local.getFormatted_address().contains(local.getName()))
                .collect(Collectors.toList());
      } else {
        // Altrimenti è una città
        // Prenderà le Coordinate della città
        List<LocalModel> finalLocalForName = localForName;
        getResponseMapsLatLngs(autocompleteLocalModel).stream()
            .findAny()
            .flatMap(geo -> geo.getCandidates().stream().findFirst())
            .ifPresent(
                ca -> {
                  double lat = ca.getGeometry().getLocation().getLat();
                  double lng = ca.getGeometry().getLocation().getLng();
                  // E mi restituirà tutti i locali di quella città
                  fromNameToDistance.set(splitWithCoordinates(lat + "," + lng, finalLocalForName));
                });
      }
    }
    return localForName;
  }

  private AutocompleteLocalModel getAutocompleteLocalModel(String nameOrCity)
      throws GoogleMapsException {
    // Controlla se esiste il locale nei parametri ricevuti
    AutocompleteLocalModel local =
        googleMapsFeign.autoComplete(nameOrCity, language, key).getBody();
    // Se esiste e la categoria è quella richiesta,dammi le coordinate
    if (!util.controllerTypeLocal(local)) {
      throw new GoogleMapsException(
          "Questo locale non esiste o non è un tipologia richiesta!!Riprova!");
    }
    if (local != null) local.getPredictions().removeIf(a -> !a.getDescription().endsWith("Italia"));
    return local;
  }

  // Se il nome della città o del locale è a null , splitta la logica per le coordinate
  private List<DistanceResponse> splitWithCoordinates(
      String coordinate, List<LocalModel> localForName) {
    List<DistanceResponse> fromNameToDistance;
    List<Result> results =
        getGoogleMapsPositionForEachCity(coordinate, null, keyword, this.radius, null).getResults();
    for (Result r : results) localForName.add(mapped.mappedResult(r));
    // Controlla a db se esiste un locale nella provincia
    Set<String> searchProvince = new HashSet<>();
    localForName.forEach(
        local ->
            searchProvince.add(
                local.getFormatted_address() != null
                    ? util.splitProvince(local.getFormatted_address())
                    : util.splitAddress(local.getVicinity())));
    fromNameToDistance = getAllFromResourceByCities(new ArrayList<>(searchProvince));
    return fromNameToDistance;
  }

  // Controlla se in db esiste il locale
  private List<DistanceResponse> mappedResponseDistance(
      List<DistanceResponse> distanceResponses, DistanceFromApiGoogleModel distance) {

    // Prende tutte le province
    List<String> address = util.getStringsIsVerified(distanceResponses, distance);
    if (!address.isEmpty()) {
      // Controlla a DB se c'è corrispondenza con la via del locale
      setDistanceResponseByProfileVerified(roleFeign.isVerified(address), distanceResponses);
    }

    distanceResponses =
        distanceResponses.stream()
            .filter(local -> local.getDistance() != null)
            .collect(Collectors.toList());

    // Ordina in base alla distanza
    distanceResponses.sort(Comparator.comparing(DistanceResponse::getDistance));
    return distanceResponses;
  }

  private void setDistanceResponseByProfileVerified(
      Map<String, VerifiedLocal> map, List<DistanceResponse> finalDistanceResponses) {
    map.keySet()
        .forEach(
            a ->
                finalDistanceResponses.forEach(
                    distanceResponse -> {
                      if (a.equals(distanceResponse.getAddresses())) {
                        distanceResponse.setVerified(map.get(a).getIsVerified());
                        distanceResponse.setTypesGoogle(map.get(a).getTypesGoogle());
                        distanceResponse.setTypesLocal(map.get(a).getGetTypesLocal());
                        distanceResponse.setActualDay(
                            KeyWeek.valueOf(LocalDateTime.now().getDayOfWeek().name()).codeWeek);
                        distanceResponse.setOffers(mapped.mapperOfferList(map.get(a).getOffers()));
                        /* Se il locale è registrato, non ha la referenza google foto e l'untente
                        ha deciso di usare le foto google, chiama l'api dei dettagli di maps */
                        if (distanceResponse.getPhotoReference() == null
                            && !map.get(a).isSelfPhoto()) {
                          distanceResponse.setPhotoReference(
                              Objects.requireNonNull(
                                      googleMapsFeign
                                          .getAllDetails(
                                              map.get(a).getPlaceId(), null, key, language)
                                          .getBody())
                                  .getResult()
                                  .getPhotos()
                                  .stream()
                                  .map(PhotoModel::getPhoto_reference)
                                  .findAny()
                                  .orElse(null));
                        } else {
                          distanceResponse.setSelfPhoto(map.get(a).isSelfPhoto());
                          distanceResponse.setPhotoProfile(map.get(a).getPhotoProfile());
                        }
                      }
                    }));
  }

  // Accumula le coordinate e restituisce le distanze
  //  private DistanceFromApiGoogleModel getDistanceFromApiGoogleModel(
  //      String personalCoordinates, List<DistanceResponse> distanceResponses) {
  //    // Accumula le coordinate
  //    //    distanceResponses = distanceResponses.subList(0, 25); // Limite dei 25 sulle
  // coordinate
  //    StringBuilder allCoordinates = new StringBuilder();
  //    for (DistanceResponse coordinate : distanceResponses)
  //      allCoordinates.append(coordinate.getCoordinate()).append("|");
  //
  //    // API per calcolare la distanza
  //    return googleMapsFeign
  //        .distanceFromGoogle(personalCoordinates, allCoordinates.toString(), language, key)
  //        .getBody();
  //  }

  // Accumula le coordinate e restituisce le distanze
  private DistanceFromApiGoogleModel getDistanceFromApiGoogleModel(
      String personalCoordinates, List<DistanceResponse> distanceResponses) {

    DistanceFromApiGoogleModel distance = new DistanceFromApiGoogleModel();

    List<String> destinationAddresses = new ArrayList<>();
    List<String> originAddresses = new ArrayList<>();
    List<Row> rows = new ArrayList<>();
    List<DistanceResponse> dist;
    List<Element> element = new ArrayList<>();
    boolean stop;
    // Finchè la lista non sarà minore di 25 , continuerà ad accumulare coordinate
    // Metodo adattato per il limite di 25 coordinate se si usa un account gratis Google
    do {
      if (distanceResponses.size() > 25) {
        dist = distanceResponses.subList(0, 25);
        distanceResponses = distanceResponses.subList(dist.size(), distanceResponses.size());
        stop = true;
      } else {
        dist = distanceResponses;
        stop = false;
      }

      StringBuilder allCoordinates = new StringBuilder();
      for (DistanceResponse coordinate : dist)
        allCoordinates.append(coordinate.getCoordinate()).append("|");

      DistanceFromApiGoogleModel model =
          googleMapsFeign
              .distanceFromGoogle(personalCoordinates, allCoordinates.toString(), language, key)
              .getBody();

      if (model != null) {
        destinationAddresses.addAll(model.getDestination_addresses());
        originAddresses.addAll(model.getOrigin_addresses());
        model
            .getRows()
            .forEach(
                a -> {
                  element.addAll(a.getElements());
                  a.setElements(element);
                });
        rows = model.getRows();
      }
    } while (distanceResponses.size() > 25 || stop);

    distance.setDestination_addresses(destinationAddresses);
    distance.setOrigin_addresses(originAddresses);
    distance.setRows(rows);

    return distance;
  }

  // Recupera tutti i dettagli dei locali passati in input
  private List<DistanceResponse> getDistanceResponses(
      String personalCoordinates, String keyword, String radius, String rankby) {
    List<DistanceResponse> distanceResponses = new ArrayList<>();
    String pageToken = null;
    do {
      // Api per posizione e qualche dettaglio
      GoogleMapsModel responseEntity =
          getGoogleMapsPositionForEachCity(personalCoordinates, pageToken, keyword, radius, rankby);
      pageToken = Objects.requireNonNull(responseEntity).getNext_page_token();

      // Mappa in base alla via le rispettive coordinate e prima di mappare
      // controlla se il locale è della tipologia richiesta

      responseEntity
          .getResults()
          .forEach(
              result -> {
                if (util.controllerTypeLocal(result))
                  distanceResponses.add(mapped.mapperDistanceResponse(result));
              });

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    } while (pageToken != null);

    // Restituisce tutti i locali con le corrispondenze delle province
    distanceResponses.addAll(
        getAllFromResourceByCities(
            distanceResponses.stream()
                .map(DistanceResponse::getProvince)
                .collect(Collectors.toList())));

    return new ArrayList<>(new HashSet<>(distanceResponses));
  }

  private List<DistanceResponse> getAllFromResourceByCities(List<String> province) {
    return roleFeign.getAllFromResourceByCities(province);
  }

  // Mappa il tipo di ritorno omogeneo per il FE
  private List<LocalModel> getLocationCityModels(List<Result> results) {
    List<GoogleMapsModel> cityModels = new ArrayList<>();
    for (Result result : results) cityModels.add(getAllDetailsForEachCity(result.getPlace_id()));
    return util.mappedLocationCityModels(cityModels);
  }

  // Logica che chiama l'API di maps che restituisce determinati parametri che servono per la
  // registrazione del Locale
  private List<Result> getResults(List<ResponseMapsLatLng> responseMapsLatLngList) {
    List<Result> results = new ArrayList<>();
    for (ResponseMapsLatLng responseMapsLatLng : responseMapsLatLngList) {
      for (Candidates candidate : responseMapsLatLng.getCandidates()) {
        String lat = String.valueOf(candidate.getGeometry().getLocation().getLat());
        String lng = String.valueOf(candidate.getGeometry().getLocation().getLng());
        // Cerca nei 2km in base alle coordinate
        results.addAll(
            Objects.requireNonNull(
                    googleMapsFeign
                        .getAllCity(lat + "," + lng, "2", null, key, null, null, null, language)
                        .getBody())
                .getResults());
      }
    }
    // Filtra e controlla se la tipologia è quella giusta
    return results.stream()
        .filter(
            res -> {
              for (String type : res.getTypes()) if (this.types.contains(type)) return true;
              return false;
            })
        .collect(Collectors.toList());
  }

  // Restituisce una lista di tutte le coordinate dato un determinato input AutocompleteLocalModel
  private List<ResponseMapsLatLng> getResponseMapsLatLngs(AutocompleteLocalModel local) {
    List<ResponseMapsLatLng> responseMapsLatLngList = new ArrayList<>();
    for (Prediction prediction : local.getPredictions()) {
      responseMapsLatLngList.add(
          Objects.requireNonNull(
              googleMapsFeign
                  .getAllCityWithLatAndLng(
                      prediction.getDescription(), inputtype, fieldsForCoordinates, language, key)
                  .getBody()));
    }
    return responseMapsLatLngList;
  }
}
