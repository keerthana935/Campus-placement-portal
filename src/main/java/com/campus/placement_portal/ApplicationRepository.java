package com.campus.placement_portal;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByStudentId(Long studentId);

    boolean existsByStudentIdAndJobId(Long studentId, Long jobId);

    List<Application> findByJobId(Long jobId);
}