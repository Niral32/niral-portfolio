import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { catchError, of } from 'rxjs';
import { ExperienceDto } from '../../models/portfolio.models';
import { PortfolioApiService } from '../../services/portfolio-api.service';

@Component({
  selector: 'app-experience',
  imports: [AsyncPipe],
  templateUrl: './experience.component.html',
  styleUrl: './experience.component.scss'
})
export class ExperienceComponent {
  private readonly api = inject(PortfolioApiService);

  protected readonly experience$ = this.api.getExperience().pipe(catchError(() => of([] as ExperienceDto[])));
}
