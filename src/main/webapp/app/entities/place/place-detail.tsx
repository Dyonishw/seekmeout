import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction, openFile, byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './place.reducer';
import { IPlace } from 'app/shared/model/place.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IPlaceDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class PlaceDetail extends React.Component<IPlaceDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { placeEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="seekMeOutApp.place.detail.title">Place</Translate> [<b>{placeEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="address">
                <Translate contentKey="seekMeOutApp.place.address">Address</Translate>
              </span>
            </dt>
            <dd>{placeEntity.address}</dd>
            <dt>
              <span id="phoneNumber">
                <Translate contentKey="seekMeOutApp.place.phoneNumber">Phone Number</Translate>
              </span>
            </dt>
            <dd>{placeEntity.phoneNumber}</dd>
            <dt>
              <span id="description">
                <Translate contentKey="seekMeOutApp.place.description">Description</Translate>
              </span>
            </dt>
            <dd>{placeEntity.description}</dd>
            <dt>
              <span id="openHours">
                <Translate contentKey="seekMeOutApp.place.openHours">Open Hours</Translate>
              </span>
            </dt>
            <dd>{placeEntity.openHours}</dd>
            <dt>
              <span id="name">
                <Translate contentKey="seekMeOutApp.place.name">Name</Translate>
              </span>
            </dt>
            <dd>{placeEntity.name}</dd>
            <dt>
              <span id="pricePerHour">
                <Translate contentKey="seekMeOutApp.place.pricePerHour">Price Per Hour</Translate>
              </span>
            </dt>
            <dd>{placeEntity.pricePerHour}</dd>
            <dt>
              <span id="contactForm">
                <Translate contentKey="seekMeOutApp.place.contactForm">Contact Form</Translate>
              </span>
            </dt>
            <dd>{placeEntity.contactForm}</dd>
            <dt>
              <span id="pictures">
                <Translate contentKey="seekMeOutApp.place.pictures">Pictures</Translate>
              </span>
            </dt>
            <dd>
              {placeEntity.pictures ? (
                <div>
                  <a onClick={openFile(placeEntity.picturesContentType, placeEntity.pictures)}>
                    <img src={`data:${placeEntity.picturesContentType};base64,${placeEntity.pictures}`} style={{ maxHeight: '30px' }} />
                  </a>
                  <span>
                    {placeEntity.picturesContentType}, {byteSize(placeEntity.pictures)}
                  </span>
                </div>
              ) : null}
            </dd>
            <dt>
              <span id="facilities">
                <Translate contentKey="seekMeOutApp.place.facilities">Facilities</Translate>
              </span>
            </dt>
            <dd>{placeEntity.facilities}</dd>
            <dt>
              <Translate contentKey="seekMeOutApp.place.activityPlace">Activity Place</Translate>
            </dt>
            <dd>
              {placeEntity.activityPlaces
                ? placeEntity.activityPlaces.map((val, i) => (
                    <span key={val.id}>
                      <a>{val.type}</a>
                      {i === placeEntity.activityPlaces.length - 1 ? '' : ', '}
                    </span>
                  ))
                : null}
            </dd>
            <dt>
              <Translate contentKey="seekMeOutApp.place.rolePlaceUser">Role Place User</Translate>
            </dt>
            <dd>{placeEntity.rolePlaceUserLogin ? placeEntity.rolePlaceUserLogin : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/place" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>&nbsp;
        </Col>
      </Row>
    );
  }
}

const mapStateToProps = ({ place }: IRootState) => ({
  placeEntity: place.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(PlaceDetail);
