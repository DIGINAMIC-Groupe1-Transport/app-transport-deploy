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

    dataSource = new MatTableDataSource<CarpoolDTO>([]);
    displayedColumns: string[] = ['startLabel', 'endLabel', 'estimatedDepartureTime', 'estimatedArrivalTime', 'occupiedSeats', 'actions'];

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

        this.carpoolsService.loadOrganizedCarpools()
            .pipe(takeUntil(this.destroy$))
            .subscribe();

        this.carpoolsService.organizedCarpools$.pipe(
            takeUntil(this.destroy$)
        ).subscribe(carpools => {
            this.organizedCarpools = carpools;
            this.dataSource.data = carpools;
            this.cdRef.detectChanges();
        });
    }

    ngAfterViewInit() {

        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
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