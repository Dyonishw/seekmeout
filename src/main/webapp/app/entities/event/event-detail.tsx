import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity, updateEntity } from './event.reducer';
import { IEvent } from 'app/shared/model/event.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

import { mapIdList } from 'app/shared/util/entity-utils';

export interface IEventDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class EventDetail extends React.Component<IEventDetailProps> {

  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { eventEntity, loading, updating } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="seekMeOutApp.event.detail.title">Event</Translate> [<b>{eventEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
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
              <span id="casualDescription">
                <Translate contentKey="seekMeOutApp.event.casualDescription">Casual Description</Translate>
              </span>
            </dt>
            <dd>{eventEntity.casualDescription}</dd>
            <dt>
              <Translate contentKey="seekMeOutApp.event.activityEvent">Activity Event</Translate>
            </dt>
            <dd>{eventEntity.activityEventType ? eventEntity.activityEventType : ''}</dd>
            <dt>
              <Translate contentKey="seekMeOutApp.event.placeEvent">Place Event</Translate>
            </dt>
            <dd>{eventEntity.placeEventName ? eventEntity.placeEventName : ''}</dd>
            <dt>
              <Translate contentKey="seekMeOutApp.event.eventUser">Event User</Translate>
            </dt>
            <dd>
              {eventEntity.eventUsers
                ? eventEntity.eventUsers.map((val, i) => (
                    <span key={val.id}>
                      <a><Link to={`attending/${val.login}`}>{val.login}</Link></a>
                      {i === eventEntity.eventUsers.length - 1 ? '' : ', '}
                    </span>
                  ))
                : null}{' '}
            </dd>
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
  eventEntity: event.entity,
  loading: event.loading,
  updating: event.updating,
  updateSuccess: event.updateSuccess
});

const mapDispatchToProps = { getEntity, updateEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(EventDetail);
