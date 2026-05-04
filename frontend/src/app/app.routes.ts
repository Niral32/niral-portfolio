import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', loadComponent: () => import('./components/home/home.component').then((m) => m.HomeComponent) },
  { path: 'about', loadComponent: () => import('./components/about/about.component').then((m) => m.AboutComponent) },
  { path: 'skills', loadComponent: () => import('./components/skills/skills.component').then((m) => m.SkillsComponent) },
  {
    path: 'projects',
    loadComponent: () => import('./components/projects/projects.component').then((m) => m.ProjectsComponent)
  },
  {
    path: 'experience',
    loadComponent: () => import('./components/experience/experience.component').then((m) => m.ExperienceComponent)
  },
  { path: 'resume', loadComponent: () => import('./components/resume/resume.component').then((m) => m.ResumeComponent) },
  {
    path: 'contact',
    loadComponent: () => import('./components/contact/contact.component').then((m) => m.ContactComponent)
  },
  {
    path: 'blog',
    loadComponent: () => import('./components/blog/blog.component').then((m) => m.BlogComponent)
  },
  {
    path: 'blog/:slug',
    loadComponent: () => import('./components/blog-detail/blog-detail.component').then((m) => m.BlogDetailComponent)
  },
  {
    path: 'admin',
    loadComponent: () => import('./components/admin/admin.component').then((m) => m.AdminComponent)
  },
  { path: '**', redirectTo: '' }
];
