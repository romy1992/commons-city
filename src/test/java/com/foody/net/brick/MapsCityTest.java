package com.foody.net.brick;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.foody.net.commons.city.model.HoursModel;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
// @ContextConfiguration(
//    classes = {
//      GoogleMapsCityController.class,
//      GoogleMapsCityServiceImpl.class,
//      LocationCityModelMappedImpl.class
//    })
// @SpringBootTest(classes = BrickCityApplication.class)
public class MapsCityTest {

  private static final List<String> WEEK_DAY_TEXT_EN =
      Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
  private static final List<String> WEEK_DAY_TEXT_IT =
      Arrays.asList("lunedì", "martedì", "mercoledì", "giovedì", "venerdì", "sabato", "domenica");

  private List<String> weekSplit =
      Arrays.asList(
          "Monday: 8:30 AM – 1:45 PM, 5:00 – 9:30 PM",
          "Tuesday: 8:30 AM – 6:30 PM",
          "Wednesday: 8:30 AM – 1:45 PM, 5:00 – 9:30 PM",
          "Thursday: 8:30 AM – 1:45 PM, 5:00 – 9:30 PM",
          "Friday: 8:30 AM – 6:00 PM",
          "Saturday: 8:30 AM – 1:45 PM, 5:00 – 9:30 PM",
          "Sunday: Closed");

  private List<String> weekContinued =
      Arrays.asList(
          "lunedì: 19:30-00:00",
          "martedì: 19:30-00:00",
          "mercoledì: Chiuso",
          "giovedì: 19:30-00:00",
          "venerdì: 19:30-00:00",
          "sabato: 12:30-15:30, 19:30-00:00",
          "domenica: 12:30-15:30, 19:30-00:00");

  @Test
  public void testDay() throws JsonProcessingException {
    List<HoursModel> hoursModels = new ArrayList<>();

    weekContinued.forEach(
        dayFor -> {
          HoursModel hoursModel = new HoursModel();
          AtomicInteger pos = new AtomicInteger();
          String day = WEEK_DAY_TEXT_IT.get(pos.get()).trim();
          //          String postDay = dayFor.replace(day.concat(": "), "").trim();
          //          if (postDay.equals("Chiuso")) {
          //            hoursModel.setOpen(false);
          //          } else {
          //            hoursModel.setOpen(true);
          //            /*Divide l'orario tra Mattina e pomeriggio..Se la lista contiene solo un
          // valore allora
          //            vorrà dire che sarà aperta solo metà giornata o orario continuato
          //             */
          //            String[] splitHour = dayFor.split(",".trim());
          //            for (String h : splitHour) {
          //              String splitDivisor[] = h.split(":");
          //              int a = Integer.parseInt(splitDivisor[0]);
          //              if (a < 15) {
          //                hoursModel.setOpenAM(splitDivisor[0].concat(splitDivisor[1]));
          //              }
          //            }
          //          }

          hoursModel.setDay(day.toUpperCase());
          hoursModel.setOpenAM("00:00");
          hoursModel.setCloseAM("00:00");
          hoursModel.setOpenPM("00:00");
          hoursModel.setClosePM("00:00");
          hoursModel.setOpen(true);
          hoursModel.setContinuedSchedule(true);
          pos.set(pos.get() + 1);
        });
  }
  //  @MockBean private GoogleMapsFeign googleMapsFeign;

  //  @MockBean private ResourceProfileFeign roleFeign;
  //  @Autowired private GoogleMapsCityController controller;

  @Test
  @Ignore
  public void test() throws IOException {
    //    ObjectMapper objectMapper = new ObjectMapper();
    //    List<LocalModel> cityModels =
    //        objectMapper.readValue(
    //            new File("src/main/resources/json/detailsCity.json"),
    //            new TypeReference<List<LocalModel>>() {});
    //
    //    cityModels.forEach(a -> a.setIdLocal(UUID.randomUUID()));
    //
    //    objectMapper.writeValue(new File("src/main/resources/json/detailsCity2.json"),
    // cityModels);
    //
    //    Assert.assertNotNull(cityModels);
  }

  @Test
  @Ignore
  public void autocompleteLocalTest() {
    //    EasyRandom easyRandom = new EasyRandom(new EasyRandomParameters().collectionSizeRange(1,
    // 1));
    //    AutocompleteLocalModel autocompleteLocalModel =
    //        easyRandom.nextObject(AutocompleteLocalModel.class);
    //    ResponseMapsLatLng responseMapsLatLng = easyRandom.nextObject(ResponseMapsLatLng.class);
    //    GoogleMapsModel googleMapsModel = easyRandom.nextObject(GoogleMapsModel.class);
    //
    //    autocompleteLocalModel.getPredictions().stream()
    //        .findFirst()
    //        .ifPresent(
    //            a -> {
    //              a.getTypes().clear();
    //              a.getTypes().add("restaurant");
    //            });
    //
    //    Mockito.when(
    //            googleMapsFeign.autoComplete(
    //                ArgumentMatchers.notNull(), ArgumentMatchers.notNull(),
    // ArgumentMatchers.notNull()))
    //        .thenReturn(ResponseEntity.ok(autocompleteLocalModel));
    //
    //    Mockito.when(
    //            googleMapsFeign.getAllCityWithLatAndLng(
    //                ArgumentMatchers.notNull(),
    //                ArgumentMatchers.notNull(),
    //                ArgumentMatchers.notNull(),
    //                ArgumentMatchers.notNull()))
    //        .thenReturn(ResponseEntity.ok(responseMapsLatLng));
    //
    //    Mockito.when(
    //            googleMapsFeign.getAllDetails(
    //                ArgumentMatchers.notNull(),
    //                ArgumentMatchers.notNull(),
    //                ArgumentMatchers.notNull(),
    //                ArgumentMatchers.notNull()))
    //        .thenReturn(ResponseEntity.ok(googleMapsModel));
    //
    //    Mockito.when(
    //            googleMapsFeign.getAllCity(
    //                ArgumentMatchers.notNull(),
    //                ArgumentMatchers.notNull(),
    //                ArgumentMatchers.isNull(),
    //                ArgumentMatchers.notNull(),
    //                ArgumentMatchers.isNull(),
    //                ArgumentMatchers.isNull(),
    //                ArgumentMatchers.isNull(),
    //                ArgumentMatchers.notNull()))
    //        .thenReturn(ResponseEntity.ok(googleMapsModel));
    //
    //    List<LocalModel> localModels =
    //        Objects.requireNonNull(controller.autocompleteLocal("test").getBody()).getData();
    //    Assert.assertNotNull(localModels);
  }
}
