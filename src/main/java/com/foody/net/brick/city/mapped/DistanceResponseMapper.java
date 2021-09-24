package com.foody.net.brick.city.mapped;

import com.commons.shared.mapper.FNMapper;
import com.foody.net.brick.city.resource.entity.DistanceResponseEntity;
import com.foody.net.commons.city.model.google.DistanceResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DistanceResponseMapper
    extends FNMapper<DistanceResponse, DistanceResponseEntity> {}
