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

/**
 * Criteria class for the Activity entity. This class is used in ActivityResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /activities?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ActivityCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter type;

    private IntegerFilter numberOfPlayers;

    private StringFilter officialDuration;

    private StringFilter officialRules;

    private StringFilter shortDescription;

    private StringFilter recommendedGear;

    private StringFilter longDescription;

    private LongFilter activityPlaceId;

    private LongFilter activityEventId;

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getType() {
        return type;
    }

    public void setType(StringFilter type) {
        this.type = type;
    }

    public IntegerFilter getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(IntegerFilter numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public StringFilter getOfficialDuration() {
        return officialDuration;
    }

    public void setOfficialDuration(StringFilter officialDuration) {
        this.officialDuration = officialDuration;
    }

    public StringFilter getOfficialRules() {
        return officialRules;
    }

    public void setOfficialRules(StringFilter officialRules) {
        this.officialRules = officialRules;
    }

    public StringFilter getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(StringFilter shortDescription) {
        this.shortDescription = shortDescription;
    }

    public StringFilter getRecommendedGear() {
        return recommendedGear;
    }

    public void setRecommendedGear(StringFilter recommendedGear) {
        this.recommendedGear = recommendedGear;
    }

    public StringFilter getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(StringFilter longDescription) {
        this.longDescription = longDescription;
    }

    public LongFilter getActivityPlaceId() {
        return activityPlaceId;
    }

    public void setActivityPlaceId(LongFilter activityPlaceId) {
        this.activityPlaceId = activityPlaceId;
    }

    public LongFilter getActivityEventId() {
        return activityEventId;
    }

    public void setActivityEventId(LongFilter activityEventId) {
        this.activityEventId = activityEventId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ActivityCriteria that = (ActivityCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(type, that.type) &&
            Objects.equals(numberOfPlayers, that.numberOfPlayers) &&
            Objects.equals(officialDuration, that.officialDuration) &&
            Objects.equals(officialRules, that.officialRules) &&
            Objects.equals(shortDescription, that.shortDescription) &&
            Objects.equals(recommendedGear, that.recommendedGear) &&
            Objects.equals(longDescription, that.longDescription) &&
            Objects.equals(activityPlaceId, that.activityPlaceId) &&
            Objects.equals(activityEventId, that.activityEventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        type,
        numberOfPlayers,
        officialDuration,
        officialRules,
        shortDescription,
        recommendedGear,
        longDescription,
        activityPlaceId,
        activityEventId
        );
    }

    @Override
    public String toString() {
        return "ActivityCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (type != null ? "type=" + type + ", " : "") +
                (numberOfPlayers != null ? "numberOfPlayers=" + numberOfPlayers + ", " : "") +
                (officialDuration != null ? "officialDuration=" + officialDuration + ", " : "") +
                (officialRules != null ? "officialRules=" + officialRules + ", " : "") +
                (shortDescription != null ? "shortDescription=" + shortDescription + ", " : "") +
                (recommendedGear != null ? "recommendedGear=" + recommendedGear + ", " : "") +
                (longDescription != null ? "longDescription=" + longDescription + ", " : "") +
                (activityPlaceId != null ? "activityPlaceId=" + activityPlaceId + ", " : "") +
                (activityEventId != null ? "activityEventId=" + activityEventId + ", " : "") +
            "}";
    }

}
