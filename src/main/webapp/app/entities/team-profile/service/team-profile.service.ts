import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITeamProfile, NewTeamProfile } from '../team-profile.model';

export type PartialUpdateTeamProfile = Partial<ITeamProfile> & Pick<ITeamProfile, 'id'>;

export type EntityResponseType = HttpResponse<ITeamProfile>;
export type EntityArrayResponseType = HttpResponse<ITeamProfile[]>;

@Injectable({ providedIn: 'root' })
export class TeamProfileService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/team-profiles');

  create(teamProfile: NewTeamProfile): Observable<EntityResponseType> {
    return this.http.post<ITeamProfile>(this.resourceUrl, teamProfile, { observe: 'response' });
  }

  update(teamProfile: ITeamProfile): Observable<EntityResponseType> {
    return this.http.put<ITeamProfile>(`${this.resourceUrl}/${this.getTeamProfileIdentifier(teamProfile)}`, teamProfile, {
      observe: 'response',
    });
  }

  partialUpdate(teamProfile: PartialUpdateTeamProfile): Observable<EntityResponseType> {
    return this.http.patch<ITeamProfile>(`${this.resourceUrl}/${this.getTeamProfileIdentifier(teamProfile)}`, teamProfile, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITeamProfile>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITeamProfile[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTeamProfileIdentifier(teamProfile: Pick<ITeamProfile, 'id'>): number {
    return teamProfile.id;
  }

  compareTeamProfile(o1: Pick<ITeamProfile, 'id'> | null, o2: Pick<ITeamProfile, 'id'> | null): boolean {
    return o1 && o2 ? this.getTeamProfileIdentifier(o1) === this.getTeamProfileIdentifier(o2) : o1 === o2;
  }

  addTeamProfileToCollectionIfMissing<Type extends Pick<ITeamProfile, 'id'>>(
    teamProfileCollection: Type[],
    ...teamProfilesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const teamProfiles: Type[] = teamProfilesToCheck.filter(isPresent);
    if (teamProfiles.length > 0) {
      const teamProfileCollectionIdentifiers = teamProfileCollection.map(teamProfileItem => this.getTeamProfileIdentifier(teamProfileItem));
      const teamProfilesToAdd = teamProfiles.filter(teamProfileItem => {
        const teamProfileIdentifier = this.getTeamProfileIdentifier(teamProfileItem);
        if (teamProfileCollectionIdentifiers.includes(teamProfileIdentifier)) {
          return false;
        }
        teamProfileCollectionIdentifiers.push(teamProfileIdentifier);
        return true;
      });
      return [...teamProfilesToAdd, ...teamProfileCollection];
    }
    return teamProfileCollection;
  }
}
