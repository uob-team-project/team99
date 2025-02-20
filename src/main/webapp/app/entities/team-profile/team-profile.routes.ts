import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import TeamProfileResolve from './route/team-profile-routing-resolve.service';

const teamProfileRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/team-profile.component').then(m => m.TeamProfileComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/team-profile-detail.component').then(m => m.TeamProfileDetailComponent),
    resolve: {
      teamProfile: TeamProfileResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/team-profile-update.component').then(m => m.TeamProfileUpdateComponent),
    resolve: {
      teamProfile: TeamProfileResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/team-profile-update.component').then(m => m.TeamProfileUpdateComponent),
    resolve: {
      teamProfile: TeamProfileResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default teamProfileRoute;
