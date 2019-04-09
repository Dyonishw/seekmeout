import { IPlace } from 'app/shared/model/place.model';
import { IEvent } from 'app/shared/model/event.model';

export interface IActivity {
  id?: number;
  type?: string;
  numberOfPlayers?: number;
  officialDuration?: string;
  officialRules?: string;
  shortDescription?: string;
  recommendedGear?: string;
  longDescription?: string;
  activityPlaces?: IPlace[];
  activityEvents?: IEvent[];
}

export const defaultValue: Readonly<IActivity> = {};
