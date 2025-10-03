package org.campus.campusradarbackend.service;

import lombok.RequiredArgsConstructor;
import org.campus.campusradarbackend.model.RecruiterProfile;
import org.campus.campusradarbackend.model.StudentProfile;
import org.campus.campusradarbackend.repository.RecruiterProfileRepository;
import org.campus.campusradarbackend.repository.StudentProfileRepository;
import org.campus.campusradarbackend.repository.UserRepository;
import org.campus.campusradarbackend.dto.JwtAuthenticationResponse;
import org.campus.campusradarbackend.dto.SignInRequest;
import org.campus.campusradarbackend.dto.SignUpRequest;
import org.campus.campusradarbackend.model.Role;
import org.campus.campusradarbackend.model.User;
import org.campus.campusradarbackend.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public JwtAuthenticationResponse signup(SignUpRequest request) {
        // Create the core User object
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .isEnabled(false) // User is disabled by default until admin approval
                .build();

        // Save the user first to generate an ID
        User savedUser = userRepository.save(user);

        // Based on the role, create and associate the specific profile
        if (request.getRole() == Role.STUDENT) {
            if (request.getRollNumber() == null || request.getRollNumber().isBlank()) {
                throw new IllegalArgumentException("Roll number is required for student registration.");
            }
            StudentProfile studentProfile = new StudentProfile();
            studentProfile.setUser(savedUser);
            studentProfile.setRollNumber(request.getRollNumber());
            studentProfileRepository.save(studentProfile);
            savedUser.setStudentProfile(studentProfile);
        } else if (request.getRole() == Role.RECRUITER) {
            if (request.getCompanyName() == null || request.getLinkedInProfile() == null || request.getWorkEmail() == null) {
                throw new IllegalArgumentException("Company Name, LinkedIn Profile, and Work Email are required for recruiter registration.");
            }
            RecruiterProfile recruiterProfile = new RecruiterProfile();
            recruiterProfile.setUser(savedUser);
            recruiterProfile.setCompanyName(request.getCompanyName());
            recruiterProfile.setLinkedInProfile(request.getLinkedInProfile());
            recruiterProfile.setWorkEmail(request.getWorkEmail());
            recruiterProfileRepository.save(recruiterProfile);
            savedUser.setRecruiterProfile(recruiterProfile);
        }

        var jwt = jwtService.generateToken(savedUser);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    public JwtAuthenticationResponse signin(SignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        var jwt = jwtService.generateToken((UserDetails) user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }
}
