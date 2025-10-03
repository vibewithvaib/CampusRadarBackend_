package org.campus.campusradarbackend.service;

import lombok.RequiredArgsConstructor;
import org.campus.campusradarbackend.dto.ApplicationResponse;
import org.campus.campusradarbackend.dto.InternshipPostingResponse;
import org.campus.campusradarbackend.dto.UserResponse;
import org.campus.campusradarbackend.model.InternshipApplication;
import org.campus.campusradarbackend.model.InternshipPosting;
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
    private final InternshipPostingRepository internshipRepository;
    private final ApplicationRepository applicationRepository;
    private final InternshipPostingRepository internshipPostingRepository;
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InternshipPostingResponse> getAllInternships() {
        List<InternshipPosting> postings = internshipRepository.findAll();
        return postings.stream()
                .map(InternshipPostingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getAllApplications() {
        List<InternshipApplication> applications = applicationRepository.findAll();
        return applications.stream()
                .map(ApplicationResponse::fromEntity)
                .collect(Collectors.toList());
    }


    public List<UserResponse> getPendingUserApprovals() {
        return userRepository.findByEnabledFalse().stream().map(UserResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public UserResponse approveUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(true);
        User savedUser = userRepository.save(user);
        return UserResponse.fromEntity(savedUser);
    }

    public List<InternshipPostingResponse> getPendingInternshipApprovals() {
        return internshipPostingRepository.findByApprovedIsFalse().stream().map(InternshipPostingResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public InternshipPostingResponse approveInternship(Long internshipId) {
        InternshipPosting internship = internshipPostingRepository.findById(internshipId).orElseThrow(() -> new RuntimeException("Internship not found"));
        internship.setApproved(true);
        InternshipPosting savedInternship = internshipPostingRepository.save(internship);
        return InternshipPostingResponse.fromEntity(savedInternship);
    }
}
