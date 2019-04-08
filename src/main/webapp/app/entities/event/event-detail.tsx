import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './event.reducer';
import { IEvent } from 'app/shared/model/event.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IEventDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class EventDetail extends React.Component<IEventDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { eventEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="seekMeOutApp.event.detail.title">Event</Translate> [<b>{eventEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="activityType">
                <Translate contentKey="seekMeOutApp.event.activityType">Activity Type</Translate>
              </span>
            </dt>
            <dd>{eventEntity.activityType}</dd>
            <dt>
              <span id="takingPlaceAt">
                <Translate contentKey="seekMeOutApp.event.takingPlaceAt">Taking Place At</Translate>
              </span>
            </dt>
            <dd>{eventEntity.takingPlaceAt}</dd>
            <dt>
              <span id="peopleAttending">
                <Translate contentKey="seekMeOutApp.event.peopleAttending">People Attending</Translate>
              </span>
            </dt>
            <dd>{eventEntity.peopleAttending}</dd>
            <dt>
              <span id="casual">
                <Translate contentKey="seekMeOutApp.event.casual">Casual</Translate>
              </span>
            </dt>
            <dd>{eventEntity.casual ? 'true' : 'false'}</dd>
            <dt>
              <span id="hour">
                <Translate contentKey="seekMeOutApp.event.hour">Hour</Translate>
              </span>
            </dt>
            <dd>
              <TextFormat value={eventEntity.hour} type="date" format={APP_LOCAL_DATE_FORMAT} />
            </dd>
            <dt>
              <Translate contentKey="seekMeOutApp.event.activityEvent">Activity Event</Translate>
            </dt>
            <dd>{eventEntity.activityEventType ? eventEntity.activityEventType : ''}</dd>
            <dt>
              <Translate contentKey="seekMeOutApp.event.placeEvent">Place Event</Translate>
            </dt>
            <dd>{eventEntity.placeEventName ? eventEntity.placeEventName : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/event" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>&nbsp;
          <Button tag={Link} to={`/entity/event/${eventEntity.id}/edit`} replace color="primary">
            <FontAwesomeIcon icon="pencil-alt" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.edit">Edit</Translate>
            </span>
          </Button>
        </Col>
      </Row>
    );
  }
}

const mapStateToProps = ({ event }: IRootState) => ({
  eventEntity: event.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(EventDetail);
