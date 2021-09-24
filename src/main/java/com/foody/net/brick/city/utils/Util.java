package com.foody.net.brick.city.utils;

import com.foody.net.brick.city.mapped.LocationCityModelMapped;
import com.foody.net.brick.city.resource.crudservice.DistanceResponseResource;
import com.foody.net.commons.city.model.LocalModel;
import com.foody.net.commons.city.model.google.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class Util {

  @Autowired private LocationCityModelMapped mapped;
  @Autowired private DistanceResponseResource responseResource;

  @Value("#{'${google.maps.types}'.split(',')}")
  private List<String> types;

  @Value("#{'${google.maps.types.search}'.split(',')}")
  private List<String> typesSearch;

  @Value("${google.maps.types.locality}")
  private String locality;

  // Controlla se il tipo di locale è quello dedicato By Predication
  public boolean controllerTypeLocal(AutocompleteLocalModel local) {
    if (local != null && local.getPredictions() != null && !local.getPredictions().isEmpty())
      for (Prediction prediction : local.getPredictions()) if (controlType(prediction)) return true;

    return false;
  }

  // Logica che Controlla se il tipo di locale è quello dedicato By Predication
  private boolean controlType(Prediction prediction) {
    if (prediction.getTypes() != null && !prediction.getTypes().isEmpty())
      for (String t : prediction.getTypes()) if (types.contains(t)) return true;
    return false;
  }

  public boolean controllerTypeLocal(Result results) {
    for (String type : Arrays.asList("supermarket", "grocery_or_supermarket")) {
      if (results.getTypes().contains(type)) return false;
    }
    return true;
  }

  public String setTexKmM(String value) {
    if (value == null) return null;
    if (value.contains("km")) return value.replace("km", "").replace(",", ".").trim();
    if (value.contains("m")) return value.replace("m", "").replace(",", ".").trim();

    return value.trim();
  }

  // Logica che Mappa il tipo di ritorno omogeneo per il FE
  public List<LocalModel> mappedLocationCityModels(List<GoogleMapsModel> googleMapsModels) {
    Set<LocalModel> localModels = new HashSet<>();
    List<LocalModel> finalLocalModels = new ArrayList<>();
    googleMapsModels.forEach(model -> localModels.add(mapped.mapped(model)));
    Set<String> addressSingle =
        localModels.stream().map(LocalModel::getFormatted_address).collect(Collectors.toSet());

    // Rimuove se il nome è uguale alla via
    localModels.removeIf(b -> b.getName().equals(b.getVicinity()));
    // Rimuove se la via contiene il nome del locale
    localModels.removeIf(b -> b.getFormatted_address().contains(b.getName()));
    // Rimuove nella lista i locali duplicati
    localModels.forEach(
        loc -> {
          LocalModel searchLocal =
              finalLocalModels.stream()
                  .filter(a -> loc.getFormatted_address().equals(a.getFormatted_address()))
                  .findAny()
                  .orElse(null);
          if (addressSingle.contains(loc.getFormatted_address()) && searchLocal == null) {
            finalLocalModels.add(loc);
          }
        });

    return new ArrayList<>(finalLocalModels);
  }

  // Splitta e restiuisce ,in base all'indirizzo, il nome della città
  public String splitAddress(String vicinity) {
    if (vicinity != null) {
      String[] split = vicinity.split(",");
      return split[split.length - 1].trim();
    }
    return null;
  }

  public String splitProvince(String addresses) {
    if (addresses != null) {
      String[] split = addresses.split(",");
      return split[split.length - 1]
          .replace("Provincia di", "")
          .replace("Provincia dell'", "")
          .trim();
    }
    return null;
  }

  // Controlla se il locale è aperto o in base alle categorie richieste
  public List<DistanceResponse> searchByCategoriesOrOpenLocal(
      boolean open, List<String> categories, List<DistanceResponse> isVerified) {
    // Filtra tutto per categorie richieste se la lista di categories ha almeno un valore
    List<DistanceResponse> categoryCheck = new ArrayList<>();
    if (!categories.isEmpty()) {
      for (String category : categories) {
        categoryCheck.addAll(
            isVerified.stream()
                .filter(a -> a.getTypesLocal() != null)
                .filter(a -> a.getTypesLocal().contains(category))
                .collect(Collectors.toList()));
      }
      isVerified = categoryCheck;
    }

    // Filtra tutto per i locali aperti in questo momento se open è true
    isVerified =
        open
            ? isVerified.stream().filter(DistanceResponse::isOpenNow).collect(Collectors.toList())
            : isVerified;
    return isVerified;
  }

  // Logica che controlla se è in DB il locale e setta al modello di ritorno le rispettive distanze
  public List<String> getStringsIsVerified(
      List<DistanceResponse> distanceResponses, DistanceFromApiGoogleModel distance) {
    int position = 0;
    List<String> isVerified = new ArrayList<>();
    // Ciclo per settargli le distanze
    while (position < distanceResponses.size()) {
      if (distance != null && !distance.getRows().isEmpty()) {
        List<Element> element =
            Objects.requireNonNull(distance.getRows().stream().findFirst().orElse(null))
                .getElements();

        Distance dist = element.get(position).getDistance();
        String addresses = distance.getDestination_addresses().get(position);

        if (dist != null && dist.getText() != null && addresses != null) {
          distanceResponses.get(position).setDistance(new BigDecimal(setTexKmM(dist.getText())));
          distanceResponses.get(position).setAddresses(addresses);
          // Mappa l'input per controllare se il locale è registrato al nostro servizio
          isVerified.add(distanceResponses.get(position).getAddresses());
        }
      }
      position++;
    }
    return isVerified;
  }

  public Map<String, List<DistanceResponse>> filterByCategory(
      List<DistanceResponse> responses, boolean save) {
    Map<String, List<DistanceResponse>> stringListMap = new HashMap<>();
    Set<String> categories = new HashSet<>();
    responses.forEach(
        re -> {
          categories.addAll(
              re.getTypesLocal() != null ? re.getTypesLocal() : Collections.emptyList());
          categories.addAll(
              re.getTypesGoogle() != null ? re.getTypesGoogle() : Collections.emptyList());
        });
    categories.forEach(
        cat -> {
          if (setCategory(cat.toLowerCase())) {

            // Se è registrato mi inserisci le sue categorie
            List<DistanceResponse> distanceResponsesTypeLocal =
                responses.stream()
                    .filter(
                        dis ->
                            dis.isVerified()
                                && dis.getTypesLocal() != null
                                && dis.getTypesLocal().contains(cat))
                    .collect(Collectors.toList());

            // sovrascrivendo quelle di google
            distanceResponsesTypeLocal.addAll(
                responses.stream()
                    .filter(
                        dis ->
                            !dis.isVerified()
                                && dis.getTypesGoogle() != null
                                && dis.getTypesGoogle().contains(cat))
                    .collect(Collectors.toList()));
            if (!distanceResponsesTypeLocal.isEmpty())
              stringListMap.put(cat, distanceResponsesTypeLocal);
          }
        });

    // Adattato per il FE lato User per specificare un ritorno vuoto
    Map<String, List<DistanceResponse>> listMap = translateCategory(stringListMap);
    // Rimuovere duplicati in base al nome del locale
    listMap.forEach(
        (k, v) -> {
          List<DistanceResponse> finalList = removeDuplicateByNameLocal(v);
          v.clear();
          v.addAll(finalList);
        });
    // Salva i risultati della ricerca
    if (save) listMap.forEach((k, v) -> v.forEach(response -> responseResource.insert(response)));

    return listMap.isEmpty()
        ? new HashMap<>(Collections.singletonMap("EMPTY", Collections.emptyList()))
        : listMap;
  }

  private boolean setCategory(String category) {
    return !category.equals(locality)
        && (types.contains(category) || typesSearch.contains(category));
  }

  private Map<String, List<DistanceResponse>> translateCategory(
      Map<String, List<DistanceResponse>> stringListMap) {
    Map<String, List<DistanceResponse>> newMapTranslate = new HashMap<>();
    Set<OfferCityModel> offerModels = new HashSet<>();

    stringListMap.forEach(
        (k, v) -> {
          for (KeyCategory cat : KeyCategory.values()) {
            if (k.equalsIgnoreCase(cat.name())) {
              newMapTranslate.put(cat.codeField, v);
            }
            v.forEach(
                a ->
                    Optional.ofNullable(a.getOffers())
                        .ifPresent(
                            o ->
                                o.forEach(
                                    off -> {
                                      if (offerModels.stream()
                                          .noneMatch(
                                              m -> m.getIdOffer().equals(off.getIdOffer()))) {
                                        offerModels.add(mapped.mapperOfferList(a, off));
                                      }
                                    })));
          }
        });

    if (!offerModels.isEmpty()) {
      DistanceResponse distanceResponse = new DistanceResponse();
      distanceResponse.setOffers(new ArrayList<>(offerModels));
      newMapTranslate.put("OFFERTE VICINE", Collections.singletonList(distanceResponse));
    }
    return newMapTranslate;
  }

  // Rimuove i duplicati in base al nome del locale
  private List<DistanceResponse> removeDuplicateByNameLocal(List<DistanceResponse> list) {
    List<DistanceResponse> finalList = new ArrayList<>();
    Set<String> nameSingle =
        list.stream().map(DistanceResponse::getNameLocal).collect(Collectors.toSet());

    list.forEach(
        loc -> {
          DistanceResponse searchLocal =
              finalList.stream()
                  .filter(a -> loc.getNameLocal().equals(a.getNameLocal()))
                  .findAny()
                  .orElse(null);
          if (nameSingle.contains(loc.getNameLocal()) && searchLocal == null) {
            finalList.add(loc);
          }
        });

    return finalList;
  }
}
