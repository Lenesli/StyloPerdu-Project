package com.example.demo.service;


import com.example.demo.repository.UserRepository;
import com.example.demo.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User updateProfileImage(String email, String base64Image) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (base64Image != null && !base64Image.isEmpty()) {
            // Convert Base64 string to byte array
            byte[] profileImage = Base64.getDecoder().decode(
                    base64Image.replaceAll("^data:image/[^;]+;base64,", "")
            );
            user.setProfileImage(profileImage);
            return userRepository.save(user);
        }

        return user;
    }

    public String getProfileImageAsBase64(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getProfileImage() != null) {
            return Base64.getEncoder().encodeToString(user.getProfileImage());
        }

        return null;
    }
}