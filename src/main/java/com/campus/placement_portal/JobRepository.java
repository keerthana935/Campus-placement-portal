package com.campus.placement_portal;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByIsApprovedTrue();

    List<Job> findByCompanyId(Long companyId);
}