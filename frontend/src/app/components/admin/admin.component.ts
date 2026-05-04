import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { DatePipe, NgClass } from '@angular/common';
import { RouterLink } from '@angular/router';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import {
  AdminService,
  BlogPostRequest,
  ExperienceRequest,
  ProjectRequest,
  ResumeInfo,
  SkillRequest
} from '../../services/admin.service';
import { PortfolioApiService } from '../../services/portfolio-api.service';
import { environment } from '../../../environments/environment';
import { BlogPostSummary, SiteSettingsDto } from '../../models/portfolio.models';
import {
  AboutContentDto,
  ContactMessageDetail,
  ExperienceDto,
  ProjectDto,
  SkillCategory,
  SkillDto
} from '../../models/portfolio.models';

type TabKey = 'messages' | 'skills' | 'projects' | 'experience' | 'blog' | 'about' | 'resume' | 'identity';

@Component({
  selector: 'app-admin',
  imports: [ReactiveFormsModule, DatePipe, NgClass, RouterLink],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.scss'
})
export class AdminComponent implements OnInit {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly auth = inject(AuthService);
  private readonly admin = inject(AdminService);
  private readonly publicApi = inject(PortfolioApiService);

  protected readonly skillCategories: SkillCategory[] = ['BACKEND', 'FRONTEND', 'DATABASE', 'TOOLS'];
  protected readonly resumeUrl = (): string =>
    `${environment.apiUrl}/api/resume?t=${this.resumeInfo().uploadedAt ?? Date.now()}`;

  protected readonly isAuthenticated = this.auth.isAuthenticated;
  protected readonly activeTab = signal<TabKey>('messages');

  // ---- Login -----------------------------------------------------
  protected readonly loginForm = this.fb.group({
    username: this.fb.control('', { validators: [Validators.required] }),
    password: this.fb.control('', { validators: [Validators.required] })
  });
  protected readonly loggingIn = signal(false);
  protected readonly loginError = signal<string | null>(null);

  // ---- Messages --------------------------------------------------
  protected readonly messages = signal<ContactMessageDetail[]>([]);
  protected readonly loadingMessages = signal(false);
  protected readonly messageError = signal<string | null>(null);
  protected readonly expandedMessageId = signal<number | null>(null);
  protected readonly unreadCount = computed(
    () => this.messages().filter((m) => m.readAt === null).length
  );

  // ---- Skills ----------------------------------------------------
  protected readonly skills = signal<SkillDto[]>([]);
  protected readonly skillForm = this.fb.group({
    id: this.fb.control<number | null>(null),
    name: this.fb.control('', { validators: [Validators.required, Validators.maxLength(120)] }),
    category: this.fb.control<SkillCategory>('BACKEND', { validators: [Validators.required] }),
    displayOrder: this.fb.control<number>(0)
  });
  protected readonly skillError = signal<string | null>(null);

  // ---- Projects --------------------------------------------------
  protected readonly projects = signal<ProjectDto[]>([]);
  protected readonly projectForm = this.fb.group({
    id: this.fb.control<number | null>(null),
    title: this.fb.control('', { validators: [Validators.required, Validators.maxLength(200)] }),
    description: this.fb.control('', { validators: [Validators.required, Validators.maxLength(2000)] }),
    techStack: this.fb.control('', { validators: [Validators.required, Validators.maxLength(500)] }),
    linkUrl: this.fb.control(''),
    displayOrder: this.fb.control<number>(0)
  });
  protected readonly projectError = signal<string | null>(null);

  // ---- Experience ------------------------------------------------
  protected readonly experiences = signal<ExperienceDto[]>([]);
  protected readonly experienceForm = this.fb.group({
    id: this.fb.control<number | null>(null),
    roleTitle: this.fb.control('', { validators: [Validators.required, Validators.maxLength(200)] }),
    organization: this.fb.control('', { validators: [Validators.required, Validators.maxLength(200)] }),
    summary: this.fb.control('', { validators: [Validators.required, Validators.maxLength(4000)] }),
    startPeriod: this.fb.control(''),
    endPeriod: this.fb.control(''),
    displayOrder: this.fb.control<number>(0)
  });
  protected readonly experienceError = signal<string | null>(null);

