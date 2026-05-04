import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { NgClass } from '@angular/common';
import { PortfolioApiService } from '../../services/portfolio-api.service';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, RouterLinkActive, NgClass],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent {
  private readonly api = inject(PortfolioApiService);

  protected readonly menuOpen = signal(false);
  protected readonly photoOpen = signal(false);

  // Editable identity fetched from /api/settings. Defaults shown until it loads.
  protected readonly ownerName = signal('Niral Patel');
  protected readonly ownerTitle = signal('Java Full Stack Developer');

  // Photo URL — backend serves it; if 404 we fall back to the static profile.jpg.
  // Cache-busting with a timestamp so a fresh upload shows immediately.
  protected readonly photoLoadedAt = signal<number>(Date.now());
  protected readonly photoUrl = computed(
    () => `${environment.apiUrl}/api/identity/photo?t=${this.photoLoadedAt()}`
  );

  // First name for "Hire <FirstName>" CTA — falls back to the full name.
  protected readonly firstName = computed(() => {
    const parts = this.ownerName().trim().split(/\s+/);
    return parts[0] || this.ownerName();
  });

  constructor() {
    this.api.getSettings().subscribe({
      next: (s) => {
        if (s.ownerName) this.ownerName.set(s.ownerName);
        if (s.ownerTitle) this.ownerTitle.set(s.ownerTitle);
      }
    });
  }

  protected toggleMenu(): void {
    this.menuOpen.update((v) => !v);
  }

  protected closeMenu(): void {
    this.menuOpen.set(false);
  }

  protected openPhoto(event: Event): void {
    // Prevent the surrounding <a routerLink="/"> from navigating when the avatar is clicked.
    event.preventDefault();
    event.stopPropagation();
    this.photoOpen.set(true);
  }

  protected closePhoto(): void {
    this.photoOpen.set(false);
  }

  /** Called by <img onerror> if /api/identity/photo returns 404 — show local fallback. */
  protected onPhotoError(img: HTMLImageElement): void {
    if (!img.src.endsWith('/profile.jpg')) {
      img.src = '/profile.jpg';
    }
  }
}
