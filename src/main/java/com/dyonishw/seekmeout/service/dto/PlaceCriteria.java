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
 * Criteria class for the Place entity. This class is used in PlaceResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /places?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class PlaceCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter address;

    private StringFilter phoneNumber;

    private StringFilter description;

    private StringFilter openHours;

    private StringFilter name;

    private IntegerFilter pricePerHour;

    private StringFilter contactForm;

    private StringFilter facilities;

    private LongFilter activityPlaceId;

    private LongFilter placeEventId;

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getAddress() {
        return address;
    }

    public void setAddress(StringFilter address) {
        this.address = address;
    }

    public StringFilter getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(StringFilter phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public StringFilter getDescription() {
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public StringFilter getOpenHours() {
        return openHours;
    }

    public void setOpenHours(StringFilter openHours) {
        this.openHours = openHours;
    }

    public StringFilter getName() {
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public IntegerFilter getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(IntegerFilter pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public StringFilter getContactForm() {
        return contactForm;
    }

    public void setContactForm(StringFilter contactForm) {
        this.contactForm = contactForm;
    }

    public StringFilter getFacilities() {
        return facilities;
    }

    public void setFacilities(StringFilter facilities) {
        this.facilities = facilities;
    }

    public LongFilter getActivityPlaceId() {
        return activityPlaceId;
    }

    public void setActivityPlaceId(LongFilter activityPlaceId) {
        this.activityPlaceId = activityPlaceId;
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
        final PlaceCriteria that = (PlaceCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(address, that.address) &&
            Objects.equals(phoneNumber, that.phoneNumber) &&
            Objects.equals(description, that.description) &&
            Objects.equals(openHours, that.openHours) &&
            Objects.equals(name, that.name) &&
            Objects.equals(pricePerHour, that.pricePerHour) &&
            Objects.equals(contactForm, that.contactForm) &&
            Objects.equals(facilities, that.facilities) &&
            Objects.equals(activityPlaceId, that.activityPlaceId) &&
            Objects.equals(placeEventId, that.placeEventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        address,
        phoneNumber,
        description,
        openHours,
        name,
        pricePerHour,
        contactForm,
        facilities,
        activityPlaceId,
        placeEventId
        );
    }

    @Override
    public String toString() {
        return "PlaceCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (address != null ? "address=" + address + ", " : "") +
                (phoneNumber != null ? "phoneNumber=" + phoneNumber + ", " : "") +
                (description != null ? "description=" + description + ", " : "") +
                (openHours != null ? "openHours=" + openHours + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (pricePerHour != null ? "pricePerHour=" + pricePerHour + ", " : "") +
                (contactForm != null ? "contactForm=" + contactForm + ", " : "") +
                (facilities != null ? "facilities=" + facilities + ", " : "") +
                (activityPlaceId != null ? "activityPlaceId=" + activityPlaceId + ", " : "") +
                (placeEventId != null ? "placeEventId=" + placeEventId + ", " : "") +
            "}";
    }

}
