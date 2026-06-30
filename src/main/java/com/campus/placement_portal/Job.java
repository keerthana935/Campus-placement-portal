package com.campus.placement_portal;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    private String title;
    private String description;
    private String location;
    private String packageRange;
    private Integer openings;
    private String requiredSkills;
    private String jobType; // "FULLTIME" or "INTERNSHIP"
    private LocalDate deadline;
    private Boolean isApproved = false;

    // ---- Getters and Setters ----

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }
    public void setCompany(Company company) {
        this.company = company;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public String getPackageRange() {
        return packageRange;
    }
    public void setPackageRange(String packageRange) {
        this.packageRange = packageRange;
    }

    public Integer getOpenings() {
        return openings;
    }
    public void setOpenings(Integer openings) {
        this.openings = openings;
    }

    public String getRequiredSkills() {
        return requiredSkills;
    }
    public void setRequiredSkills(String requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public String getJobType() {
        return jobType;
    }
    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public LocalDate getDeadline() {
        return deadline;
    }
    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public Boolean getIsApproved() {
        return isApproved;
    }
    public void setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
    }
}