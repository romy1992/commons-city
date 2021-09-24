package com.foody.net.brick.city.resource.crudservice;

import com.commons.shared.resource.FNResource;
import com.foody.net.brick.city.mapped.DistanceResponseMapper;
import com.foody.net.brick.city.resource.entity.DistanceResponseEntity;
import com.foody.net.brick.city.resource.repository.DistanceResponseRepository;
import com.foody.net.commons.city.model.google.DistanceResponse;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DistanceResponseResource
    extends FNResource<
        DistanceResponse,
        UUID,
        DistanceResponseEntity,
        DistanceResponseRepository,
        DistanceResponseMapper> {
  @Override
  public DistanceResponse insert(DistanceResponse model) {
    return mapper.converterEntityToModel(
        repository.saveAndFlush(mapper.converterModelToEntity(model)));
  }

  @Override
  public DistanceResponse getById(UUID primaryKey) {
    return mapper.converterEntityToModel(repository.findById(primaryKey).orElse(null));
  }

  @Override
  public DistanceResponse update(DistanceResponse model) {
    return mapper.converterEntityToModel(
        repository.saveAndFlush(mapper.converterModelToEntity(model)));
  }

  @Override
  public boolean deleteById(UUID primaryKey) {
    return false;
  }

  @Override
  public List<DistanceResponse> getAll(UUID primaryKey) {
    return repository.findAllById(Collections.singletonList(primaryKey)).stream()
        .map(a -> mapper.converterEntityToModel(a))
        .collect(Collectors.toList());
  }

  @Override
  public List<DistanceResponse> search(DistanceResponse model) {
    List<DistanceResponse> distanceResponses =
        repository.findAllByProvince(model.getProvince()).stream()
            .map(a -> mapper.converterEntityToModel(a))
            .collect(Collectors.toList());
    if (model.getProvinceReference() != null)
      distanceResponses.addAll(
          repository.findAllByProvince(model.getProvinceReference()).stream()
              .map(a -> mapper.converterEntityToModel(a))
              .collect(Collectors.toList()));
    return distanceResponses;
  }
}
