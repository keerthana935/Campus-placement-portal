package com.campus.placement_portal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class JobController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @GetMapping("/test-hash")
    @ResponseBody
    public String testHash() {
        org.springframework.security.crypto.password.PasswordEncoder encoder =
            org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return encoder.encode("admin123");
    }

    @GetMapping("/seed-jobs")
    @ResponseBody
    public String seedJobs() {
        Company company = new Company();
        company.setCompanyName("TechCorp India");
        company.setEmail("hr@techcorp.com");
        company.setPassword("dummy");
        company.setSector("IT");
        company.setLocation("Hyderabad");
        company.setIsApproved(true);
        companyRepository.save(company);

        Job job1 = new Job();
        job1.setCompany(company);
        job1.setTitle("Software Engineer");
        job1.setDescription("Looking for a Java developer skilled in Spring Boot.");
        job1.setLocation("Hyderabad");
        job1.setPackageRange("8-12 LPA");
        job1.setOpenings(5);
        job1.setRequiredSkills("Java, Spring Boot, MySQL");
        job1.setJobType("FULLTIME");
        job1.setDeadline(LocalDate.now().plusDays(30));
        job1.setIsApproved(true);
        jobRepository.save(job1);

        return "<h3>✅ Sample jobs added!</h3>";
    }

    @GetMapping("/jobs")
    public String listJobs(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        List<Job> jobs = jobRepository.findByIsApprovedTrue();
        Student student = studentRepository.findByEmail(userDetails.getUsername()).orElse(null);

        List<Application> myApplications = applicationRepository.findByStudentId(student.getId());

        List<Long> appliedJobIds = myApplications.stream()
                .map(a -> a.getJob().getId())
                .collect(Collectors.toList());

        long shortlistedCount = myApplications.stream()
                .filter(a -> "SHORTLISTED".equals(a.getStatus()))
                .count();

        List<String> skillsList = student.getSkills() != null
                ? Arrays.asList(student.getSkills().split(","))
                : Arrays.asList("No skills added yet");

        List<String> cities = jobs.stream()
                .map(Job::getLocation)
                .distinct()
                .collect(Collectors.toList());

        // ----- Wrap jobs with days-left info, hiding expired jobs -----
        List<JobWithDaysLeft> jobsWithDeadline = jobs.stream()
                .map(job -> {
                    long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), job.getDeadline());
                    return new JobWithDaysLeft(job, daysLeft);
                })
                .filter(j -> j.getDaysLeft() >= 0)
                .collect(Collectors.toList());

        model.addAttribute("student", student);
        model.addAttribute("jobs", jobsWithDeadline);
        model.addAttribute("appliedJobIds", appliedJobIds);
        model.addAttribute("appliedCount", myApplications.size());
        model.addAttribute("shortlistedCount", shortlistedCount);
        model.addAttribute("skillsList", skillsList);
        model.addAttribute("cities", cities);

        return "jobs";
    }

    // Helper class so the template can read job.title AND job.daysLeft
    public static class JobWithDaysLeft {
        private final Job job;
        private final long daysLeft;

        public JobWithDaysLeft(Job job, long daysLeft) {
            this.job = job;
            this.daysLeft = daysLeft;
        }

        public Long getId() { return job.getId(); }
        public String getTitle() { return job.getTitle(); }
        public String getDescription() { return job.getDescription(); }
        public String getLocation() { return job.getLocation(); }
        public String getPackageRange() { return job.getPackageRange(); }
        public String getJobType() { return job.getJobType(); }
        public Integer getOpenings() { return job.getOpenings(); }
        public String getRequiredSkills() { return job.getRequiredSkills(); }
        public Company getCompany() { return job.getCompany(); }
        public long getDaysLeft() { return daysLeft; }
    }

    @GetMapping("/apply/{jobId}")
    @ResponseBody
    public String applyToJob(@PathVariable Long jobId, @AuthenticationPrincipal UserDetails userDetails) {
        Student student = studentRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (applicationRepository.existsByStudentIdAndJobId(student.getId(), jobId)) {
            return "<h3>⚠️ You already applied to this job!</h3><a href='/jobs'>Back</a>";
        }

        Application application = new Application();
        application.setStudent(student);
        application.setJob(job);
        application.setStatus("PENDING");
        application.setAppliedAt(LocalDateTime.now());

        applicationRepository.save(application);

        return "<h3>✅ Successfully applied to " + job.getTitle() + "!</h3>" +
               "<a href='/jobs'>Back to Jobs</a>";
    }

    @GetMapping("/my-applications")
    public String myApplications(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Student student = studentRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Application> applications = applicationRepository.findByStudentId(student.getId());

        model.addAttribute("applications", applications);
        return "my-applications";
    }
}