import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'Authorities' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'user-profile',
    data: { pageTitle: 'UserProfiles' },
    loadChildren: () => import('./user-profile/user-profile.routes'),
  },
  {
    path: 'team-profile',
    data: { pageTitle: 'TeamProfiles' },
    loadChildren: () => import('./team-profile/team-profile.routes'),
  },
  {
    path: 'screenshot',
    data: { pageTitle: 'Screenshots' },
    loadChildren: () => import('./screenshot/screenshot.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