  // ---- About -----------------------------------------------------
  protected readonly aboutForm = this.fb.group({
    summary: this.fb.control('', { validators: [Validators.maxLength(4000)] }),
    educationHtml: this.fb.control('', { validators: [Validators.maxLength(4000)] }),
    passions: this.fb.control('', { validators: [Validators.maxLength(4000)] })
  });
  protected readonly aboutSaved = signal(false);
  protected readonly aboutError = signal<string | null>(null);

  // ---- Site settings ---------------------------------------------
  protected readonly settings = signal<SiteSettingsDto>({
    contactEnabled: true,
    contactDisabledMessage: '',
    ownerName: 'Niral Patel',
    ownerTitle: 'Java Full Stack Developer',
    bookingEnabled: true
  });
  protected readonly settingsSaving = signal(false);

  // ---- Identity (name, title, photo) -----------------------------
  protected readonly identityForm = this.fb.group({
    ownerName: this.fb.control('', { validators: [Validators.required, Validators.maxLength(120)] }),
    ownerTitle: this.fb.control('', { validators: [Validators.required, Validators.maxLength(200)] })
  });
  protected readonly identitySaving = signal(false);
  protected readonly identitySaved = signal(false);
  protected readonly identityError = signal<string | null>(null);

  protected readonly photoUploading = signal(false);
  protected readonly photoMessage = signal<string | null>(null);
  protected readonly photoError = signal<string | null>(null);
  // Cache-busted URL so the preview updates the instant we upload.
  protected readonly photoVersion = signal<number>(Date.now());
  protected readonly identityPhotoUrl = (): string =>
    `${environment.apiUrl}/api/identity/photo?t=${this.photoVersion()}`;

  // ---- Blog ------------------------------------------------------
  protected readonly blogPosts = signal<BlogPostSummary[]>([]);
  protected readonly blogForm = this.fb.group({
    id: this.fb.control<number | null>(null),
    title: this.fb.control('', { validators: [Validators.required, Validators.maxLength(200)] }),
    slug: this.fb.control(''),
    excerpt: this.fb.control('', { validators: [Validators.maxLength(500)] }),
    contentMarkdown: this.fb.control('', { validators: [Validators.required] }),
    coverUrl: this.fb.control(''),
    published: this.fb.control(false)
  });
  protected readonly blogError = signal<string | null>(null);
  protected readonly blogCoverUploading = signal(false);
  protected readonly blogCoverMessage = signal<string | null>(null);
  protected readonly blogCoverHas = signal(false);
  protected readonly blogCoverVersion = signal<number>(Date.now());

  protected readonly blogCoverPreview = (): string | null => {
    const id = this.blogForm.controls.id.value;
    const url = this.blogForm.controls.coverUrl.value;
    if (id && this.blogCoverHas()) {
      // Need slug to fetch — refresh through blogPosts list
      const post = this.blogPosts().find((p) => p.id === id);
      if (post) return `${environment.apiUrl}/api/blog/${post.slug}/cover?t=${this.blogCoverVersion()}`;
    }
    return url || null;
  };

  // ---- Resume ----------------------------------------------------
  protected readonly resumeInfo = signal<ResumeInfo>({
    present: false,
    filename: null,
    sizeBytes: 0,
    uploadedAt: null
  });
  protected readonly uploadingResume = signal(false);
  protected readonly resumeError = signal<string | null>(null);
  protected readonly resumeMessage = signal<string | null>(null);

  ngOnInit(): void {
    if (this.isAuthenticated()) {
      this.loadAll();
    }
  }

  // ============================================================
  // Login / logout
  // ============================================================

  protected submitLogin(): void {
    this.loginError.set(null);
    this.loginForm.markAllAsTouched();
    if (this.loginForm.invalid) return;
    this.loggingIn.set(true);
    this.auth.login(this.loginForm.getRawValue()).subscribe({
      next: () => {
        this.loggingIn.set(false);
        this.loginForm.reset();
        this.loadAll();
      },
      error: (err) => {
        this.loggingIn.set(false);
        this.loginError.set(this.errMsg(err, 'Login failed.'));
      }
    });
  }

  protected logout(): void {
    this.auth.logout();
    this.messages.set([]);
    this.skills.set([]);
    this.projects.set([]);
    this.experiences.set([]);
  }

