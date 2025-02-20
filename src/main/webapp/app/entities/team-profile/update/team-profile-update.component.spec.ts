import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IScreenshot } from 'app/entities/screenshot/screenshot.model';
import { ScreenshotService } from 'app/entities/screenshot/service/screenshot.service';
import { IUserProfile } from 'app/entities/user-profile/user-profile.model';
import { UserProfileService } from 'app/entities/user-profile/service/user-profile.service';
import { ITeamProfile } from '../team-profile.model';
import { TeamProfileService } from '../service/team-profile.service';
import { TeamProfileFormService } from './team-profile-form.service';

import { TeamProfileUpdateComponent } from './team-profile-update.component';

describe('TeamProfile Management Update Component', () => {
  let comp: TeamProfileUpdateComponent;
  let fixture: ComponentFixture<TeamProfileUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let teamProfileFormService: TeamProfileFormService;
  let teamProfileService: TeamProfileService;
  let screenshotService: ScreenshotService;
  let userProfileService: UserProfileService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TeamProfileUpdateComponent],
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
      .overrideTemplate(TeamProfileUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TeamProfileUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    teamProfileFormService = TestBed.inject(TeamProfileFormService);
    teamProfileService = TestBed.inject(TeamProfileService);
    screenshotService = TestBed.inject(ScreenshotService);
    userProfileService = TestBed.inject(UserProfileService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Screenshot query and add missing value', () => {
      const teamProfile: ITeamProfile = { id: 456 };
      const imageGalleries: IScreenshot[] = [{ id: 17056 }];
      teamProfile.imageGalleries = imageGalleries;

      const screenshotCollection: IScreenshot[] = [{ id: 32734 }];
      jest.spyOn(screenshotService, 'query').mockReturnValue(of(new HttpResponse({ body: screenshotCollection })));
      const additionalScreenshots = [...imageGalleries];
      const expectedCollection: IScreenshot[] = [...additionalScreenshots, ...screenshotCollection];
      jest.spyOn(screenshotService, 'addScreenshotToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ teamProfile });
      comp.ngOnInit();

      expect(screenshotService.query).toHaveBeenCalled();
      expect(screenshotService.addScreenshotToCollectionIfMissing).toHaveBeenCalledWith(
        screenshotCollection,
        ...additionalScreenshots.map(expect.objectContaining),
      );
      expect(comp.screenshotsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call UserProfile query and add missing value', () => {
      const teamProfile: ITeamProfile = { id: 456 };
      const members: IUserProfile[] = [{ id: 12001 }];
      teamProfile.members = members;

      const userProfileCollection: IUserProfile[] = [{ id: 24273 }];
      jest.spyOn(userProfileService, 'query').mockReturnValue(of(new HttpResponse({ body: userProfileCollection })));
      const additionalUserProfiles = [...members];
      const expectedCollection: IUserProfile[] = [...additionalUserProfiles, ...userProfileCollection];
      jest.spyOn(userProfileService, 'addUserProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ teamProfile });
      comp.ngOnInit();

      expect(userProfileService.query).toHaveBeenCalled();
      expect(userProfileService.addUserProfileToCollectionIfMissing).toHaveBeenCalledWith(
        userProfileCollection,
        ...additionalUserProfiles.map(expect.objectContaining),
      );
      expect(comp.userProfilesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const teamProfile: ITeamProfile = { id: 456 };
      const imageGallery: IScreenshot = { id: 6843 };
      teamProfile.imageGalleries = [imageGallery];
      const member: IUserProfile = { id: 29124 };
      teamProfile.members = [member];

      activatedRoute.data = of({ teamProfile });
      comp.ngOnInit();

      expect(comp.screenshotsSharedCollection).toContain(imageGallery);
      expect(comp.userProfilesSharedCollection).toContain(member);
      expect(comp.teamProfile).toEqual(teamProfile);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeamProfile>>();
      const teamProfile = { id: 123 };
      jest.spyOn(teamProfileFormService, 'getTeamProfile').mockReturnValue(teamProfile);
      jest.spyOn(teamProfileService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teamProfile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: teamProfile }));
      saveSubject.complete();

      // THEN
      expect(teamProfileFormService.getTeamProfile).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(teamProfileService.update).toHaveBeenCalledWith(expect.objectContaining(teamProfile));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeamProfile>>();
      const teamProfile = { id: 123 };
      jest.spyOn(teamProfileFormService, 'getTeamProfile').mockReturnValue({ id: null });
      jest.spyOn(teamProfileService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teamProfile: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: teamProfile }));
      saveSubject.complete();

      // THEN
      expect(teamProfileFormService.getTeamProfile).toHaveBeenCalled();
      expect(teamProfileService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeamProfile>>();
      const teamProfile = { id: 123 };
      jest.spyOn(teamProfileService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teamProfile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(teamProfileService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareScreenshot', () => {
      it('Should forward to screenshotService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(screenshotService, 'compareScreenshot');
        comp.compareScreenshot(entity, entity2);
        expect(screenshotService.compareScreenshot).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareUserProfile', () => {
      it('Should forward to userProfileService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userProfileService, 'compareUserProfile');
        comp.compareUserProfile(entity, entity2);
        expect(userProfileService.compareUserProfile).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
