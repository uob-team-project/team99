import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../team-profile.test-samples';

import { TeamProfileFormService } from './team-profile-form.service';

describe('TeamProfile Form Service', () => {
  let service: TeamProfileFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TeamProfileFormService);
  });

  describe('Service methods', () => {
    describe('createTeamProfileFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTeamProfileFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            teamID: expect.any(Object),
            appLink: expect.any(Object),
            appDescription: expect.any(Object),
            logoPic: expect.any(Object),
            nickName: expect.any(Object),
            slogan: expect.any(Object),
            votes: expect.any(Object),
            imageGalleries: expect.any(Object),
            members: expect.any(Object),
          }),
        );
      });

      it('passing ITeamProfile should create a new form with FormGroup', () => {
        const formGroup = service.createTeamProfileFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            teamID: expect.any(Object),
            appLink: expect.any(Object),
            appDescription: expect.any(Object),
            logoPic: expect.any(Object),
            nickName: expect.any(Object),
            slogan: expect.any(Object),
            votes: expect.any(Object),
            imageGalleries: expect.any(Object),
            members: expect.any(Object),
          }),
        );
      });
    });

    describe('getTeamProfile', () => {
      it('should return NewTeamProfile for default TeamProfile initial value', () => {
        const formGroup = service.createTeamProfileFormGroup(sampleWithNewData);

        const teamProfile = service.getTeamProfile(formGroup) as any;

        expect(teamProfile).toMatchObject(sampleWithNewData);
      });

      it('should return NewTeamProfile for empty TeamProfile initial value', () => {
        const formGroup = service.createTeamProfileFormGroup();

        const teamProfile = service.getTeamProfile(formGroup) as any;

        expect(teamProfile).toMatchObject({});
      });

      it('should return ITeamProfile', () => {
        const formGroup = service.createTeamProfileFormGroup(sampleWithRequiredData);

        const teamProfile = service.getTeamProfile(formGroup) as any;

        expect(teamProfile).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITeamProfile should not enable id FormControl', () => {
        const formGroup = service.createTeamProfileFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTeamProfile should disable id FormControl', () => {
        const formGroup = service.createTeamProfileFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
