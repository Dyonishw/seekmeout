import axios from 'axios';
import { ICrudSearchAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IPlace, defaultValue } from 'app/shared/model/place.model';

export const ACTION_TYPES = {
  SEARCH_PLACES: 'place/SEARCH_PLACES',
  FETCH_PLACE_LIST: 'place/FETCH_PLACE_LIST',
  FETCH_PLACE: 'place/FETCH_PLACE',
  CREATE_PLACE: 'place/CREATE_PLACE',
  UPDATE_PLACE: 'place/UPDATE_PLACE',
  DELETE_PLACE: 'place/DELETE_PLACE',
  RESET: 'place/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IPlace>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type PlaceState = Readonly<typeof initialState>;

// Reducer

export default (state: PlaceState = initialState, action): PlaceState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_PLACES):
    case REQUEST(ACTION_TYPES.FETCH_PLACE_LIST):
    case REQUEST(ACTION_TYPES.FETCH_PLACE):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_PLACE):
    case REQUEST(ACTION_TYPES.UPDATE_PLACE):
    case REQUEST(ACTION_TYPES.DELETE_PLACE):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_PLACES):
    case FAILURE(ACTION_TYPES.FETCH_PLACE_LIST):
    case FAILURE(ACTION_TYPES.FETCH_PLACE):
    case FAILURE(ACTION_TYPES.CREATE_PLACE):
    case FAILURE(ACTION_TYPES.UPDATE_PLACE):
    case FAILURE(ACTION_TYPES.DELETE_PLACE):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_PLACES):
    case SUCCESS(ACTION_TYPES.FETCH_PLACE_LIST):
      return {
        ...state,
        loading: false,
        totalItems: action.payload.headers['x-total-count'],
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_PLACE):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_PLACE):
    case SUCCESS(ACTION_TYPES.UPDATE_PLACE):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_PLACE):
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

const apiUrl = 'api/places';
const apiSearchUrl = 'api/_search/places';

// Actions

export const getSearchEntities: ICrudSearchAction<IPlace> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_PLACES,
  payload: axios.get<IPlace>(`${apiSearchUrl}?query=${query}${sort ? `&page=${page}&size=${size}&sort=${sort}` : ''}`)
});

export const getEntities: ICrudGetAllAction<IPlace> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_PLACE_LIST,
    payload: axios.get<IPlace>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IPlace> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_PLACE,
    payload: axios.get<IPlace>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IPlace> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_PLACE,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IPlace> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_PLACE,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IPlace> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_PLACE,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
