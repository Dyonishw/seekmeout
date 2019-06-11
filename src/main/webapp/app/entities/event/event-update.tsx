import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IActivity } from 'app/shared/model/activity.model';
import { getEntities as getActivities } from 'app/entities/activity/activity.reducer';
import { IPlace } from 'app/shared/model/place.model';
import { getEntities as getPlaces } from 'app/entities/place/place.reducer';
import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { getEntity, updateEntity, createEntity, reset } from './event.reducer';
import { IEvent } from 'app/shared/model/event.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IEventUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IEventUpdateState {
  isNew: boolean;
  idseventUser: any[];
  activityEventId: string;
  placeEventId: string;
}

export class EventUpdate extends React.Component<IEventUpdateProps, IEventUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      idseventUser: [],
      activityEventId: '0',
      placeEventId: '0',
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
    this.props.getPlaces();
    this.props.getUsers();
  }

  saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const { eventEntity } = this.props;
      const entity = {
        ...eventEntity,
        ...values,
        eventUsers: mapIdList(values.eventUsers)
      };

      if (this.state.isNew) {
        this.props.createEntity(entity);
      } else {
        this.props.updateEntity(entity);
      }
    }
  };

  handleClose = () => {
    this.props.history.push('/entity/event');
  };

  render() {
    const { eventEntity, activities, places, users, loading, updating, account } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="seekMeOutApp.event.home.createOrEditLabel">
              <Translate contentKey="seekMeOutApp.event.home.createOrEditLabel">Create or edit a Event</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            <dl className="jh-entity-details">
              <dt>
                <span id="casual">
                  <Translate contentKey="seekMeOutApp.event.eventUser">Event User</Translate>
                </span>
              </dt>
              <dd>
              {eventEntity.eventUsers
                      ? eventEntity.eventUsers.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.login}
                          </option>
                        ))
                      : null}
              </dd>
            </dl>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : eventEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="event-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="casualLabel" check>
                    <AvInput id="event-casual" type="checkbox" className="form-control" name="casual" />
                    <Translate contentKey="seekMeOutApp.event.casual">Casual</Translate>
                  </Label>
                </AvGroup>
                <AvGroup>
                  <Label id="hourLabel" for="hour">
                    <Translate contentKey="seekMeOutApp.event.hour">Hour</Translate>
                  </Label>
                  <AvField
                    id="event-hour"
                    type="date"
                    className="form-control"
                    name="hour"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="casualDescriptionLabel" for="casualDescription">
                    <Translate contentKey="seekMeOutApp.event.casualDescription">Casual Description</Translate>
                  </Label>
                  <AvField id="event-casualDescription" type="text" name="casualDescription" />
                </AvGroup>
                <AvGroup>
                  <Label for="activityEvent.type">
                    <Translate contentKey="seekMeOutApp.event.activityEvent">Activity Event</Translate>
                  </Label>
                  <AvInput id="event-activityEvent" type="select" className="form-control" name="activityEventId">
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
                  <Label for="placeEvent.name">
                    <Translate contentKey="seekMeOutApp.event.placeEvent">Place Event</Translate>
                  </Label>
                  <AvInput id="event-placeEvent" type="select" className="form-control" name="placeEventId">
                    <option value="" key="0" />
                    {places
                      ? places.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.name}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                {!isNew ?
                <AvGroup>
                  <Label for="users">
                    <Translate contentKey="seekMeOutApp.event.eventUser">Event User</Translate>
                  </Label>
                  <AvInput
                    id="event-eventUser"
                    type="select"
                    multiple
                    className="form-control"
                    name="eventUsers"
                    value={eventEntity.eventUsers && eventEntity.eventUsers.map(e => e.id)}
                    required hidden
                  >
                    <option value="" key="0" />
                    {eventEntity.eventUsers
                      ? eventEntity.eventUsers.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.login}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                :
                <AvGroup>
                  <AvInput id="event-eventUser"
                    type="select"
                    multiple
                    className="form-control"
                    name="eventUsers"
                    value={eventEntity.eventUsers}
                    required hidden
                  >
                    <option value={account.id} key={account.id}>
                        {account.login}
                    </option>
                    </AvInput>
                </AvGroup> }
                <Button tag={Link} id="cancel-save" to="/entity/event" replace color="info">
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
  account: storeState.authentication.account,
  activities: storeState.activity.entities,
  places: storeState.place.entities,
  users: storeState.userManagement.users,
  eventEntity: storeState.event.entity,
  loading: storeState.event.loading,
  updating: storeState.event.updating,
  updateSuccess: storeState.event.updateSuccess
});

const mapDispatchToProps = {
  getActivities,
  getPlaces,
  getUsers,
  getEntity,
  updateEntity,
  createEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(EventUpdate);
