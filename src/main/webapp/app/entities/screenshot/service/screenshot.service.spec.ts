import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IScreenshot } from '../screenshot.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../screenshot.test-samples';

import { ScreenshotService } from './screenshot.service';

const requireRestSample: IScreenshot = {
  ...sampleWithRequiredData,
};

describe('Screenshot Service', () => {
  let service: ScreenshotService;
  let httpMock: HttpTestingController;
  let expectedResult: IScreenshot | IScreenshot[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ScreenshotService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Screenshot', () => {
      const screenshot = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(screenshot).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Screenshot', () => {
      const screenshot = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(screenshot).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Screenshot', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Screenshot', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Screenshot', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addScreenshotToCollectionIfMissing', () => {
      it('should add a Screenshot to an empty array', () => {
        const screenshot: IScreenshot = sampleWithRequiredData;
        expectedResult = service.addScreenshotToCollectionIfMissing([], screenshot);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(screenshot);
      });

      it('should not add a Screenshot to an array that contains it', () => {
        const screenshot: IScreenshot = sampleWithRequiredData;
        const screenshotCollection: IScreenshot[] = [
          {
            ...screenshot,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addScreenshotToCollectionIfMissing(screenshotCollection, screenshot);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Screenshot to an array that doesn't contain it", () => {
        const screenshot: IScreenshot = sampleWithRequiredData;
        const screenshotCollection: IScreenshot[] = [sampleWithPartialData];
        expectedResult = service.addScreenshotToCollectionIfMissing(screenshotCollection, screenshot);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(screenshot);
      });

      it('should add only unique Screenshot to an array', () => {
        const screenshotArray: IScreenshot[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const screenshotCollection: IScreenshot[] = [sampleWithRequiredData];
        expectedResult = service.addScreenshotToCollectionIfMissing(screenshotCollection, ...screenshotArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const screenshot: IScreenshot = sampleWithRequiredData;
        const screenshot2: IScreenshot = sampleWithPartialData;
        expectedResult = service.addScreenshotToCollectionIfMissing([], screenshot, screenshot2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(screenshot);
        expect(expectedResult).toContain(screenshot2);
      });

      it('should accept null and undefined values', () => {
        const screenshot: IScreenshot = sampleWithRequiredData;
        expectedResult = service.addScreenshotToCollectionIfMissing([], null, screenshot, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(screenshot);
      });

      it('should return initial array if no Screenshot is added', () => {
        const screenshotCollection: IScreenshot[] = [sampleWithRequiredData];
        expectedResult = service.addScreenshotToCollectionIfMissing(screenshotCollection, undefined, null);
        expect(expectedResult).toEqual(screenshotCollection);
      });
    });

    describe('compareScreenshot', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareScreenshot(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareScreenshot(entity1, entity2);
        const compareResult2 = service.compareScreenshot(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareScreenshot(entity1, entity2);
        const compareResult2 = service.compareScreenshot(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareScreenshot(entity1, entity2);
        const compareResult2 = service.compareScreenshot(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
