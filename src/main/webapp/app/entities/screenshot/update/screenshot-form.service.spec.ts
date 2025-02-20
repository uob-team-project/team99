import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../screenshot.test-samples';

import { ScreenshotFormService } from './screenshot-form.service';

describe('Screenshot Form Service', () => {
  let service: ScreenshotFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ScreenshotFormService);
  });

  describe('Service methods', () => {
    describe('createScreenshotFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createScreenshotFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            pic: expect.any(Object),
            caption: expect.any(Object),
            milestone: expect.any(Object),
            teamProfiles: expect.any(Object),
          }),
        );
      });

      it('passing IScreenshot should create a new form with FormGroup', () => {
        const formGroup = service.createScreenshotFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            pic: expect.any(Object),
            caption: expect.any(Object),
            milestone: expect.any(Object),
            teamProfiles: expect.any(Object),
          }),
        );
      });
    });

    describe('getScreenshot', () => {
      it('should return NewScreenshot for default Screenshot initial value', () => {
        const formGroup = service.createScreenshotFormGroup(sampleWithNewData);

        const screenshot = service.getScreenshot(formGroup) as any;

        expect(screenshot).toMatchObject(sampleWithNewData);
      });

      it('should return NewScreenshot for empty Screenshot initial value', () => {
        const formGroup = service.createScreenshotFormGroup();

        const screenshot = service.getScreenshot(formGroup) as any;

        expect(screenshot).toMatchObject({});
      });

      it('should return IScreenshot', () => {
        const formGroup = service.createScreenshotFormGroup(sampleWithRequiredData);

        const screenshot = service.getScreenshot(formGroup) as any;

        expect(screenshot).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IScreenshot should not enable id FormControl', () => {
        const formGroup = service.createScreenshotFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewScreenshot should disable id FormControl', () => {
        const formGroup = service.createScreenshotFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
