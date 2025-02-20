import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ITeamProfile, NewTeamProfile } from '../team-profile.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITeamProfile for edit and NewTeamProfileFormGroupInput for create.
 */
type TeamProfileFormGroupInput = ITeamProfile | PartialWithRequiredKeyOf<NewTeamProfile>;

type TeamProfileFormDefaults = Pick<NewTeamProfile, 'id' | 'imageGalleries' | 'members'>;

type TeamProfileFormGroupContent = {
  id: FormControl<ITeamProfile['id'] | NewTeamProfile['id']>;
  teamID: FormControl<ITeamProfile['teamID']>;
  appLink: FormControl<ITeamProfile['appLink']>;
  appDescription: FormControl<ITeamProfile['appDescription']>;
  logoPic: FormControl<ITeamProfile['logoPic']>;
  logoPicContentType: FormControl<ITeamProfile['logoPicContentType']>;
  nickName: FormControl<ITeamProfile['nickName']>;
  slogan: FormControl<ITeamProfile['slogan']>;
  votes: FormControl<ITeamProfile['votes']>;
  imageGalleries: FormControl<ITeamProfile['imageGalleries']>;
  members: FormControl<ITeamProfile['members']>;
};

export type TeamProfileFormGroup = FormGroup<TeamProfileFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TeamProfileFormService {
  createTeamProfileFormGroup(teamProfile: TeamProfileFormGroupInput = { id: null }): TeamProfileFormGroup {
    const teamProfileRawValue = {
      ...this.getFormDefaults(),
      ...teamProfile,
    };
    return new FormGroup<TeamProfileFormGroupContent>({
      id: new FormControl(
        { value: teamProfileRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      teamID: new FormControl(teamProfileRawValue.teamID, {
        validators: [Validators.required, Validators.min(0)],
      }),
      appLink: new FormControl(teamProfileRawValue.appLink),
      appDescription: new FormControl(teamProfileRawValue.appDescription),
      logoPic: new FormControl(teamProfileRawValue.logoPic),
      logoPicContentType: new FormControl(teamProfileRawValue.logoPicContentType),
      nickName: new FormControl(teamProfileRawValue.nickName),
      slogan: new FormControl(teamProfileRawValue.slogan),
      votes: new FormControl(teamProfileRawValue.votes),
      imageGalleries: new FormControl(teamProfileRawValue.imageGalleries ?? []),
      members: new FormControl(teamProfileRawValue.members ?? []),
    });
  }

  getTeamProfile(form: TeamProfileFormGroup): ITeamProfile | NewTeamProfile {
    return form.getRawValue() as ITeamProfile | NewTeamProfile;
  }

  resetForm(form: TeamProfileFormGroup, teamProfile: TeamProfileFormGroupInput): void {
    const teamProfileRawValue = { ...this.getFormDefaults(), ...teamProfile };
    form.reset(
      {
        ...teamProfileRawValue,
        id: { value: teamProfileRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TeamProfileFormDefaults {
    return {
      id: null,
      imageGalleries: [],
      members: [],
    };
  }
}
