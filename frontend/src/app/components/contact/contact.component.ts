import { Component, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { PortfolioApiService } from '../../services/portfolio-api.service';
import { SiteSettingsDto } from '../../models/portfolio.models';

@Component({
  selector: 'app-contact',
  imports: [ReactiveFormsModule],
  templateUrl: './contact.component.html',
  styleUrl: './contact.component.scss'
})
export class ContactComponent {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly api = inject(PortfolioApiService);

  protected readonly submitting = signal(false);
  protected readonly success = signal<string | null>(null);
  protected readonly error = signal<string | null>(null);

  // Default to "enabled" so the form shows immediately; if the settings call
  // returns disabled we hide it.
  protected readonly settings = signal<SiteSettingsDto>({
    contactEnabled: true,
    contactDisabledMessage: '',
    ownerName: '',
    ownerTitle: '',
    bookingEnabled: true
  });

  constructor() {
    this.api.getSettings().subscribe({
      next: (s) => this.settings.set(s)
    });
  }

  protected readonly form = this.fb.group({
    name: this.fb.control('', { validators: [Validators.required, Validators.maxLength(120)] }),
    email: this.fb.control('', { validators: [Validators.required, Validators.email, Validators.maxLength(254)] }),
    subject: this.fb.control('', { validators: [Validators.required, Validators.maxLength(200)] }),
    message: this.fb.control('', { validators: [Validators.required, Validators.maxLength(4000)] })
  });

  protected submit(): void {
    this.success.set(null);
    this.error.set(null);
    this.form.markAllAsTouched();
    if (this.form.invalid) {
      return;
    }

    this.submitting.set(true);
    this.api.submitContact(this.form.getRawValue()).subscribe({
      next: () => {
        this.submitting.set(false);
        this.success.set('Thanks — your message was sent. I will get back to you soon.');
        this.form.reset();
      },
      error: (err: unknown) => {
        this.submitting.set(false);
        this.error.set(this.formatError(err));
      }
    });
  }

  private formatError(err: unknown): string {
    if (err instanceof HttpErrorResponse) {
      const body = err.error as { message?: string; details?: string[] } | null;
      if (body?.details?.length) {
        return body.details.join(' ');
      }
      if (body?.message) {
        return body.message;
      }
      return err.message || 'Request failed. Please try again.';
    }
    return 'Request failed. Please try again.';
  }
}
