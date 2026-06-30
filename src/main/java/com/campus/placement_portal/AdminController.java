package com.campus.placement_portal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalStudents", studentRepository.count());
        model.addAttribute("totalCompanies", companyRepository.count());
        model.addAttribute("totalJobs", jobRepository.count());
        model.addAttribute("totalApplications", applicationRepository.count());

        // ----- Applications by Status -----
        List<Application> allApplications = applicationRepository.findAll();
        Map<String, Long> statusCountMap = allApplications.stream()
                .collect(Collectors.groupingBy(Application::getStatus, Collectors.counting()));

        List<String> statusLabels = new ArrayList<>(statusCountMap.keySet());
        List<Long> statusCounts = statusLabels.stream()
                .map(statusCountMap::get)
                .collect(Collectors.toList());

        // ----- Students by Branch -----
        List<Student> allStudents = studentRepository.findAll();
        Map<String, Long> branchCountMap = allStudents.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getBranch() != null ? s.getBranch() : "Unknown",
                        Collectors.counting()));

        List<String> branchLabels = new ArrayList<>(branchCountMap.keySet());
        List<Long> branchCounts = branchLabels.stream()
                .map(branchCountMap::get)
                .collect(Collectors.toList());

        model.addAttribute("statusLabels", statusLabels);
        model.addAttribute("statusCounts", statusCounts);
        model.addAttribute("branchLabels", branchLabels);
        model.addAttribute("branchCounts", branchCounts);

        return "admin-dashboard";
    }

    @GetMapping("/admin/students")
    public String allStudents(Model model) {
        List<Student> students = studentRepository.findAll();
        model.addAttribute("students", students);
        return "admin-students";
    }

    @GetMapping("/admin/companies")
    public String allCompanies(Model model) {
        List<Company> companies = companyRepository.findAll();
        model.addAttribute("companies", companies);
        return "admin-companies";
    }
}