  protected switchTab(tab: TabKey): void {
    this.activeTab.set(tab);
  }

  // ============================================================
  // Loaders
  // ============================================================

  protected loadAll(): void {
    this.refreshMessages();
    this.refreshSkills();
    this.refreshProjects();
    this.refreshExperiences();
    this.refreshAbout();
    this.refreshResume();
    this.refreshBlog();
    this.refreshSettings();
  }

  protected refreshSettings(): void {
    this.publicApi.getSettings().subscribe({
      next: (s) => {
        this.settings.set(s);
        this.identityForm.patchValue({
          ownerName: s.ownerName,
          ownerTitle: s.ownerTitle
        });
      }
    });
  }

  protected saveIdentity(): void {
    this.identityError.set(null);
    this.identitySaved.set(false);
    this.identityForm.markAllAsTouched();
    if (this.identityForm.invalid) return;
    this.identitySaving.set(true);
    const v = this.identityForm.getRawValue();
    const next: SiteSettingsDto = {
      ...this.settings(),
      ownerName: v.ownerName,
      ownerTitle: v.ownerTitle
    };
    this.admin.updateSettings(next).subscribe({
      next: (s) => {
        this.settings.set(s);
        this.identitySaving.set(false);
        this.identitySaved.set(true);
      },
      error: (err) => {
        this.identitySaving.set(false);
        this.identityError.set(this.errMsg(err, 'Could not save identity.'));
      }
    });
  }

  protected uploadPhoto(evt: Event): void {
    const input = evt.target as HTMLInputElement;
    const file = input.files?.[0];
    input.value = '';
    if (!file) return;
    const lower = file.name.toLowerCase();
    if (!lower.endsWith('.jpg') && !lower.endsWith('.jpeg') && !lower.endsWith('.png') && !lower.endsWith('.webp')) {
      this.photoError.set('Please choose a JPG, PNG, or WebP image.');
      return;
    }
    this.photoError.set(null);
    this.photoMessage.set(null);
    this.photoUploading.set(true);
    this.admin.uploadProfilePhoto(file).subscribe({
      next: () => {
        this.photoUploading.set(false);
        this.photoMessage.set('Photo updated. Refresh the page to see it in the navbar.');
        this.photoVersion.set(Date.now());
      },
      error: (err) => {
        this.photoUploading.set(false);
        this.photoError.set(this.errMsg(err, 'Upload failed.'));
      }
    });
  }

  protected deletePhoto(): void {
    if (!confirm('Reset profile photo to the default?')) return;
    this.photoError.set(null);
    this.admin.deleteProfilePhoto().subscribe({
      next: () => {
        this.photoMessage.set('Photo reset. Refresh the page to see the default in the navbar.');
        this.photoVersion.set(Date.now());
      },
      error: (err) => this.photoError.set(this.errMsg(err, 'Delete failed.'))
    });
  }

  protected toggleContact(): void {
    this.toggleSetting('contactEnabled');
  }

  protected toggleBooking(): void {
    this.toggleSetting('bookingEnabled');
  }

  private toggleSetting(key: 'contactEnabled' | 'bookingEnabled'): void {
    this.settingsSaving.set(true);
    const current = this.settings();
    const next: SiteSettingsDto = { ...current, [key]: !current[key] };
    this.admin.updateSettings(next).subscribe({
      next: (s) => {
        this.settings.set(s);
        this.settingsSaving.set(false);
      },
      error: () => this.settingsSaving.set(false)
    });
  }

  protected refreshBlog(): void {
    this.admin.listAllBlog().subscribe({
      next: (list) => this.blogPosts.set(list)
    });
  }

  protected refreshResume(): void {
    this.admin.resumeInfo().subscribe({
      next: (info) => this.resumeInfo.set(info),
      error: () => {
        /* ignored — admin auth issues handled by message refresh */
      }
    });
  }

  protected refreshMessages(): void {
    this.loadingMessages.set(true);
    this.messageError.set(null);
    this.admin.listMessages().subscribe({
      next: (list) => {
        this.loadingMessages.set(false);
        this.messages.set(list);
      },
      error: (err) => {
        this.loadingMessages.set(false);
        if (err instanceof HttpErrorResponse && (err.status === 401 || err.status === 403)) {
          this.auth.logout();
        }
        this.messageError.set(this.errMsg(err, 'Could not load messages.'));
      }
    });
  }

