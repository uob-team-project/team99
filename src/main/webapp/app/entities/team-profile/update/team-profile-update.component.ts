import { Component, ElementRef, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IScreenshot } from 'app/entities/screenshot/screenshot.model';
import { ScreenshotService } from 'app/entities/screenshot/service/screenshot.service';
import { IUserProfile } from 'app/entities/user-profile/user-profile.model';
import { UserProfileService } from 'app/entities/user-profile/service/user-profile.service';
import { TeamProfileService } from '../service/team-profile.service';
import { ITeamProfile } from '../team-profile.model';
import { TeamProfileFormGroup, TeamProfileFormService } from './team-profile-form.service';

@Component({
  standalone: true,
  selector: 'jhi-team-profile-update',
  templateUrl: './team-profile-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TeamProfileUpdateComponent implements OnInit {
  isSaving = false;
  teamProfile: ITeamProfile | null = null;

  screenshotsSharedCollection: IScreenshot[] = [];
  userProfilesSharedCollection: IUserProfile[] = [];

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected teamProfileService = inject(TeamProfileService);
  protected teamProfileFormService = inject(TeamProfileFormService);
  protected screenshotService = inject(ScreenshotService);
  protected userProfileService = inject(UserProfileService);
  protected elementRef = inject(ElementRef);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TeamProfileFormGroup = this.teamProfileFormService.createTeamProfileFormGroup();

  compareScreenshot = (o1: IScreenshot | null, o2: IScreenshot | null): boolean => this.screenshotService.compareScreenshot(o1, o2);

  compareUserProfile = (o1: IUserProfile | null, o2: IUserProfile | null): boolean => this.userProfileService.compareUserProfile(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ teamProfile }) => {
      this.teamProfile = teamProfile;
      if (teamProfile) {
        this.updateForm(teamProfile);
      }

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('teamproject24App.error', { message: err.message })),
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector(`#${idInput}`)) {
      this.elementRef.nativeElement.querySelector(`#${idInput}`).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const teamProfile = this.teamProfileFormService.getTeamProfile(this.editForm);
    if (teamProfile.id !== null) {
      this.subscribeToSaveResponse(this.teamProfileService.update(teamProfile));
    } else {
      this.subscribeToSaveResponse(this.teamProfileService.create(teamProfile));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITeamProfile>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(teamProfile: ITeamProfile): void {
    this.teamProfile = teamProfile;
    this.teamProfileFormService.resetForm(this.editForm, teamProfile);

    this.screenshotsSharedCollection = this.screenshotService.addScreenshotToCollectionIfMissing<IScreenshot>(
      this.screenshotsSharedCollection,
      ...(teamProfile.imageGalleries ?? []),
    );
    this.userProfilesSharedCollection = this.userProfileService.addUserProfileToCollectionIfMissing<IUserProfile>(
      this.userProfilesSharedCollection,
      ...(teamProfile.members ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.screenshotService
      .query()
      .pipe(map((res: HttpResponse<IScreenshot[]>) => res.body ?? []))
      .pipe(
        map((screenshots: IScreenshot[]) =>
          this.screenshotService.addScreenshotToCollectionIfMissing<IScreenshot>(screenshots, ...(this.teamProfile?.imageGalleries ?? [])),
        ),
      )
      .subscribe((screenshots: IScreenshot[]) => (this.screenshotsSharedCollection = screenshots));

    this.userProfileService
      .query()
      .pipe(map((res: HttpResponse<IUserProfile[]>) => res.body ?? []))
      .pipe(
        map((userProfiles: IUserProfile[]) =>
          this.userProfileService.addUserProfileToCollectionIfMissing<IUserProfile>(userProfiles, ...(this.teamProfile?.members ?? [])),
        ),
      )
      .subscribe((userProfiles: IUserProfile[]) => (this.userProfilesSharedCollection = userProfiles));
  }
}
