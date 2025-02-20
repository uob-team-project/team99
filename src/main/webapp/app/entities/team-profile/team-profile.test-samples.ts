import { ITeamProfile, NewTeamProfile } from './team-profile.model';

export const sampleWithRequiredData: ITeamProfile = {
  id: 2448,
  teamID: 6606,
};

export const sampleWithPartialData: ITeamProfile = {
  id: 8012,
  teamID: 4592,
  appDescription: '../fake-data/blob/hipster.txt',
  nickName: 'speedy times pharmacopoeia',
  slogan: 'lest',
};

export const sampleWithFullData: ITeamProfile = {
  id: 21791,
  teamID: 21770,
  appLink: 'eek mealy',
  appDescription: '../fake-data/blob/hipster.txt',
  logoPic: '../fake-data/blob/hipster.png',
  logoPicContentType: 'unknown',
  nickName: 'bench hence rosin',
  slogan: 'after',
  votes: 1676,
};

export const sampleWithNewData: NewTeamProfile = {
  teamID: 9781,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
