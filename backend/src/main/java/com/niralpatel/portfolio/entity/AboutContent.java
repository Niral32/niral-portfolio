package com.niralpatel.portfolio.entity;

import jakarta.persistence.*;

/**
 * Single-row entity storing editable text for the About page.
 * The admin UI lets Niral update headline + body fields, and the public
 * About page renders whatever is in this row. We force {@code id = 1} so
 * there's only ever one row.
 */
@Entity
@Table(name = "about_content")
public class AboutContent {

    @Id
    private Long id = 1L;

    @Column(nullable = false, length = 4000)
    private String summary = "";

    @Column(name = "education_html", length = 4000)
    private String educationHtml = "";

    @Column(name = "passions", length = 4000)
    private String passions = "";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getEducationHtml() {
        return educationHtml;
    }

    public void setEducationHtml(String educationHtml) {
        this.educationHtml = educationHtml;
    }

    public String getPassions() {
        return passions;
    }

    public void setPassions(String passions) {
        this.passions = passions;
    }
}
