import { Component, inject, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { catchError, map, of } from 'rxjs';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-resume',
  imports: [RouterLink],
  templateUrl: './resume.component.html',
  styleUrl: './resume.component.scss'
})
export class ResumeComponent {
  private readonly http = inject(HttpClient);
  protected readonly resumeUrl = `${environment.apiUrl}/api/resume`;

  // 'loading' | 'present' | 'absent'
  protected readonly status = signal<'loading' | 'present' | 'absent'>('loading');

  constructor() {
    // Any error (404, network, auth) → treat as absent. Better to show the
    // friendly "coming soon" message than a broken View button.
    this.http
      .head(this.resumeUrl, { observe: 'response' })
      .pipe(
        map(() => 'present' as const),
        catchError(() => of('absent' as const))
      )
      .subscribe((s) => this.status.set(s));
  }
}