  protected refreshSkills(): void {
    this.publicApi.getSkills().subscribe({
      next: (list) => this.skills.set(list)
    });
  }

  protected refreshProjects(): void {
    this.publicApi.getProjects().subscribe({
      next: (list) => this.projects.set(list)
    });
  }

  protected refreshExperiences(): void {
    this.publicApi.getExperience().subscribe({
      next: (list) => this.experiences.set(list)
    });
  }

  protected refreshAbout(): void {
    this.publicApi.getAbout().subscribe({
      next: (a) => this.aboutForm.patchValue(a)
    });
  }

  // ============================================================
  // Messages
  // ============================================================

  protected toggleMessage(id: number): void {
    this.expandedMessageId.set(this.expandedMessageId() === id ? null : id);
    const msg = this.messages().find((m) => m.id === id);
    if (msg && msg.readAt === null) this.setRead(msg, true);
  }

  protected setRead(m: ContactMessageDetail, read: boolean, evt?: Event): void {
    evt?.stopPropagation();
    this.admin.setRead(m.id, read).subscribe({
      next: (u) => this.messages.update((l) => l.map((x) => (x.id === u.id ? u : x)))
    });
  }

  protected deleteMessage(m: ContactMessageDetail, evt: Event): void {
    evt.stopPropagation();
    if (!confirm(`Delete message from ${m.name}?`)) return;
    this.admin.deleteMessage(m.id).subscribe({
      next: () => this.messages.update((l) => l.filter((x) => x.id !== m.id))
    });
  }

  // ============================================================
  // Skills
  // ============================================================

  protected resetSkillForm(): void {
    this.skillForm.reset({ id: null, name: '', category: 'BACKEND', displayOrder: 0 });
    this.skillError.set(null);
  }

  protected editSkill(s: SkillDto): void {
    this.skillForm.patchValue(s);
    this.skillError.set(null);
  }

  protected saveSkill(): void {
    this.skillError.set(null);
    this.skillForm.markAllAsTouched();
    if (this.skillForm.invalid) return;
    const v = this.skillForm.getRawValue();
    const req: SkillRequest = {
      name: v.name,
      category: v.category,
      displayOrder: v.displayOrder ?? 0
    };
    const obs = v.id ? this.admin.updateSkill(v.id, req) : this.admin.createSkill(req);
    obs.subscribe({
      next: () => {
        this.refreshSkills();
        this.resetSkillForm();
      },
      error: (err) => this.skillError.set(this.errMsg(err, 'Could not save skill.'))
    });
  }

  protected deleteSkill(s: SkillDto): void {
    if (!confirm(`Delete skill "${s.name}"?`)) return;
    this.admin.deleteSkill(s.id).subscribe({
      next: () => this.skills.update((l) => l.filter((x) => x.id !== s.id))
    });
  }

  // ============================================================
  // Projects
  // ============================================================

  protected resetProjectForm(): void {
    this.projectForm.reset({ id: null, title: '', description: '', techStack: '', linkUrl: '', displayOrder: 0 });
    this.projectError.set(null);
  }

  protected editProject(p: ProjectDto): void {
    this.projectForm.patchValue({
      id: p.id,
      title: p.title,
      description: p.description,
      techStack: p.techStack,
      linkUrl: p.linkUrl ?? '',
      displayOrder: p.displayOrder
    });
    this.projectError.set(null);
  }

  protected saveProject(): void {
    this.projectError.set(null);
    this.projectForm.markAllAsTouched();
    if (this.projectForm.invalid) return;
    const v = this.projectForm.getRawValue();
    const req: ProjectRequest = {
      title: v.title,
      description: v.description,
      techStack: v.techStack,
      linkUrl: v.linkUrl || undefined,
      displayOrder: v.displayOrder ?? 0
    };
    const obs = v.id ? this.admin.updateProject(v.id, req) : this.admin.createProject(req);
    obs.subscribe({
      next: () => {
        this.refreshProjects();
        this.resetProjectForm();
      },
      error: (err) => this.projectError.set(this.errMsg(err, 'Could not save project.'))
    });
  }

