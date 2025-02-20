import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IScreenshot } from '../screenshot.model';
import { ScreenshotService } from '../service/screenshot.service';

const screenshotResolve = (route: ActivatedRouteSnapshot): Observable<null | IScreenshot> => {
  const id = route.params.id;
  if (id) {
    return inject(ScreenshotService)
      .find(id)
      .pipe(
        mergeMap((screenshot: HttpResponse<IScreenshot>) => {
          if (screenshot.body) {
            return of(screenshot.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default screenshotResolve;
