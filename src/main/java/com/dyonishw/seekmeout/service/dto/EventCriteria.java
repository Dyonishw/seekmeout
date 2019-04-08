package com.dyonishw.seekmeout.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.LocalDateFilter;

/**
 * Criteria class for the Event entity. This class is used in EventResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /events?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class EventCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter activityType;

    private StringFilter takingPlaceAt;

    private StringFilter peopleAttending;

    private BooleanFilter casual;

    private LocalDateFilter hour;

    private LongFilter activityEventId;

    private LongFilter placeEventId;

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getActivityType() {
        return activityType;
    }

    public void setActivityType(StringFilter activityType) {
        this.activityType = activityType;
    }

    public StringFilter getTakingPlaceAt() {
        return takingPlaceAt;
    }

    public void setTakingPlaceAt(StringFilter takingPlaceAt) {
        this.takingPlaceAt = takingPlaceAt;
    }

    public StringFilter getPeopleAttending() {
        return peopleAttending;
    }

    public void setPeopleAttending(StringFilter peopleAttending) {
        this.peopleAttending = peopleAttending;
    }

    public BooleanFilter getCasual() {
        return casual;
    }

    public void setCasual(BooleanFilter casual) {
        this.casual = casual;
    }

    public LocalDateFilter getHour() {
        return hour;
    }

    public void setHour(LocalDateFilter hour) {
        this.hour = hour;
    }

    public LongFilter getActivityEventId() {
        return activityEventId;
    }

    public void setActivityEventId(LongFilter activityEventId) {
        this.activityEventId = activityEventId;
    }

    public LongFilter getPlaceEventId() {
        return placeEventId;
    }

    public void setPlaceEventId(LongFilter placeEventId) {
        this.placeEventId = placeEventId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EventCriteria that = (EventCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(activityType, that.activityType) &&
            Objects.equals(takingPlaceAt, that.takingPlaceAt) &&
            Objects.equals(peopleAttending, that.peopleAttending) &&
            Objects.equals(casual, that.casual) &&
            Objects.equals(hour, that.hour) &&
            Objects.equals(activityEventId, that.activityEventId) &&
            Objects.equals(placeEventId, that.placeEventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        activityType,
        takingPlaceAt,
        peopleAttending,
        casual,
        hour,
        activityEventId,
        placeEventId
        );
    }

    @Override
    public String toString() {
        return "EventCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (activityType != null ? "activityType=" + activityType + ", " : "") +
                (takingPlaceAt != null ? "takingPlaceAt=" + takingPlaceAt + ", " : "") +
                (peopleAttending != null ? "peopleAttending=" + peopleAttending + ", " : "") +
                (casual != null ? "casual=" + casual + ", " : "") +
                (hour != null ? "hour=" + hour + ", " : "") +
                (activityEventId != null ? "activityEventId=" + activityEventId + ", " : "") +
                (placeEventId != null ? "placeEventId=" + placeEventId + ", " : "") +
            "}";
    }

}
