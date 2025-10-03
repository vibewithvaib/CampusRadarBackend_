package org.campus.campusradarbackend.repository;

import org.campus.campusradarbackend.model.InternshipApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<InternshipApplication,Long> {
    boolean existsByStudentIdAndInternshipId(Long studentId, Long internshipId);
}
