package org.campus.campusradarbackend.repository;

import org.campus.campusradarbackend.model.RecruiterProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruiterProfileRepository extends JpaRepository<RecruiterProfile, Long> {

}
