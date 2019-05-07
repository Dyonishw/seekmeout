import { IActivity } from 'app/shared/model/activity.model';
import { IEvent } from 'app/shared/model/event.model';

export interface IPlace {
  id?: number;
  address?: string;
  phoneNumber?: string;
  description?: string;
  openHours?: string;
  name?: string;
  pricePerHour?: number;
  contactForm?: string;
  picturesContentType?: string;
  pictures?: any;
  facilities?: string;
  activityPlaces?: IActivity[];
  placeEvents?: IEvent[];
}

export const defaultValue: Readonly<IPlace> = {};
