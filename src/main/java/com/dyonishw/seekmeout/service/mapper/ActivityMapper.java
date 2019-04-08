package com.dyonishw.seekmeout.service.mapper;

import com.dyonishw.seekmeout.domain.*;
import com.dyonishw.seekmeout.service.dto.ActivityDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Activity and its DTO ActivityDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ActivityMapper extends EntityMapper<ActivityDTO, Activity> {


    @Mapping(target = "activityPlaces", ignore = true)
    @Mapping(target = "activityEvents", ignore = true)
    Activity toEntity(ActivityDTO activityDTO);

    default Activity fromId(Long id) {
        if (id == null) {
            return null;
        }
        Activity activity = new Activity();
        activity.setId(id);
        return activity;
    }
}
