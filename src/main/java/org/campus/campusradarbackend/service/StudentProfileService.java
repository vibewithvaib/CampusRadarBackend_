package org.campus.campusradarbackend.service;

//import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.campus.campusradarbackend.dto.StudentProfileRequest;
import org.campus.campusradarbackend.dto.StudentProfileResponse;
import org.campus.campusradarbackend.model.StudentProfile;
import org.campus.campusradarbackend.model.User;
import org.campus.campusradarbackend.repository.StudentProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentProfileService {

    private final StudentProfileRepository studentProfileRepository;

    @Transactional(readOnly = true)
    public StudentProfileResponse getStudentProfile(User user) {
        StudentProfile profileEntity = studentProfileRepository.findByUserId(user.getId())
                .orElse(new StudentProfile(user));

        return StudentProfileResponse.fromEntity(profileEntity);
    }

    @Transactional
    public StudentProfile createOrUpdateProfile(User user, StudentProfileRequest request) {

        StudentProfile profile = studentProfileRepository.findByUserId(user.getId())
                .orElse(new StudentProfile(user));

        profile.setHeadline(request.getHeadline());
        profile.setResumeUrl(request.getResumeUrl());
        profile.setSkills(request.getSkills());

        return studentProfileRepository.save(profile);
    }
}
