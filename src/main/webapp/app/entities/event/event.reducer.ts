import axios from 'axios';
import { ICrudSearchAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IEvent, defaultValue } from 'app/shared/model/event.model';

export const ACTION_TYPES = {
  SEARCH_EVENTS: 'event/SEARCH_EVENTS',
  FETCH_EVENT_LIST: 'event/FETCH_EVENT_LIST',
  FETCH_EVENT: 'event/FETCH_EVENT',
  CREATE_EVENT: 'event/CREATE_EVENT',
  UPDATE_EVENT: 'event/UPDATE_EVENT',
  DELETE_EVENT: 'event/DELETE_EVENT',
  RESET: 'event/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IEvent>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type EventState = Readonly<typeof initialState>;

// Reducer

export default (state: EventState = initialState, action): EventState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_EVENTS):
    case REQUEST(ACTION_TYPES.FETCH_EVENT_LIST):
    case REQUEST(ACTION_TYPES.FETCH_EVENT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_EVENT):
    case REQUEST(ACTION_TYPES.UPDATE_EVENT):
    case REQUEST(ACTION_TYPES.DELETE_EVENT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_EVENTS):
    case FAILURE(ACTION_TYPES.FETCH_EVENT_LIST):
    case FAILURE(ACTION_TYPES.FETCH_EVENT):
    case FAILURE(ACTION_TYPES.CREATE_EVENT):
    case FAILURE(ACTION_TYPES.UPDATE_EVENT):
    case FAILURE(ACTION_TYPES.DELETE_EVENT):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_EVENTS):
    case SUCCESS(ACTION_TYPES.FETCH_EVENT_LIST):
      return {
        ...state,
        loading: false,
        totalItems: action.payload.headers['x-total-count'],
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_EVENT):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_EVENT):
    case SUCCESS(ACTION_TYPES.UPDATE_EVENT):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_EVENT):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {}
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState
      };
    default:
      return state;
  }
};

const apiUrl = 'api/events';
const apiSearchUrl = 'api/_search/events';

// Actions

export const getSearchEntities: ICrudSearchAction<IEvent> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_EVENTS,
  payload: axios.get<IEvent>(`${apiSearchUrl}?query=${query}${sort ? `&page=${page}&size=${size}&sort=${sort}` : ''}`)
});

export const getEntities: ICrudGetAllAction<IEvent> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_EVENT_LIST,
    payload: axios.get<IEvent>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IEvent> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_EVENT,
    payload: axios.get<IEvent>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IEvent> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_EVENT,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IEvent> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_EVENT,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IEvent> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_EVENT,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
