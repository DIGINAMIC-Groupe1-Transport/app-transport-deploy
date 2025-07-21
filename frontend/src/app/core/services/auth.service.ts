import { Injectable, inject, signal, computed, effect } from '@angular/core';
import { Observable, BehaviorSubject, tap, of, throwError, catchError } from 'rxjs';
import { LoginRequest } from '../../shared/models/auth/login.model';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AuthResponse } from '../../shared/models/auth/auth-response-model';
import { Router } from '@angular/router';
import { apiUrl } from '../api/api-url-builder';
import { ApiRoutes } from '../api/api-routes';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly TOKEN_KEY = 'auth_token';
  private readonly API_URL = environment.apiBase;
  
  private router = inject(Router);
  private http = inject(HttpClient);
    
  // Utilisation des signals Angular
  private authToken = signal<string | null>(this.getStoredToken());
  
  // Computed pour l'état d'authentification
  public isAuthenticated = computed(() => {
    const token = this.authToken();
    return this.isTokenValid(token);
  });
  
  // Computed pour le rôle utilisateur
  public userRole = computed(() => {
    const token = this.authToken();
    return this.extractUserRole(token);
  });
  
  // Computed pour vérifier si admin
  public isAdmin = computed(() => {
    const roles = this.userRole();
    const isAdmin = Array.isArray(roles) ? roles.includes('ADMIN') : roles === 'ADMIN';
    console.log("test role boolean:", isAdmin);
    return isAdmin;
  });
  public currentUser = computed(() => {
    const token = this.authToken();
    return this.extractUserSubject(token);
  });
  
  // Effect pour gérer les changements d'état
  private authEffect = effect(() => {
    const authenticated = this.isAuthenticated();
    if (!authenticated && this.authToken()) {
      // Token expiré ou invalide
      this.handleExpiredToken();
    }
  });
  
  // Méthodes publiques
  login(credentials: LoginRequest): Observable<AuthResponse> {
    const url = apiUrl(ApiRoutes.authentication);
    return this.http.post<AuthResponse>(url, credentials)
      .pipe(
        tap(response => {
          const token = response.data?.body;
          this.setToken(token);
        }),
        catchError(error => {
          console.error('Login error:', error);
          return throwError(() => error);
        })
      );
  }
  
  logout(): void {
    this.removeToken();
    this.router.navigate(['/login']);
  }

  // La méthode refreshToken est désactivée car il n'y a pas d'endpoint défini
  // refreshToken(): Observable<AuthResponse> {
  //   // Implémentation du refresh token si nécessaire
  //   const refreshUrl = apiUrl(ApiRoutes.refreshToken);
  //   return this.http.post<AuthResponse>(refreshUrl, {})
  //     .pipe(
  //       tap(response => {
  //         const token = response.data?.body;
  //         this.setToken(token);
  //       }),
  //       catchError(error => {
  //         this.logout();
  //         return throwError(() => error);
  //       })
  //     );
  // }
  
  // Méthodes privées
  private getStoredToken(): string | null {
    if (typeof localStorage !== 'undefined') {
      return localStorage.getItem(this.TOKEN_KEY);
    }
    return null;
  }
  
  private setToken(token: string): void {
    if (typeof localStorage !== 'undefined') {
      localStorage.setItem(this.TOKEN_KEY, token);
    }
    this.authToken.set(token);
  }
  
  private removeToken(): void {
    if (typeof localStorage !== 'undefined') {
      localStorage.removeItem(this.TOKEN_KEY);
    }
    this.authToken.set(null);
  }
  
  private isTokenValid(token: string | null): boolean {
    if (!token) return false;
    
    try {
      const payload = this.decodeTokenPayload(token);
      const now = Math.floor(Date.now() / 1000);
      return payload.exp > now;
    } catch {
      return false;
    }
  }
  
  private decodeTokenPayload(token: string): any {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(jsonPayload);
    } catch (error) {
      console.error('Error decoding token:', error);
      throw error;
    }
  }
  
  private extractUserRole(token: string | null): string | string[] | null {
    if (!token) return null;
    
    try {
      const payload = this.decodeTokenPayload(token);
      // Gérer les deux formats: roles array ou role string
      return payload.roles || payload.role || null;
    } catch {
      return null;
    }
  }
  
  private extractUserSubject(token: string | null): string | null {
    if (!token) return null;
    
    try {
      const payload = this.decodeTokenPayload(token);
      return payload.sub || payload.username || payload.email || null;
    } catch {
      return null;
    }
  }
  
  private handleExpiredToken(): void {
    console.warn('Token expired, logging out...');
    this.logout();
  }
  
  // Méthodes utilitaires publiques
  getToken(): string | null {
    return this.authToken();
  }
  
  hasRole(role: string): boolean {
    const userRoles = this.userRole();
    if (Array.isArray(userRoles)) {
      return userRoles.includes(role);
    }
    return userRoles === role;
  }
  
  hasAnyRole(roles: string[]): boolean {
    return roles.some(role => this.hasRole(role));
  }
  
  getTokenExpirationDate(): Date | null {
    const token = this.authToken();
    if (!token) return null;
    
    try {
      const payload = this.decodeTokenPayload(token);
      return new Date(payload.exp * 1000);
    } catch {
      return null;
    }
  }
  
  getTimeUntilExpiration(): number | null {
    const expirationDate = this.getTokenExpirationDate();
    if (!expirationDate) return null;
    
    return Math.max(0, expirationDate.getTime() - Date.now());
  }
}