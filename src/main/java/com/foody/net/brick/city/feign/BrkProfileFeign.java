package com.foody.net.brick.city.feign;

import com.foody.net.commons.city.model.LocalModel;
import com.foody.net.commons.city.model.VerifiedLocal;
import com.foody.net.commons.city.model.google.DistanceResponse;
import com.foody.net.commons.city.model.google.Location;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(
    name = "brk-profile",
    contextId = "ResourceRoleFeign",
    configuration = FeignClientsConfiguration.class)
@RequestMapping("api/city")
public interface BrkProfileFeign {

  @PostMapping("controller/isVerified")
  Map<String, VerifiedLocal> isVerified(@RequestBody List<String> input);

  @PostMapping("getAllLocalVerifiedFromCity")
  List<DistanceResponse> getAllFromResourceByCities(@RequestBody List<String> provinces);

  @GetMapping("getAllCoordinates")
  Map<String, Location> getAllCoordinates();

  @GetMapping("getLocalByNameAndAddress")
  LocalModel getLocalByNameAndAddress(
      @RequestParam(name = "nameLocal") String nameLocal,
      @RequestParam(name = "address") String address);
}
