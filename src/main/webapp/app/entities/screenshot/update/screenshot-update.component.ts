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
import { ITeamProfile } from 'app/entities/team-profile/team-profile.model';
import { TeamProfileService } from 'app/entities/team-profile/service/team-profile.service';
import { Milestone } from 'app/entities/enumerations/milestone.model';
import { ScreenshotService } from '../service/screenshot.service';
import { IScreenshot } from '../screenshot.model';
import { ScreenshotFormGroup, ScreenshotFormService } from './screenshot-form.service';

@Component({
  standalone: true,
  selector: 'jhi-screenshot-update',
  templateUrl: './screenshot-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ScreenshotUpdateComponent implements OnInit {
  isSaving = false;
  screenshot: IScreenshot | null = null;
  milestoneValues = Object.keys(Milestone);

  teamProfilesSharedCollection: ITeamProfile[] = [];

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected screenshotService = inject(ScreenshotService);
  protected screenshotFormService = inject(ScreenshotFormService);
  protected teamProfileService = inject(TeamProfileService);
  protected elementRef = inject(ElementRef);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ScreenshotFormGroup = this.screenshotFormService.createScreenshotFormGroup();

  compareTeamProfile = (o1: ITeamProfile | null, o2: ITeamProfile | null): boolean => this.teamProfileService.compareTeamProfile(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ screenshot }) => {
      this.screenshot = screenshot;
      if (screenshot) {
        this.updateForm(screenshot);
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
    const screenshot = this.screenshotFormService.getScreenshot(this.editForm);
    if (screenshot.id !== null) {
      this.subscribeToSaveResponse(this.screenshotService.update(screenshot));
    } else {
      this.subscribeToSaveResponse(this.screenshotService.create(screenshot));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IScreenshot>>): void {
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

  protected updateForm(screenshot: IScreenshot): void {
    this.screenshot = screenshot;
    this.screenshotFormService.resetForm(this.editForm, screenshot);

    this.teamProfilesSharedCollection = this.teamProfileService.addTeamProfileToCollectionIfMissing<ITeamProfile>(
      this.teamProfilesSharedCollection,
      ...(screenshot.teamProfiles ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.teamProfileService
      .query()
      .pipe(map((res: HttpResponse<ITeamProfile[]>) => res.body ?? []))
      .pipe(
        map((teamProfiles: ITeamProfile[]) =>
          this.teamProfileService.addTeamProfileToCollectionIfMissing<ITeamProfile>(teamProfiles, ...(this.screenshot?.teamProfiles ?? [])),
        ),
      )
      .subscribe((teamProfiles: ITeamProfile[]) => (this.teamProfilesSharedCollection = teamProfiles));
  }
}
