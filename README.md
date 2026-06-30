# 🎓 CampusHire — Campus Recruitment & Placement Portal

A full-stack Java web application for managing campus placements, built with **Spring Boot**, **Spring Security**, **PostgreSQL/MySQL**, and **Thymeleaf**.

🔗 **Live Demo:** [campus-placement-portal-tnds.onrender.com](https://campus-placement-portal-tnds.onrender.com)
> Note: hosted on Render's free tier — the app may take ~50 seconds to wake up on first load after inactivity.

## Features

### 🎓 Student Module
- Register / Login (encrypted passwords via Spring Security)
- Browse and search/filter available jobs by type, city, and keyword
- Apply to jobs and track application status
- Edit profile, manage skills, upload resume (PDF)
- Deadline countdown badges on job listings

### 🏢 Company Module
- Register / Login
- Post new job openings
- View applicants per job
- Shortlist or reject candidates

### ⚙️ Admin Module
- Dashboard with live stats (students, companies, jobs, applications)
- Interactive charts (Chart.js) — applications by status, students by branch
- View all registered students and companies

## Tech Stack

- **Backend:** Java 17, Spring Boot, Spring Data JPA, Spring Security
- **Frontend:** Thymeleaf, HTML/CSS, JavaScript (live search/filter, Chart.js)
- **Database:** MySQL (local development), PostgreSQL (production/Render)
- **Build Tool:** Maven
- **Deployment:** Docker + Render

## Running Locally

1. Clone the repo
2. Set up a local MySQL database named `campus_db`
3. Run `mvn spring-boot:run`
4. Visit `http://localhost:8080`

## Author

Built by Keerthana as a hands-on learning project covering full-stack Java development, authentication, database design, and cloud deployment.