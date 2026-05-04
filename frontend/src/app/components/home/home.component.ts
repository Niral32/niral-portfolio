import { AsyncPipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { catchError, map, of } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ProjectDto } from '../../models/portfolio.models';
import { PortfolioApiService } from '../../services/portfolio-api.service';

@Component({
  selector: 'app-home',
  imports: [RouterLink, AsyncPipe],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {
  private readonly api = inject(PortfolioApiService);

  protected readonly resumeUrl = `${environment.apiUrl}/api/resume`;
  protected readonly photoVersion = signal<number>(Date.now());
  protected readonly photoUrl = computed(
    () => `${environment.apiUrl}/api/identity/photo?t=${this.photoVersion()}`
  );

  protected readonly ownerName = signal('Niral Patel');
  protected readonly ownerTitle = signal('Java Full Stack Developer');

  /** Top 3 projects for the home preview. */
  protected readonly recentProjects$ = this.api.getProjects().pipe(
    map((list) => list.slice(0, 3)),
    catchError(() => of<ProjectDto[]>([]))
  );

  constructor() {
    this.api.getSettings().subscribe({
      next: (s) => {
        if (s.ownerName) this.ownerName.set(s.ownerName);
        if (s.ownerTitle) this.ownerTitle.set(s.ownerTitle);
      }
    });
  }

  protected onPhotoError(img: HTMLImageElement): void {
    if (!img.src.endsWith('/profile.jpg')) {
      img.src = '/profile.jpg';
    }
  }
}
