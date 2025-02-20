import { IUserProfile, NewUserProfile } from './user-profile.model';

export const sampleWithRequiredData: IUserProfile = {
  id: 6424,
};

export const sampleWithPartialData: IUserProfile = {
  id: 28113,
  nickName: 'coliseum',
};

export const sampleWithFullData: IUserProfile = {
  id: 12876,
  uobUsername: 'on',
  nickName: 'beside',
  profilePic: '../fake-data/blob/hipster.png',
  profilePicContentType: 'unknown',
};

export const sampleWithNewData: NewUserProfile = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
