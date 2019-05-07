import { Moment } from 'moment';
import { IUser } from 'app/shared/model/user.model';

export interface IEvent {
  id?: number;
  casual?: boolean;
  hour?: Moment;
  casualDescription?: string;
  activityEventType?: string;
  activityEventId?: number;
  placeEventName?: string;
  placeEventId?: number;
  eventUsers?: IUser[];
}

export const defaultValue: Readonly<IEvent> = {
  casual: false
};
