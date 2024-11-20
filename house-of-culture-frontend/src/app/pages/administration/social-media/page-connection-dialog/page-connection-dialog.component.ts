import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {FacebookPageResponseTs} from "../../../../../../out/api/model/facebook-page-response";
import {ConnectFacebookPageRequestTs} from "../../../../../../out/api/model/connect-facebook-page-request";

@Component({
  selector: 'app-page-connection-dialog',
  templateUrl: './page-connection-dialog.component.html',
  styleUrls: ['./page-connection-dialog.component.scss']
})
export class PageConnectionDialogComponent {
  // TODO: obsluzyc 0 stron
  selectedPage?: FacebookPageResponseTs =
    this.availablePages.length !== 0 ? this.availablePages[0] : undefined
  connectInstagram: boolean = false

  constructor(
    public dialogRef: MatDialogRef<PageConnectionDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public availablePages: FacebookPageResponseTs[],
  ) {}

  closeDialog() {
    this.dialogRef.close()
  }

  getOnCloseResult(): ConnectFacebookPageRequestTs {
    return {
      pageId: this.selectedPage!!.id,
      instagramId: this.selectedPage!!.instagramId
    }
  }

  isInstagramConnected() {
    return this.selectedPage?.instagramId !== null
  }

  instagramCheckboxText() {
    return this.isInstagramConnected()
      ? `Połącz konto Instagram: ${this.selectedPage?.instagramUsername}`
      : `Strona ${this.selectedPage!!.name} nie jest połączona z żadnym kontem Instagram.`
  }
}
