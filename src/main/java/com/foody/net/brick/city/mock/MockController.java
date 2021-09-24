package com.foody.net.brick.city.mock;

import com.commons.shared.general.Result;
import com.foody.net.commons.city.model.HoursModel;
import com.foody.net.commons.city.model.LocalModel;
import com.foody.net.commons.city.model.SocialModel;
import com.foody.net.commons.city.model.google.AutocompleteLocalModel;
import com.foody.net.commons.city.model.google.DistanceFromApiGoogleModel;
import com.foody.net.commons.city.model.google.GoogleMapsModel;
import com.foody.net.commons.city.model.google.Prediction;
import com.foody.net.commons.city.model.google.ResponseMapsLatLng;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Blob;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/google/maps")
@CrossOrigin(
    origins = {
      "http://localhost:8100",
      "http://localhost:8200",
      "http://localhost:8101",
      "http://localhost:8201",
      "http://localhost"
    })
public class MockController {

  private static final List<String> WEEK_DAY_TEXT_EN =
      Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
  private static final List<String> WEEK_DAY_TEXT_IT =
      Arrays.asList("Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica");

  private List<String> weekSplit =
      Arrays.asList(
          "Monday: 8:30 AM – 1:45 PM, 5:00 – 9:30 PM",
          "Tuesday: 8:30 AM – 6:30 PM",
          "Wednesday: 8:30 AM – 1:45 PM, 5:00 – 9:30 PM",
          "Thursday: 8:30 AM – 1:45 PM, 5:00 – 9:30 PM",
          "Friday: 8:30 AM – 6:00 PM",
          "Saturday: 8:30 AM – 1:45 PM, 5:00 – 9:30 PM",
          "Sunday: Closed");

  //  @GetMapping("autocomplete/{value}")
  public ResponseEntity<Result<List<LocalModel>>> autocompleteLocal(
      @PathVariable("value") String value) {

    List<LocalModel> localModels =
        new EasyRandom(
                new EasyRandomParameters()
                    .collectionSizeRange(1, 1)
                    .excludeField(FieldPredicates.ofType(UUID.class))
                    .excludeField(FieldPredicates.ofType(Blob.class)))
            .objects(LocalModel.class, 2)
            .collect(Collectors.toList());
    localModels.forEach(
        local -> {
          local.getOffers().clear();
          local.getCategories().clear();
          local.getTypes().clear();
          local.getTypes().add("Carne");
          local.getTypes().add("Pesce");
          local.getTypes().add("Forno");

          local.getSocials().clear();
          SocialModel socialModelFacebook = new SocialModel();
          socialModelFacebook.setNameSocial("facebook");
          socialModelFacebook.setUrlSocial("urlFacebookTest");

          SocialModel socialModelInst = new SocialModel();
          socialModelInst.setNameSocial("instagram");
          socialModelInst.setUrlSocial("urlInstagramTest");

          SocialModel socialModelWa = new SocialModel();
          socialModelWa.setNameSocial("whatsapp");
          socialModelWa.setUrlSocial("urlWhatsappTest");

          local.getSocials().add(socialModelFacebook);
          local.getSocials().add(socialModelInst);
          local.getSocials().add(socialModelWa);

          local.setHours(testDay());

          local.setGallery(null);
          local.setHoliday(null);
        });

    return ResponseEntity.ok()
        .body(Result.createResult(true, Result.ResponseCode.COMPLETED.toString(), localModels));
  }

