import { ITeamProfile } from 'app/entities/team-profile/team-profile.model';
import { Milestone } from 'app/entities/enumerations/milestone.model';

export interface IScreenshot {
  id: number;
  pic?: string | null;
  picContentType?: string | null;
  caption?: string | null;
  milestone?: keyof typeof Milestone | null;
  teamProfiles?: ITeamProfile[] | null;
}

export type NewScreenshot = Omit<IScreenshot, 'id'> & { id: null };
