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
export class ParticipateComponent {

    private destroy$ = new Subject<void>();

    participateCarpool: CarpoolDTO[] = [];

    dataSource = new MatTableDataSource<CarpoolDTO>([]);
    displayedColumns: string[] = ['startLabel', 'endLabel', 'estimatedDepartureTime', 'estimatedArrivalTime', 'actions'];

    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;

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
            this.participateCarpool = carpools;
            this.dataSource.data = carpools;
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
                            console.log('Covoiturage créé avec succès:', response);
                        } else {
                            console.error('Échec de la création du covoiturage');
                        }
                    },
                    error: (error) => {
                        console.error('Erreur lors de la création:', error);
                    }
                });

            }

        });
    }

    ngAfterViewInit() {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

}
