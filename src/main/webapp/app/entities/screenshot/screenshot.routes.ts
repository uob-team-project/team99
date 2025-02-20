import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ScreenshotResolve from './route/screenshot-routing-resolve.service';

const screenshotRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/screenshot.component').then(m => m.ScreenshotComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/screenshot-detail.component').then(m => m.ScreenshotDetailComponent),
    resolve: {
      screenshot: ScreenshotResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/screenshot-update.component').then(m => m.ScreenshotUpdateComponent),
    resolve: {
      screenshot: ScreenshotResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/screenshot-update.component').then(m => m.ScreenshotUpdateComponent),
    resolve: {
      screenshot: ScreenshotResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default screenshotRoute;
