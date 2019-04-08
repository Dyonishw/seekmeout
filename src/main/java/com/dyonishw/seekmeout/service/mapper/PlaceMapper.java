package com.dyonishw.seekmeout.service.mapper;

import com.dyonishw.seekmeout.domain.*;
import com.dyonishw.seekmeout.service.dto.PlaceDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Place and its DTO PlaceDTO.
 */
@Mapper(componentModel = "spring", uses = {ActivityMapper.class})
public interface PlaceMapper extends EntityMapper<PlaceDTO, Place> {


    @Mapping(target = "placeEvents", ignore = true)
    Place toEntity(PlaceDTO placeDTO);

    default Place fromId(Long id) {
        if (id == null) {
            return null;
        }
        Place place = new Place();
        place.setId(id);
        return place;
    }
}
