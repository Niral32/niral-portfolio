import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  AboutContentDto,
  BlogPostDetail,
  BlogPostSummary,
  ContactMessageDetail,
  ExperienceDto,
  ProjectDto,
  SiteSettingsDto,
  SkillCategory,
  SkillDto
} from '../models/portfolio.models';

export interface BlogPostRequest {
  title: string;
  slug?: string;
  excerpt?: string;
  contentMarkdown: string;
  coverUrl?: string;
  published: boolean;
}

export interface SkillRequest {
  name: string;
  category: SkillCategory;
  displayOrder?: number;
}

export interface ProjectRequest {
  title: string;
  description: string;
  techStack: string;
  linkUrl?: string;
  displayOrder?: number;
}

export interface ExperienceRequest {
  roleTitle: string;
  organization: string;
  summary: string;
  startPeriod?: string;
  endPeriod?: string;
  displayOrder?: number;
}

@Injectable({ providedIn: 'root' })
export class AdminService {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiUrl;

  // Messages -----------------------------------------------------------------
  listMessages(): Observable<ContactMessageDetail[]> {
    return this.http.get<ContactMessageDetail[]>(`${this.base}/api/admin/messages`);
  }
  setRead(id: number, read: boolean): Observable<ContactMessageDetail> {
    return this.http.patch<ContactMessageDetail>(`${this.base}/api/admin/messages/${id}`, { read });
  }
  deleteMessage(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/api/admin/messages/${id}`);
  }

  // Skills -------------------------------------------------------------------
  createSkill(req: SkillRequest): Observable<SkillDto> {
    return this.http.post<SkillDto>(`${this.base}/api/admin/skills`, req);
  }
  updateSkill(id: number, req: SkillRequest): Observable<SkillDto> {
    return this.http.put<SkillDto>(`${this.base}/api/admin/skills/${id}`, req);
  }
  deleteSkill(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/api/admin/skills/${id}`);
  }

  // Projects -----------------------------------------------------------------
  createProject(req: ProjectRequest): Observable<ProjectDto> {
    return this.http.post<ProjectDto>(`${this.base}/api/admin/projects`, req);
  }
  updateProject(id: number, req: ProjectRequest): Observable<ProjectDto> {
    return this.http.put<ProjectDto>(`${this.base}/api/admin/projects/${id}`, req);
  }
  deleteProject(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/api/admin/projects/${id}`);
  }

  // Experience ---------------------------------------------------------------
  createExperience(req: ExperienceRequest): Observable<ExperienceDto> {
    return this.http.post<ExperienceDto>(`${this.base}/api/admin/experience`, req);
  }
  updateExperience(id: number, req: ExperienceRequest): Observable<ExperienceDto> {
    return this.http.put<ExperienceDto>(`${this.base}/api/admin/experience/${id}`, req);
  }
  deleteExperience(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/api/admin/experience/${id}`);
  }

  // About --------------------------------------------------------------------
  updateAbout(body: AboutContentDto): Observable<AboutContentDto> {
    return this.http.put<AboutContentDto>(`${this.base}/api/admin/about`, body);
  }

  // Site settings ------------------------------------------------------------
  updateSettings(body: SiteSettingsDto): Observable<SiteSettingsDto> {
    return this.http.put<SiteSettingsDto>(`${this.base}/api/admin/settings`, body);
  }

  // Profile photo ------------------------------------------------------------
  uploadProfilePhoto(file: File): Observable<void> {
    const fd = new FormData();
    fd.append('file', file);
    return this.http.post<void>(`${this.base}/api/admin/identity/photo`, fd);
  }
  deleteProfilePhoto(): Observable<void> {
    return this.http.delete<void>(`${this.base}/api/admin/identity/photo`);
  }

  // Blog ---------------------------------------------------------------------
  listAllBlog(): Observable<BlogPostSummary[]> {
    return this.http.get<BlogPostSummary[]>(`${this.base}/api/admin/blog`);
  }
  getBlog(id: number): Observable<BlogPostDetail> {
    return this.http.get<BlogPostDetail>(`${this.base}/api/admin/blog/${id}`);
  }
  createBlog(req: BlogPostRequest): Observable<BlogPostDetail> {
    return this.http.post<BlogPostDetail>(`${this.base}/api/admin/blog`, req);
  }
  updateBlog(id: number, req: BlogPostRequest): Observable<BlogPostDetail> {
    return this.http.put<BlogPostDetail>(`${this.base}/api/admin/blog/${id}`, req);
  }
  deleteBlog(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/api/admin/blog/${id}`);
  }
  uploadBlogCover(id: number, file: File): Observable<void> {
    const fd = new FormData();
    fd.append('file', file);
    return this.http.post<void>(`${this.base}/api/admin/blog/${id}/cover`, fd);
  }
  deleteBlogCover(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/api/admin/blog/${id}/cover`);
  }

  // Resume -------------------------------------------------------------------
  resumeInfo(): Observable<ResumeInfo> {
    return this.http.get<ResumeInfo>(`${this.base}/api/admin/resume/info`);
  }
  uploadResume(file: File): Observable<ResumeInfo> {
    const fd = new FormData();
    fd.append('file', file);
    return this.http.post<ResumeInfo>(`${this.base}/api/admin/resume`, fd);
  }
  deleteResume(): Observable<void> {
    return this.http.delete<void>(`${this.base}/api/admin/resume`);
  }
}

export interface ResumeInfo {
  present: boolean;
  filename: string | null;
  sizeBytes: number;
  uploadedAt: string | null;
}
