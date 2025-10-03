package org.campus.campusradarbackend.service;

import lombok.RequiredArgsConstructor;
import org.campus.campusradarbackend.dto.InternshipPostRequest;
import org.campus.campusradarbackend.dto.InternshipPostingResponse;
import org.campus.campusradarbackend.model.InternshipPosting;
import org.campus.campusradarbackend.model.User;
import org.campus.campusradarbackend.repository.InternshipPostingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipPostingRepository internshipRepository;

    @Transactional
    public InternshipPosting createInternship(User recruiter, InternshipPostRequest request) {
        InternshipPosting newPosting = new InternshipPosting();
        newPosting.setRecruiter(recruiter);
        newPosting.setTitle(request.getTitle());
        newPosting.setDescription(request.getDescription());
        newPosting.setLocation(request.getLocation());
        newPosting.setDurationInWeeks(request.getDurationInWeeks());
        newPosting.setStipend(request.getStipend());
        newPosting.setRequiredSkills(request.getRequiredSkills());
        newPosting.setApproved(false);
        return internshipRepository.save(newPosting);
    }

    @Transactional(readOnly = true)
    public List<InternshipPostingResponse> getInternshipsByRecruiter(User recruiter) {
        List<InternshipPosting> postings = internshipRepository.findByRecruiterId(recruiter.getId());

        return postings.stream()
                .map(InternshipPostingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<InternshipPostingResponse> getAllApprovedInternships() {
        return internshipRepository.findByApprovedIsTrue()
                .stream()
                .map(InternshipPostingResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
