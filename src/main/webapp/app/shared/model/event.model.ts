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
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;  
}

export const defaultValue: Readonly<IEvent> = {
  casual: false
};
