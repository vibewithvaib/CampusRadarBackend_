package org.campus.campusradarbackend.repository;

import org.campus.campusradarbackend.model.InternshipPosting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InternshipPostingRepository extends JpaRepository<InternshipPosting, Integer> {
    List<InternshipPosting> findByRecruiterId(Long recruiterId);
}
