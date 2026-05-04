import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { catchError, map, of } from 'rxjs';
import { SkillCategory, SkillDto } from '../../models/portfolio.models';
import { PortfolioApiService } from '../../services/portfolio-api.service';

type GroupedSkills = Record<SkillCategory, SkillDto[]>;

const CATEGORY_ORDER: SkillCategory[] = ['BACKEND', 'FRONTEND', 'DATABASE', 'TOOLS'];

const CATEGORY_LABEL: Record<SkillCategory, string> = {
  BACKEND: 'Backend',
  FRONTEND: 'Frontend',
  DATABASE: 'Database',
  TOOLS: 'Tools'
};

@Component({
  selector: 'app-skills',
  imports: [AsyncPipe],
  templateUrl: './skills.component.html',
  styleUrl: './skills.component.scss'
})
export class SkillsComponent {
  private readonly api = inject(PortfolioApiService);

  protected readonly categoryLabel = CATEGORY_LABEL;
  protected readonly categoryOrder = CATEGORY_ORDER;

  protected readonly groupedSkills$ = this.api.getSkills().pipe(
    map((skills) => this.group(skills)),
    catchError(() => of({} as GroupedSkills))
  );

  private group(skills: SkillDto[]): GroupedSkills {
    const grouped: GroupedSkills = {
      BACKEND: [],
      FRONTEND: [],
      DATABASE: [],
      TOOLS: []
    };
    for (const s of skills) {
      grouped[s.category]?.push(s);
    }
    return grouped;
  }
}
