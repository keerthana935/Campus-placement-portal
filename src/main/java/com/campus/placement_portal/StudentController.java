package com.campus.placement_portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    @ResponseBody
    public String showRegisterForm() {
        return "<h2>Student Registration</h2>" +
               "<form method='POST' action='/register'>" +
               "Name: <input type='text' name='name'/><br><br>" +
               "Email: <input type='email' name='email'/><br><br>" +
               "Password: <input type='password' name='password'/><br><br>" +
               "Branch: <input type='text' name='branch'/><br><br>" +
               "CGPA: <input type='text' name='cgpa'/><br><br>" +
               "<button type='submit'>Register</button>" +
               "</form>";
    }

    @PostMapping("/register")
    @ResponseBody
    public String registerStudent(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String branch,
            @RequestParam Double cgpa) {

        if (studentRepository.existsByEmail(email)) {
            return "<h3>❌ Email already registered!</h3>";
        }

        Student student = new Student();
        student.setName(name);
        student.setEmail(email);
        student.setPassword(passwordEncoder.encode(password));
        student.setBranch(branch);
        student.setCgpa(cgpa);

        studentRepository.save(student);

        return "<h3>✅ Registration successful! Welcome, " + name + "</h3>";
    }

    // ----- My Profile (view) -----
    @GetMapping("/my-profile")
    public String showProfile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Student student = studentRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        model.addAttribute("student", student);
        return "my-profile";
    }

    // ----- My Profile (save changes) -----
    @PostMapping("/my-profile")
    public String updateProfile(
            @RequestParam String name,
            @RequestParam String branch,
            @RequestParam Double cgpa,
            @RequestParam String skills,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {

        Student student = studentRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        student.setName(name);
        student.setBranch(branch);
        student.setCgpa(cgpa);
        student.setSkills(skills);

        studentRepository.save(student);

        model.addAttribute("student", student);
        model.addAttribute("saved", true);
        return "my-profile";
    }

    // ----- Resume Upload -----
    @PostMapping("/upload-resume")
    public String uploadResume(
            @RequestParam("resume") MultipartFile file,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {

        Student student = studentRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        try {
            if (!file.isEmpty()) {
                String uploadDir = "uploads/resumes/";
                java.io.File dir = new java.io.File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                String fileName = "student_" + student.getId() + "_" + file.getOriginalFilename();
                java.nio.file.Path path = java.nio.file.Paths.get(uploadDir + fileName);
                java.nio.file.Files.write(path, file.getBytes());

                student.setResumePath(uploadDir + fileName);
                studentRepository.save(student);
            }
        } catch (Exception e) {
            model.addAttribute("uploadError", e.getMessage());
        }

        model.addAttribute("student", student);
        return "my-profile";
    }
}