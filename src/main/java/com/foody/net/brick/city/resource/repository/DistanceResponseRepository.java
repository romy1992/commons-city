package com.foody.net.brick.city.resource.repository;

import com.foody.net.brick.city.resource.entity.DistanceResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DistanceResponseRepository extends JpaRepository<DistanceResponseEntity, UUID> {

  List<DistanceResponseEntity> findAllByCity(String city);
  List<DistanceResponseEntity> findAllByProvince(String province);
}
