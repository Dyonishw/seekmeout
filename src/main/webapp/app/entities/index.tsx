import React from 'react';
import { Switch } from 'react-router-dom';

// tslint:disable-next-line:no-unused-variable
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Activity from './activity';
import Place from './place';
import Event from './event';
/* jhipster-needle-add-route-import - JHipster will add routes here */

const Routes = ({ match }) => (
  <div>
    <Switch>
      {/* prettier-ignore */}
      <ErrorBoundaryRoute path={`${match.url}/activity`} component={Activity} />
      <ErrorBoundaryRoute path={`${match.url}/place`} component={Place} />
      <ErrorBoundaryRoute path={`${match.url}/event`} component={Event} />
      {/* jhipster-needle-add-route-path - JHipster will routes here */}
    </Switch>
  </div>
);

export default Routes;
