import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { catchError, of } from 'rxjs';
import { ProjectDto } from '../../models/portfolio.models';
import { PortfolioApiService } from '../../services/portfolio-api.service';

@Component({
  selector: 'app-projects',
  imports: [AsyncPipe, RouterLink],
  templateUrl: './projects.component.html',
  styleUrl: './projects.component.scss'
})
export class ProjectsComponent {
  private readonly api = inject(PortfolioApiService);

  protected readonly projects$ = this.api.getProjects().pipe(catchError(() => of([] as ProjectDto[])));
}
