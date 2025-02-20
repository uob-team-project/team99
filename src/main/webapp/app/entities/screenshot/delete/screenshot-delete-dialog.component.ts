import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IScreenshot } from '../screenshot.model';
import { ScreenshotService } from '../service/screenshot.service';

@Component({
  standalone: true,
  templateUrl: './screenshot-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ScreenshotDeleteDialogComponent {
  screenshot?: IScreenshot;

  protected screenshotService = inject(ScreenshotService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.screenshotService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
