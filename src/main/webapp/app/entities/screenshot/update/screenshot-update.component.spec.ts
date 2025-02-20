import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ITeamProfile } from 'app/entities/team-profile/team-profile.model';
import { TeamProfileService } from 'app/entities/team-profile/service/team-profile.service';
import { ScreenshotService } from '../service/screenshot.service';
import { IScreenshot } from '../screenshot.model';
import { ScreenshotFormService } from './screenshot-form.service';

import { ScreenshotUpdateComponent } from './screenshot-update.component';

describe('Screenshot Management Update Component', () => {
  let comp: ScreenshotUpdateComponent;
  let fixture: ComponentFixture<ScreenshotUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let screenshotFormService: ScreenshotFormService;
  let screenshotService: ScreenshotService;
  let teamProfileService: TeamProfileService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ScreenshotUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ScreenshotUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ScreenshotUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    screenshotFormService = TestBed.inject(ScreenshotFormService);
    screenshotService = TestBed.inject(ScreenshotService);
    teamProfileService = TestBed.inject(TeamProfileService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call TeamProfile query and add missing value', () => {
      const screenshot: IScreenshot = { id: 456 };
      const teamProfiles: ITeamProfile[] = [{ id: 15689 }];
      screenshot.teamProfiles = teamProfiles;

      const teamProfileCollection: ITeamProfile[] = [{ id: 16726 }];
      jest.spyOn(teamProfileService, 'query').mockReturnValue(of(new HttpResponse({ body: teamProfileCollection })));
      const additionalTeamProfiles = [...teamProfiles];
      const expectedCollection: ITeamProfile[] = [...additionalTeamProfiles, ...teamProfileCollection];
      jest.spyOn(teamProfileService, 'addTeamProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ screenshot });
      comp.ngOnInit();

      expect(teamProfileService.query).toHaveBeenCalled();
      expect(teamProfileService.addTeamProfileToCollectionIfMissing).toHaveBeenCalledWith(
        teamProfileCollection,
        ...additionalTeamProfiles.map(expect.objectContaining),
      );
      expect(comp.teamProfilesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const screenshot: IScreenshot = { id: 456 };
      const teamProfile: ITeamProfile = { id: 14263 };
      screenshot.teamProfiles = [teamProfile];

      activatedRoute.data = of({ screenshot });
      comp.ngOnInit();

      expect(comp.teamProfilesSharedCollection).toContain(teamProfile);
      expect(comp.screenshot).toEqual(screenshot);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IScreenshot>>();
      const screenshot = { id: 123 };
      jest.spyOn(screenshotFormService, 'getScreenshot').mockReturnValue(screenshot);
      jest.spyOn(screenshotService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ screenshot });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: screenshot }));
      saveSubject.complete();

      // THEN
      expect(screenshotFormService.getScreenshot).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(screenshotService.update).toHaveBeenCalledWith(expect.objectContaining(screenshot));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IScreenshot>>();
      const screenshot = { id: 123 };
      jest.spyOn(screenshotFormService, 'getScreenshot').mockReturnValue({ id: null });
      jest.spyOn(screenshotService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ screenshot: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: screenshot }));
      saveSubject.complete();

      // THEN
      expect(screenshotFormService.getScreenshot).toHaveBeenCalled();
      expect(screenshotService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IScreenshot>>();
      const screenshot = { id: 123 };
      jest.spyOn(screenshotService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ screenshot });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(screenshotService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTeamProfile', () => {
      it('Should forward to teamProfileService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(teamProfileService, 'compareTeamProfile');
        comp.compareTeamProfile(entity, entity2);
        expect(teamProfileService.compareTeamProfile).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
