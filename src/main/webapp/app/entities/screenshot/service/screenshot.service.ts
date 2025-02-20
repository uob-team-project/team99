import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IScreenshot, NewScreenshot } from '../screenshot.model';

export type PartialUpdateScreenshot = Partial<IScreenshot> & Pick<IScreenshot, 'id'>;

export type EntityResponseType = HttpResponse<IScreenshot>;
export type EntityArrayResponseType = HttpResponse<IScreenshot[]>;

@Injectable({ providedIn: 'root' })
export class ScreenshotService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/screenshots');

  create(screenshot: NewScreenshot): Observable<EntityResponseType> {
    return this.http.post<IScreenshot>(this.resourceUrl, screenshot, { observe: 'response' });
  }

  update(screenshot: IScreenshot): Observable<EntityResponseType> {
    return this.http.put<IScreenshot>(`${this.resourceUrl}/${this.getScreenshotIdentifier(screenshot)}`, screenshot, {
      observe: 'response',
    });
  }

  partialUpdate(screenshot: PartialUpdateScreenshot): Observable<EntityResponseType> {
    return this.http.patch<IScreenshot>(`${this.resourceUrl}/${this.getScreenshotIdentifier(screenshot)}`, screenshot, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IScreenshot>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IScreenshot[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getScreenshotIdentifier(screenshot: Pick<IScreenshot, 'id'>): number {
    return screenshot.id;
  }

  compareScreenshot(o1: Pick<IScreenshot, 'id'> | null, o2: Pick<IScreenshot, 'id'> | null): boolean {
    return o1 && o2 ? this.getScreenshotIdentifier(o1) === this.getScreenshotIdentifier(o2) : o1 === o2;
  }

  addScreenshotToCollectionIfMissing<Type extends Pick<IScreenshot, 'id'>>(
    screenshotCollection: Type[],
    ...screenshotsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const screenshots: Type[] = screenshotsToCheck.filter(isPresent);
    if (screenshots.length > 0) {
      const screenshotCollectionIdentifiers = screenshotCollection.map(screenshotItem => this.getScreenshotIdentifier(screenshotItem));
      const screenshotsToAdd = screenshots.filter(screenshotItem => {
        const screenshotIdentifier = this.getScreenshotIdentifier(screenshotItem);
        if (screenshotCollectionIdentifiers.includes(screenshotIdentifier)) {
          return false;
        }
        screenshotCollectionIdentifiers.push(screenshotIdentifier);
        return true;
      });
      return [...screenshotsToAdd, ...screenshotCollection];
    }
    return screenshotCollection;
  }
}
