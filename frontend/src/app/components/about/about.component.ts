import { AsyncPipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { RouterLink } from '@angular/router';
import { catchError, map, of } from 'rxjs';
import { AboutContentDto } from '../../models/portfolio.models';
import { PortfolioApiService } from '../../services/portfolio-api.service';
import { environment } from '../../../environments/environment';

interface AboutView {
  summary: string;
  educationHtml: SafeHtml;
  passions: string;
}

@Component({
  selector: 'app-about',
  imports: [AsyncPipe, RouterLink],
  templateUrl: './about.component.html',
  styleUrl: './about.component.scss'
})
export class AboutComponent {
  private readonly api = inject(PortfolioApiService);
  private readonly sanitizer = inject(DomSanitizer);

  protected readonly photoVersion = signal<number>(Date.now());
  protected readonly photoUrl = computed(
    () => `${environment.apiUrl}/api/identity/photo?t=${this.photoVersion()}`
  );

  protected readonly ownerName = signal('Niral Patel');
  protected readonly ownerTitle = signal('Java Full Stack Developer');

  protected readonly about$ = this.api.getAbout().pipe(
    map((c: AboutContentDto): AboutView => ({
      summary: c.summary,
      educationHtml: this.sanitizer.bypassSecurityTrustHtml(c.educationHtml || ''),
      passions: c.passions
    })),
    catchError(() => of<AboutView | null>(null))
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
