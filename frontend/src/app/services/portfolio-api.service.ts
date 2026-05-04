import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  AboutContentDto,
  BlogPostDetail,
  BlogPostSummary,
  ContactRequest,
  ContactResponse,
  ExperienceDto,
  ProjectDto,
  SiteSettingsDto,
  SkillDto
} from '../models/portfolio.models';

@Injectable({ providedIn: 'root' })
export class PortfolioApiService {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiUrl;

  getProjects(): Observable<ProjectDto[]> {
    return this.http.get<ProjectDto[]>(`${this.base}/api/projects`);
  }

  getSkills(): Observable<SkillDto[]> {
    return this.http.get<SkillDto[]>(`${this.base}/api/skills`);
  }

  getExperience(): Observable<ExperienceDto[]> {
    return this.http.get<ExperienceDto[]>(`${this.base}/api/experience`);
  }

  getAbout(): Observable<AboutContentDto> {
    return this.http.get<AboutContentDto>(`${this.base}/api/about`);
  }

  listBlogPosts(): Observable<BlogPostSummary[]> {
    return this.http.get<BlogPostSummary[]>(`${this.base}/api/blog`);
  }

  getBlogPost(slug: string): Observable<BlogPostDetail> {
    return this.http.get<BlogPostDetail>(`${this.base}/api/blog/${encodeURIComponent(slug)}`);
  }

  likeBlogPost(id: number): Observable<{ likeCount: number }> {
    return this.http.post<{ likeCount: number }>(`${this.base}/api/blog/${id}/like`, {});
  }

  getSettings(): Observable<SiteSettingsDto> {
    return this.http.get<SiteSettingsDto>(`${this.base}/api/settings`);
  }

  submitContact(body: ContactRequest): Observable<ContactResponse> {
    return this.http.post<ContactResponse>(`${this.base}/api/contact`, body);
  }
}
