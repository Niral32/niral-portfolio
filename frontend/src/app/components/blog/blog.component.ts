import { AsyncPipe, DatePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { catchError, of } from 'rxjs';
import { BlogPostSummary } from '../../models/portfolio.models';
import { PortfolioApiService } from '../../services/portfolio-api.service';

@Component({
  selector: 'app-blog',
  imports: [AsyncPipe, DatePipe, RouterLink],
  templateUrl: './blog.component.html',
  styleUrl: './blog.component.scss'
})
export class BlogComponent {
  private readonly api = inject(PortfolioApiService);
  protected readonly posts$ = this.api
    .listBlogPosts()
    .pipe(catchError(() => of<BlogPostSummary[]>([])));
}
