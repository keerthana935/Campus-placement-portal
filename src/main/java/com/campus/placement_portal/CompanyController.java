package com.campus.placement_portal;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CompanyController {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    // ----- Company Registration -----
    @GetMapping("/company/register")
    @ResponseBody
    public String showRegisterForm() {
        return "<h2>Company Registration</h2>" +
               "<form method='POST' action='/company/register'>" +
               "Company Name: <input type='text' name='companyName'/><br><br>" +
               "Email: <input type='email' name='email'/><br><br>" +
               "Password: <input type='password' name='password'/><br><br>" +
               "Sector: <input type='text' name='sector'/><br><br>" +
               "Location: <input type='text' name='location'/><br><br>" +
               "<button type='submit'>Register</button>" +
               "</form>";
    }

    @PostMapping("/company/register")
    @ResponseBody
    public String registerCompany(
            @RequestParam String companyName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String sector,
            @RequestParam String location) {

        Company company = new Company();
        company.setCompanyName(companyName);
        company.setEmail(email);
        company.setPassword(passwordEncoder.encode(password));
        company.setSector(sector);
        company.setLocation(location);
        company.setIsApproved(true);

        companyRepository.save(company);

        return "<h3>✅ Company registered successfully! Welcome, " + companyName + "</h3>";
    }

    // ----- Post Job (show form) -----
    @GetMapping("/company/post-job")
    public String showPostJobForm() {
        return "post-job";
    }

    // ----- Post Job (handle submission) -----
    @PostMapping("/company/post-job")
    public String postJob(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String location,
            @RequestParam String packageRange,
            @RequestParam Integer openings,
            @RequestParam String requiredSkills,
            @RequestParam String jobType,
            @RequestParam Integer deadlineDays,
            @AuthenticationPrincipal UserDetails userDetails) {

        Company company = companyRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Job job = new Job();
        job.setCompany(company);
        job.setTitle(title);
        job.setDescription(description);
        job.setLocation(location);
        job.setPackageRange(packageRange);
        job.setOpenings(openings);
        job.setRequiredSkills(requiredSkills);
        job.setJobType(jobType);
        job.setDeadline(LocalDate.now().plusDays(deadlineDays));
        job.setIsApproved(true);

        jobRepository.save(job);

        return "redirect:/company/my-jobs";
    }

    // ----- My Jobs (list) -----
    @GetMapping("/company/my-jobs")
    public String myJobs(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Company company = companyRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        List<Job> jobs = jobRepository.findByCompanyId(company.getId());

        // attach applicant count to each job for the template
        List<JobWithCount> jobsWithCounts = jobs.stream()
                .map(job -> new JobWithCount(job, applicationRepository.findByJobId(job.getId()).size()))
                .toList();

        model.addAttribute("jobs", jobsWithCounts);
        return "company-jobs";
    }

    // Small helper class so the template can read job.title AND job.applicantCount
    public static class JobWithCount {
        private final Job job;
        private final int applicantCount;

        public JobWithCount(Job job, int applicantCount) {
            this.job = job;
            this.applicantCount = applicantCount;
        }

        public Long getId() { return job.getId(); }
        public String getTitle() { return job.getTitle(); }
        public String getLocation() { return job.getLocation(); }
        public String getPackageRange() { return job.getPackageRange(); }
        public String getJobType() { return job.getJobType(); }
        public Integer getOpenings() { return job.getOpenings(); }
        public int getApplicantCount() { return applicantCount; }
    }

    // ----- View Applicants -----
    @GetMapping("/company/job/{jobId}/applicants")
    public String viewApplicants(@PathVariable Long jobId, Model model) {
        List<Application> applications = applicationRepository.findByJobId(jobId);
        model.addAttribute("applications", applications);
        return "applicants";
    }

    // ----- Shortlist -----
    @GetMapping("/company/application/{appId}/shortlist")
    public String shortlistApplication(@PathVariable Long appId) {
        Application app = applicationRepository.findById(appId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        app.setStatus("SHORTLISTED");
        applicationRepository.save(app);
        return "redirect:/company/my-jobs";
    }

    // ----- Reject -----
    @GetMapping("/company/application/{appId}/reject")
    public String rejectApplication(@PathVariable Long appId) {
        Application app = applicationRepository.findById(appId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        app.setStatus("REJECTED");
        applicationRepository.save(app);
        return "redirect:/company/my-jobs";
    }
}