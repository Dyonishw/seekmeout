package com.dyonishw.seekmeout.service.mapper;

import com.dyonishw.seekmeout.domain.*;
import com.dyonishw.seekmeout.service.dto.EventDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Event and its DTO EventDTO.
 */
@Mapper(componentModel = "spring", uses = {ActivityMapper.class, PlaceMapper.class, UserMapper.class})
public interface EventMapper extends EntityMapper<EventDTO, Event> {

    @Mapping(source = "activityEvent.id", target = "activityEventId")
    @Mapping(source = "activityEvent.type", target = "activityEventType")
    @Mapping(source = "placeEvent.id", target = "placeEventId")
    @Mapping(source = "placeEvent.name", target = "placeEventName")
    EventDTO toDto(Event event);

    @Mapping(source = "activityEventId", target = "activityEvent")
    @Mapping(source = "placeEventId", target = "placeEvent")
    Event toEntity(EventDTO eventDTO);

    default Event fromId(Long id) {
        if (id == null) {
            return null;
        }
        Event event = new Event();
        event.setId(id);
        return event;
    }
}
