import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITeamProfile } from '../team-profile.model';
import { TeamProfileService } from '../service/team-profile.service';

const teamProfileResolve = (route: ActivatedRouteSnapshot): Observable<null | ITeamProfile> => {
  const id = route.params.id;
  if (id) {
    return inject(TeamProfileService)
      .find(id)
      .pipe(
        mergeMap((teamProfile: HttpResponse<ITeamProfile>) => {
          if (teamProfile.body) {
            return of(teamProfile.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default teamProfileResolve;
