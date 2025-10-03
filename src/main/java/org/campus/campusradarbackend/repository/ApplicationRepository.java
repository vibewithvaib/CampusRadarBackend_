package org.campus.campusradarbackend.repository;

import org.campus.campusradarbackend.model.InternshipApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<InternshipApplication,Long> {
    boolean existsByStudentIdAndInternshipId(Long studentId, Long internshipId);
    List<InternshipApplication> findByInternshipId(Long internshipId);
    List<InternshipApplication> findByStudentId(Long studentId);
}
