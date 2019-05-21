import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, setFileData, openFile, byteSize, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IActivity } from 'app/shared/model/activity.model';
import { getEntities as getActivities } from 'app/entities/activity/activity.reducer';
import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { getEntity, updateEntity, createEntity, setBlob, reset } from './place.reducer';
import { IPlace } from 'app/shared/model/place.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IPlaceUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IPlaceUpdateState {
  isNew: boolean;
  idsactivityPlace: any[];
  rolePlaceUserId: string;
}

export class PlaceUpdate extends React.Component<IPlaceUpdateProps, IPlaceUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      idsactivityPlace: [],
      rolePlaceUserId: '0',
      isNew: !this.props.match.params || !this.props.match.params.id
    };
  }

  componentWillUpdate(nextProps, nextState) {
    if (nextProps.updateSuccess !== this.props.updateSuccess && nextProps.updateSuccess) {
      this.handleClose();
    }
  }

  componentDidMount() {
    if (this.state.isNew) {
      this.props.reset();
    } else {
      this.props.getEntity(this.props.match.params.id);
    }

    this.props.getActivities();
    this.props.getUsers();
  }

  onBlobChange = (isAnImage, name) => event => {
    setFileData(event, (contentType, data) => this.props.setBlob(name, data, contentType), isAnImage);
  };

  clearBlob = name => () => {
    this.props.setBlob(name, undefined, undefined);
  };

  saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const { placeEntity } = this.props;
      const entity = {
        ...placeEntity,
        ...values,
        activityPlaces: mapIdList(values.activityPlaces)
      };

      if (this.state.isNew) {
        this.props.createEntity(entity);
      } else {
        this.props.updateEntity(entity);
      }
    }
  };

  redirectToCreate = () => {
    this.props.history.push('/register-place');
  }

  handleClose = () => {
    this.props.history.goBack();
  };

  render() {
    const { placeEntity, activities, users, loading, updating } = this.props;
    const { isNew } = this.state;

    const { pictures, picturesContentType } = placeEntity;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="seekMeOutApp.place.home.createOrEditLabel">
              <Translate contentKey="seekMeOutApp.place.home.createOrEditLabel">Create or edit a Place</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : placeEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="place-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="addressLabel" for="address">
                    <Translate contentKey="seekMeOutApp.place.address">Address</Translate>
                  </Label>
                  <AvField
                    id="place-address"
                    type="text"
                    name="address"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="phoneNumberLabel" for="phoneNumber">
                    <Translate contentKey="seekMeOutApp.place.phoneNumber">Phone Number</Translate>
                  </Label>
                  <AvField
                    id="place-phoneNumber"
                    type="text"
                    name="phoneNumber"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="descriptionLabel" for="description">
                    <Translate contentKey="seekMeOutApp.place.description">Description</Translate>
                  </Label>
                  <AvField
                    id="place-description"
                    type="text"
                    name="description"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') },
                      minLength: { value: 15, errorMessage: translate('entity.validation.minlength', { min: 15 }) },
                      maxLength: { value: 255, errorMessage: translate('entity.validation.maxlength', { max: 255 }) }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="openHoursLabel" for="openHours">
                    <Translate contentKey="seekMeOutApp.place.openHours">Open Hours</Translate>
                  </Label>
                  <AvField
                    id="place-openHours"
                    type="text"
                    name="openHours"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="nameLabel" for="name">
                    <Translate contentKey="seekMeOutApp.place.name">Name</Translate>
                  </Label>
                  <AvField
                    id="place-name"
                    type="text"
                    name="name"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') },
                      minLength: { value: 3, errorMessage: translate('entity.validation.minlength', { min: 3 }) }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="pricePerHourLabel" for="pricePerHour">
                    <Translate contentKey="seekMeOutApp.place.pricePerHour">Price Per Hour</Translate>
                  </Label>
                  <AvField
                    id="place-pricePerHour"
                    type="string"
                    className="form-control"
                    name="pricePerHour"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') },
                      min: { value: 1, errorMessage: translate('entity.validation.min', { min: 1 }) },
                      number: { value: true, errorMessage: translate('entity.validation.number') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="contactFormLabel" for="contactForm">
                    <Translate contentKey="seekMeOutApp.place.contactForm">Contact Form</Translate>
                  </Label>
                  <AvField id="place-contactForm" type="text" name="contactForm" />
                </AvGroup>
                <AvGroup>
                  <AvGroup>
                    <Label id="picturesLabel" for="pictures">
                      <Translate contentKey="seekMeOutApp.place.pictures">Pictures</Translate>
                    </Label>
                    <br />
                    {pictures ? (
                      <div>
                        <a onClick={openFile(picturesContentType, pictures)}>
                          <img src={`data:${picturesContentType};base64,${pictures}`} style={{ maxHeight: '100px' }} />
                        </a>
                        <br />
                        <Row>
                          <Col md="11">
                            <span>
                              {picturesContentType}, {byteSize(pictures)}
                            </span>
                          </Col>
                          <Col md="1">
                            <Button color="danger" onClick={this.clearBlob('pictures')}>
                              <FontAwesomeIcon icon="times-circle" />
                            </Button>
                          </Col>
                        </Row>
                      </div>
                    ) : null}
                    <input id="file_pictures" type="file" onChange={this.onBlobChange(true, 'pictures')} accept="image/*" />
                    <AvInput type="hidden" name="pictures" value={pictures} />
                  </AvGroup>
                </AvGroup>
                <AvGroup>
                  <Label id="facilitiesLabel" for="facilities">
                    <Translate contentKey="seekMeOutApp.place.facilities">Facilities</Translate>
                  </Label>
                  <AvField id="place-facilities" type="text" name="facilities" />
                </AvGroup>
                <AvGroup>
                  <Label for="activities">
                    <Translate contentKey="seekMeOutApp.place.activityPlace">Activity Place</Translate>
                  </Label>
                  <AvInput
                    id="place-activityPlace"
                    type="select"
                    multiple
                    className="form-control"
                    name="activityPlaces"
                    value={placeEntity.activityPlaces && placeEntity.activityPlaces.map(e => e.id)}
                  >
                    <option value="" key="0" />
                    {activities
                      ? activities.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.type}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="rolePlaceUser.login">
                    <Translate contentKey="seekMeOutApp.place.rolePlaceUser">Role Place User</Translate>
                  </Label>
                  <AvInput id="place-rolePlaceUser" type="select" className="form-control" name="rolePlaceUserId">
                    <option value="" key="0" />
                    {users
                      ? users.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.login}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <Button tag={Link} id="cancel-save" to="/entity/place" replace color="info">
                  <FontAwesomeIcon icon="arrow-left" />&nbsp;
                  <span className="d-none d-md-inline">
                    <Translate contentKey="entity.action.back">Back</Translate>
                  </span>
                </Button>
                &nbsp;
                <Button color="primary" id="save-entity" type="submit" disabled={updating}>
                  <FontAwesomeIcon icon="save" />&nbsp;
                  <Translate contentKey="entity.action.save">Save</Translate>
                </Button>
              </AvForm>
            )}
          </Col>
        </Row>
      </div>
    );
  }
}

const mapStateToProps = (storeState: IRootState) => ({
  activities: storeState.activity.entities,
  users: storeState.userManagement.users,
  placeEntity: storeState.place.entity,
  loading: storeState.place.loading,
  updating: storeState.place.updating,
  updateSuccess: storeState.place.updateSuccess
});

const mapDispatchToProps = {
  getActivities,
  getUsers,
  getEntity,
  updateEntity,
  setBlob,
  createEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(PlaceUpdate);
