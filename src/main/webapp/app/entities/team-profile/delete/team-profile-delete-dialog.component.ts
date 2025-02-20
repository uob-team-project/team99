import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ITeamProfile } from '../team-profile.model';
import { TeamProfileService } from '../service/team-profile.service';

@Component({
  standalone: true,
  templateUrl: './team-profile-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class TeamProfileDeleteDialogComponent {
  teamProfile?: ITeamProfile;

  protected teamProfileService = inject(TeamProfileService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.teamProfileService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