  protected deleteProject(p: ProjectDto): void {
    if (!confirm(`Delete project "${p.title}"?`)) return;
    this.admin.deleteProject(p.id).subscribe({
      next: () => this.projects.update((l) => l.filter((x) => x.id !== p.id))
    });
  }

  // ============================================================
  // Experience
  // ============================================================

  protected resetExperienceForm(): void {
    this.experienceForm.reset({
      id: null,
      roleTitle: '',
      organization: '',
      summary: '',
      startPeriod: '',
      endPeriod: '',
      displayOrder: 0
    });
    this.experienceError.set(null);
  }

  protected editExperience(e: ExperienceDto): void {
    this.experienceForm.patchValue({
      id: e.id,
      roleTitle: e.roleTitle,
      organization: e.organization,
      summary: e.summary,
      startPeriod: e.startPeriod ?? '',
      endPeriod: e.endPeriod ?? '',
      displayOrder: e.displayOrder
    });
    this.experienceError.set(null);
  }

  protected saveExperience(): void {
    this.experienceError.set(null);
    this.experienceForm.markAllAsTouched();
    if (this.experienceForm.invalid) return;
    const v = this.experienceForm.getRawValue();
    const req: ExperienceRequest = {
      roleTitle: v.roleTitle,
      organization: v.organization,
      summary: v.summary,
      startPeriod: v.startPeriod || undefined,
      endPeriod: v.endPeriod || undefined,
      displayOrder: v.displayOrder ?? 0
    };
    const obs = v.id ? this.admin.updateExperience(v.id, req) : this.admin.createExperience(req);
    obs.subscribe({
      next: () => {
        this.refreshExperiences();
        this.resetExperienceForm();
      },
      error: (err) => this.experienceError.set(this.errMsg(err, 'Could not save experience.'))
    });
  }

  protected deleteExperience(e: ExperienceDto): void {
    if (!confirm(`Delete experience "${e.roleTitle} – ${e.organization}"?`)) return;
    this.admin.deleteExperience(e.id).subscribe({
      next: () => this.experiences.update((l) => l.filter((x) => x.id !== e.id))
    });
  }

  // ============================================================
  // About
  // ============================================================

  protected saveAbout(): void {
    this.aboutSaved.set(false);
    this.aboutError.set(null);
    const v = this.aboutForm.getRawValue();
    const body: AboutContentDto = {
      summary: v.summary || '',
      educationHtml: v.educationHtml || '',
      passions: v.passions || ''
    };
    this.admin.updateAbout(body).subscribe({
      next: () => this.aboutSaved.set(true),
      error: (err) => this.aboutError.set(this.errMsg(err, 'Could not save About content.'))
    });
  }

  // ============================================================
  // Blog
  // ============================================================

  protected resetBlogForm(): void {
    this.blogForm.reset({
      id: null,
      title: '',
      slug: '',
      excerpt: '',
      contentMarkdown: '',
      coverUrl: '',
      published: false
    });
    this.blogError.set(null);
    this.blogCoverHas.set(false);
    this.blogCoverMessage.set(null);
  }

  protected editBlog(b: BlogPostSummary): void {
    this.blogCoverMessage.set(null);
    // Need full content — fetch the detail.
    this.admin.getBlog(b.id).subscribe({
      next: (full) => {
        this.blogForm.patchValue({
          id: full.id,
          title: full.title,
          slug: full.slug,
          excerpt: full.excerpt ?? '',
          contentMarkdown: full.contentMarkdown,
          coverUrl: full.coverUrl ?? '',
          published: full.published
        });
        this.blogCoverHas.set(full.hasCoverImage);
        this.blogCoverVersion.set(Date.now());
      }
    });
    this.blogError.set(null);
  }

  protected uploadBlogCover(evt: Event): void {
    const input = evt.target as HTMLInputElement;
    const file = input.files?.[0];
    input.value = '';
    if (!file) return;
    const id = this.blogForm.controls.id.value;
    if (!id) {
      this.blogError.set('Save the post first, then upload an image.');
      return;
    }
    const lower = file.name.toLowerCase();
    if (!/\.(jpe?g|png|webp)$/.test(lower)) {
      this.blogError.set('Please choose a JPG, PNG, or WebP image.');
      return;
    }
    this.blogCoverUploading.set(true);
    this.blogError.set(null);
    this.admin.uploadBlogCover(id, file).subscribe({
      next: () => {
        this.blogCoverUploading.set(false);
        this.blogCoverHas.set(true);
        this.blogCoverVersion.set(Date.now());
        this.blogCoverMessage.set('Image uploaded.');
        this.refreshBlog();
      },
      error: (err) => {
        this.blogCoverUploading.set(false);
        this.blogError.set(this.errMsg(err, 'Upload failed.'));
      }
    });
  }

