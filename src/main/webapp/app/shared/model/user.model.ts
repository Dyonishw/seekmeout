export interface IUser {
  id?: any;
  login?: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  activated?: boolean;
  langKey?: string;
  authorities?: any[];
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  password?: string;
}

export interface ICurrentUser {
  currentUser?: IUser;
}

export const defaultValue: Readonly<IUser> = {
  id: '',
  login: 'asd',
  firstName: '',
  lastName: '',
  email: '',
  activated: false,
  langKey: '',
  authorities: [],
  createdBy: '',
  createdDate: null,
  lastModifiedBy: '',
  lastModifiedDate: null,
  password: ''
};
export const defaultCurrentUser: Readonly<ICurrentUser> = {
  currentUser: defaultValue;
};
