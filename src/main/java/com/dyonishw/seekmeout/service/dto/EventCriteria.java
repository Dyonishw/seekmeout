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

    private BooleanFilter casual;

    private LocalDateFilter hour;

    private StringFilter casualDescription;

    private LongFilter activityEventId;

    private LongFilter placeEventId;

    private LongFilter eventUserId;

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
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

    public StringFilter getCasualDescription() {
        return casualDescription;
    }

    public void setCasualDescription(StringFilter casualDescription) {
        this.casualDescription = casualDescription;
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

    public LongFilter getEventUserId() {
        return eventUserId;
    }

    public void setEventUserId(LongFilter eventUserId) {
        this.eventUserId = eventUserId;
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
            Objects.equals(casual, that.casual) &&
            Objects.equals(hour, that.hour) &&
            Objects.equals(casualDescription, that.casualDescription) &&
            Objects.equals(activityEventId, that.activityEventId) &&
            Objects.equals(placeEventId, that.placeEventId) &&
            Objects.equals(eventUserId, that.eventUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        casual,
        hour,
        casualDescription,
        activityEventId,
        placeEventId,
        eventUserId
        );
    }

    @Override
    public String toString() {
        return "EventCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (casual != null ? "casual=" + casual + ", " : "") +
                (hour != null ? "hour=" + hour + ", " : "") +
                (casualDescription != null ? "casualDescription=" + casualDescription + ", " : "") +
                (activityEventId != null ? "activityEventId=" + activityEventId + ", " : "") +
                (placeEventId != null ? "placeEventId=" + placeEventId + ", " : "") +
                (eventUserId != null ? "eventUserId=" + eventUserId + ", " : "") +
            "}";
    }

}
