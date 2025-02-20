import { IScreenshot, NewScreenshot } from './screenshot.model';

export const sampleWithRequiredData: IScreenshot = {
  id: 21565,
};

export const sampleWithPartialData: IScreenshot = {
  id: 15780,
};

export const sampleWithFullData: IScreenshot = {
  id: 15562,
  pic: '../fake-data/blob/hipster.png',
  picContentType: 'unknown',
  caption: 'rubbery though only',
  milestone: 'M3',
};

export const sampleWithNewData: NewScreenshot = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
