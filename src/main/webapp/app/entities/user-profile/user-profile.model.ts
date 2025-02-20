import { IUser } from 'app/entities/user/user.model';
import { ITeamProfile } from 'app/entities/team-profile/team-profile.model';

export interface IUserProfile {
  id: number;
  uobUsername?: string | null;
  nickName?: string | null;
  profilePic?: string | null;
  profilePicContentType?: string | null;
  user?: Pick<IUser, 'id'> | null;
  teams?: ITeamProfile[] | null;
}

export type NewUserProfile = Omit<IUserProfile, 'id'> & { id: null };
