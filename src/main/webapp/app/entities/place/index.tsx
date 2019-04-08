import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Place from './place';
import PlaceDetail from './place-detail';
import PlaceUpdate from './place-update';
import PlaceDeleteDialog from './place-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={PlaceUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={PlaceUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={PlaceDetail} />
      <ErrorBoundaryRoute path={match.url} component={Place} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={PlaceDeleteDialog} />
  </>
);

export default Routes;
