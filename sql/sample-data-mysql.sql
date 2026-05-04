-- Sample portfolio data (MySQL)
-- Run after schema-mysql.sql
--
-- Sourced strictly from Niral Patel's resume (Niral_Patel_Java_Developer.pdf):
-- Skills, Professional Experience, and supporting summary points only.
-- Resume contains no Projects section, so the projects table is left empty —
-- add real entries below when you have GitHub repos or work samples to feature.

-- IMPORTANT: contact_messages is intentionally NOT truncated so reloading
-- the seed never wipes real visitor messages.
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE work_experience;
TRUNCATE TABLE skills;
TRUNCATE TABLE projects;
SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------------
-- Projects
-- ---------------------------------------------------------------------------
-- Resume contains no projects section. Add your real projects here, e.g.:
--
-- INSERT INTO projects (title, description, tech_stack, display_order) VALUES
-- (
--     'Project name',
--     'Short description of what it does and the impact it had.',
--     'Java 17, Spring Boot, MySQL, Angular, Docker',
--     1
-- );
--
-- (Intentionally empty until real project data is provided.)

-- ---------------------------------------------------------------------------
-- Skills (categorized into BACKEND / FRONTEND / DATABASE / TOOLS)
-- ---------------------------------------------------------------------------
INSERT INTO skills (name, category, display_order) VALUES
-- Backend / programming languages / frameworks / testing
('Java', 'BACKEND', 1),
('Spring Boot', 'BACKEND', 2),
('Spring MVC', 'BACKEND', 3),
('Spring Core', 'BACKEND', 4),
('Spring Security', 'BACKEND', 5),
('Spring Data JPA', 'BACKEND', 6),
('Spring Framework', 'BACKEND', 7),
('Hibernate', 'BACKEND', 8),
('JPA', 'BACKEND', 9),
('JDBC', 'BACKEND', 10),
('RESTful APIs', 'BACKEND', 11),
('Microservices', 'BACKEND', 12),
('Apache Kafka', 'BACKEND', 13),
('JWT', 'BACKEND', 14),
('OAuth 2.0', 'BACKEND', 15),
('JUnit', 'BACKEND', 16),
('Mockito', 'BACKEND', 17),
('Selenium', 'BACKEND', 18),
('Swagger', 'BACKEND', 19),
-- Frontend
('Angular', 'FRONTEND', 1),
('ReactJS', 'FRONTEND', 2),
('TypeScript', 'FRONTEND', 3),
('JavaScript (ES6+)', 'FRONTEND', 4),
('HTML5', 'FRONTEND', 5),
('CSS3', 'FRONTEND', 6),
('Bootstrap', 'FRONTEND', 7),
-- Databases
('MySQL', 'DATABASE', 1),
('PostgreSQL', 'DATABASE', 2),
('Oracle', 'DATABASE', 3),
('MongoDB', 'DATABASE', 4),
('SQL', 'DATABASE', 5),
-- DevOps, Cloud, Tools
('Docker', 'TOOLS', 1),
('Kubernetes', 'TOOLS', 2),
('AWS (EC2, S3, Lambda)', 'TOOLS', 3),
('Microsoft Azure', 'TOOLS', 4),
('Jenkins', 'TOOLS', 5),
('GitHub Actions', 'TOOLS', 6),
('CI/CD Pipelines', 'TOOLS', 7),
('Git', 'TOOLS', 8),
('GitHub', 'TOOLS', 9),
('Maven', 'TOOLS', 10),
('Gradle', 'TOOLS', 11),
('JIRA', 'TOOLS', 12),
('Postman', 'TOOLS', 13),
('Eclipse', 'TOOLS', 14),
('VS Code', 'TOOLS', 15),
('Log4J', 'TOOLS', 16),
('OpenShift', 'TOOLS', 17);

-- ---------------------------------------------------------------------------
-- Professional Experience (from resume)
-- ---------------------------------------------------------------------------
INSERT INTO work_experience (role_title, organization, summary, start_period, end_period, display_order) VALUES
(
    'Full Stack Java Developer',
    'South Medic Incorporated',
    'Designed, developed, and deployed scalable enterprise applications using Java, Spring Boot, and Microservices architecture, integrating RESTful APIs and relational databases per detailed design specifications. Built responsive front-end components with React.js, HTML5, CSS3, and JavaScript, improving user experience and page-load performance. Used Hibernate and JPA for ORM with optimized SQL queries to improve database and application efficiency. Implemented secure authentication and authorization using JWT and OAuth 2.0. Deployed and managed applications on AWS (EC2, S3, RDS) for scalable, reliable hosting. Automated build, test, and deployment via CI/CD pipelines using Jenkins, Git, and Docker. Integrated Java services with cloud AI/ML platforms such as AWS SageMaker for model training, deployment, and data processing. Participated in the full SDLC — requirement analysis, design, development, testing, deployment, and production support — within an Agile/Scrum environment.',
    '2024',
    'Present',
    1
),
(
    'Software Developer',
    'In N Out',
    'Designed and built backend web services using Spring Boot, Hibernate, and MySQL. Developed dynamic web applications with JavaScript, Angular, and Bootstrap for a seamless user experience. Wrote unit tests with JUnit and Mockito, improving code reliability by 95%. Managed application deployment using Docker, reducing deployment time by 40%. Implemented application security using the Spring Security framework. Participated in client requirement gathering and applied Object-Oriented Analysis and Design patterns. Resolved technical issues through cross-team collaboration to improve overall development efficiency.',
    '2022',
    '2023',
    2
),
(
    'Software Developer Intern',
    'Venom Technologies',
    'Collaborated with the team in an Agile/SCRUM environment to deliver tasks within project deadlines. Contributed to designing, developing, and testing Java applications under the guidance of senior developers. Applied hands-on experience in object-oriented programming, data structures, and core Java. Used Git/GitHub for collaborative version control. Assisted senior developers in deploying applications to cloud and local servers.',
    '2021',
    '2022',
    3
);