  protected clearBlogCover(): void {
    const id = this.blogForm.controls.id.value;
    if (!id) return;
    if (!confirm('Remove the uploaded cover image?')) return;
    this.admin.deleteBlogCover(id).subscribe({
      next: () => {
        this.blogCoverHas.set(false);
        this.blogCoverMessage.set('Image removed.');
        this.refreshBlog();
      }
    });
  }

  protected saveBlog(): void {
    this.blogError.set(null);
    this.blogForm.markAllAsTouched();
    if (this.blogForm.invalid) return;
    const v = this.blogForm.getRawValue();
    const req: BlogPostRequest = {
      title: v.title,
      slug: v.slug || undefined,
      excerpt: v.excerpt || undefined,
      contentMarkdown: v.contentMarkdown,
      coverUrl: v.coverUrl || undefined,
      published: !!v.published
    };
    const obs = v.id ? this.admin.updateBlog(v.id, req) : this.admin.createBlog(req);
    obs.subscribe({
      next: () => {
        this.refreshBlog();
        this.resetBlogForm();
      },
      error: (err) => this.blogError.set(this.errMsg(err, 'Could not save post.'))
    });
  }

  protected deleteBlog(b: BlogPostSummary): void {
    if (!confirm(`Delete post "${b.title}"? This cannot be undone.`)) return;
    this.admin.deleteBlog(b.id).subscribe({
      next: () => this.blogPosts.update((l) => l.filter((x) => x.id !== b.id))
    });
  }

  // ============================================================
  // Resume
  // ============================================================

  protected uploadResume(evt: Event): void {
    const input = evt.target as HTMLInputElement;
    const file = input.files?.[0];
    input.value = ''; // allow re-upload of same file later
    if (!file) return;
    const lower = file.name.toLowerCase();
    if (!lower.endsWith('.pdf') && !lower.endsWith('.doc') && !lower.endsWith('.docx')) {
      this.resumeError.set('Please choose a PDF, DOC, or DOCX file.');
      return;
    }
    if (file.size > 10 * 1024 * 1024) {
      this.resumeError.set('File is larger than 10 MB.');
      return;
    }
    this.resumeError.set(null);
    this.resumeMessage.set(null);
    this.uploadingResume.set(true);
    this.admin.uploadResume(file).subscribe({
      next: (info) => {
        this.uploadingResume.set(false);
        this.resumeInfo.set(info);
        this.resumeMessage.set(`Uploaded ${info.filename} (${this.fmtSize(info.sizeBytes)}).`);
      },
      error: (err) => {
        this.uploadingResume.set(false);
        this.resumeError.set(this.errMsg(err, 'Upload failed.'));
      }
    });
  }

  protected deleteResume(): void {
    if (!confirm('Delete the current resume? Visitors will see "Resume coming soon" until you upload a new one.')) return;
    this.resumeError.set(null);
    this.admin.deleteResume().subscribe({
      next: () => {
        this.resumeInfo.set({ present: false, filename: null, sizeBytes: 0, uploadedAt: null });
        this.resumeMessage.set('Resume deleted.');
      },
      error: (err) => this.resumeError.set(this.errMsg(err, 'Delete failed.'))
    });
  }

  protected fmtSize(bytes: number): string {
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / 1024 / 1024).toFixed(2)} MB`;
  }

  // ============================================================
  // Helpers
  // ============================================================

  private errMsg(err: unknown, fallback: string): string {
    if (err instanceof HttpErrorResponse) {
      const body = err.error as { message?: string; details?: string[] } | null;
      if (body?.details?.length) return body.details.join(' ');
      const serverMsg = body?.message ?? '';
      const status = err.status === 0 ? 'no response' : `HTTP ${err.status}`;
      const detail = serverMsg || err.statusText || err.message;
      return `${fallback} (${status}${detail ? `: ${detail}` : ''})`;
    }
    return fallback;
  }
}
