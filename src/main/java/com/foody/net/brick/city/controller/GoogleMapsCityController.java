package com.foody.net.brick.city.controller;

import com.commons.shared.general.Result;
import com.foody.net.brick.city.exception.GoogleMapsException;
import com.foody.net.brick.city.service.BrkProfileService;
import com.foody.net.brick.city.service.GoogleMapsCityService;
import com.foody.net.commons.city.model.LocalModel;
import com.foody.net.commons.city.model.google.DistanceResponse;
import com.foody.net.commons.city.model.google.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
public class GoogleMapsCityController {

  @Autowired private GoogleMapsCityService service;
  @Autowired private BrkProfileService brkProfileService;

  // Per registrazione
  @GetMapping("autocomplete/{value}")
  public ResponseEntity<Result<List<LocalModel>>> autocompleteLocal(
      @PathVariable("value") String value) {
    try {
      return ResponseEntity.ok()
          .body(
              Result.createResult(
                  true,
                  Result.ResponseCode.COMPLETED.toString(),
                  service.autocompleteLocalForSigIn(value, null)));
    } catch (GoogleMapsException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Result.createResult(Result.ResponseCode.REFUSED.toString(), e));
    }
  }

  // Get all nella home dei locali in base alle coordinate
  @GetMapping("distanceFrom/{personalCoordinates}")
  public ResponseEntity<Result<Map<String, List<DistanceResponse>>>> personalCoordinates(
      @PathVariable("personalCoordinates") String personalCoordinates) {
    try {
      // Controlla se ci sono locali di ricerca già salvati in base alle coordinate
      Map<String, List<DistanceResponse>> listMap =
          service.searchLocalByGeneral(personalCoordinates);
      // Se la lista non è vuota allora mi ritorna i locali trovati in db
      if (!listMap.isEmpty())
        return ResponseEntity.ok()
            .body(Result.createResult(true, Result.ResponseCode.COMPLETED.toString(), listMap));

      // Altrimenti vuol dire che quelle coordinate sono nuove e verranno salvate alla fine di
      // questa logica
      return ResponseEntity.ok()
          .body(
              Result.createResult(
                  true,
                  Result.ResponseCode.COMPLETED.toString(),
                  service.getDistance(personalCoordinates, null, "search")));
    } catch (GoogleMapsException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Result.createResult(Result.ResponseCode.REFUSED.toString(), e));
    }
  }

  // Ricerca per i filtri
  @PostMapping("getByFilter")
  public ResponseEntity<Result<Map<String, List<DistanceResponse>>>> searchByFilter(
      @RequestParam(name = "nameOrCity", required = false) String nameOrCity,
      @RequestParam(name = "personalCoordinates") String personalCoordinates,
      @RequestParam(name = "open", required = false) boolean open,
      @RequestBody List<String> categories) {
    try {
      return ResponseEntity.ok()
          .body(
              Result.createResult(
                  true,
                  Result.ResponseCode.COMPLETED.toString(),
                  service.searchByValue(personalCoordinates, nameOrCity, open, categories)));
    } catch (GoogleMapsException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Result.createResult(Result.ResponseCode.REFUSED.toString(), e));
    }
  }

  // Per le mappe
  @GetMapping("getAllCoordinates")
  public ResponseEntity<Result<Map<String, Location>>> personalCoordinatesSearch() {
    return ResponseEntity.ok()
        .body(
            Result.createResult(
                true,
                Result.ResponseCode.COMPLETED.toString(),
                brkProfileService.getAllLocation()));
  }

  // Per click sul locale registrato
  @GetMapping("getLocalByNameAndAddress")
  public ResponseEntity<Result<LocalModel>> getLocalByNameAndAddress(
      @RequestParam(name = "nameLocal") String nameLocal,
      @RequestParam(name = "address") String address) {
    return ResponseEntity.ok()
        .body(
            Result.createResult(
                true,
                Result.ResponseCode.COMPLETED.toString(),
                brkProfileService.searchLocal(nameLocal, address)));
  }
}
