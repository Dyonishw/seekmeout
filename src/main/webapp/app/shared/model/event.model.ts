import { Moment } from 'moment';

export interface IEvent {
  id?: number;
  activityType?: string;
  takingPlaceAt?: string;
  peopleAttending?: string;
  casual?: boolean;
  hour?: Moment;
  activityEventType?: string;
  activityEventId?: number;
  placeEventName?: string;
  placeEventId?: number;
}

export const defaultValue: Readonly<IEvent> = {
  casual: false
};