  private List<HoursModel> testDay() {
    List<HoursModel> hoursModels = new ArrayList<>();
    if (weekSplit != null && !weekSplit.isEmpty()) {
      weekSplit.forEach(
          we -> {
            AtomicInteger pos = new AtomicInteger();
            HoursModel hoursModel = new HoursModel();
            WEEK_DAY_TEXT_EN.forEach(
                en -> {
                  if (we.contains(en)) {
                    hoursModel.setDay(WEEK_DAY_TEXT_IT.get(pos.get()));
                    List<String> split = Arrays.asList(we.split(","));

                    String a = split.get(0).replace(en + ": ", "").trim();
                    String[] splitPosOne = a.split("–");

                    if (split.size() <= 1 && !split.get(0).endsWith("Closed")) {
                      hoursModel.setContinuedSchedule(true);

                      int b =
                          Integer.parseInt(splitPosOne[0].replace("AM", "").trim().substring(0, 1));
                      int c =
                          Integer.parseInt(splitPosOne[1].replace("PM", "").trim().substring(0, 1));

                      if (b < 10)
                        hoursModel.setOpenAM(
                            0 + splitPosOne[0].replace("AM", "").trim().concat(":00"));
                      else
                        hoursModel.setOpenAM(splitPosOne[0].replace("AM", "").trim().concat(":00"));

                      if (c < 10)
                        hoursModel.setClosePM(
                            0 + splitPosOne[1].replace("PM", "").trim().concat(":00"));
                      else
                        hoursModel.setClosePM(
                            splitPosOne[1].replace("PM", "").trim().concat(":00"));

                    } else if (split.size() != 1) {
                      hoursModel.setContinuedSchedule(false);

                      String[] splitPosTwo = split.get(1).split("–");
                      int b =
                          Integer.parseInt(splitPosOne[0].replace("AM", "").trim().substring(0, 1));
                      int c =
                          Integer.parseInt(splitPosOne[1].replace("PM", "").trim().substring(0, 1));

                      int d = Integer.parseInt(splitPosTwo[0].trim().substring(0, 1));

                      int e =
                          Integer.parseInt(splitPosTwo[1].replace("PM", "").trim().substring(0, 1));

                      if (b < 10)
                        hoursModel.setOpenAM(
                            0 + splitPosOne[0].replace("AM", "").trim().concat(":00"));
                      else
                        hoursModel.setOpenAM(splitPosOne[0].replace("AM", "").trim().concat(":00"));

                      if (c < 10)
                        hoursModel.setCloseAM(
                            0 + splitPosOne[1].replace("PM", "").trim().concat(":00"));
                      else
                        hoursModel.setCloseAM(
                            splitPosOne[1].replace("PM", "").trim().concat(":00"));

                      if (d < 10) hoursModel.setOpenPM(0 + splitPosTwo[0].trim().concat(":00"));
                      else hoursModel.setOpenPM(splitPosTwo[0].trim().concat(":00"));

                      if (e < 10)
                        hoursModel.setClosePM(
                            0 + splitPosTwo[1].replace("PM", "").trim().concat(":00"));
                      else
                        hoursModel.setClosePM(
                            splitPosTwo[1].replace("PM", "").trim().concat(":00"));
                    }

                    hoursModel.setOpen(!split.get(0).endsWith("Closed"));
                  }
                  pos.set(pos.get() + 1);
                });
            hoursModels.add(hoursModel);
          });
    }
    hoursModels.forEach(
        h -> {
          if (h.getCloseAM() != null) {
            LocalTime closeAM = LocalTime.parse(h.getCloseAM()).plusHours(12);
            h.setCloseAM(closeAM.toString());
          }
          if (h.getOpenPM() != null) {
            LocalTime openPM = LocalTime.parse(h.getOpenPM()).plusHours(12);
            h.setOpenPM(openPM.toString());
          }
          if (h.getClosePM() != null) {
            LocalTime closePM = LocalTime.parse(h.getClosePM()).plusHours(12);
            h.setClosePM(closePM.toString());
          }
        });
    return hoursModels;
  }

  public ResponseEntity<ResponseMapsLatLng> getAllCityWithLatAndLng(
      @RequestParam(name = "input") String input,
      @RequestParam(name = "inputtype") String inputType,
      @RequestParam(name = "fields") String fields,
      @RequestParam(name = "key") String key) {

    return ResponseEntity.ok(
        new EasyRandom(new EasyRandomParameters().collectionSizeRange(2, 2))
            .nextObject(ResponseMapsLatLng.class));
  }

  public ResponseEntity<GoogleMapsModel> getAllCity(
      @RequestParam(name = "location") String location,
      @RequestParam(name = "radius", required = false) String radius,
      @RequestParam(name = "keyword", required = false) String keyword,
      @RequestParam(name = "key") String key,
      @RequestParam(name = "rankby", required = false) String rankby,
      @RequestParam(name = "pagetoken", required = false) String pageToken,
      @RequestParam(name = "type", required = false) String type,
      @RequestParam(name = "language") String language) {
    return ResponseEntity.ok(
        new EasyRandom(new EasyRandomParameters().collectionSizeRange(2, 2))
            .nextObject(GoogleMapsModel.class));
  }

  public ResponseEntity<GoogleMapsModel> getAllDetails(
      @RequestParam(name = "place_id") String placeId,
      @RequestParam(name = "fields") String fields,
      @RequestParam(name = "key") String key,
      @RequestParam(name = "language") String language) {
    return ResponseEntity.ok(
        new EasyRandom(new EasyRandomParameters().collectionSizeRange(2, 2))
            .nextObject(GoogleMapsModel.class));
  }

  public ResponseEntity<String> getAllPhotos(
      @RequestParam(name = "maxwidth") String maxwidth,
      @RequestParam(name = "photoreference") String photoreference,
      @RequestParam(name = "key") String key) {
    return ResponseEntity.ok("test");
  }

  public ResponseEntity<AutocompleteLocalModel> autoComplete(
      @RequestParam(name = "input") String input,
      @RequestParam(name = "language") String language,
      @RequestParam(name = "key") String key) {

    AutocompleteLocalModel autocompleteLocalModel1 = new AutocompleteLocalModel();
    Prediction prediction = new Prediction();
    prediction.setDescription("Grill Event");
    prediction.setTypes(Arrays.asList("restaurant", "bar"));

    Prediction prediction2 = new Prediction();
    prediction2.setDescription("Ciccio");
    prediction2.setTypes(Arrays.asList("restaurant", "bar"));
    autocompleteLocalModel1.setPredictions(Arrays.asList(prediction, prediction2));

    return ResponseEntity.ok(autocompleteLocalModel1);
  }

  public ResponseEntity<DistanceFromApiGoogleModel> distanceFromGoogle(
      @RequestParam(name = "origins") String origins,
      @RequestParam(name = "destinations") String destinations,
      @RequestParam(name = "key") String key) {

    return ResponseEntity.ok(
        new EasyRandom(new EasyRandomParameters().collectionSizeRange(2, 2))
            .nextObject(DistanceFromApiGoogleModel.class));
  }
}
