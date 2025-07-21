import { Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';
import { AdminGuard } from './core/guards/admin.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'covoiturages/recherche', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () =>
      import('./features/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'covoiturages',
    children: [
      {
        path: 'recherche',
        loadComponent: () =>
          import('./features/carpool/search/search.component').then(
            (m) => m.SearchComponent
          ),
        canActivate: [AuthGuard],
      },
      {
        path: 'tableau',
        children: [
          {
            path: '',
            loadComponent: () =>
              import('./features/carpool/dashboard/dashboard.component').then(
                (m) => m.DashboardComponent
              ),
            canActivate: [AuthGuard],
          },
          {
            path: 'creation/vehicule',
            loadComponent: () =>
              import(
                './features/carpool/dashboard/organize/vehicle/vehicle.component'
              ).then((m) => m.VehicleComponent),
            canActivate: [AuthGuard],
          },
          {
            path: 'creation/covoiturage',
            loadComponent: () =>
              import(
                './features/carpool/dashboard/organize/create/create.component'
              ).then((m) => m.CreateComponent),
            canActivate: [AuthGuard],
          },
        ],
      },
    ],
  },
  {
    path: 'vehicules',
    children: [
      {
        path: 'reservation',
        loadComponent: () =>
          import(
            './features/vehicle-reservation/vehicle-reservation.component'
          ).then((m) => m.VehicleReservationComponent),
        canActivate: [AuthGuard],
      },
      {
        path: 'ajout',
        loadComponent: () =>
          import('./features/vehicle-management/vehicle-add/vehicle-add.component').then(
            (m) => m.VehicleAddComponent
          ),
        canActivate: [AuthGuard],
      },
      {
        path: 'gestion',
        loadComponent: () =>
          import('./features/vehicle-management/vehicle-list/vehicle-management.component').then(
            (m) => m.VehicleManagementComponent
          ),
        canActivate: [AuthGuard, AdminGuard],
      },
    ],
  },
  {
    path: '**',
    loadComponent: () =>
      import('./core/errors/forbidden/forbidden.component').then(
        (m) => m.ForbiddenComponent
      ),
    canActivate: [AuthGuard],
  },
];
