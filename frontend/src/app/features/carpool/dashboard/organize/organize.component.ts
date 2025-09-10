import { ChangeDetectorRef } from '@angular/core'
import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, OnDestroy, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations'; // Add this line
import { Router } from '@angular/router';
import { CarpoolDTO } from 'app/shared/models/carpool/carpool.dto';
import { CarpoolsService } from 'app/shared/services/carpools.service';
import { Observable, Subject, takeUntil } from 'rxjs';
import { CreateComponent } from "./create/create.component";
import { Dialog } from '@angular/cdk/dialog';
import { DetailsModalComponent } from '../../details/details.modal.component';

@Component({
    selector: 'app-organize',
    standalone: true,
    imports: [MatTableModule, MatPaginator, MatPaginatorModule, MatSort, MatSortModule, CommonModule, CreateComponent],
    templateUrl: './organize.component.html',
    styleUrl: './organize.component.css'
})

export class OrganizeComponent implements OnInit, OnDestroy, AfterViewInit {

    private destroy$ = new Subject<void>();

    organizedCarpools: CarpoolDTO[] = [];

    dataSourceCurrent = new MatTableDataSource<CarpoolDTO>([]);
    dataSourcePast = new MatTableDataSource<CarpoolDTO>([]);

    displayedColumnsCurrent: string[] = ['startLabel', 'endLabel', 'estimatedDepartureTime', 'estimatedArrivalTime', 'occupiedSeats', 'actions'];
    displayedColumnsPast: string[] = ['startLabel', 'endLabel', 'estimatedDepartureTime', 'estimatedArrivalTime', 'occupiedSeats'];

    @ViewChild(MatPaginator) paginatorPast!: MatPaginator;
    @ViewChild(MatSort) sortPast!: MatSort;

    @ViewChild(MatPaginator) paginatorCurrent!: MatPaginator;
    @ViewChild(MatSort) sortCurrent!: MatSort;

    constructor(
        private carpoolsService: CarpoolsService,
        private dialog: Dialog,
        private cdRef: ChangeDetectorRef
    ) { }

    private loadData(): void {

        this.carpoolsService.loadOrganizedCarpools()
            .pipe(takeUntil(this.destroy$))
            .subscribe();

        this.carpoolsService.organizedCarpools$.pipe(
            takeUntil(this.destroy$)
        ).subscribe(carpools => {

            const now = new Date().getTime();

            this.organizedCarpools = carpools;

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

  ngOnInit(): void {
          this.loadData();
      }

    ngAfterViewInit() {
        this.dataSourcePast.paginator = this.paginatorPast;
        this.dataSourcePast.sort = this.sortPast;
        this.dataSourceCurrent.paginator = this.paginatorCurrent;
        this.dataSourceCurrent.sort = this.sortCurrent;
    }

    openModalAsDelete(carpoolId: number): void {
        this.dialog.open(DetailsModalComponent, {
            data: {
                carpoolId: carpoolId,
                mode: 'delete'
            }
        }).closed.subscribe(result => {
            if (result !== true) {
                return;
            } else {
                this.carpoolsService.deleteOrganizedCarpool(carpoolId).subscribe({
                    next: (response) => {
                        console.log(response.message);
                    },
                    error: (err) => {
                        console.error('Erreur lors de la suppression', err);
                    }
                });
            }

        });
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }
}
