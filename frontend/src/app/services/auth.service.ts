import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { LoginRequest, LoginResponse } from '../models/portfolio.models';

const TOKEN_KEY = 'portfolio.admin.token';
const TOKEN_EXPIRES_KEY = 'portfolio.admin.expiresAt';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiUrl;

  private readonly tokenSignal = signal<string | null>(this.readStoredToken());
  readonly isAuthenticated = computed(() => this.tokenSignal() !== null);

  login(req: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.base}/api/auth/login`, req).pipe(
      tap((res) => {
        const expiresAt = Date.now() + res.expiresInMs;
        try {
          sessionStorage.setItem(TOKEN_KEY, res.token);
          sessionStorage.setItem(TOKEN_EXPIRES_KEY, String(expiresAt));
        } catch {
          /* sessionStorage may be unavailable in some browser contexts; ignore */
        }
        this.tokenSignal.set(res.token);
      })
    );
  }

  logout(): void {
    try {
      sessionStorage.removeItem(TOKEN_KEY);
      sessionStorage.removeItem(TOKEN_EXPIRES_KEY);
    } catch {
      /* ignore */
    }
    this.tokenSignal.set(null);
  }

  token(): string | null {
    return this.tokenSignal();
  }

  private readStoredToken(): string | null {
    try {
      const token = sessionStorage.getItem(TOKEN_KEY);
      const expiresAtRaw = sessionStorage.getItem(TOKEN_EXPIRES_KEY);
      if (!token || !expiresAtRaw) {
        return null;
      }
      const expiresAt = Number(expiresAtRaw);
      if (Number.isNaN(expiresAt) || expiresAt <= Date.now()) {
        sessionStorage.removeItem(TOKEN_KEY);
        sessionStorage.removeItem(TOKEN_EXPIRES_KEY);
        return null;
      }
      return token;
    } catch {
      return null;
    }
  }
}
