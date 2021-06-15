package com.example.redditclone.service;

import com.example.redditclone.dto.RegisterRequestDTO;

import com.example.redditclone.exception.SpringRedditException;
import com.example.redditclone.exception.UserDoesNotExistException;
import com.example.redditclone.exception.VerificationTokenNotFoundException;
import com.example.redditclone.model.NotificationEmail;
import com.example.redditclone.model.User;
import com.example.redditclone.model.VerificationToken;
import com.example.redditclone.repository.UserRepository;
import com.example.redditclone.repository.VerificationTokenRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {


    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository, VerificationTokenRepository verificationTokenRepository, MailService mailService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.mailService = mailService;
    }


    @Transactional
    public void signUp(RegisterRequestDTO registerRequestDTO) throws SpringRedditException {
        User user = modelMapper.map(registerRequestDTO, User.class);
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);
        userRepository.save(user);

        String token = getVerificationToken(user);
        mailService.sendMail(new NotificationEmail("Please Activate your Account",
                user.getEmail(), "Thank you for signing up to Spring Reddit, " +
                "please click on the below url to activate your account : " +
                "http://localhost:8080/api/auth/accountVerification/" + token));

    }

    private String getVerificationToken(User user) {
        VerificationToken verificationToken = new VerificationToken(UUID.randomUUID().toString(), user);
        verificationTokenRepository.save(verificationToken);
        return verificationToken.getToken();
    }


    public void verifyAccount(String token) throws VerificationTokenNotFoundException, UserDoesNotExistException {
        VerificationToken verificationToken = verificationTokenRepository.findVerificationTokenByToken(token)
                .orElseThrow(VerificationTokenNotFoundException::new);
        getUserAndEnable(verificationToken.getUser());
    }

    private void getUserAndEnable(User user) throws UserDoesNotExistException {
        User actualUser = userRepository.findById(user.getUserId()).orElseThrow(UserDoesNotExistException::new);
        userRepository.save(actualUser).setEnabled(true);
    }
}
