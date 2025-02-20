import { IScreenshot } from 'app/entities/screenshot/screenshot.model';
import { IUserProfile } from 'app/entities/user-profile/user-profile.model';

export interface ITeamProfile {
  id: number;
  teamID?: number | null;
  appLink?: string | null;
  appDescription?: string | null;
  logoPic?: string | null;
  logoPicContentType?: string | null;
  nickName?: string | null;
  slogan?: string | null;
  votes?: number | null;
  imageGalleries?: IScreenshot[] | null;
  members?: IUserProfile[] | null;
}

export type NewTeamProfile = Omit<ITeamProfile, 'id'> & { id: null };
