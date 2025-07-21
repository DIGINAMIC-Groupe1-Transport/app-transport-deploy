import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { SnackbarService } from '../services/snackbar.service';

export const httpErrorInterceptor: HttpInterceptorFn = (req, next) => {
  const snackbar = inject(SnackbarService);
  
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      const message = error.error?.message || 'Erreur inattendue du serveur';
      snackbar.show(message);
      return throwError(() => error);
    })
  );
};