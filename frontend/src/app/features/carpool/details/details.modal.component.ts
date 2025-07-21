import { DIALOG_DATA, DialogRef } from '@angular/cdk/dialog';
import { CommonModule } from '@angular/common';
import { Component, Inject, Input, OnInit } from '@angular/core';
import { CarpoolDTO } from 'app/shared/models/carpool/carpool.dto';
import { CarpoolsService } from 'app/shared/services/carpools.service';

@Component({
  selector: 'app-details-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './details.modal.component.html',
  styleUrl: './details.modal.component.css'
})
export class DetailsModalComponent implements OnInit {


  mode!: string;
  carpoolId!: number;
  isLoading: boolean = false;
  carpoolDetails!: CarpoolDTO;

  constructor(
    private carpoolsService: CarpoolsService,
    private dialogRef: DialogRef<boolean>,
    @Inject(DIALOG_DATA) public data: {
      carpoolId: number,
      mode: string
    }
  ) {
  }
  ngOnInit(): void {
    this.mode = this.data.mode;
    this.carpoolId = this.data.carpoolId;
    this.loadSearcheCarpoolDetails();
    this.isLoading = true
  }

  loadSearcheCarpoolDetails() {
   this.carpoolsService.getCarpoolDetails(this.carpoolId).subscribe(
    result => {
      this.carpoolDetails = result.data,
      this.isLoading = false
    }
   )
  }

  actionIsConfirmed(confirmed: boolean) {
    this.dialogRef.close(confirmed);
  }

}
