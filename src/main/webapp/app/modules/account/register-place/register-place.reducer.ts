import axios from 'axios';
import { translate } from 'react-jhipster';

import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

export const ACTION_TYPES = {
CREATE_PLACE_ACCOUNT: 'register-place/CREATE_PLACE_ACCOUNT',
RESET_PLACE: 'register-place/RESET_PLACE'
};

const initialState = {
  login: null as string,
  loading: false,
  registrationSuccess: false,
  registrationFailure: false,
  errorMessage: null ,
  cacheLogin: null as string
};

export type RegisterPlaceState = Readonly<typeof initialState>;

// Reducer
export default (state: RegisterPlaceState = initialState, action): RegisterPlaceState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.CREATE_PLACE_ACCOUNT):
      return {
        ...state,
        loading: true,
      };
    case FAILURE(ACTION_TYPES.CREATE_PLACE_ACCOUNT):
      return {
        ...initialState,
        registrationFailure: true,
        errorMessage: action.payload.response.data.errorKey,
      };
    case SUCCESS(ACTION_TYPES.CREATE_PLACE_ACCOUNT):
      return {
        ...initialState,
        registrationSuccess: true,
        cacheLogin: action.meta.placeUserLogin
      };
    case ACTION_TYPES.RESET_PLACE:
      return {
        ...initialState
      };
    default:
      return state;
  }
};

// Actions
export const handlePlaceRegister = (login, email, password, langKey = 'en') => ({
  type: ACTION_TYPES.CREATE_PLACE_ACCOUNT,
  payload: axios.post('api/register-place', { login, email, password, langKey }),
  meta: {
    successMessage: translate('register.messages.success'),
    placeUserLogin: login
  }
});

export const reset = () => ({
  type: ACTION_TYPES.RESET_PLACE
});
