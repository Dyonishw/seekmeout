import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './activity.reducer';
import { IActivity } from 'app/shared/model/activity.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IActivityDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class ActivityDetail extends React.Component<IActivityDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { activityEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="seekMeOutApp.activity.detail.title">Activity</Translate> [<b>{activityEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="type">
                <Translate contentKey="seekMeOutApp.activity.type">Type</Translate>
              </span>
            </dt>
            <dd>{activityEntity.type}</dd>
            <dt>
              <span id="numberOfPlayers">
                <Translate contentKey="seekMeOutApp.activity.numberOfPlayers">Number Of Players</Translate>
              </span>
            </dt>
            <dd>{activityEntity.numberOfPlayers}</dd>
            <dt>
              <span id="officialDuration">
                <Translate contentKey="seekMeOutApp.activity.officialDuration">Official Duration</Translate>
              </span>
            </dt>
            <dd>{activityEntity.officialDuration}</dd>
            <dt>
              <span id="officialRules">
                <Translate contentKey="seekMeOutApp.activity.officialRules">Official Rules</Translate>
              </span>
            </dt>
            <dd>{activityEntity.officialRules}</dd>
            <dt>
              <span id="shortDescription">
                <Translate contentKey="seekMeOutApp.activity.shortDescription">Short Description</Translate>
              </span>
            </dt>
            <dd>{activityEntity.shortDescription}</dd>
            <dt>
              <span id="recommendedGear">
                <Translate contentKey="seekMeOutApp.activity.recommendedGear">Recommended Gear</Translate>
              </span>
            </dt>
            <dd>{activityEntity.recommendedGear}</dd>
          </dl>
          <Button tag={Link} to="/entity/activity" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>&nbsp;
          <Button tag={Link} to={`/entity/activity/${activityEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ activity }: IRootState) => ({
  activityEntity: activity.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ActivityDetail);
