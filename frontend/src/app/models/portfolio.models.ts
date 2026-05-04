export type SkillCategory = 'BACKEND' | 'FRONTEND' | 'DATABASE' | 'TOOLS';

export interface ProjectDto {
  id: number;
  title: string;
  description: string;
  techStack: string;
  linkUrl: string | null;
  displayOrder: number;
}

export interface SkillDto {
  id: number;
  name: string;
  category: SkillCategory;
  displayOrder: number;
}

export interface ExperienceDto {
  id: number;
  roleTitle: string;
  organization: string;
  summary: string;
  startPeriod: string | null;
  endPeriod: string | null;
  displayOrder: number;
}

export interface SiteSettingsDto {
  contactEnabled: boolean;
  contactDisabledMessage: string;
  ownerName: string;
  ownerTitle: string;
  bookingEnabled: boolean;
}

export interface AboutContentDto {
  summary: string;
  educationHtml: string;
  passions: string;
}

export interface BlogPostSummary {
  id: number;
  title: string;
  slug: string;
  excerpt: string;
  coverUrl: string | null;
  hasCoverImage: boolean;
  publishedAt: string | null;
  likeCount: number;
}

export interface BlogPostDetail extends BlogPostSummary {
  contentMarkdown: string;
  published: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface ContactRequest {
  name: string;
  email: string;
  subject: string;
  message: string;
}

export interface ContactResponse {
  id: number;
  name: string;
  email: string;
  subject: string;
  createdAt: string;
}

export interface ContactMessageDetail {
  id: number;
  name: string;
  email: string;
  subject: string;
  message: string;
  createdAt: string;
  readAt: string | null;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  type: string;
  expiresInMs: number;
}
