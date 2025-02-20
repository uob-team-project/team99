import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IScreenshot, NewScreenshot } from '../screenshot.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IScreenshot for edit and NewScreenshotFormGroupInput for create.
 */
type ScreenshotFormGroupInput = IScreenshot | PartialWithRequiredKeyOf<NewScreenshot>;

type ScreenshotFormDefaults = Pick<NewScreenshot, 'id' | 'teamProfiles'>;

type ScreenshotFormGroupContent = {
  id: FormControl<IScreenshot['id'] | NewScreenshot['id']>;
  pic: FormControl<IScreenshot['pic']>;
  picContentType: FormControl<IScreenshot['picContentType']>;
  caption: FormControl<IScreenshot['caption']>;
  milestone: FormControl<IScreenshot['milestone']>;
  teamProfiles: FormControl<IScreenshot['teamProfiles']>;
};

export type ScreenshotFormGroup = FormGroup<ScreenshotFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ScreenshotFormService {
  createScreenshotFormGroup(screenshot: ScreenshotFormGroupInput = { id: null }): ScreenshotFormGroup {
    const screenshotRawValue = {
      ...this.getFormDefaults(),
      ...screenshot,
    };
    return new FormGroup<ScreenshotFormGroupContent>({
      id: new FormControl(
        { value: screenshotRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      pic: new FormControl(screenshotRawValue.pic),
      picContentType: new FormControl(screenshotRawValue.picContentType),
      caption: new FormControl(screenshotRawValue.caption),
      milestone: new FormControl(screenshotRawValue.milestone),
      teamProfiles: new FormControl(screenshotRawValue.teamProfiles ?? []),
    });
  }

  getScreenshot(form: ScreenshotFormGroup): IScreenshot | NewScreenshot {
    return form.getRawValue() as IScreenshot | NewScreenshot;
  }

  resetForm(form: ScreenshotFormGroup, screenshot: ScreenshotFormGroupInput): void {
    const screenshotRawValue = { ...this.getFormDefaults(), ...screenshot };
    form.reset(
      {
        ...screenshotRawValue,
        id: { value: screenshotRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ScreenshotFormDefaults {
    return {
      id: null,
      teamProfiles: [],
    };
  }
}
