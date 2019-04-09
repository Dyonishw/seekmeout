import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IPlace } from 'app/shared/model/place.model';
import { getEntities as getPlaces } from 'app/entities/place/place.reducer';
import { getEntity, updateEntity, createEntity, reset } from './activity.reducer';
import { IActivity } from 'app/shared/model/activity.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IActivityUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IActivityUpdateState {
  isNew: boolean;
  activityPlaceId: string;
}

export class ActivityUpdate extends React.Component<IActivityUpdateProps, IActivityUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      activityPlaceId: '0',
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

    this.props.getPlaces();
  }

  saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const { activityEntity } = this.props;
      const entity = {
        ...activityEntity,
        ...values
      };

      if (this.state.isNew) {
        this.props.createEntity(entity);
      } else {
        this.props.updateEntity(entity);
      }
    }
  };

  handleClose = () => {
    this.props.history.push('/entity/activity');
  };

  render() {
    const { activityEntity, places, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="seekMeOutApp.activity.home.createOrEditLabel">
              <Translate contentKey="seekMeOutApp.activity.home.createOrEditLabel">Create or edit a Activity</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : activityEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="activity-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="typeLabel" for="type">
                    <Translate contentKey="seekMeOutApp.activity.type">Type</Translate>
                  </Label>
                  <AvField
                    id="activity-type"
                    type="text"
                    name="type"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="numberOfPlayersLabel" for="numberOfPlayers">
                    <Translate contentKey="seekMeOutApp.activity.numberOfPlayers">Number Of Players</Translate>
                  </Label>
                  <AvField
                    id="activity-numberOfPlayers"
                    type="string"
                    className="form-control"
                    name="numberOfPlayers"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') },
                      min: { value: 2, errorMessage: translate('entity.validation.min', { min: 2 }) },
                      number: { value: true, errorMessage: translate('entity.validation.number') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="officialDurationLabel" for="officialDuration">
                    <Translate contentKey="seekMeOutApp.activity.officialDuration">Official Duration</Translate>
                  </Label>
                  <AvField id="activity-officialDuration" type="text" name="officialDuration" />
                </AvGroup>
                <AvGroup>
                  <Label id="officialRulesLabel" for="officialRules">
                    <Translate contentKey="seekMeOutApp.activity.officialRules">Official Rules</Translate>
                  </Label>
                  <AvField id="activity-officialRules" type="text" name="officialRules" />
                </AvGroup>
                <AvGroup>
                  <Label id="shortDescriptionLabel" for="shortDescription">
                    <Translate contentKey="seekMeOutApp.activity.shortDescription">Short Description</Translate>
                  </Label>
                  <AvField
                    id="activity-shortDescription"
                    type="text"
                    name="shortDescription"
                    validate={{
                      minLength: { value: 15, errorMessage: translate('entity.validation.minlength', { min: 15 }) }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="recommendedGearLabel" for="recommendedGear">
                    <Translate contentKey="seekMeOutApp.activity.recommendedGear">Recommended Gear</Translate>
                  </Label>
                  <AvField id="activity-recommendedGear" type="text" name="recommendedGear" />
                </AvGroup>
                <AvGroup>
                  <Label id="longDescriptionLabel" for="longDescription">
                    <Translate contentKey="seekMeOutApp.activity.longDescription">Long Description</Translate>
                  </Label>
                  <AvField id="activity-longDescription" type="text" name="longDescription" />
                </AvGroup>
                <Button tag={Link} id="cancel-save" to="/entity/activity" replace color="info">
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
  places: storeState.place.entities,
  activityEntity: storeState.activity.entity,
  loading: storeState.activity.loading,
  updating: storeState.activity.updating,
  updateSuccess: storeState.activity.updateSuccess
});

const mapDispatchToProps = {
  getPlaces,
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
)(ActivityUpdate);
