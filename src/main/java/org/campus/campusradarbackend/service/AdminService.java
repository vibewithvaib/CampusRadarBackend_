package org.campus.campusradarbackend.service;

import lombok.RequiredArgsConstructor;
import org.campus.campusradarbackend.dto.*;
import org.campus.campusradarbackend.model.InternshipApplication;
import org.campus.campusradarbackend.model.InternshipPosting;
import org.campus.campusradarbackend.model.Role;
import org.campus.campusradarbackend.model.User;
import org.campus.campusradarbackend.repository.ApplicationRepository;
import org.campus.campusradarbackend.repository.InternshipPostingRepository;
import org.campus.campusradarbackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final InternshipPostingRepository internshipPostingRepository;

    public List<StudentDetailResponse> getPendingStudents() {
        return userRepository.findByisEnabledFalseAndRole(Role.STUDENT)
                .stream()
                .map(StudentDetailResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<RecruiterDetailResponse> getPendingRecruiters() {
        return userRepository.findByisEnabledFalseAndRole(Role.RECRUITER)
                .stream()
                .map(RecruiterDetailResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<InternshipPosting> getPendingInternships() {
        return internshipPostingRepository.findByisApprovedFalse();
    }


    @Transactional
    public void approveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Transactional
    public void approveInternship(Long internshipId) {
        InternshipPosting posting = internshipPostingRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship posting not found with ID: " + internshipId));
        posting.setApproved(true);
        internshipPostingRepository.save(posting);
    }
}
