package com.campus.placement_portal;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CompanyRepository companyRepository;

    // Pre-encoded password for "admin123" using bcrypt
    private static final String ADMIN_ENCODED_PASSWORD = "{bcrypt}$2a$10$ioNgSQOEi89fWWDxvTxOje6Pw4Zo8K5vEXaEuaviwdLhgZXWAKY6y";
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // Check hardcoded admin first
        if (email.equals("admin@campus.com")) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(() -> "ROLE_ADMIN");
            return new User("admin@campus.com", ADMIN_ENCODED_PASSWORD, authorities);
        }

        // Check if it's a student
        var studentOpt = studentRepository.findByEmail(email);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(() -> "ROLE_STUDENT");
            return new User(student.getEmail(), student.getPassword(), authorities);
        }

        // Check if it's a company
        var companyOpt = companyRepository.findByEmail(email);
        if (companyOpt.isPresent()) {
            Company company = companyOpt.get();
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(() -> "ROLE_COMPANY");
            return new User(company.getEmail(), company.getPassword(), authorities);
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}