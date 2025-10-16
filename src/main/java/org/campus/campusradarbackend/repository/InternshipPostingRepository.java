package org.campus.campusradarbackend.repository;

import org.campus.campusradarbackend.dto.InternshipPostingResponse;
import org.campus.campusradarbackend.model.InternshipPosting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InternshipPostingRepository extends JpaRepository<InternshipPosting, Long> {
    List<InternshipPosting> findByRecruiterId(Long recruiterId);
    List<InternshipPosting> findByisApprovedTrue();
    List<InternshipPosting> findByisApprovedFalse();
}
