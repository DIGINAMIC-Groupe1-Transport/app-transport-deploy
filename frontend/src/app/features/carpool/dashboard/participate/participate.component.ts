import { Dialog } from '@angular/cdk/dialog';
import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, ViewChild } from '@angular/core';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { CarpoolDTO } from 'app/shared/models/carpool/carpool.dto';
import { CarpoolsService } from 'app/shared/services/carpools.service';
import { Subject, takeUntil } from 'rxjs';
import { DetailsModalComponent } from '../../details/details.modal.component';

@Component({
    selector: 'app-participate',
    standalone: true,
    imports: [MatTableModule, MatPaginatorModule, MatPaginator, MatPaginatorModule, MatSort, MatSortModule, CommonModule],
    templateUrl: './participate.component.html',
    styleUrl: './participate.component.css'
})
export class ParticipateComponent implements OnInit, OnDestroy, AfterViewInit {

    private destroy$ = new Subject<void>();

    participateCarpool: CarpoolDTO[] = [];

    dataSourceCurrent = new MatTableDataSource<CarpoolDTO>([]);
    dataSourcePast = new MatTableDataSource<CarpoolDTO>([]);

    displayedColumnsCurrent: string[] = ['startLabel', 'endLabel', 'estimatedDepartureTime', 'estimatedArrivalTime', 'actions'];
    displayedColumnsPast: string[] = ['startLabel', 'endLabel', 'estimatedDepartureTime', 'estimatedArrivalTime'];

    @ViewChild(MatPaginator) paginatorPast!: MatPaginator;
    @ViewChild(MatSort) sortPast!: MatSort;

    @ViewChild(MatPaginator) paginatorCurrent!: MatPaginator;
    @ViewChild(MatSort) sortCurrent!: MatSort;

    constructor(
        private carpoolsService: CarpoolsService,
        private dialog: Dialog,
        private cdRef: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        this.loadData();
    }

    private loadData(): void {

        this.carpoolsService.loadParticipateCarpools()
            .pipe(takeUntil(this.destroy$))
            .subscribe();

        this.carpoolsService.participateCarpools$.pipe(
            takeUntil(this.destroy$)
        ).subscribe(carpools => {

            const now = new Date().getTime();

            this.participateCarpool = carpools;

            this.dataSourceCurrent.data = carpools.filter(element => {
                  const dateToCheck = new Date(element.estimatedDepartureTime).getTime();
                  return dateToCheck > now;
                });

            this.dataSourcePast.data = carpools.filter(element => {
                  const dateToCheck = new Date(element.estimatedDepartureTime).getTime();
                  return dateToCheck <= now;
                });
            this.cdRef.detectChanges();
        });
    }


    openModalAsCancel(carpoolId: number): void {
        this.dialog.open(DetailsModalComponent, {
            data: {
                carpoolId: carpoolId,
                mode: 'cancel'
            }
        }).closed.subscribe(result => {
            if (result !== true) {
                return;
            } else {

                this.carpoolsService.deleteParticipateCarpool(carpoolId).subscribe({
                    next: (response) => {
                        if (response) {
                            console.log('Covoiturage annulé avec succés:', response);
                        } else {
                            console.error('Échec lors de l\'annulation du covoiturage');
                        }
                    },
                    error: (error) => {
                        console.error('Échec lors de l\'annulation du covoiturage:', error);
                    }
                });

            }

        });
    }

    ngAfterViewInit() {
        this.dataSourcePast.paginator = this.paginatorPast;
        this.dataSourcePast.sort = this.sortPast;
        this.dataSourceCurrent.paginator = this.paginatorCurrent;
        this.dataSourceCurrent.sort = this.sortCurrent;
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

}
