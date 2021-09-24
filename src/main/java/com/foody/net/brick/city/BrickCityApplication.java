package com.foody.net.brick.city;

import com.commons.authorization.config.ConfigAuth;
import com.commons.authorization.config.GenericConfigForAuth;
import com.commons.authorization.config.JWTWebSecurityConfigForAuth;
import com.foody.net.brick.city.feign.BrkProfileFeign;
import com.foody.net.brick.city.feign.GoogleMapsFeign;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableFeignClients(clients = {GoogleMapsFeign.class, BrkProfileFeign.class})
@Import({ConfigAuth.class, GenericConfigForAuth.class, JWTWebSecurityConfigForAuth.class})
public class BrickCityApplication {

  public static void main(String[] args) {
    SpringApplication.run(BrickCityApplication.class, args);
  }
}
