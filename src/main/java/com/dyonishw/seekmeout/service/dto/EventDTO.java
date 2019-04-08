package com.dyonishw.seekmeout.service.dto;
import java.time.LocalDate;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Event entity.
 */
public class EventDTO implements Serializable {

    private Long id;

    private String activityType;

    private String takingPlaceAt;

    private String peopleAttending;

    @NotNull
    private Boolean casual;

    @NotNull
    private LocalDate hour;


    private Long activityEventId;

    private String activityEventType;

    private Long placeEventId;

    private String placeEventName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getTakingPlaceAt() {
        return takingPlaceAt;
    }

    public void setTakingPlaceAt(String takingPlaceAt) {
        this.takingPlaceAt = takingPlaceAt;
    }

    public String getPeopleAttending() {
        return peopleAttending;
    }

    public void setPeopleAttending(String peopleAttending) {
        this.peopleAttending = peopleAttending;
    }

    public Boolean isCasual() {
        return casual;
    }

    public void setCasual(Boolean casual) {
        this.casual = casual;
    }

    public LocalDate getHour() {
        return hour;
    }

    public void setHour(LocalDate hour) {
        this.hour = hour;
    }

    public Long getActivityEventId() {
        return activityEventId;
    }

    public void setActivityEventId(Long activityId) {
        this.activityEventId = activityId;
    }

    public String getActivityEventType() {
        return activityEventType;
    }

    public void setActivityEventType(String activityType) {
        this.activityEventType = activityType;
    }

    public Long getPlaceEventId() {
        return placeEventId;
    }

    public void setPlaceEventId(Long placeId) {
        this.placeEventId = placeId;
    }

    public String getPlaceEventName() {
        return placeEventName;
    }

    public void setPlaceEventName(String placeName) {
        this.placeEventName = placeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EventDTO eventDTO = (EventDTO) o;
        if (eventDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), eventDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "EventDTO{" +
            "id=" + getId() +
            ", activityType='" + getActivityType() + "'" +
            ", takingPlaceAt='" + getTakingPlaceAt() + "'" +
            ", peopleAttending='" + getPeopleAttending() + "'" +
            ", casual='" + isCasual() + "'" +
            ", hour='" + getHour() + "'" +
            ", activityEvent=" + getActivityEventId() +
            ", activityEvent='" + getActivityEventType() + "'" +
            ", placeEvent=" + getPlaceEventId() +
            ", placeEvent='" + getPlaceEventName() + "'" +
            "}";
    }
}
